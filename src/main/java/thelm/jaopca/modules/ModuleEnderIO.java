package thelm.jaopca.modules;

import java.util.ArrayList;
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

	public static final String XML_MESSAGE_INGOT_DUST_ORE = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sOre\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ore%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"dust%s\" number=\"2\" />" +
			"%s" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";
	public static final String XML_MESSAGE_INGOT_DUST_ORE_EXTRA = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sOre\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ore%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"dust%s\" number=\"2\" />" +
			"<itemStack oreDictionary=\"dust%s\" chance=\"0.1\" />" +
			"%s" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";
	public static final String XML_MESSAGE_GEM_ORE = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sOre\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ore%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"gem%s\" number=\"2\" />" +
			"<itemStack oreDictionary=\"gem%s\" chance=\"0.5\" />" +
			"%s" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";
	public static final String XML_MESSAGE_GEM_ORE_EXTRA = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sOre\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ore%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"gem%s\" number=\"2\" />" +
			"<itemStack oreDictionary=\"gem%s\" chance=\"0.5\" />" +
			"<itemStack oreDictionary=\"dust%s\" chance=\"0.1\" />" +
			"%s" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";
	public static final String XML_MESSAGE_INGOT_DUST = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sIngot\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ingot%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"dust%s\" />" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";
	public static final String XML_MESSAGE_GEM_DUST = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sGem\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"gem%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"dust%s\" />" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";
	public static final String XML_MESSAGE_BLOCK_DUST = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sBlock\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"block%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"dust%s\" number=\"9\" />" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";

	public static final HashMap<IOreEntry,String> ORE_SECONDARIES = Maps.<IOreEntry,String>newHashMap();

	public static final ArrayList<String> ORE_BLACKLIST = Lists.<String>newArrayList(
			"Coal", "Iron", "Gold", "Redstone", "Diamond", "Emerald", "Lapis", "Quartz", "Copper", "Tin", "Lead", "Silver", "Nickel", "Aluminium", "Apatite",
			"ChargedCertusQuartz", "CertusQuartz", "Sulfur", "Saltpeter", "Ruby", "Peridot", "Topaz", "Tanzanite", "Malachite", "Sapphire", "Amber", "Amethyst",
			"Manganese", "Zinc", "Platinum", "Ignatius", "ShadowIron", "Lemurite", "Midasium", "Vyroxeres", "Ceruclase", "Kalendrite", "Vulcanite", "Sanguinite",
			"Prometheum", "DeepIron", "Infudicolium", "Oureclase", "AstrlSilver", "Carmot", "Mithril", "Rubracium", "Orichalcum", "Adamantine", "Atlarus",
			"Yellorium", "Ardite", "Cobalt", "Draconium", "QuartzBlack"
			);
	public static final ArrayList<String> DUST_BLACKLIST = Lists.<String>newArrayList(
			"Coal", "Iron", "Gold", "Diamond", "Emerald", "Lapis", "Quartz", "Copper", "Tin", "Lead", "Silver", "Nickel", "Aluminium", "ChargedCertusQuartz",
			"CertusQuartz", "Manganese", "Zinc", "Platinum", "Ignatius", "ShadowIron", "Lemurite", "Midasium", "Vyroxeres", "Ceruclase", "Kalendrite", "Vulcanite",
			"Sanguinite", "Prometheum", "DeepIron", "Infudicolium", "Oureclase", "AstrlSilver", "Carmot", "Mithril", "Rubracium", "Orichalcum", "Adamantine",
			"Atlarus", "Yellorium", "Ardite", "Cobalt", "Bronze", "Enderium", "Signalum", "Lumium", "Invar", "Hepatizon", "DamascusSteel", "Angmallen",
			"Brass", "Electrum", "ShadowSteel", "Inolashite", "Amordrine", "BlackSteel", "Quicksilver", "Haderoth", "Celenegil", "Tartarite", "Cyanite",
			"Blutonium", "Graphite", "Ludicrite", "Manyullyn", "AluminiumBrass"
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
				String value = config.get(Utils.to_under_score(entry.getOreName()), "enderIOSecondaryXml", "modID=\"minecraft\" itemName=\"cobblestone\" number=\"1\" chance=\"0.15\"", "The byproduct XML string for this ore in the SAG Mill. Refer to Ender IO. (Ender IO)").setRequiresMcRestart(true).getString();
				ORE_SECONDARIES.put(entry, value);
			}
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(ArrayUtils.contains(EnumOreType.ORE, entry.getOreType()) && !ORE_BLACKLIST.contains(entry.getOreName())) {
				addSAGMillOreProcessingRecipes(entry, Utils.energyI(entry, 3600), ORE_SECONDARIES.get(entry));
			}
			if(ArrayUtils.contains(EnumOreType.DUSTLESS, entry.getOreType()) && !DUST_BLACKLIST.contains(entry.getOreName())) {
				addSAGMillMaterialCrushingRecipes(entry, Utils.energyI(entry, 2400), Utils.energyI(entry, 3600));
			}
		}
	}

	public static void addSAGMillOreProcessingRecipes(IOreEntry entry, int energy, String secondary) {
		secondary = StringUtils.isEmpty(secondary) ? "" : "<itemStack " + secondary + "/>";
		String ore = entry.getOreName();
		String extra = entry.getExtra();
		switch(entry.getOreType()) {
		case DUST:
		case INGOT:
			if(entry.hasExtra()) {
				FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_INGOT_DUST_ORE_EXTRA, ore, energy, ore, ore, extra, secondary));
			}
			else {
				FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_INGOT_DUST_ORE, ore, energy, ore, ore, secondary));
			}
			break;
		case GEM:
			if(entry.hasExtra()) {
				FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_GEM_ORE_EXTRA, ore, energy, ore, ore, ore, extra, secondary));
			}
			else {
				FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_GEM_ORE, ore, energy, ore, ore, ore, secondary));
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
			FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_INGOT_DUST, ore, energy, ore, ore));
			if(Utils.doesOreNameExist("block"+entry.getOreName())) {
				FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_BLOCK_DUST, ore, energy, ore, ore));
			}
			break;
		case GEM:
		case GEM_ORELESS:
			FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_GEM_DUST, ore, energy, ore, ore));
			if(Utils.doesOreNameExist("block"+entry.getOreName())) {
				FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_BLOCK_DUST, ore, energy, ore, ore));
			}
			break;
		default:
			break;
		}
	}
}
