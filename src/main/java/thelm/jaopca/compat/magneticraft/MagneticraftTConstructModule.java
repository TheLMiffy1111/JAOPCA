package thelm.jaopca.compat.magneticraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.tconstruct.TConstructHelper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = {"magneticraft", "tconstruct"}, classDependencies = "com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipeManager")
public class MagneticraftTConstructModule implements IModule {

	private static final Set<String> CHUNK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Cobalt", "Copper", "Galena", "Gold", "Iron", "Lead", "Mithril", "Nickel",
			"Osmium", "Silver", "Tin", "Tungsten", "Zinc"));
	private static final Set<String> HEAVY_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Copper", "Gold", "Iron", "Lead", "Steel", "Tungsten"));
	private static Set<String> configRockyChunkToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configChunkToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configHeavyPlateToMoltenBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = false;

	@Override
	public String getName() {
		return "magneticraft_tconstruct";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.rockyChunkToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rock chunk melting recipes added."),
				configRockyChunkToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.chunkToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have chunk melting recipes added."),
				configChunkToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.heavyPlateToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have heavy plate melting recipes added."),
				configHeavyPlateToMoltenBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getTemperature(stack)-300;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName("", name);
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(type == MaterialType.INGOT && !CHUNK_BLACKLIST.contains(name) && !configRockyChunkToMoltenBlacklist.contains(name)) {
						String rockyChunkOredict = miscHelper.getOredictName("rockyChunk", name);
						if(oredict.contains(rockyChunkOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("magneticraft_tconstruct.rocky_chunk_to_molten", name),
									rockyChunkOredict, moltenName, 288, tempFunction);
						}
					}
					if(type == MaterialType.INGOT && !CHUNK_BLACKLIST.contains(name) && !configChunkToMoltenBlacklist.contains(name)) {
						String chunkOredict = miscHelper.getOredictName("chunk", name);
						if(oredict.contains(chunkOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("magneticraft_tconstruct.chunk_to_molten", name),
									chunkOredict, moltenName, 288, tempFunction);
						}
					}
					if(!HEAVY_PLATE_BLACKLIST.contains(name) && !configHeavyPlateToMoltenBlacklist.contains(name)) {
						String heavyPlateOredict = miscHelper.getOredictName("heavyPlate", name);
						if(oredict.contains(heavyPlateOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("magneticraft_tconstruct.heavy_plate_to_molten", name),
									heavyPlateOredict, moltenName, 576, tempFunction);
						}
					}
				}
			}
		}
	}
}
