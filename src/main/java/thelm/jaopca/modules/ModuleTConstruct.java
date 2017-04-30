package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleTConstruct extends ModuleAbstract {

	public static final ItemEntry MOLTEN_ENTRY = new ItemEntry(EnumEntryType.FLUID, "molten", null, ImmutableList.<String>of(
			"Iron", "Cobalt", "Ardite", "Gold", "Copper", "Tin", "Lead", "Nickel", "Silver", "Aluminum", "Zinc"
			));

	@Override
	public String getName() {
		return "tconstruct";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(MOLTEN_ENTRY);
	}

	@Override
	public void setCustomProperties() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("molten")) {
			Fluid fluid = JAOPCAApi.FLUIDS_TABLE.get("molten", entry.getOreName());
			fluid.setDensity(2000);
			fluid.setViscosity(10000);
			fluid.setTemperature(Math.min(1000, (int)(500*entry.getEnergyModifier())));
			fluid.setLuminosity(10);
			fluid.setRarity(EnumRarity.UNCOMMON);
		}
	}

	@Override
	public void registerRecipes() {
		//Use TConstruct's internal method because it adds everything for us
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("molten")) {
			TinkerSmeltery.registerOredictMeltingCasting(JAOPCAApi.FLUIDS_TABLE.get("molten", entry.getOreName()), entry.getOreName());
		}
	}
}
