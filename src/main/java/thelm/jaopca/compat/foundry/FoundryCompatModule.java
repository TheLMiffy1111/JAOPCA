package thelm.jaopca.compat.foundry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exter.foundry.api.FoundryAPI;
import exter.foundry.fluid.LiquidMetalRegistry;
import exter.foundry.item.FoundryItems;
import exter.foundry.item.ItemMold;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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

@JAOPCAModule(modDependencies = "foundry@[3,)")
public class FoundryCompatModule implements IModule {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Set<String> BLACKLIST = new TreeSet<>();
	private static Set<String> configMaterialToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configBlockToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configDustToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configTinyDustToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configSmallDustToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configPlateToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configGearToLiquidBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configTableToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();
	private static Set<String> configTableToBlockBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configTableToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configToRodBlacklist = new TreeSet<>();
	private static Set<String> configTableToRodBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configTableMoltenToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToBlockBlacklist = new TreeSet<>();
	private static Set<String> configTableMoltenToBlockBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToDustBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToPlateBlacklist = new TreeSet<>();
	private static Set<String> configTableMoltenToPlateBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToGearBlacklist = new TreeSet<>();
	private static Set<String> configMoltenToRodBlacklist = new TreeSet<>();
	private static Set<String> configTableMoltenToRodBlacklist = new TreeSet<>();

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
		BLACKLIST.addAll(LiquidMetalRegistry.INSTANCE.getFluidNames());
		Collections.addAll(BLACKLIST, "Aluminum", "Constantan");
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
				config.getDefinedStringList("recipes.tinyDustToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have tiny dust melting recipes added."),
				configTinyDustToLiquidBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.smallDustToLiquidMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have small dust melting recipes added."),
				configSmallDustToLiquidBlacklist);
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
				config.getDefinedStringList("recipes.tableToMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting table recipes added."),
				configTableToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block casting recipes added."),
				configToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tableToBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block casting table recipes added."),
				configTableToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget casting recipes added."),
				configToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have atomizer to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate casting recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tableToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate casting table recipes added."),
				configTableToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear casting recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tableToRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have rod casting table recipes added."),
				configTableToRodBlacklist);
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting recipes added."),
				configMoltenToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tableMoltenToMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting table recipes added."),
				configTableMoltenToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten block casting recipes added."),
				configMoltenToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tableMoltenToBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten block casting table recipes added."),
				configTableMoltenToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten nugget casting recipes added."),
				configMoltenToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have atomizer molten to dust recipes added."),
				configMoltenToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten plate casting recipes added."),
				configMoltenToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tableMoltenToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten plate casting table recipes added."),
				configTableMoltenToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.moltenToGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten gear casting recipes added."),
				configMoltenToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.tableMoltenToRodMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have molten rod casting table recipes added."),
				configTableMoltenToRodBlacklist);
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
		ItemStack ingotCast = FoundryItems.mold(ItemMold.SubItem.INGOT);
		ItemStack blockCast = FoundryItems.mold(ItemMold.SubItem.BLOCK);
		ItemStack nuggetCast = FoundryItems.mold(ItemMold.SubItem.NUGGET);
		ItemStack plateCast = FoundryItems.mold(ItemMold.SubItem.PLATE);
		ItemStack gearCast = FoundryItems.mold(ItemMold.SubItem.GEAR);
		ItemStack rodCast = FoundryItems.mold(ItemMold.SubItem.ROD);
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
					if(!configTinyDustToLiquidBlacklist.contains(name)) {
						String tinyDustOredict = miscHelper.getOredictName("dustTiny", name);
						if(oredict.contains(tinyDustOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("foundry.tiny_dust_to_liquid", name),
									tinyDustOredict, 1, liquidName, baseAmount/9,
									tempFunction, speedFunction);
						}
					}
					if(!configSmallDustToLiquidBlacklist.contains(name)) {
						String smallDustOredict = miscHelper.getOredictName("dustSmall", name);
						if(oredict.contains(smallDustOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("foundry.small_dust_to_liquid", name),
									smallDustOredict, 1, liquidName, baseAmount/4,
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
					if(!configTableToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerCastingTableRecipe(
								miscHelper.getRecipeKey("foundry.liquid_to_material_table", name),
								liquidName, baseAmount,
								materialOredict, 1, "ingot");
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
					if(!configTableToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_block_table", name),
									liquidName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9),
									blockOredict, 1, "block");
						}
					}
					if(!configToNuggetBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_nugget", name),
									liquidName, ceilDiv(baseAmount, 9), nuggetCast,
									nuggetOredict, 1, speedFunction);
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
					if(!configTableToPlateBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_plate_table", name),
									liquidName, baseAmount,
									plateOredict, 1, "plate");
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
					if(!configToRodBlacklist.contains(name)) {
						String rodOredict = miscHelper.getOredictName("rod", name);
						if(oredict.contains(rodOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_rod", name),
									liquidName, ceilDiv(baseAmount, 2), rodCast,
									rodOredict, 1, speedFunction);
						}
					}
					if(!configTableToRodBlacklist.contains(name)) {
						String rodOredict = miscHelper.getOredictName("rod", name);
						if(oredict.contains(rodOredict)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("foundry.liquid_to_rod_table", name),
									liquidName, ceilDiv(baseAmount, 2),
									rodOredict, 1, "rod");
						}
					}
				}
			}
			if(type.isIngot() && !BLACKLIST.contains(name) && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName("", name);
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(!configMoltenToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerCastingRecipe(
								miscHelper.getRecipeKey("foundry.molten_to_material", name),
								moltenName, 144, ingotCast,
								materialOredict, 1, speedFunction);
					}
					if(!configTableMoltenToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerCastingTableRecipe(
								miscHelper.getRecipeKey("foundry.molten_to_material_table", name),
								moltenName, 144,
								materialOredict, 1, "ingot");
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
					if(!configTableMoltenToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_block_table", name),
									moltenName, 144*(material.isSmallStorageBlock() ? 4 : 9),
									blockOredict, 1, "block");
						}
					}
					if(!configMoltenToNuggetBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_nugget", name),
									moltenName, 16, nuggetCast,
									nuggetOredict, 1, speedFunction);
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
					if(!configTableMoltenToPlateBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_plate_table", name),
									moltenName, 144,
									plateOredict, 1, "plate");
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
					if(!configMoltenToRodBlacklist.contains(name)) {
						String rodOredict = miscHelper.getOredictName("rod", name);
						if(oredict.contains(rodOredict)) {
							helper.registerCastingRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_rod", name),
									moltenName, 72, rodCast,
									rodOredict, 1, speedFunction);
						}
					}
					if(!configTableMoltenToRodBlacklist.contains(name)) {
						String rodOredict = miscHelper.getOredictName("rod", name);
						if(oredict.contains(rodOredict)) {
							helper.registerCastingTableRecipe(
									miscHelper.getRecipeKey("foundry.molten_to_rod_table", name),
									moltenName, 72,
									rodOredict, 1, "rod");
						}
					}
				}
			}
		}
	}

	@Override
	public void onPostInit(IModuleData moduleData, FMLPostInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
		}
	}

	public int ceilDiv(int a, int b) {
		return a/b + (a%b == 0 ? 0 : 1);
	}
}
