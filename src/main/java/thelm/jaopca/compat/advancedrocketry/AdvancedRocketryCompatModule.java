package thelm.jaopca.compat.advancedrocketry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "advancedrocketry")
public class AdvancedRocketryCompatModule implements IModule {

	private static final Set<String> TO_STICK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "iridium", "iron", "steel", "titanium", "titaniumaluminide", "titaniumiridium"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "copper", "gold", "iridium", "iron", "silicon", "steel", "tin",
			"titanium", "titaniumaluminide", "titaniumiridium"));
	private static final Set<String> TO_SHEET_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "copper", "gold", "iridium", "iron", "steel", "titanium",
			"titaniumaluminide", "titaniumiridium"));
	private static Set<String> configToStickBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToSheetBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "advancedrocketry_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStickMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have lathe recipes added."),
				configToStickBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rolling machine to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toSheetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rolling machine to sheet recipes added."),
				configToSheetBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AdvancedRocketryHelper helper = AdvancedRocketryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(ArrayUtils.contains(MaterialType.INGOTS, type) &&
					!TO_STICK_BLACKLIST.contains(name) && !configToStickBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation stickLocation = miscHelper.getTagLocation("sticks", material.getName());
				if(api.getItemTags().contains(stickLocation)) {
					helper.registerLatheRecipe(
							new ResourceLocation("jaopca", "advancedrocketry.material_to_stick."+material.getName()),
							new Object[] {
									materialLocation,
							}, new Object[] {
									stickLocation, 2,
							}, 300, 20);
				}
			}
			if(ArrayUtils.contains(MaterialType.INGOTS, type) &&
					!TO_PLATE_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerRollingMachineRecipe(
							new ResourceLocation("jaopca", "advancedrocketry.material_to_plate."+material.getName()),
							new Object[] {
									materialLocation,
							}, Fluids.WATER, 10, new Object[] {
									plateLocation,
							}, 300, 20);
				}
			}
			if(ArrayUtils.contains(MaterialType.INGOTS, type) &&
					!TO_SHEET_BLACKLIST.contains(name) && !configToSheetBlacklist.contains(name)) {
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				ResourceLocation sheetLocation = miscHelper.getTagLocation("sheets", material.getName());
				if(api.getItemTags().contains(plateLocation) && api.getItemTags().contains(sheetLocation)) {
					helper.registerRollingMachineRecipe(
							new ResourceLocation("jaopca", "advancedrocketry.material_to_sheet."+material.getName()),
							new Object[] {
									plateLocation,
							}, Fluids.WATER, 10, new Object[] {
									sheetLocation,
							}, 300, 20);
				}
			}
		}
	}
}
