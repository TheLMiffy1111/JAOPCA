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
public class TechRebornIndustrialDustModule implements IModule {

	private static Set<String> configWaterBlacklist = new TreeSet<>();
	private static Set<String> configSodiumPersulfateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "techreborn_industrial_dust";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(1, "dusts");
		builder.put(2, "small_dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.DUST);
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
				config.getDefinedStringList("recipes.sodiumPersulfateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have sodium persulfate	 industrial grinder recipes added."),
				configSodiumPersulfateBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Fluid sodiumPersulfate = BuiltInRegistries.FLUID.get(ResourceLocation.parse("techreborn:sodium_persulfate"));
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
			ResourceLocation secondExtraSmallDustLocation = miscHelper.getTagLocation("small_dusts", material.getExtra(2).getName());
			if(!configWaterBlacklist.contains(material.getName())) {
				Object[] output = {
						materialLocation, 5,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraDustLocation, 1);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustLocation, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_water", material.getName()),
						oreLocation, 1, Fluids.WATER, 81000, 64, 100, output);
			}
			if(!configSodiumPersulfateBlacklist.contains(material.getName())) {
				Object[] output = {
						materialLocation, 5,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraDustLocation, 3);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustLocation, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_sodium_persulfate", material.getName()),
						oreLocation, 1, sodiumPersulfate, 81000, 64, 100, output);
			}
		}
	}
}
