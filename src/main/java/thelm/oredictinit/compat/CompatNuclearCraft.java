package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;
import thelm.oredictinit.api.OreDictInitApi;

public class CompatNuclearCraft implements ICompat {

	@Override
	public String getName() {
		return "nuclearcraft";
	}

	@Override
	public void register() {
		try {
			Class<?> oreDictClass = Class.forName("nc.handler.OreDictHandler");
			Method initMethod = oreDictClass.getDeclaredMethod("registerOres");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		OreDictInitApi.addToJAOPCABlacklist("Americium241Base");
		OreDictInitApi.addToJAOPCABlacklist("Americium243Base");
		OreDictInitApi.addToJAOPCABlacklist("Berkelium247Base");
		OreDictInitApi.addToJAOPCABlacklist("Californium250Base");
		OreDictInitApi.addToJAOPCABlacklist("Californium252Base");
		OreDictInitApi.addToJAOPCABlacklist("Curium246Base");
		OreDictInitApi.addToJAOPCABlacklist("Neptunium237Base");
		OreDictInitApi.addToJAOPCABlacklist("Plutonium238Base");
		OreDictInitApi.addToJAOPCABlacklist("Plutonium242Base");
		OreDictInitApi.addToJAOPCABlacklist("Thorium230Base");
		OreDictInitApi.addToJAOPCABlacklist("Uranium238Base");

		if(getItem("nuclearcraft", "dust_tiny_lead") != null) {
			Item item = getItem("nuclearcraft", "dust_tiny_lead");
			OreDictionary.registerOre("dustTinyLead", item);
		}

		if(getItem("nuclearcraft", "thorium") != null) {
			Item item = getItem("nuclearcraft", "thorium");
			OreDictionary.registerOre("nuggetThorium230", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetThorium230Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetThorium232", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetThorium232Oxide", new ItemStack(item, 1, 7));
		}
		if(getItem("nuclearcraft", "uranium") != null) {
			Item item = getItem("nuclearcraft", "uranium");
			OreDictionary.registerOre("nuggetUranium233", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetUranium233Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetUranium235", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetUranium235Oxide", new ItemStack(item, 1, 7));
			OreDictionary.registerOre("nuggetUranium238", new ItemStack(item, 1, 10));
			OreDictionary.registerOre("nuggetUranium238Oxide", new ItemStack(item, 1, 11));
		}
		if(getItem("nuclearcraft", "neptunium") != null) {
			Item item = getItem("nuclearcraft", "neptunium");
			OreDictionary.registerOre("nuggetNeptunium236", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetNeptunium236Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetNeptunium237", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetNeptunium237Oxide", new ItemStack(item, 1, 7));
		}
		if(getItem("nuclearcraft", "plutonium") != null) {
			Item item = getItem("nuclearcraft", "plutonium");
			OreDictionary.registerOre("nuggetPlutonium238", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetPlutonium238Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetPlutonium239", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetPlutonium239Oxide", new ItemStack(item, 1, 7));
			OreDictionary.registerOre("nuggetPlutonium241", new ItemStack(item, 1, 10));
			OreDictionary.registerOre("nuggetPlutonium241Oxide", new ItemStack(item, 1, 11));
			OreDictionary.registerOre("nuggetPlutonium242", new ItemStack(item, 1, 14));
			OreDictionary.registerOre("nuggetPlutonium242Oxide", new ItemStack(item, 1, 15));
		}
		if(getItem("nuclearcraft", "americium") != null) {
			Item item = getItem("nuclearcraft", "americium");
			OreDictionary.registerOre("nuggetAmericium241", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetAmericium241Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetAmericium242", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetAmericium242Oxide", new ItemStack(item, 1, 7));
			OreDictionary.registerOre("nuggetAmericium243", new ItemStack(item, 1, 10));
			OreDictionary.registerOre("nuggetAmericium243Oxide", new ItemStack(item, 1, 11));
		}
		if(getItem("nuclearcraft", "curium") != null) {
			Item item = getItem("nuclearcraft", "curium");
			OreDictionary.registerOre("nuggetCurium243", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetCurium243Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetCurium245", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetCurium245Oxide", new ItemStack(item, 1, 7));
			OreDictionary.registerOre("nuggetCurium246", new ItemStack(item, 1, 10));
			OreDictionary.registerOre("nuggetCurium246Oxide", new ItemStack(item, 1, 11));
			OreDictionary.registerOre("nuggetCurium247", new ItemStack(item, 1, 14));
			OreDictionary.registerOre("nuggetCurium247Oxide", new ItemStack(item, 1, 15));
		}
		if(getItem("nuclearcraft", "berkelium") != null) {
			Item item = getItem("nuclearcraft", "berkelium");
			OreDictionary.registerOre("nuggetBerkelium247", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetBerkelium247Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetBerkelium248", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetBerkelium248Oxide", new ItemStack(item, 1, 7));
		}
		if(getItem("nuclearcraft", "californium") != null) {
			Item item = getItem("nuclearcraft", "californium");
			OreDictionary.registerOre("nuggetCalifornium249", new ItemStack(item, 1, 2));
			OreDictionary.registerOre("nuggetCalifornium249Oxide", new ItemStack(item, 1, 3));
			OreDictionary.registerOre("nuggetCalifornium250", new ItemStack(item, 1, 6));
			OreDictionary.registerOre("nuggetCalifornium250Oxide", new ItemStack(item, 1, 7));
			OreDictionary.registerOre("nuggetCalifornium251", new ItemStack(item, 1, 10));
			OreDictionary.registerOre("nuggetCalifornium251Oxide", new ItemStack(item, 1, 11));
			OreDictionary.registerOre("nuggetCalifornium252", new ItemStack(item, 1, 14));
			OreDictionary.registerOre("nuggetCalifornium252Oxide", new ItemStack(item, 1, 15));
		}
		if(getItem("nuclearcraft", "boron") != null) {
			Item item = getItem("nuclearcraft", "boron");
			OreDictionary.registerOre("nuggetBoron10", new ItemStack(item, 1, 1));
			OreDictionary.registerOre("nuggetBoron11", new ItemStack(item, 1, 3));
		}
		if(getItem("nuclearcraft", "lithium") != null) {
			Item item = getItem("nuclearcraft", "lithium");
			OreDictionary.registerOre("nuggetLithium6", new ItemStack(item, 1, 1));
			OreDictionary.registerOre("nuggetLithium7", new ItemStack(item, 1, 3));
		}
	}
}
