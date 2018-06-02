package thelm.jaopca.modules;

import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Lists;

import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleThermalExpansion extends ModuleBase {

	@Override
	public String getName() {
		return "thermalexpansion";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.newArrayList(
				"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Aluminium", "Nickel", "Platinum", "Iridium", "Mithril"
				);
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return Utils.<EnumOreType>enumSetOf(EnumOreType.INGOTS);
	}

	@Override
	public void postInit() {
		ItemStack cinnabar = Utils.getOreStack("crystalCinnabar", 1);
		ItemStack richSlag = Utils.getOreStack("crystalSlagRich", 1);

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(entry.getOreType() == EnumOreType.INGOT && entry.hasExtra()) {
				replacePulverizerRecipe(4000, Utils.getOreStack("ore", entry, 1), Utils.getOreStack("dust", entry, 2), Utils.getOreStackExtra("dust", entry, 1), 10);
				replaceInductionSmelterRecipe(4000, Utils.getOreStack("ore", entry, 1), cinnabar.copy(), Utils.getOreStack("ingot", entry, 3), Utils.getOreStackExtra("ingot", entry, 1), 100);
			}
			if(Loader.isModLoaded("tconstruct")) {
				addCrucibleRecipes(entry);
			}
		}

		if(JAOPCAApi.isModuleLoaded("thaumcraft")) {
			for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("cluster")) {
				boolean hasExtra = entry.hasExtra();
				addPulverizerRecipe(3000, Utils.getOreStack("cluster", entry, 1), Utils.getOreStack("dust", entry, 3), hasExtra ? Utils.getOreStackExtra("dust", entry, 1) : ItemStack.EMPTY, hasExtra ? 20 : 0);
			}
		}
	}

	public static boolean addCrucibleRecipes(IOreEntry entry) {
		if(entry == null) {
			return false;
		}
		Fluid fluid = FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName()));

		if(fluid == null) {
			return false;
		}

		int energy = 4000;
		int fluidIngot = 144;

		ItemStack nugget = Utils.getOreStack("nugget", entry, 1);
		ItemStack ingot = Utils.getOreStack("ingot", entry, 1);
		ItemStack ore = Utils.getOreStack("ore", entry, 1);
		ItemStack block = Utils.getOreStack("block", entry, 1);
		ItemStack dust = Utils.getOreStack("dust", entry, 1);
		ItemStack plate = Utils.getOreStack("plate", entry, 1);

		if(!nugget.isEmpty()) {
			addCrucibleRecipe(energy / 8, nugget, new FluidStack(fluid, fluidIngot / 9));
		}
		if(!ingot.isEmpty()) {
			addCrucibleRecipe(energy, ingot, new FluidStack(fluid, fluidIngot));
		}
		if(!ore.isEmpty()) {
			addCrucibleRecipe(energy * 2, ore, new FluidStack(fluid, fluidIngot * 2));
		}
		if(!block.isEmpty()) {
			addCrucibleRecipe(energy * 8, block, new FluidStack(fluid, fluidIngot * 9));
		}
		if(!dust.isEmpty()) {
			addCrucibleRecipe(energy / 2, dust, new FluidStack(fluid, fluidIngot));
		}
		if(!plate.isEmpty()) {
			addCrucibleRecipe(energy, plate, new FluidStack(fluid, fluidIngot));
		}
		return true;
	}

	//Much more reliable than IMC
	public static void replacePulverizerRecipe(int energy, ItemStack input, ItemStack output, ItemStack bonus, int chance) {
		PulverizerManager.removeRecipe(input);
		PulverizerManager.addRecipe(energy, input, output, bonus, chance);
	}

	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack output, ItemStack bonus, int chance) {
		PulverizerManager.addRecipe(energy, input, output, bonus, chance);
	}

	public static void replaceInductionSmelterRecipe(int energy, ItemStack input1, ItemStack input2, ItemStack output1, ItemStack output2, int chance) {
		SmelterManager.removeRecipe(input1, input2);
		SmelterManager.addRecipe(energy, input1, input2, output1, output2, chance);
	}

	public static void addCrucibleRecipe(int energy, ItemStack input, FluidStack output) {
		CrucibleManager.addRecipe(energy, input, output);
	}

	public static void addNumismaticFuel(ItemStack input, int energy) {
		NumismaticManager.addFuel(input, energy);
	}

	public static void addInsolatorRecipes(int energy, int water, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int secondaryChanceRich, int secondaryChanceFlux) {
		InsolatorManager.addDefaultRecipe(energy, water, primaryInput, primaryOutput, secondaryOutput, secondaryChance, secondaryChanceRich, secondaryChanceFlux, Type.STANDARD);
	}
}
