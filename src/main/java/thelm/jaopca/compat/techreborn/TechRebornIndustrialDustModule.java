package thelm.jaopca.compat.techreborn;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import techreborn.init.ModFluids;
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
		builder.put(1, "dust");
		builder.put(2, "small_dust");
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
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
			String secondExtraSmallDustOredict = miscHelper.getOredictName("dustSmall", material.getExtra(2).getName());
			if(!configWaterBlacklist.contains(material.getName())) {
				Object[] output = {
						materialOredict, 5,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraDustOredict, 1);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustOredict, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_water", material.getName()),
						oreOredict, 1, FluidRegistry.WATER, 1000, 100, 64, output);
			}
			if(!configSodiumPersulfateBlacklist.contains(material.getName())) {
				Object[] output = {
						materialOredict, 5,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraDustOredict, 3);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustOredict, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_sodium_persulfate", material.getName()),
						oreOredict, 1, ModFluids.SODIUMPERSULFATE, 1000, 100, 64, output);
			}
		}
	}
}
