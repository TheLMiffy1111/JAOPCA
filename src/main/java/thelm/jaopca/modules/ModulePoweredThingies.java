package thelm.jaopca.modules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.ndrei.teslapoweredthingies.api.PoweredThingiesAPI;
import net.ndrei.teslapoweredthingies.common.IRecipeOutput;
import net.ndrei.teslapoweredthingies.common.OreOutput;
import net.ndrei.teslapoweredthingies.common.SecondaryOreOutput;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;
import net.ndrei.teslapoweredthingies.items.TeslifiedObsidian;
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipe;
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRegistry;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModulePoweredThingies extends ModuleBase {

	//I'm sure there are alloys in this list
	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Coal", "Diamond", "Emerald", "Lapis", "Redstone", "Adamantine", "Antimony", "Aquarium", "Bismuth", "Brass",
			"Bronze", "Coldiron", "Copper", "Cupronickel", "Electrum", "Invar", "Lead", "Mercury", "Mithril", "Nickel", "Pewter",
			"Platinum", "Silver", "Starsteel", "Tin", "Zinc", "Aluminium", "AluminiumBrass", "Cadmium", "GalvanizedSteel", "Iridium",
			"Magnesium", "Manganese", "Nichrome", "Osmium", "Plutonium", "Rutile", "StainlessSteel", "Tantalum", "Titanium", "Tungsten",
			"Uranium", "Zirconium"
			);

	public static final ItemEntry TESLA_LUMP_ENTRY = new ItemEntry(EnumEntryType.ITEM, "teslaLump", new ModelResourceLocation("jaopca:tesla_lump#inventory"), BLACKLIST).
			setOreTypes(EnumOreType.ORE);
	public static final ItemEntry AUGMENTED_LUMP_ENTRY = new ItemEntry(EnumEntryType.ITEM, "augmentedLump", new ModelResourceLocation("jaopca:augmented_lump#inventory"), BLACKLIST).
			setOreTypes(EnumOreType.ORE);

	@Override
	public String getName() {
		return "poweredthingies";
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
		return Utils.<EnumOreType>enumSetOf(EnumOreType.ORE);
	}

	@Override
	public List<ItemEntryGroup> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(TESLA_LUMP_ENTRY, AUGMENTED_LUMP_ENTRY));
	}

	@Override
	public void preInit() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("teslaLump")) {
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
			if(entry.hasExtra()) {
				outputList.add(new SecondaryOreOutput(0.1F, "dust"+entry.getExtra(), 1));
			}
			if(entry.hasSecondExtra()) {
				outputList.add(new SecondaryOreOutput(0.05F, "dust"+entry.getSecondExtra(), 1));
			}
			IRecipeOutput[] outputs = outputList.toArray(new IRecipeOutput[0]);
			try {
				Method addDefaultMethod = PowderMakerRegistry.class.getDeclaredMethod("addDefaultOreRecipe", String.class, String.class, Integer.TYPE, IRecipeOutput[].class);
				addDefaultMethod.setAccessible(true);
				addDefaultMethod.invoke(PowderMakerRegistry.INSTANCE, StringUtils.uncapitalize(entry.getOreName()), "dust"+entry.getOreName(), amount, outputs);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
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
			ArrayList<IRecipeOutput> outputList = Lists.<IRecipeOutput>newArrayList();
			outputList.add(new OreOutput("dust"+entry.getOreName(), amount));
			outputList.add(new SecondaryOutput(0.15F, Blocks.COBBLESTONE));
			if(entry.hasExtra()) {
				outputList.add(new SecondaryOreOutput(0.1F, "dust"+entry.getExtra(), 1));
			}
			if(entry.hasSecondExtra()) {
				outputList.add(new SecondaryOreOutput(0.05F, "dust"+entry.getSecondExtra(), 1));
			}
			for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
				PowderMakerRegistry.INSTANCE.addRecipe(new PowderMakerRecipe(new ResourceLocation("jaopca", ore.getItem().getRegistryName().toString().replace(':', '_')),
						Lists.newArrayList(ore), outputList), true);
			}
		}

		ItemStack tO = new ItemStack(TeslifiedObsidian.INSTANCE, 1);
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("augmentedLump")) {
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
			ItemStack tLump = Utils.getOreStack("teslaLump", entry, 1);
			ItemStack aLump = Utils.getOreStack("augmentedLump", entry, 1);
			PoweredThingiesAPI.compoundMakerRegistry.registerRecipe(aLump, null, new ItemStack[] {tLump}, null, new ItemStack[] {tO.copy()});
			ArrayList<IRecipeOutput> outputList = Lists.<IRecipeOutput>newArrayList(
					new OreOutput("dust"+entry.getOreName(), amount)
					);
			for(int i = 0; i < amount/2; ++i) {
				outputList.add(new SecondaryOreOutput(0.24F, "dust"+entry.getOreName(), 1));
			}
			PowderMakerRegistry.INSTANCE.addRecipe(new PowderMakerRecipe(aLump.getItem().getRegistryName(),
					Lists.newArrayList(aLump), outputList), true);
		}
	}
}
