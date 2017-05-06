package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;
import thelm.jaopca.api.fluid.FluidBase;

public class ModuleMolten extends ModuleAbstract {

	public static final ItemEntry MOLTEN_ENTRY = new ItemEntry(EnumEntryType.FLUID, "molten", null);

	public static final ArrayList<String> MOLTEN_BLACKLIST_TCON = Lists.<String>newArrayList(
			"Iron", "Cobalt", "Ardite", "Gold", "Copper", "Tin", "Lead", "Nickel", "Silver", "Aluminum", "Zinc"
			);

	@Override
	public String getName() {
		return "molten";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		if(Loader.isModLoaded("tconstruct"))
			MOLTEN_ENTRY.blacklist.addAll(MOLTEN_BLACKLIST_TCON);
		return Lists.<ItemEntry>newArrayList(MOLTEN_ENTRY);
	}

	@Override
	public void setCustomProperties() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("molten")) {
			FluidBase fluid = JAOPCAApi.FLUIDS_TABLE.get("molten", entry.getOreName());
			fluid.setDensity(2000);
			fluid.setViscosity(10000);
			fluid.setTemperature(Math.min(1000, (int)(500*Math.sqrt(entry.getEnergyModifier()))));
			fluid.setLuminosity(10);
			fluid.setRarity(EnumRarity.UNCOMMON);
		}
	}

	@Override
	public void registerRecipes() {}
}
