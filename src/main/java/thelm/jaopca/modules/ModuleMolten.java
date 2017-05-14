package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.fluid.FluidProperties;

public class ModuleMolten implements IModule {

	public static final FluidProperties MOLTEN_PROPERTIES = new FluidProperties().
			setDensityFunc((entry)->{return 2000;}).
			setViscosityFunc((entry)->{return 10000;}).
			setTemperatureFunc((entry)->{return Math.min(1000, (int)(500*Math.sqrt(entry.getEnergyModifier())));}).
			setLuminosityFunc((entry)->{return 10;}).
			setRarity(EnumRarity.UNCOMMON);

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
	public void registerRecipes() {}
}
