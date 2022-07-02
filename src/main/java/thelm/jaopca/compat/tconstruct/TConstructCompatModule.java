package thelm.jaopca.compat.tconstruct;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
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

@JAOPCAModule(modDependencies = "tconstruct")
public class TConstructCompatModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configBlockToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configPlateToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configGearToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();
	private static Set<String> configMaterialCastBlacklist = new TreeSet<>();
	private static Set<String> configNuggetCastBlacklist = new TreeSet<>();
	private static Set<String> configPlateCastBlacklist = new TreeSet<>();
	private static Set<String> configGearCastBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = true;

	@Override
	public String getName() {
		return "tconstruct_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		TinkerRegistry.getMaterialIntegrations().stream().filter(mi->mi.fluid != null).
		map(mi->mi.oreSuffix).forEach(BLACKLIST::add);
		BLACKLIST.add("Emerald");
		IMiscHelper helper = MiscHelper.INSTANCE;
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material melting recipes added."),
				configMaterialToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block melting recipes added."),
				configBlockToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget melting recipes added."),
				configNuggetToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have dust melting recipes added."),
				configDustToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.plateToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate melting recipes added."),
				configPlateToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.gearToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear melting recipes added."),
				configGearToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block casting recipes added."),
				configToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget casting recipes added."),
				configToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate casting recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear casting recipes added."),
				configToGearBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialCastMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material cast recipes added."),
				configMaterialCastBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetCastMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget cast recipes added."),
				configNuggetCastBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialCastMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have plate cast recipes added."),
				configPlateCastBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialCastMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear cast recipes added."),
				configGearCastBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getTemperature(stack)-300;
		ItemStack ingotCast = TinkerSmeltery.castIngot;
		ItemStack gemCast = TinkerSmeltery.castGem;
		ItemStack nuggetCast = TinkerSmeltery.castNugget;
		ItemStack plateCast = TinkerSmeltery.castPlate;
		ItemStack gearCast = TinkerSmeltery.castGear;
		List<FluidStack> castFluids = TinkerSmeltery.castCreationFluids;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !BLACKLIST.contains(name) && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName("", name);
				int baseAmount = material.getType().isIngot() ? 144 : 666;
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(!configMaterialToMoltenBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerMeltingRecipe(
								miscHelper.getRecipeKey("tconstruct.material_to_molten", name),
								materialOredict, moltenName, baseAmount, tempFunction);
					}
					if(!configBlockToMoltenBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.block_to_molten", name),
									blockOredict, moltenName, baseAmount*9, tempFunction);
						}
					}
					if(!configNuggetToMoltenBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.nugget_to_molten", name),
									nuggetOredict, moltenName, baseAmount/9, tempFunction);
						}
					}
					if(!configPlateToMoltenBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.plate_to_molten", name),
									plateOredict, moltenName, baseAmount, tempFunction);
						}
					}
					if(!configGearToMoltenBlacklist.contains(name)) {
						String gearOredict = miscHelper.getOredictName("gear", name);
						if(oredict.contains(gearOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.gear_to_molten", name),
									gearOredict, moltenName, baseAmount*4, tempFunction);
						}
					}
					if(!configToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("tconstruct.molten_to_material", name),
								(type.isIngot() ? ingotCast : gemCast), moltenName, baseAmount, materialOredict,
								tempFunction, false, false);
					}
					if(!configToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerBasinCastingRecipe(
									miscHelper.getRecipeKey("tconstruct.molten_to_block", name),
									null, moltenName, baseAmount*9, blockOredict,
									tempFunction, false, false);
						}
					}
					if(!configToNuggetBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerTableCastingRecipe(
									miscHelper.getRecipeKey("tconstruct.molten_to_nugget", name),
									nuggetCast, moltenName, baseAmount/9, nuggetOredict,
									tempFunction, false, false);
						}
					}
					if(!configToPlateBlacklist.contains(name)) {
						String plateOredict = miscHelper.getOredictName("plate", name);
						if(oredict.contains(plateOredict)) {
							helper.registerTableCastingRecipe(
									miscHelper.getRecipeKey("tconstruct.molten_to_plate", name),
									plateCast, moltenName, baseAmount, plateOredict,
									tempFunction, false, false);
						}
					}
					if(!configToGearBlacklist.contains(name)) {
						String gearOredict = miscHelper.getOredictName("gear", name);
						if(oredict.contains(gearOredict)) {
							helper.registerTableCastingRecipe(
									miscHelper.getRecipeKey("tconstruct.molten_to_gear", name),
									gearCast, moltenName, baseAmount*4, gearOredict,
									tempFunction, false, false);
						}
					}
				}
			}
			if(!type.isDust() && !BLACKLIST.contains(name) && !configMaterialCastBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(type.isIngot()) {
					int i = 0;
					for(FluidStack stack : castFluids) {
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("tconstruct.material_cast_"+i++, name),
								materialOredict, stack, stack.amount, ingotCast,
								tempFunction, true, true);
					}
				}
				else {
					int i = 0;
					for(FluidStack stack : castFluids) {
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("tconstruct.material_cast_"+i++, name),
								materialOredict, stack, stack.amount, gemCast,
								tempFunction, true, true);
					}
				}
			}
			if(!type.isDust() && !BLACKLIST.contains(name) && !configNuggetCastBlacklist.contains(name)) {
				String nuggetOredict = miscHelper.getOredictName("nugget", name);
				if(oredict.contains(nuggetOredict)) {
					int i = 0;
					for(FluidStack stack : castFluids) {
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("tconstruct.nugget_cast_"+i++, name),
								nuggetOredict, stack, stack.amount, nuggetCast,
								tempFunction, true, true);
					}
				}
			}
			if(!type.isDust() && !BLACKLIST.contains(name) && !configPlateCastBlacklist.contains(name)) {
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(plateOredict)) {
					int i = 0;
					for(FluidStack stack : castFluids) {
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("tconstruct.plate_cast_"+i++, name),
								plateOredict, stack, stack.amount, plateCast,
								tempFunction, true, true);
					}
				}
			}
			if(!type.isDust() && !BLACKLIST.contains(name) && !configGearCastBlacklist.contains(name)) {
				String gearOredict = miscHelper.getOredictName("gear", name);
				if(oredict.contains(gearOredict)) {
					int i = 0;
					for(FluidStack stack : castFluids) {
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("tconstruct.gear_cast_"+i++, name),
								gearOredict, stack, stack.amount, gearCast,
								tempFunction, true, true);
					}
				}
			}
		}
	}
}
