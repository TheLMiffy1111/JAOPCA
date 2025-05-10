package thelm.jaopca.compat.techreborn;

import java.util.ArrayList;
import java.util.Arrays;
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
public class TechRebornIndustrialIngotModule implements IModule {

	static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"bauxite", "certus_quartz", "cinnabar", "coal", "copper", "diamond", "emerald", "galena", "gold", "iron",
			"iridium", "lapis", "lead", "netherite", "netherite_scrap", "peridot", "pyrite", "quartz", "redstone",
			"ruby", "sapphire", "sheldonite", "silver", "sodalite", "sphalerite", "tin", "tungsten"));
	private static Set<String> configWaterBlacklist = new TreeSet<>();
	private static Set<String> configMercuryBlacklist = new TreeSet<>();
	private static Set<String> configSodiumPersulfateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "techreborn_industrial_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(1, "nuggets");
		builder.put(1, "small_dusts");
		builder.put(1, "dusts");
		builder.put(2, "dusts");
		return builder.build();
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
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.sodiumPersulfateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have sodium persulfate	 industrial grinder recipes added."),
				configSodiumPersulfateBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Fluid mercury = BuiltInRegistries.FLUID.get(ResourceLocation.parse("techreborn:mercury"));
		Fluid sodiumPersulfate = BuiltInRegistries.FLUID.get(ResourceLocation.parse("techreborn:sodium_persulfate"));
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			if(!configWaterBlacklist.contains(material.getName())) {
				Object[] output = {
						rawMaterialLocation, 2,
				};
				if(material.hasExtra(1)) {
					IMaterial extraMaterial = material.getExtra(1);
					ResourceLocation extraLocation;
					int extraCount;
					if(extraMaterial.getType().isIngot()) {
						extraLocation = miscHelper.getTagLocation("nuggets", extraMaterial.getName());
						extraCount = 3;
					}
					else {
						extraLocation = miscHelper.getTagLocation("small_dusts", extraMaterial.getName());
						extraCount = 2;
					}
					output = ArrayUtils.addAll(output, extraLocation, extraCount);
				}
				if(material.hasExtra(2)) {
					IMaterial extraMaterial = material.getExtra(2);
					ResourceLocation extraLocation;
					int extraCount;
					if(extraMaterial.getType().isIngot()) {
						extraLocation = miscHelper.getTagLocation("nuggets", extraMaterial.getName());
						extraCount = 3;
					}
					else {
						extraLocation = miscHelper.getTagLocation("small_dusts", extraMaterial.getName());
						extraCount = 2;
					}
					output = ArrayUtils.addAll(output, extraLocation, extraCount);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_raw_material_water", material.getName()),
						oreLocation, 1, Fluids.WATER, 81000, 64, 100, output);
			}
			if(!configMercuryBlacklist.contains(material.getName())) {
				Object[] output = {
						rawMaterialLocation, 3,
				};
				if(material.hasExtra(1)) {
					IMaterial extraMaterial = material.getExtra(1);
					ResourceLocation extraLocation;
					int extraCount;
					if(extraMaterial.getType().isIngot()) {
						extraLocation = miscHelper.getTagLocation("nuggets", extraMaterial.getName());
						extraCount = 3;
					}
					else {
						extraLocation = miscHelper.getTagLocation("small_dusts", extraMaterial.getName());
						extraCount = 2;
					}
					output = ArrayUtils.addAll(output, extraLocation, extraCount);
				}
				if(material.hasExtra(2)) {
					IMaterial extraMaterial = material.getExtra(2);
					ResourceLocation extraLocation;
					int extraCount;
					if(extraMaterial.getType().isIngot()) {
						extraLocation = miscHelper.getTagLocation("nuggets", extraMaterial.getName());
						extraCount = 3;
					}
					else {
						extraLocation = miscHelper.getTagLocation("small_dusts", extraMaterial.getName());
						extraCount = 2;
					}
					output = ArrayUtils.addAll(output, extraLocation, extraCount);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_raw_material_mercury", material.getName()),
						oreLocation, 1, mercury, 81000, 64, 100, output);
			}
			if(!configSodiumPersulfateBlacklist.contains(material.getName()) && material.hasExtra(1)) {
				IMaterial extraMaterial = material.getExtra(1);
				ResourceLocation extraLocation;
				if(extraMaterial.getType() == MaterialType.INGOT) {
					extraLocation = miscHelper.getTagLocation("raw_materials", extraMaterial.getName());
				}
				else {
					extraLocation = miscHelper.getTagLocation("dusts", extraMaterial.getName());
				}
				Object[] output = {
						rawMaterialLocation, 2,
						extraLocation, 1,
				};
				if(material.hasExtra(2)) {
					if(extraMaterial.getType() == MaterialType.INGOT) {
						extraLocation = miscHelper.getTagLocation("raw_materials", extraMaterial.getName());
					}
					else {
						extraLocation = miscHelper.getTagLocation("dusts", extraMaterial.getName());
					}
					output = ArrayUtils.addAll(output, extraLocation, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_raw_material_sodium_persulfate", material.getName()),
						oreLocation, 1, sodiumPersulfate, 81000, 64, 100, output);
			}
		}
	}
}
