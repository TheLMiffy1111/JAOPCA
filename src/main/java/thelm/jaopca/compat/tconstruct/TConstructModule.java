package thelm.jaopca.compat.tconstruct;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import tconstruct.library.crafting.FluidType;
import tconstruct.util.config.PHConstruct;
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

@JAOPCAModule(modDependencies = "TConstruct")
public class TConstructModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();

	private static boolean jaopcaOnly = true;
	private static Map<IMaterial, Integer> tempMap = new TreeMap<>();
	public static ToIntFunction<IMaterial> tempFunction;

	private Map<IMaterial, IDynamicSpecConfig> configs;

	public TConstructModule() {
		tempFunction = this::getTemperature;
	}

	@Override
	public String getName() {
		return "tconstruct";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "molten");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		if(BLACKLIST.isEmpty()) {
			BLACKLIST.addAll(FluidType.fluidTypes.keySet());
			if(Loader.isModLoaded("Mariculture")) {
				Collections.addAll(BLACKLIST, "Magnesium", "Rutile");
			}
		}
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
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		for(IMaterial material : moduleData.getMaterials()) {
			if(!jaopcaOnly || moltenMaterials.contains(material)) {
				String oreOredict = miscHelper.getOredictName("ore", material.getName());
				String moltenName = miscHelper.getFluidName(".molten", material.getName());
				boolean isIngot = material.getType().isIngot();
				int amount = (int)Math.floor(144*PHConstruct.ingotsPerOre);
				int temperature = tempFunction.applyAsInt(material);
				helper.registerMeltingRecipe(
						miscHelper.getRecipeKey("tconstruct.ore_to_molten", material.getName()),
						oreOredict, oreOredict, moltenName, amount, temperature);
			}
		}
	}

	public int getTemperature(IMaterial material) {
		return tempMap.computeIfAbsent(material, key->configs.get(key).getDefinedInt("tconstruct.temperature", 600, "The base smeltery temperature of this material."));
	}
}
