package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.recipe.RecipeHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleMekanism implements IModule {

	public static final HashBasedTable<String,String,Gas> GASES_TABLE = HashBasedTable.<String,String,Gas>create();

	public static final ItemEntry DIRTY_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustDirty", new ModelResourceLocation("jaopca:dustDirty#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			));
	public static final ItemEntry CLUMP_ENTRY = new ItemEntry(EnumEntryType.ITEM, "clump", new ModelResourceLocation("jaopca:clump#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			));
	public static final ItemEntry SHARD_ENTRY = new ItemEntry(EnumEntryType.ITEM, "shard", new ModelResourceLocation("jaopca:shard#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			));
	public static final ItemEntry CRYSTAL_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crystal", new ModelResourceLocation("jaopca:crystal#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			));
	public static final ItemEntry CLEAN_SLURRY_ENTRY = new ItemEntry(EnumEntryType.CUSTOM, "slurryClean", null, ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			));
	public static final ItemEntry SLURRY_ENTRY = new ItemEntry(EnumEntryType.CUSTOM, "slurry", null, ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			));

	public static final ArrayList<String> DIRTY_DUST_2_DUST_BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			);
	public static final ArrayList<String> MINOR_COMPAT_BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead", "Nickel", "Aluminum", "Uranium", "Draconium"
			);

	@Override
	public String getName() {
		return "mekanism";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.newArrayList(DIRTY_DUST_ENTRY,CLUMP_ENTRY,SHARD_ENTRY,CRYSTAL_ENTRY,CLEAN_SLURRY_ENTRY,SLURRY_ENTRY);
	}

	@Override
	public void registerCustom(ItemEntry entry, List<IOreEntry> allOres) {
		for(IOreEntry ore : allOres) {
			GasBase gas = new GasBase(entry, ore);
			GasRegistry.register(gas);
			GASES_TABLE.put(entry.name, ore.getOreName(), gas);
		}
	}

	@Override
	public void registerRecipes() {
		//Will switch to IMC later (if it works though)
		for(IOreEntry entry : JAOPCAApi.ORE_ENTRY_LIST) {
			ItemStack dust = Utils.getOreStack("dust"+entry.getOreName(),1);

			if(!MINOR_COMPAT_BLACKLIST.contains(entry.getOreName())) {
				RecipeHelper.addCombinerRecipe(Utils.resizeStack(dust,8), Utils.getOreStack("ore",entry,1));

				for(ItemStack ore : OreDictionary.getOres("ore" + entry.getOreName())) {
					RecipeHelper.addEnrichmentChamberRecipe(Utils.resizeStack(ore, 1), Utils.resizeStack(dust,2));
				}

				for(ItemStack ore : OreDictionary.getOres("ingot" + entry.getOreName())) {
					RecipeHelper.addCrusherRecipe(Utils.resizeStack(ore, 1), Utils.resizeStack(dust,1));
				}
			}

			if(!DIRTY_DUST_2_DUST_BLACKLIST.contains(entry.getOreName())) {
				for(ItemStack ore : OreDictionary.getOres("dustDirty" + entry.getOreName())) {
					RecipeHelper.addEnrichmentChamberRecipe(Utils.resizeStack(ore, 1), Utils.resizeStack(dust,1));
				}
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustDirty")) {
			for(ItemStack ore : OreDictionary.getOres("clump" + entry.getOreName())) {
				RecipeHelper.addCrusherRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("dustDirty", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("clump")) {
			for(ItemStack ore : OreDictionary.getOres("ore" + entry.getOreName())) {
				RecipeHelper.addPurificationChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("clump", entry, 3));
			}

			for(ItemStack ore : OreDictionary.getOres("shard" + entry.getOreName())) {
				RecipeHelper.addPurificationChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("clump", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("shard")) {
			for(ItemStack ore : OreDictionary.getOres("ore" + entry.getOreName())) {
				RecipeHelper.addChemicalInjectionChamberRecipe(Utils.resizeStack(ore, 1), "hydrogenChloride", Utils.getOreStack("shard", entry, 4));
			}

			for(ItemStack ore : OreDictionary.getOres("crystal" + entry.getOreName())) {
				RecipeHelper.addChemicalInjectionChamberRecipe(Utils.resizeStack(ore, 1), "hydrogenChloride", Utils.getOreStack("shard", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystal")) {
			if(GASES_TABLE.contains("slurryClean", entry.getOreName())) {
				RecipeHelper.addChemicalCrystallizerRecipe(new GasStack(GASES_TABLE.get("slurryClean",entry.getOreName()), 200), Utils.getOreStack("crystal", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("slurryClean")) {
			if(GASES_TABLE.contains("slurry", entry.getOreName())) {
				RecipeHelper.addChemicalWasherRecipe(new GasStack(GASES_TABLE.get("slurry",entry.getOreName()),1), new GasStack(GASES_TABLE.get("slurryClean",entry.getOreName()),1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("slurry")) {
			for(ItemStack ore : OreDictionary.getOres("ore" + entry.getOreName())) {
				RecipeHelper.addChemicalDissolutionChamberRecipe(Utils.resizeStack(ore, 1), new GasStack(GASES_TABLE.get("slurry",entry.getOreName()), 1000));
			}
		}
	}

	public static void addCrusherRecipe(ItemStack input, ItemStack output) {
		output.stackSize = 6;
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.writeToNBT(new NBTTagCompound()));
		msg.setTag("output", output.writeToNBT(new NBTTagCompound()));
		FMLInterModComms.sendMessage("Mekanism", "CrusherRecipe", msg);
	}

	public static class GasBase extends Gas {

		public final IOreEntry oreEntry;
		public final ItemEntry itemEntry;

		public GasBase(ItemEntry itemEntry, IOreEntry oreEntry) {
			super(itemEntry.name+oreEntry.getOreName(), "mekanism:blocks/liquid/Liquid" + (itemEntry.prefix.contains("Clean") ? "Clean" : "") + "Ore");
			setUnlocalizedName("jaopca."+itemEntry.name);
			this.oreEntry = oreEntry;
			this.itemEntry = itemEntry;
		}

		@Override
		public String getLocalizedName() {
			return String.format(super.getLocalizedName(), I18n.canTranslate("jaopca.entry."+oreEntry.getOreName()) ? I18n.translateToLocal("jaopca.entry."+oreEntry.getOreName()) : oreEntry.getOreName());
		}
	}
}
