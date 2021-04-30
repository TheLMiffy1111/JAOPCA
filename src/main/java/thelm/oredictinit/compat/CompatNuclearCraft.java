package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
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
	}
}
