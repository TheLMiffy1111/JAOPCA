package thelm.jaopca.compat.techreborn;

import java.util.ArrayList;
import java.util.Arrays;
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
public class TechRebornIndustrialIngotModule implements IModule {

	static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Amethyst", "Apatite", "Ardite", "Bauxite", "Cinnabar", "Coal", "Cobalt",
			"Copper", "Diamond", "Emerald", "Galena", "Gold", "Iridium", "Iron", "Lapis", "Lead", "Malachite",
			"NetherQuartz", "Nickel", "Niter", "Osmium", "Peridot", "Pitchblende", "Pyrite", "Quartz", "Redstone",
			"Ruby", "Saltpeter", "Sapphire", "Sheldonite", "Silver", "Sodalite", "Sphalerite", "Sulfur", "Tanzanite",
			"Teslatite", "Tin", "Topaz", "Tungsten", "Uranium", "Zinc"));
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
		builder.put(0, "dust");
		builder.put(1, "dust");
		builder.put(1, "small_dust");
		builder.put(2, "small_dust");
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
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			String extraSmallDustOredict = miscHelper.getOredictName("dustSmall", material.getExtra(1).getName());
			String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
			String secondExtraSmallDustOredict = miscHelper.getOredictName("dustSmall", material.getExtra(2).getName());
			if(!configWaterBlacklist.contains(material.getName())) {
				Object[] output = {
						dustOredict, 2,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraSmallDustOredict, 1);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustOredict, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_dust_water", material.getName()),
						oreOredict, 1, FluidRegistry.WATER, 1000, 100, 64, output);
			}
			if(!configMercuryBlacklist.contains(material.getName())) {
				Object[] output = {
						dustOredict, 3,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraDustOredict, 1);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustOredict, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_dust_mercury", material.getName()),
						oreOredict, 1, ModFluids.MERCURY, 1000, 100, 64, output);
			}
			if(!configSodiumPersulfateBlacklist.contains(material.getName())) {
				Object[] output = {
						dustOredict, 3,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraSmallDustOredict, 1);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustOredict, 1);
				}
				helper.registerIndustrialGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_dust_sodium_persulfate", material.getName()),
						oreOredict, 1, ModFluids.SODIUMPERSULFATE, 1000, 100, 64, output);
			}
		}
	}
}
