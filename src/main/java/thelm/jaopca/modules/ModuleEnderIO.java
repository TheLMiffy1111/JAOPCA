package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleEnderIO extends ModuleBase {

	public static final HashMap<IOreEntry,String> ORE_BYPRODUCTS = Maps.<IOreEntry,String>newHashMap();

	public static final ArrayList<String> ORE_BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Tin", "Lead", "Silver", "Nickel", "Aluminium", "Coal", "Redstone", "Diamond", "Emerald", "Lapis", "Quartz",
			"Apatite", "CertusQuartz", "ChargedCertusQuartz", "Sulfur", "Saltpeter", "Ruby", "Peridot", "Topaz", "Tanzanite", "Malachite", "Sapphire",
			"Amber", "Amethyst", "Manganese", "Zinc", "Platinum", "Ignatius", "ShadowIron", "Lemurite", "Midasium", "Vyroxeres", "Ceruclase",
			"Kalendrite", "Vulcanite", "Sanguinite", "Prometheum", "DeepIron", "Infudicolium", "Oureclase", "AstrlSilver", "Carmot", "Mithril",
			"Rubracium", "Orichalcum", "Adamantine", "Atlarus", "Osmium", "Yellorium", "Ardite", "Cobalt", "QuartzBlack", "Uranium", "Steel",
			"Titanium", "Magnesium", "Tungsten", "Rutile", "Draconium", "Prosperity", "Inferium", "DimensionalShard", "EnderBiotite", "Iridium",
			"Thorium", "Boron", "Lithium", "Bauxite", "Galena", "Pyrite", "Cinnibar", "Sphalerite", "Sheldonite", "Sodalite", "AstralStarmetal",
			"Dilithium", "Tritanium"
			);
	public static final ArrayList<String> DUST_BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Tin", "Lead", "Silver", "Nickel", "Aluminium", "Coal", "Redstone", "Diamond", "Emerald", "Lapis", "Quartz",
			"CertusQuartz", "ChargedCertusQuartz", "Manganese", "Zinc", "Platinum", "Ignatius", "ShadowIron", "Lemurite", "Midasium", "Vyroxeres",
			"Ceruclase", "Kalendrite", "Vulcanite", "Sanguinite", "Prometheum", "DeepIron", "Infudicolium", "Oureclase", "AstrlSilver", "Carmot",
			"Mithril", "Rubracium", "Orichalcum", "Adamantine", "Atlarus", "Osmium", "Yellorium", "Ardite", "Cobalt", "Uranium", "Steel", "Titanium",
			"Magnesium", "Tungsten", "Rutile", "Draconium", "ElecticalSteel", "EnergeticAlloy", "VibrantAlloy", "RedstoneAlloy", "ConductiveIron",
			"PulsatingIron", "DarkSteel", "Soularium", "Bronze", "Enderium", "Signalum", "Lumium", "Invar", "Hepatizon", "DamascusSteel", "Angmallen",
			"Brass", "Electrum", "ShadowSteel", "Inolashite", "Amordrine", "BlackSteel", "Quicksilver", "Haderoth", "Celenegil", "Tartarite",
			"Cyanite", "Blutonium", "Graphite", "Ludicrite", "Manyullyn", "AluminiumBrass"
			);

	@Override
	public String getName() {
		return "enderio";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return EnumSet.<EnumOreType>allOf(EnumOreType.class);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(ArrayUtils.contains(EnumOreType.ORE, entry.getOreType()) && !ORE_BLACKLIST.contains(entry.getOreName())) {
				String value = config.get(Utils.to_under_score(entry.getOreName()), "enderIOByproductXml", "name=\"oredict:cobblestone\" amount=\"1\" chance=\"0.15\"").setRequiresMcRestart(true).getString();
				ORE_BYPRODUCTS.put(entry, value);
			}
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(ArrayUtils.contains(EnumOreType.ORE, entry.getOreType()) && !ORE_BLACKLIST.contains(entry.getOreName())) {
				addSAGMillOreProcessingRecipes(entry, Utils.energyI(entry, 3600), ORE_BYPRODUCTS.get(entry));
			}
			if(ArrayUtils.contains(EnumOreType.DUSTLESS, entry.getOreType()) && !DUST_BLACKLIST.contains(entry.getOreName())) {
				addSAGMillMaterialCrushingRecipes(entry, Utils.energyI(entry, 2400), Utils.energyI(entry, 3600));
			}
		}
	}

	public static void addSAGMillOreProcessingRecipes(IOreEntry entry, int energy, String byproduct) {
		byproduct = StringUtils.isEmpty(byproduct) ? "" : "<output " + byproduct + " />";
		String ore = entry.getOreName();
		String extra = entry.getExtra();
		switch(entry.getOreType()) {
		case DUST:
		case INGOT:
			if(entry.hasExtra()) {
				FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_INGOT_DUST_ORE_EXTRA, ore, energy, ore, ore, extra, byproduct));
			}
			else {
				FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_INGOT_DUST_ORE, ore, energy, ore, ore, byproduct));
			}
			break;
		case GEM:
			if(entry.hasExtra()) {
				if(Utils.getOreStack("gem", entry, 1).getMaxStackSize() < 2) {
					FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_GEM_ORE_EXTRA_UNSTACKABLE, ore, energy, ore, ore, ore, ore, extra, byproduct));
				}
				else {
					FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_GEM_ORE_EXTRA, ore, energy, ore, ore, ore, extra, byproduct));
				}
			}
			else {
				if(Utils.getOreStack("gem", entry, 1).getMaxStackSize() < 2) {
					FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_GEM_ORE_UNSTACKABLE, ore, energy, ore, ore, ore, ore, byproduct));
				}
				else {
					FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_GEM_ORE, ore, energy, ore, ore, ore, byproduct));
				}
			}
			break;
		default:
			break;
		}
	}

	public static void addSAGMillMaterialCrushingRecipes(IOreEntry entry, int energy, int energyBlock) {
		String ore = entry.getOreName();
		switch(entry.getOreType()) {
		case INGOT:
		case INGOT_ORELESS:
			FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_INGOT_DUST, ore, energy, ore, ore));
			if(Utils.doesOreNameExist("block"+entry.getOreName())) {
				FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_BLOCK_DUST, ore, energy, ore, ore));
			}
			break;
		case GEM:
		case GEM_ORELESS:
			FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_GEM_DUST, ore, energy, ore, ore));
			if(Utils.doesOreNameExist("block"+entry.getOreName())) {
				FMLInterModComms.sendMessage("enderio", "recipe:xml", String.format(XML_MESSAGE_BLOCK_DUST, ore, energy, ore, ore));
			}
			break;
		default:
			break;
		}
	}

	public static final String XML_MESSAGE_INGOT_DUST_ORE = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Ore\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\">" +
			"<input name=\"ore%s\" />" +
			"<output name=\"dust%s\" amount=\"2\" />" +
			"%s" +
			"</sagmilling>" +
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_INGOT_DUST_ORE_EXTRA = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Ore\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\">" +
			"<input name=\"ore%s\" />" +
			"<output name=\"dust%s\" amount=\"2\" />" +
			"<output name=\"dust%s\" chance=\"0.1\" />" +
			"%s" +
			"</sagmilling>" +
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_GEM_ORE = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Ore\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\">" +
			"<input name=\"ore%s\" />" +
			"<output name=\"gem%s\" amount=\"2\" />" + 
			"<output name=\"gem%s\" chance=\"0.5\" />" +
			"%s" +
			"</sagmilling>" + 
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_GEM_ORE_EXTRA = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Ore\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\">" +
			"<input name=\"ore%s\" />" +
			"<output name=\"gem%s\" amount=\"2\" />" + 
			"<output name=\"gem%s\" chance=\"0.5\" />" +
			"<output name=\"dust%s\" chance=\"0.1\" />" +
			"%s" +
			"</sagmilling>" + 
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_GEM_ORE_UNSTACKABLE = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Ore\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\">" +
			"<input name=\"ore%s\" />" +
			"<output name=\"gem%s\" />" + 
			"<output name=\"gem%s\" />" + 
			"<output name=\"gem%s\" chance=\"0.5\" />" +
			"%s" +
			"</sagmilling>" + 
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_GEM_ORE_EXTRA_UNSTACKABLE = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Ore\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\">" +
			"<input name=\"ore%s\" />" +
			"<output name=\"gem%s\" />" + 
			"<output name=\"gem%s\" />" + 
			"<output name=\"gem%s\" chance=\"0.5\" />" +
			"<output name=\"dust%s\" chance=\"0.1\" />" +
			"%s" +
			"</sagmilling>" + 
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_INGOT_DUST = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Dust\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\" bonus=\"none\">" +
			"<input name=\"ingot%s\" />" +
			"<output name=\"dust%s\" />" +
			"</sagmilling>" + 
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_GEM_DUST = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Dust\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\" bonus=\"none\">" +
			"<input name=\"gem%s\" />" +
			"<output name=\"dust%s\" />" +
			"</sagmilling>" + 
			"</recipe>" +
			"</recipes>";
	public static final String XML_MESSAGE_BLOCK_DUST = "" +
			"<recipes>" +
			"<recipe name=\"Sagmill: %s Block Dust\" required=\"true\" disabled=\"false\">" +
			"<sagmilling energy=\"%d\" bonus=\"none\">" +
			"<input name=\"block%s\" />" +
			"<output name=\"dust%s\" amount=\"9\" />" +
			"</sagmilling>" + 
			"</recipe>" +
			"</recipes>";
}
