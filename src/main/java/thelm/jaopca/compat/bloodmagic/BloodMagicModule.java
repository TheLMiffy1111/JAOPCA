package thelm.jaopca.compat.bloodmagic;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

@JAOPCAModule(modDependencies = "bloodmagic")
public class BloodMagicModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "hellforged", "iron", "netherite_scrap"));

	private final IForm fragmentForm = ApiImpl.INSTANCE.newForm(this, "bloodmagic_fragments", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("bloodmagic:fragments").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm gravelForm = ApiImpl.INSTANCE.newForm(this, "bloodmagic_gravels", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("bloodmagic:gravels").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "bloodmagic";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this, fragmentForm, gravelForm));
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
		BloodMagicHelper helper = BloodMagicHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		ResourceLocation explosiveLocation = new ResourceLocation("bloodmagic:arc/explosive");
		ResourceLocation resonatorLocation = new ResourceLocation("bloodmagic:arc/resonator");
		ResourceLocation cuttingFluidLocation = new ResourceLocation("bloodmagic:arc/cuttingfluid");
		Item corruptedTinyDust = ForgeRegistries.ITEMS.getValue(new ResourceLocation("bloodmagic:corrupted_tinydust"));
		for(IMaterial material : fragmentForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			IItemInfo fragmentInfo = itemFormType.getMaterialFormInfo(fragmentForm, material);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerARCRecipe(
						new ResourceLocation("jaopca", "bloodmagic.ore_to_fragment."+material.getName()),
						oreLocation, 1, explosiveLocation, new Object[] {
								fragmentInfo, 4,
								fragmentInfo, 1, 0.5D,
						}, false);
				helper.registerARCRecipe(
						new ResourceLocation("jaopca", "bloodmagic.raw_material_to_fragment."+material.getName()),
						rawMaterialLocation, 1, explosiveLocation, new Object[] {
								fragmentInfo, 2,
								fragmentInfo, 1, 0.25D,
						}, false);
			}
			else {
				helper.registerARCRecipe(
						new ResourceLocation("jaopca", "bloodmagic.ore_to_fragment."+material.getName()),
						oreLocation, 1, explosiveLocation, new Object[] {
								fragmentInfo, 3,
						}, false);
			}
		}
		for(IMaterial material : gravelForm.getMaterials()) {
			ResourceLocation fragmentLocation = miscHelper.getTagLocation("bloodmagic:fragments", material.getName());
			IItemInfo gravelInfo = itemFormType.getMaterialFormInfo(gravelForm, material);
			helper.registerARCRecipe(
					new ResourceLocation("jaopca", "bloodmagic.fragment_to_gravel."+material.getName()),
					fragmentLocation, 1, resonatorLocation, new Object[] {
							gravelInfo, 1,
							corruptedTinyDust, 1, 0.5D,
					}, false);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation gravelLocation = miscHelper.getTagLocation("bloodmagic:gravels", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerARCRecipe(
					new ResourceLocation("jaopca", "bloodmagic.gravel_to_dust."+material.getName()),
					gravelLocation, 1, cuttingFluidLocation, new Object[] {
							dustLocation, 1,
					}, false);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerARCRecipe(
						new ResourceLocation("jaopca", "bloodmagic.ore_to_dust_arc."+material.getName()),
						oreLocation, 1, cuttingFluidLocation, new Object[] {
								dustLocation, 3,
						}, false);
				helper.registerARCRecipe(
						new ResourceLocation("jaopca", "bloodmagic.raw_material_to_dust_arc."+material.getName()),
						rawMaterialLocation, 1, cuttingFluidLocation, new Object[] {
								dustLocation, 1,
								dustLocation, 1, 0.5D,
						}, false);
			}
			else {
				helper.registerARCRecipe(
						new ResourceLocation("jaopca", "bloodmagic.ore_to_dust_arc."+material.getName()),
						oreLocation, 1, cuttingFluidLocation, new Object[] {
								dustLocation, 2,
						}, false);
			}
			helper.registerAlchemyTableRecipe(
					new ResourceLocation("jaopca", "bloodmagic.ore_to_dust_alchemy."+material.getName()), new Object[] {
							oreLocation, cuttingFluidLocation,
					}, dustLocation, 2, 400, 200, 1);
		}
	}
}
