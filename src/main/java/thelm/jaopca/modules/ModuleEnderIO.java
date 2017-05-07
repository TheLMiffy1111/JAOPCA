package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleEnderIO extends ModuleAbstract {

	public static final ArrayList<String> ORE_BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Lead", "Silver", "Nickel"
			);
	
	public static final String XML_MESSAGE =
			"<recipeGroup name=\"JAOPCA\">" + 
				"<recipe name=\"%sOre\" energyCost=\"%d\">" +
					"<input>" +
						"<itemStack oreDictionary=\"ore%s\" />" +
					"</input>" +
					"<output>" +
						"<itemStack oreDictionary=\"dust%s\" number=\"2\" />" +
						"<itemStack oreDictionary=\"dust%s\" number=\"1\" chance=\"0.1\" />" +
						"<itemStack modID=\"minecraft\" itemName=\"cobblestone\" chance=\"0.15\"/>" +
					"</output>" +
				"</recipe>" + 
			"</recipeGroup>";
	
	public static final String XML_MESSAGE_1 =
			"<recipeGroup name=\"JAOPCA\">" +
				"<recipe name=\"%sOre\" energyCost=\"%d\">" +
					"<input>" +
						"<itemStack oreDictionary=\"ore%s\" />" +
					"</input>" +
					"<output>" +
						"<itemStack oreDictionary=\"dust%s\" number=\"2\" />" +
						"<itemStack modID=\"minecraft\" itemName=\"cobblestone\" chance=\"0.15\"/>" +
					"</output>" +
				"</recipe>" +
			"</recipeGroup>";
	
	@Override
	public String getName() {
		return "enderio";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList();
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.ORE_ENTRY_LIST) {
			if(!entry.getModuleBlacklist().contains(getName())) {
				if(!ORE_BLACKLIST.contains(entry.getOreName())) {
					addSAGMillRecipe(entry.getOreName(), energyI(entry, 3600), entry.getExtra());
				}
			}
		}
	}
	
	public static void addSAGMillRecipe(String input, int energy, String extra) {
		if(OreDictionary.getOres("dust" + extra).isEmpty())
			extra = input;

		if(!extra.equals(input)) {
			FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE, input, energy, input, input, extra));
		}
		else {
			FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(XML_MESSAGE_1, input, energy, input, input));
		}
	}
}
