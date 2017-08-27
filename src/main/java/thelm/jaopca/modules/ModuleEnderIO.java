package thelm.jaopca.modules;

import java.util.HashMap;
import java.util.List;

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

	public static final String XML_MESSAGE = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" + 
			"<recipe name=\"%sOre\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ore%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"%s\" number=\"2\" />" +
			"<itemStack oreDictionary=\"%s\" number=\"1\" chance=\"0.1\" />" +
			"<itemStack %s />" +
			"</output>" +
			"</recipe>" + 
			"</recipeGroup>";

	public static final String XML_MESSAGE_1 = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" +
			"<recipe name=\"%sOre\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ore%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"%s\" number=\"2\" />" +
			"<itemStack %s />" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";

	public static final HashMap<String,String> ORE_SECONDARIES = Maps.<String,String>newHashMap();

	@Override
	public String getName() {
		return "enderio";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Iron", "Gold", "Copper", "Lead", "Silver", "Nickel"
				);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			String value = config.get(Utils.to_under_score(entry.getOreName()), "enderIOSecondaryXml", "modID=\"minecraft\" itemName=\"cobblestone\" number=\"1\" chance=\"0.15\"").setRequiresMcRestart(true).getString();
			ORE_SECONDARIES.put(entry.getOreName(), value);
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			addSAGMillRecipes(entry, Utils.energyI(entry, 3600), ORE_SECONDARIES.get(entry.getOreName()));
		}
	}

	public static void addSAGMillRecipes(IOreEntry entry, int energy, String secondary) {
		String input = entry.getOreName();
		String extra = entry.getExtra();
		String s1 = entry.getOreType()==EnumOreType.GEM?"gem":"dust";
		if(!extra.equals(input)) {
			String s2 = Utils.oreNameToType(extra)==EnumOreType.GEM?"gem":"dust";
			FMLInterModComms.sendMessage("enderio", "recipe:sagmill", String.format(XML_MESSAGE, input, energy, input, s1+input, s2+extra, secondary));
		}
		else {
			FMLInterModComms.sendMessage("enderio", "recipe:sagmill", String.format(XML_MESSAGE_1, input, energy, input, s1+input, secondary));
		}
	}
}
