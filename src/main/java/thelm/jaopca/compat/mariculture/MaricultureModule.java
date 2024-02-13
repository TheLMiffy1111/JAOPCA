package thelm.jaopca.compat.mariculture;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import mariculture.core.lib.MetalRates;
import net.minecraft.item.ItemStack;
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

@JAOPCAModule(modDependencies = "Mariculture")
public class MaricultureModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Ardite", "Bronze", "Cobalt", "Copper", "Electrum", "Gold", "Iron", "Lead",
			"Magnesium", "NaturalAluminum", "Nickel", "Osmium", "Platinum", "Rutile", "Silver", "Steel", "Tin",
			"Titanium", "Zinc"));

	private static boolean jaopcaOnly = true;
	private static Map<IMaterial, Integer> tempMap = new TreeMap<>();
	public static ToIntFunction<IMaterial> tempFunction;

	public MaricultureModule() {
		tempFunction = this::getTemperature;
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "mariculture";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "molten");
		builder.put(1, "dust");
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
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MaricultureHelper helper = MaricultureHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		for(IMaterial material : moduleData.getMaterials()) {
			if(!jaopcaOnly || moltenMaterials.contains(material)) {
				String oreOredict = miscHelper.getOredictName("ore", material.getName());
				String moltenName = miscHelper.getFluidName(".molten", material.getName());
				int amount = MetalRates.ORE;
				int temperature = tempFunction.applyAsInt(material);
				if(material.hasExtra(1)) {
					String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
					helper.registerCrucibleRecipe(
							miscHelper.getRecipeKey("mariculture.ore_to_molten", material.getName()),
							oreOredict, 1, moltenName, amount, extraDustOredict, 1, 10, temperature);
				}
				else {
					IDynamicSpecConfig config = configs.get(material);
					String configByproduct = config.getDefinedString("mariculture.byproduct", "minecraft:stone",
							miscHelper.metaItemPredicate(), "The default byproduct material to output in Mariculture's crucible if no extra material is defined.");
					ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

					helper.registerCrucibleRecipe(
							miscHelper.getRecipeKey("mariculture.ore_to_molten", material.getName()),
							oreOredict, 1, moltenName, amount, byproduct, 1, 2, temperature);
				}
			}
		}
	}

	public int getTemperature(IMaterial material) {
		return tempMap.computeIfAbsent(material, key->configs.get(key).getDefinedInt("mariculture.temperature", 1100, "The base smeltery temperature of this material."));
	}
}
