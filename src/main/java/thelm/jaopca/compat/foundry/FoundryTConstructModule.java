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
import exter.foundry.registry.LiquidMetalRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.plugins.gears.TinkerGears;
import tconstruct.smeltery.TinkerSmeltery;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.tconstruct.TConstructHelper;
import thelm.jaopca.compat.tconstruct.TConstructModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = {"foundry", "TConstruct"})
public class FoundryTConstructModule implements IModule {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Set<String> BLACKLIST = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configToGearBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "foundry_tconstruct";
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
				config.getDefinedStringList("recipes.toGearMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have gear casting recipes added."),
				configToGearBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		int baseAmount = FoundryAPI.FLUID_AMOUNT_INGOT;
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getTemperature(stack)-300;
		ItemStack ingotCast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
		ItemStack nuggetCast = new ItemStack(TinkerSmeltery.metalPattern, 1, 27);
		ItemStack gearCast = new ItemStack(TinkerGears.gearCast);
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !BLACKLIST.contains(name)) {
				String liquidName = miscHelper.getFluidName("foundry_liquid", name);
				int baseTemp = TConstructModule.tempFunction.applyAsInt(material);
				if(FluidRegistry.isFluidRegistered(liquidName)) {
					if(!configToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("foundry_tconstruct.liquid_to_material", name),
								ingotCast, liquidName, baseAmount, materialOredict,
								80, false);
					}
					if(!configToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerBasinCastingRecipe(
									miscHelper.getRecipeKey("foundry_tconstruct.liquid_to_block", name),
									null, liquidName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9), blockOredict,
									400, false);
						}
					}
					if(!configToNuggetBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerTableCastingRecipe(
									miscHelper.getRecipeKey("foundry_tconstruct.liquid_to_nugget", name),
									nuggetCast, liquidName, ceilDiv(baseAmount, 9), nuggetOredict,
									40, false);
						}
					}
					if(!configToGearBlacklist.contains(name)) {
						String gearOredict = miscHelper.getOredictName("gear", name);
						if(oredict.contains(gearOredict)) {
							helper.registerTableCastingRecipe(
									miscHelper.getRecipeKey("foundry_tconstruct.liquid_to_gear", name),
									gearCast, liquidName, baseAmount*4, gearOredict,
									50, false);
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
