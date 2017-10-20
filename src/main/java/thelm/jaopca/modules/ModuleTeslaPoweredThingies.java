package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.ndrei.teslapoweredthingies.common.IRecipeOutput;
import net.ndrei.teslapoweredthingies.common.OreOutput;
import net.ndrei.teslapoweredthingies.common.SecondaryOreOutput;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerOreRecipe;
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipe;
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipes;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleTeslaPoweredThingies extends ModuleBase {

	//I'm sure there are alloys in this list
	public static final ArrayList<String> LIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Coal", "Diamond", "Emerald", "Lapis", "Redstone", "Adamantine", "Antimony", "Aquarium", "Bismuth", "Brass",
			"Bronze", "Coldiron", "Copper", "Cupronickel", "Electrum", "Invar", "Lead", "Mercury", "Mithril", "Nickel", "Pewter",
			"Platinum", "Silver", "Starsteel", "Tin", "Zinc", "Aluminium", "AluminiumBrass", "Cadmium", "GalvanizedSteel", "Iridium",
			"Magnesium", "Manganese", "Nichrome", "Osmium", "Plutonium", "Rutile", "StainlessSteel", "Tantalum", "Titanium", "Tungsten",
			"Uranium", "Zirconium"
			);
	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Aluminium", "Nickel", "Platinum", "Iridium", "Coal", "Diamond", "Emerald",
			"Redstone", "Lapis"
			);

	public static final ItemEntry TESLA_LUMP_ENTRY = new ItemEntry(EnumEntryType.ITEM, "teslaLump", new ModelResourceLocation("jaopca:tesla_lump#inventory"), BLACKLIST).
			setOreTypes(EnumOreType.ORE);

	@Override
	public String getName() {
		return "teslapoweredthingies";
	}

	@Override
	public List<String> getOreBlacklist() {
		return BLACKLIST;
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return EnumSet.<EnumOreType>copyOf(Arrays.asList(EnumOreType.ORE));
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(TESLA_LUMP_ENTRY);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			int amount = 2;
			switch(entry.getOreType()) {
			case DUST:
				amount = 6;
				break;
			case GEM:
				amount = 3;
				break;
			default:
				break;
			}
			if(LIST.contains(entry.getOreName()) && Utils.doesOreNameExist("teslaLump"+entry.getOreName())) {
				ArrayList<IRecipeOutput> outputList = Lists.<IRecipeOutput>newArrayList();
				outputList.add(new OreOutput("dust"+entry.getOreName(), amount));
				if(entry.hasExtra()) {
					outputList.add(new SecondaryOreOutput(0.1F, "dust"+entry.getExtra(), 1));
				}
				if(entry.hasSecondExtra()) {
					outputList.add(new SecondaryOreOutput(0.05F, "dust"+entry.getSecondExtra(), 1));
				}
				IRecipeOutput[] outputs = outputList.toArray(new IRecipeOutput[0]);
				PowderMakerRecipes.INSTANCE.registerRecipe(
						new PowderMakerOreRecipe(1, "teslaLump"+entry.getOreName(), outputs)
						);
			}
			else {
				ArrayList<IRecipeOutput> outputList = Lists.<IRecipeOutput>newArrayList();
				outputList.add(new OreOutput("dust"+entry.getOreName(), amount));
				outputList.add(new SecondaryOutput(0.15F, Blocks.COBBLESTONE));
				if(entry.hasExtra()) {
					outputList.add(new SecondaryOreOutput(0.1F, "dust"+entry.getExtra(), 1));
				}
				if(entry.hasSecondExtra()) {
					outputList.add(new SecondaryOreOutput(0.05F, "dust"+entry.getSecondExtra(), 1));
				}
				IRecipeOutput[] outputs = outputList.toArray(new IRecipeOutput[0]);
				for(ItemStack ore : OreDictionary.getOres("ore"+entry.getOreName())) {
					PowderMakerRecipes.INSTANCE.registerRecipe(
							new PowderMakerRecipe(ore, outputs)
							);
				}
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("teslaLump")) {
			//Apparently TeslaPoweredThingies make ore to lump recipes for us
			int amount = 2;
			switch(entry.getOreType()) {
			case DUST:
				amount = 6;
				break;
			case GEM:
				amount = 3;
				break;
			default:
				break;
			}
			ArrayList<IRecipeOutput> outputList = Lists.<IRecipeOutput>newArrayList();
			outputList.add(new OreOutput("dust"+entry.getOreName(), amount));
			if(entry.hasExtra()) {
				outputList.add(new SecondaryOreOutput(0.1F, "dust"+entry.getExtra(), 1));
			}
			if(entry.hasSecondExtra()) {
				outputList.add(new SecondaryOreOutput(0.05F, "dust"+entry.getSecondExtra(), 1));
			}
			IRecipeOutput[] outputs = outputList.toArray(new IRecipeOutput[0]);
			PowderMakerRecipes.INSTANCE.registerRecipe(
					new PowderMakerOreRecipe(1, "teslaLump"+entry.getOreName(), outputs)
					);
		}
	}
}
