package thelm.jaopca.compat.omegacraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "omegacraft")
public class OmegaCraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "cave_magmite", "copper", "gold", "iron", "lead", "magmite", "silver", "tin"));

	private final IForm dirtyDustForm = ApiImpl.INSTANCE.newForm(this, "omegacraft_dirty_dusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("omegacraft:dirty_dusts").setDefaultMaterialBlacklist(BLACKLIST);

	private boolean recipeRegistered = false;

	public OmegaCraftModule() {
		registerTags();
	}

	@Override
	public String getName() {
		return "omegacraft";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		builder.put(1, "omegacraft");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(dirtyDustForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation dirtyDustLocation = miscHelper.getTagLocation("omegacraft:dirty_dusts", material.getName());
			ResourceLocation materialLocation = MiscHelper.INSTANCE.getTagLocation(material.getType().getFormName(), material.getName());
			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "omegacraft.dirty_dust_to_material."+material.getName()),
					dirtyDustLocation, materialLocation, 1, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "omegacraft.dirty_dust_to_material_blasting."+material.getName()),
					dirtyDustLocation, materialLocation, 1, 0.7F, 100);
		}
	}

	@Override
	public void onRecipeInjectComplete(IModuleData moduleData, IResourceManager resourceManager) {
		if(!recipeRegistered) {
			JAOPCAApi api = ApiImpl.INSTANCE;
			OmegaCraftHelper helper = OmegaCraftHelper.INSTANCE;
			IMiscHelper miscHelper = MiscHelper.INSTANCE;
			for(IMaterial material : dirtyDustForm.getMaterials()) {
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
				IItemInfo dirtyDustInfo = ItemFormType.INSTANCE.getMaterialFormInfo(dirtyDustForm, material);
				ResourceLocation extraDirtyDustLocation = miscHelper.getTagLocation("omegacraft:dirty_dusts", material.getExtra(1).getName());
				int secondaryChance = 25; //TODO config
				helper.registerCrusherRecipe(oreLocation, 1, dirtyDustInfo, 2, extraDirtyDustLocation, 1, secondaryChance);
			}
			ResourceLocation sandLocation = new ResourceLocation("sand");
			for(IMaterial material : moduleData.getMaterials()) {
				ResourceLocation dirtyDustLocation = miscHelper.getTagLocation("omegacraft:dirty_dusts", material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				helper.registerOreWasherRecipe(dirtyDustLocation, 2, dustLocation, 3);
				helper.registerAlloySmelterRecipe(dustLocation, 2, sandLocation, 1, materialLocation, 3);
			}
			recipeRegistered = true;
		}
	}

	private void registerTags() {
		JAOPCAApi api = ApiImpl.INSTANCE;

		api.registerItemTag(new ResourceLocation("forge", "ingots/magmite"),
				new ResourceLocation("omegacraft", "ingot_magmite"));
		api.registerItemTag(new ResourceLocation("forge", "ingots/cave_magmite"),
				new ResourceLocation("omegacraft", "ingot_cave_magmite"));

		api.registerItemTag(new ResourceLocation("forge", "dusts/magmite"),
				new ResourceLocation("omegacraft", "dust_magmite"));
		api.registerItemTag(new ResourceLocation("forge", "dusts/cave_magmite"),
				new ResourceLocation("omegacraft", "dust_cave_magmite"));

		api.registerItemTag(new ResourceLocation("forge", "plates/magmite"),
				new ResourceLocation("omegacraft", "plate_magmite"));
		api.registerItemTag(new ResourceLocation("forge", "plates/cave_magmite"),
				new ResourceLocation("omegacraft", "plate_cave_magmite"));

		api.registerItemTag(new ResourceLocation("forge", "storage_blocks/magmite"),
				new ResourceLocation("omegacraft", "block_magmiteb"));
		api.registerItemTag(new ResourceLocation("forge", "storage_blocks/cave_magmite"),
				new ResourceLocation("omegacraft", "block_cave_magmiteb"));

		//These are needed for proper ore processing support
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/iron"),
				new ResourceLocation("omegacraft", "dirty_dust_iron"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/gold"),
				new ResourceLocation("omegacraft", "dirty_dust_gold"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/copper"),
				new ResourceLocation("omegacraft", "dirty_dust_copper"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/tin"),
				new ResourceLocation("omegacraft", "dirty_dust_tin"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/lead"),
				new ResourceLocation("omegacraft", "dirty_dust_lead"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/silver"),
				new ResourceLocation("omegacraft", "dirty_dust_silver"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/aluminum"),
				new ResourceLocation("omegacraft", "dirty_dust_aluminum"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/magmite"),
				new ResourceLocation("omegacraft", "dirty_dust_magmite"));
		api.registerItemTag(new ResourceLocation("omegacraft", "dirty_dusts/cave_magmite"),
				new ResourceLocation("omegacraft", "dirty_dust_cave_magmite"));
	}
}
