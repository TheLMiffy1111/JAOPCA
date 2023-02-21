package thelm.jaopca.compat.foundry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import exter.foundry.api.FoundryAPI;
import exter.foundry.item.FoundryItems;
import exter.foundry.item.ItemMold;
import exter.foundry.registry.LiquidMetalRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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

@JAOPCAModule(modDependencies = "foundry")
public class FoundryCompatModule implements IModule {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Set<String> BLACKLIST = new TreeSet<>();
	private static Set<String> configMaterialToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configBlockToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configDustToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configPlateToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configGearToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToBlockBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToDustBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToPlateBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToGearBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = false;

	@Override
	public String getName() {
		return "foundry_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		BLACKLIST.addAll(LiquidMetalRegistry.instance.GetFluidNames());
		Collections.addAll(BLACKLIST, "Aluminium", "Chrome");
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material melting recipes added."),
				configMaterialToLiquidBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block melting recipes added."),
				configBlockToLiquidBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget melting recipes added."),
				configNuggetToLiquidBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have dust melting recipes added."),
				configDustToLiquidBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.plateToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate melting recipes added."),
				configPlateToLiquidBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.gearToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear melting recipes added."),
				configGearToLiquidBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block casting recipes added."),
				configToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have atomizer to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate casting recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear casting recipes added."),
				configToGearBlacklist);
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting recipes added."),
				configMoltenToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten block casting recipes added."),
				configMoltenToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have atomizer molten to dust recipes added."),
				configMoltenToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten plate casting recipes added."),
				configMoltenToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten gear casting recipes added."),
				configMoltenToGearBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FoundryHelper helper = FoundryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		int baseAmount = FoundryAPI.FLUID_AMOUNT_INGOT;
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getTemperature(stack);
		ToIntFunction<FluidStack> speedFunction = stack->100;
		ItemStack ingotCast = FoundryItems.Mold(ItemMold.MOLD_INGOT);
		ItemStack blockCast = FoundryItems.Mold(ItemMold.MOLD_BLOCK);
		ItemStack plateCast = FoundryItems.Mold(ItemMold.MOLD_PLATE);
		ItemStack gearCast = FoundryItems.Mold(ItemMold.MOLD_GEAR);
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !BLACKLIST.contains(name)) {
				String liquidName = miscHelper.getFluidName("foundry_liquid", name);
				if(FluidRegistry.isFluidRegistered(liquidName)) {
					if(!configMaterialToLiquidBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerMeltingRecipe(
								miscHelper.getRecipeKey("foundry.material_to_liquid", name),
								materialOredict, 1, liquidName, baseAmount,
								tempFunction, speedFunction);
					}
					if(!configBlockToLiquidBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("foundry.block_to_liquid", name),
									blockOredict, 1, liquidName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9),
									tempFunction, speedFunction);
						}
					}
					if(!configNuggetToLiquidBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("foundry.nugget_to_liquid", name),
									nuggetOredict, 1, liquidName, baseAmount/9,
									tempFunction, speedFunction);
						}
					}
					if(!configDustToLiquidBlacklist.contains(name)) {
						String dustOredict = miscHelper.getOredictName("dust", name);
						if(oredict.contains(dustOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("foundry.dust_to_liquid", name),
									dustOredict, 1, liquidName, baseAmount,
									tempFunction, speedFunction);
						}
					}
					if(!configPlateToLiquidBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("foundry.plate_to_liquid", name),
									plateOredict, 1, liquidName, baseAmount,
									tempFunction, speedFunction);
						}
					}
					if(!configGearToLiquidBlacklist.contains(name)) {
						String gearOredict = miscHelper.getOredictName("gear", name);
						if(oredict.contains(gearOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("foundry.gear_to_liquid", name),
									gearOredict, 1, liquidName, baseAmount*4,
									tempFunction, speedFunction);
						}
					}
					if(!configToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerCastingRecipe(
								miscHelper.getRecipeKey("foundry.liquid_to_material", name),
								liquidName, baseAmount, ingotCast,
								materialOredict, 1, speedFunction);
					}
					if(!configToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_block", name),
									liquidName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9), blockCast,
									blockOredict, 1, speedFunction);
						}
					}
					if(!configToDustBlacklist.contains(name)) {
						String dustOredict = miscHelper.getOredictName("dust", name);
						if(oredict.contains(dustOredict)) {
							helper.registerAtomizerRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_dust", name),
									liquidName, baseAmount, dustOredict, 1);
						}
					}
					if(!configToPlateBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_plate", name),
									liquidName, baseAmount, plateCast,
									plateOredict, 1, speedFunction);
						}
					}
					if(!configToGearBlacklist.contains(name)) {
						String gearOredict = miscHelper.getOredictName("gear", name);
						if(oredict.contains(gearOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_gear", name),
									liquidName, baseAmount*4, gearCast,
									gearOredict, 1, speedFunction);
						}
					}
				}
			}
			if(type.isIngot() && !BLACKLIST.contains(name) && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName(".molten", name);
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(!configMoltenToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerCastingRecipe(
								miscHelper.getRecipeKey("foundry.molten_to_material", name),
								moltenName, 144, ingotCast,
								materialOredict, 1, speedFunction);
					}
					if(!configMoltenToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_block", name),
									moltenName, 144*(material.isSmallStorageBlock() ? 4 : 9), blockCast,
									blockOredict, 1, speedFunction);
						}
					}
					if(!configMoltenToDustBlacklist.contains(name)) {
						String dustOredict = miscHelper.getOredictName("dust", name);
						if(oredict.contains(dustOredict)) {
							helper.registerAtomizerRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_dust", name),
									moltenName, 144, dustOredict, 1);
						}
					}
					if(!configMoltenToPlateBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_plate", name),
									moltenName, 144, plateCast,
									plateOredict, 1, speedFunction);
						}
					}
					if(!configMoltenToGearBlacklist.contains(name)) {
						String gearOredict = miscHelper.getOredictName("gear", name);
						if(oredict.contains(gearOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_gear", name),
									moltenName, 576, gearCast,
									gearOredict, 1, speedFunction);
						}
					}
				}
			}
		}
	}

	public int ceilDiv(int a, int b) {
		return a/b + (a%b == 0 ? 0 : 1);
	}
}
