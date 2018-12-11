package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.IProperties;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.JsonUtils;
import thelm.jaopca.api.utils.Utils;

public class ModuleMekanism extends ModuleBase {

	public static final HashBasedTable<String,String,Gas> GASES_TABLE = HashBasedTable.<String,String,Gas>create();

	public static final EnumEntryType GAS = EnumEntryType.addEntryType("GAS", ModuleMekanism::checkGasEntry, ModuleMekanism::registerGases, GasProperties.DEFAULT, ModuleMekanism::parseGasPpt);

	public static final GasProperties CLEAN_SLURRY_PROPERTIES = new GasProperties().
			setIconName("mekanism:blocks/liquid/liquidcleanore").
			setVisible(false);
	public static final GasProperties SLURRY_PROPERTIES = new GasProperties().
			setIconName("mekanism:blocks/liquid/liquidore").
			setVisible(false);

	public static final HashMap<IOreEntry,ItemStack> ORE_BASES = Maps.<IOreEntry,ItemStack>newHashMap();

	public static final ItemEntry DIRTY_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustDirty", new ModelResourceLocation("jaopca:dust_dirty#inventory"), ImmutableList.<String>of(
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
	public static final ItemEntry CLEAN_SLURRY_ENTRY = new ItemEntry(GAS, "slurryClean", null, ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			)).setProperties(CLEAN_SLURRY_PROPERTIES).skipWhenGrouped(true);
	public static final ItemEntry SLURRY_ENTRY = new ItemEntry(GAS, "slurry", null, ImmutableList.<String>of(
			"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead"
			)).setProperties(SLURRY_PROPERTIES).skipWhenGrouped(true);

	public static final ArrayList<String> MINOR_COMPAT_BLACKLIST = Lists.<String>newArrayList(
			"Nickel", "Aluminum", "Uranium", "Draconium"
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
	public EnumSet<EnumOreType> getOreTypes() {
		return Utils.<EnumOreType>enumSetOf(EnumOreType.DUSTLESS);
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead", "Quartz", "Diamond", "Lapis", "Coal", "RefinedObsidian", "Redstone", "RefinedGlowstone", "Bronze"
				);
	}

	@Override
	public List<ItemEntryGroup> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(DIRTY_DUST_ENTRY,CLUMP_ENTRY,SHARD_ENTRY,CRYSTAL_ENTRY,CLEAN_SLURRY_ENTRY,SLURRY_ENTRY));
	}

	@Override
	public void registerConfigs(Configuration config) {
		JAOPCAApi.MODULE_TO_ORES_MAP.get(this).stream().filter(entry->entry.getOreType() == EnumOreType.INGOT && !MINOR_COMPAT_BLACKLIST.contains(entry.getOreName())).forEach(entry->{
			ORE_BASES.put(entry, Utils.parseItemStack(config.get(Utils.to_under_score(entry.getOreName()), "mekanismBase", "minecraft:cobblestone", "Item to use when recreating the ore. (Mekanism)").setRequiresMcRestart(true).getString()));
		});
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			switch(entry.getOreType()) {
			case GEM: {
				for(ItemStack ore : Utils.getOres("ore" + entry.getOreName())) {
					addEnrichmentChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("gem", entry, 2));
				}
			}
			case GEM_ORELESS: {
				for(ItemStack ore : Utils.getOres("dust" + entry.getOreName())) {
					addEnrichmentChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("gem", entry, 1));
				}

				for(ItemStack ore : Utils.getOres("gem" + entry.getOreName())) {
					addCrusherRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("dust", entry, 1));
				}
				break;
			}
			case INGOT: {
				if(!MINOR_COMPAT_BLACKLIST.contains(entry.getOreName())) {
					ItemStack base = ORE_BASES.get(entry);
					for(ItemStack ore : Utils.getOres("dust" + entry.getOreName())) {
						addCombinerRecipe(Utils.resizeStack(ore, 8), base, Utils.getOreStack("ore", entry, 1));
					}

					for(ItemStack ore : Utils.getOres("ore" + entry.getOreName())) {
						addEnrichmentChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("dust", entry, 2));
					}

					for(ItemStack ore : Utils.getOres("ingot" + entry.getOreName())) {
						addCrusherRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("dust", entry, 1));
					}
				}

				for(ItemStack ore : Utils.getOres("dustDirty" + entry.getOreName())) {
					addEnrichmentChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("dust", entry, 1));
				}
				break;
			}
			case INGOT_ORELESS: {
				for(ItemStack ore : Utils.getOres("ingot" + entry.getOreName())) {
					addCrusherRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("dust", entry, 1));
				}
				break;
			}
			default:
				break;
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustDirty")) {
			for(ItemStack ore : Utils.getOres("clump" + entry.getOreName())) {
				addCrusherRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("dustDirty", entry, 1));
			}
			if(Loader.isModLoaded("ic2")) {
				ModuleIndustrialCraft.addMaceratorRecipe("clump" + entry.getOreName(), Utils.getOreStack("dustDirty", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("clump")) {
			for(ItemStack ore : Utils.getOres("ore" + entry.getOreName())) {
				addPurificationChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("clump", entry, 3));
			}

			for(ItemStack ore : Utils.getOres("shard" + entry.getOreName())) {
				addPurificationChamberRecipe(Utils.resizeStack(ore, 1), Utils.getOreStack("clump", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("shard")) {
			for(ItemStack ore : Utils.getOres("ore" + entry.getOreName())) {
				addChemicalInjectionChamberRecipe(Utils.resizeStack(ore, 1), "hydrogenchloride", Utils.getOreStack("shard", entry, 4));
			}

			for(ItemStack ore : Utils.getOres("crystal" + entry.getOreName())) {
				addChemicalInjectionChamberRecipe(Utils.resizeStack(ore, 1), "hydrogenchloride", Utils.getOreStack("shard", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystal")) {
			if(GASES_TABLE.contains("slurryClean", entry.getOreName())) {
				addChemicalCrystallizerRecipe(new GasStack(GASES_TABLE.get("slurryClean",entry.getOreName()), 200), Utils.getOreStack("crystal", entry, 1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("slurryClean")) {
			if(GASES_TABLE.contains("slurry", entry.getOreName())) {
				addChemicalWasherRecipe(new GasStack(GASES_TABLE.get("slurry",entry.getOreName()),1), new GasStack(GASES_TABLE.get("slurryClean",entry.getOreName()),1));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("slurry")) {
			for(ItemStack ore : Utils.getOres("ore" + entry.getOreName())) {
				addChemicalDissolutionChamberRecipe(Utils.resizeStack(ore, 1), new GasStack(GASES_TABLE.get("slurry",entry.getOreName()), 1000));
			}
		}
	}

	public static void addCrusherRecipe(ItemStack input, ItemStack output) {
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.writeToNBT(new NBTTagCompound()));
		msg.setTag("output", output.writeToNBT(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "CrusherRecipe", msg);
	}

	public static void addCombinerRecipe(ItemStack input, ItemStack extra, ItemStack output) {
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.writeToNBT(new NBTTagCompound()));
		msg.setTag("extra", extra.writeToNBT(new NBTTagCompound()));
		msg.setTag("output", output.writeToNBT(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "CombinerRecipe", msg);
	}

	public static void addEnrichmentChamberRecipe(ItemStack input, ItemStack output) {
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.writeToNBT(new NBTTagCompound()));
		msg.setTag("output", output.writeToNBT(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "EnrichmentChamberRecipe", msg);
	}

	public static void addPurificationChamberRecipe(ItemStack input, ItemStack output) {
		Gas gasType = GasRegistry.getGas("oxygen");
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.writeToNBT(new NBTTagCompound()));
		msg.setTag("gasType", gasType.write(new NBTTagCompound()));
		msg.setTag("output", output.writeToNBT(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "PurificationChamberRecipe", msg);
	}

	public static void addChemicalInjectionChamberRecipe(ItemStack input, String gasName, ItemStack output) {
		Gas gasType = GasRegistry.getGas(gasName);
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.writeToNBT(new NBTTagCompound()));
		msg.setTag("gasType", gasType.write(new NBTTagCompound()));
		msg.setTag("output", output.writeToNBT(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "ChemicalInjectionChamberRecipe", msg);
	}

	public static void addChemicalCrystallizerRecipe(GasStack input, ItemStack output) {
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.write(new NBTTagCompound()));
		msg.setTag("output", output.writeToNBT(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "ChemicalCrystallizerRecipe", msg);
	}

	public static void addChemicalWasherRecipe(GasStack input, GasStack output) {
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.write(new NBTTagCompound()));
		msg.setTag("output", output.write(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "ChemicalWasherRecipe", msg);
	}

	public static void addChemicalDissolutionChamberRecipe(ItemStack input, GasStack output) {
		NBTTagCompound msg = new NBTTagCompound();
		msg.setTag("input", input.writeToNBT(new NBTTagCompound()));
		msg.setTag("output", output.write(new NBTTagCompound()));
		FMLInterModComms.sendMessage("mekanism", "ChemicalDissolutionChamberRecipe", msg);
	}

	public static boolean checkGasEntry(ItemEntry entry, IOreEntry ore) {
		//return false for now
		return false;
	}

	public static void registerGases(ItemEntry entry) {
		GasProperties ppt = (GasProperties)entry.properties;

		for(IOreEntry ore : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entry.name)) {
			try {
				IGasWithProperty gas = ppt.gasClass.getConstructor(String.class, ItemEntry.class, IOreEntry.class).newInstance(ppt.iconName, entry, ore);
				gas.
				setVisible(ppt.visible);
				GasRegistry.register((Gas)gas);
				GASES_TABLE.put(entry.name, ore.getOreName(), (Gas)gas);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static GasProperties parseGasPpt(JsonObject jsonObject, JsonDeserializationContext context) {
		String iconName = JsonUtils.getString(jsonObject, "icon_name", "minecraft:blocks/water_still");
		boolean visible = JsonUtils.getBoolean(jsonObject, "visible", true);
		GasProperties ppt = new GasProperties().
				setIconName(iconName);
		return ppt;
	}

	public static class GasProperties implements IProperties {

		public static final GasProperties DEFAULT = new GasProperties();

		public String iconName = "minecraft:blocks/water_still";
		public boolean visible = true;
		public Class<? extends IGasWithProperty> gasClass = GasBase.class;

		@Override
		public EnumEntryType getType() {
			return GAS;
		}

		public GasProperties setIconName(String value) {
			iconName = value;
			return this;
		}

		public GasProperties setVisible(boolean value) {
			visible = value;
			return this;
		}

		public GasProperties setGasClass(Class<? extends IGasWithProperty> value) {
			gasClass = value;
			return this;
		}
	}

	public static interface IGasWithProperty extends IObjectWithProperty {

		public IGasWithProperty setVisible(boolean visible);

		@Override
		default void registerModels() {}
	}

	public static class GasBase extends Gas implements IGasWithProperty {

		public final IOreEntry oreEntry;
		public final ItemEntry itemEntry;

		public GasBase(String iconName, ItemEntry itemEntry, IOreEntry oreEntry) {
			super(itemEntry.name+oreEntry.getOreName(), iconName);
			setUnlocalizedName("jaopca."+itemEntry.name);
			this.oreEntry = oreEntry;
			this.itemEntry = itemEntry;
		}

		@Override
		public IOreEntry getOreEntry() {
			return oreEntry;
		}

		@Override
		public ItemEntry getItemEntry() {
			return itemEntry;
		}

		@Override
		public GasBase setVisible(boolean visible) {
			super.setVisible(visible);
			return this;
		}

		@Override
		public String getLocalizedName() {
			return Utils.smartLocalize(this.getUnlocalizedName(), this.getUnlocalizedName()+".%s", this.getOreEntry());
		}
	}
}
