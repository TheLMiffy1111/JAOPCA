package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleAppliedEnergistics implements IModule {

	@Override
	public String getName() {
		return "ae2";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dust")) {
			if(!entry.getModuleBlacklist().contains(getName())) {
				ItemStack dust = Utils.getOreStack("dust", entry, 1);
				//AOBD required this
				float doubleChance = 0.9F;
				try {
					Class<?> configClass = Class.forName("appeng.core.AEConfig");
					Object instance = configClass.getField("instance").get(null);
					double oreDoublePercentage = configClass.getField("oreDoublePercentage").getDouble(instance);

					doubleChance = (float)(oreDoublePercentage / 100.0D);
				}
				catch(Exception e) {}

				for(ItemStack stack : OreDictionary.getOres("ore" + entry.getOreName())) {
					addGrinderRecipe(stack, dust, dust, doubleChance, Utils.energyI(entry, 8));
				}
				for(ItemStack stack : OreDictionary.getOres("ingot" + entry.getOreName())) {
					addGrinderRecipe(stack, dust, Utils.energyI(entry, 4));
				}
			}
		}
	}

	public static void addGrinderRecipe(ItemStack in, ItemStack out, ItemStack optional, float chance, int turns) {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setTag("in", in.writeToNBT(new NBTTagCompound()));
		tag.setTag("out", out.writeToNBT(new NBTTagCompound()));
		tag.setTag("optional", optional.writeToNBT(new NBTTagCompound()));
		tag.setFloat("chance", chance);
		tag.setInteger("turns", turns);

		FMLInterModComms.sendMessage("appliedenergistics2", "add-grindable", tag);
	}

	public static void addGrinderRecipe(ItemStack in, ItemStack out, int turns) {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setTag("in", in.writeToNBT(new NBTTagCompound()));
		tag.setTag("out", out.writeToNBT(new NBTTagCompound()));
		tag.setInteger("turns", turns);

		FMLInterModComms.sendMessage("appliedenergistics2", "add-grindable", tag);
	}
}
