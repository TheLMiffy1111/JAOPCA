package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleThermalExpansion implements IModule {

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
				"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Aluminum", "Nickel", "Platinum", "Iridium", "Mithril"
				);
	}

	@Override
	public void registerRecipes() {
		ItemStack cinnabar = Utils.getOreStack("crystalCinnabar", 1);
		ItemStack richSlag = Utils.getOreStack("crystalSlagRich", 1);

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			boolean flag = entry.getOreName().equals(entry.getExtra());
			addPulverizerRecipe(Utils.energyI(entry, 4000), Utils.getOreStack("ore", entry, 1), Utils.getOreStack("dust", entry, 2), flag ? null : Utils.getOreStackExtra("dust", entry, 1), 10);
			addInductionSmelterRecipe(Utils.energyI(entry, 4000), Utils.getOreStack("ore", entry, 1), cinnabar.copy(), Utils.getOreStack("ingot", entry, 3), flag ? richSlag.copy() : Utils.getOreStackExtra("ingot", entry, 1), flag ? 75 : 100);
		}
	}

	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack output, ItemStack bonus, int chance) {
		NBTTagCompound data = new NBTTagCompound();

		data.setInteger("energy", energy);
		data.setTag("input", input.writeToNBT(new NBTTagCompound()));
		data.setTag("primaryOutput", output.writeToNBT(new NBTTagCompound()));

		if(bonus != null) {
			data.setTag("secondaryOutput", bonus.writeToNBT(new NBTTagCompound()));
			data.setInteger("secondaryChance", chance);
		}

		FMLInterModComms.sendMessage("addpulverizerrecipe", "thermalexpansion", data);
	}

	public static void addInductionSmelterRecipe(int energy, ItemStack input1, ItemStack input2, ItemStack output1, ItemStack output2, int chance) {
		NBTTagCompound data = new NBTTagCompound();

		data.setInteger("energy", energy);
		data.setTag("primaryInput", input1.writeToNBT(new NBTTagCompound()));
		data.setTag("secondaryInput", input2.writeToNBT(new NBTTagCompound()));
		data.setTag("primaryOutput", output1.writeToNBT(new NBTTagCompound()));

		if(output2 != null) {
			data.setTag("secondaryOutput", output2.writeToNBT(new NBTTagCompound()));
			data.setInteger("secondaryChance", chance);
		}

		FMLInterModComms.sendMessage("thermalexpansion", "addsmelterrecipe", data);
	}

	public static void addCrucibleRecipe(int energy, ItemStack input, FluidStack output) {
		NBTTagCompound message = new NBTTagCompound();

		message.setInteger("energy", energy);
		message.setTag("input", input.writeToNBT(new NBTTagCompound()));
		message.setTag("output", output.writeToNBT(new NBTTagCompound()));

		FMLInterModComms.sendMessage("thermalexpansion", "addcruciblerecipe", message);
	}
}
