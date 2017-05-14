package thelm.jaopca.modules;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleEnderIO implements IModule {

	public static final String XML_MESSAGE = "" +
			"<recipeGroup name=\"JAOPCA_EIO\">" + 
			"<recipe name=\"%sOre\" energyCost=\"%d\">" +
			"<input>" +
			"<itemStack oreDictionary=\"ore%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"dust%s\" number=\"2\" />" +
			"<itemStack oreDictionary=\"dust%s\" number=\"1\" chance=\"0.1\" />" +
			"<itemStack modID=\"%s\" itemName=\"%s\" chance=\"0.15\"/>" +
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
			"<itemStack oreDictionary=\"dust%s\" number=\"2\" />" +
			"<itemStack modID=\"%s\" itemName=\"%s\" chance=\"0.15\"/>" +
			"</output>" +
			"</recipe>" +
			"</recipeGroup>";

	public static final HashMap<String,Pair<String,String>> ORE_SECONDARIES = Maps.<String,Pair<String,String>>newHashMap();

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
			String[] value = config.get(entry.getOreName(), "enderIOSecondary", "minecraft:cobblestone").setRequiresMcRestart(true).getString().split(":");
			ORE_SECONDARIES.put(entry.getOreName(), Pair.<String,String>of(value[0], value[1]));
		}
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			addSAGMillRecipe(entry.getOreName(), Utils.energyI(entry, 3600), entry.getExtra(), ORE_SECONDARIES.get(entry.getOreName()));
		}
	}

	public static void addSAGMillRecipe(String input, int energy, String extra, Pair<String, String> secondary) {
		if(!Utils.doesOreNameExist("dust" + extra))
			extra = input;

		if(!extra.equals(input)) {
			FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE, input, energy, input, input, extra, secondary.getLeft(), secondary.getRight()));
		}
		else {
			FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_1, input, energy, input, input, secondary.getLeft(), secondary.getRight()));
		}
	}
}
