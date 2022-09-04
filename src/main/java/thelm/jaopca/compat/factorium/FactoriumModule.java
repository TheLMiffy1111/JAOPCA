package thelm.jaopca.compat.factorium;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "factorium")
public class FactoriumModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iron", "lead", "nickel", "platinum", "silver", "tin", "zinc"));

	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "factorium_chunks", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("factorium:chunks").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm powderForm = ApiImpl.INSTANCE.newForm(this, "factorium_powders", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("factorium:powders").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "factorium";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this, chunkForm, powderForm));
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FactoriumHelper helper = FactoriumHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : chunkForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			IItemInfo chunkInfo = itemFormType.getMaterialFormInfo(chunkForm, material);
			helper.registerCrusherRecipe(
					new ResourceLocation("jaopca", "factorium.ore_to_chunk."+material.getName()),
					oreLocation, chunkInfo, 2, 1F, Items.GRAVEL, 1, 0.1F, ItemStack.EMPTY, 0, 0);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerCrusherRecipe(
						new ResourceLocation("jaopca", "factorium.raw_material_to_chunk."+material.getName()),
						rawMaterialLocation, chunkInfo, 2, 1F);
			}
		}
		for(IMaterial material : powderForm.getMaterials()) {
			ResourceLocation chunkLocation = miscHelper.getTagLocation("factorium:chunks", material.getName());
			IItemInfo powderInfo = itemFormType.getMaterialFormInfo(powderForm, material);
			helper.registerGrinderRecipe(
					new ResourceLocation("jaopca", "factorium.chunk_to_powder."+material.getName()),
					chunkLocation, powderInfo, 1, 1F, ItemStack.EMPTY, 0, 0, powderInfo, 1, 0.25F);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation chunkLocation = miscHelper.getTagLocation("factorium:chunks", material.getName());
			ResourceLocation powderLocation = miscHelper.getTagLocation("factorium:powders", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "factorium.chunk_to_material."+material.getName()),
					chunkLocation, materialLocation, 1, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "factorium.chunk_to_material_blasting."+material.getName()),
					chunkLocation, materialLocation, 1, 0.7F, 100);
			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "factorium.powder_to_material."+material.getName()),
					powderLocation, materialLocation, 1, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "factorium.powder_to_material_blasting."+material.getName()),
					powderLocation, materialLocation, 1, 0.7F, 100);
			helper.registerPulverizerRecipe(
					new ResourceLocation("jaopca", "factorium.powder_to_dust."+material.getName()),
					powderLocation, dustLocation, 1, 1F, ItemStack.EMPTY, 0, 0, dustLocation, 1, 0.25F);
		}
	}
}
