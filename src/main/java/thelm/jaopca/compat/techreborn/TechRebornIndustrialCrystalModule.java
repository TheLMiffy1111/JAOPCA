package thelm.jaopca.compat.techreborn;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "techreborn")
public class TechRebornIndustrialCrystalModule implements IModule {

	private static Set<String> configWaterBlacklist = new TreeSet<>();
	private static Set<String> configMercuryBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "techreborn_industrial_crystal";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "small_dusts");
		builder.put(1, "small_dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return TechRebornIndustrialIngotModule.BLACKLIST;
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.waterMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have water industrial grinder recipes added."),
				configWaterBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.mercuryMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have mercury industrial grinder recipes added."),
				configMercuryBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Fluid mercury = BuiltInRegistries.FLUID.get(ResourceLocation.parse("techreborn:mercury"));
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			ResourceLocation smallDustLocation = miscHelper.getTagLocation("small_dusts", material.getName());
			ResourceLocation extraSmallDustLocation = miscHelper.getTagLocation("small_dusts", material.getExtra(1).getName());
			if(!configWaterBlacklist.contains(material.getName())) {
				Object[] output = {
						materialLocation, 1,
						smallDustLocation, 6,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraSmallDustLocation, 2);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_water", material.getName()),
						oreLocation, 1, Fluids.WATER, 81000, 64, 100, output);
			}
			if(!configMercuryBlacklist.contains(material.getName())) {
				Object[] output = {
						materialLocation, 2,
						smallDustLocation, 3,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, smallDustLocation, 2);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_mercury", material.getName()),
						oreLocation, 1, mercury, 81000, 64, 100, output);
			}
		}
	}
}
