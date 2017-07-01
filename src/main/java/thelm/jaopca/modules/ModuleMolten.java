package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.fluid.FluidProperties;

public class ModuleMolten extends ModuleBase {

	public static final FluidProperties MOLTEN_PROPERTIES = new FluidProperties().
			setDensityFunc((entry)->{return 2000;}).
			setViscosityFunc((entry)->{return 10000;}).
			setTemperatureFunc((entry)->{return Math.min(1000, (int)(500*Math.sqrt(entry.getEnergyModifier())));}).
			setLuminosityFunc((entry)->{return 10;}).
			setRarity(EnumRarity.UNCOMMON).
			setFillSound(SoundEvents.ITEM_BUCKET_FILL_LAVA).
			setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY_LAVA);

	public static final ItemEntry MOLTEN_ENTRY = new ItemEntry(EnumEntryType.FLUID, "molten", null);

	public static final ArrayList<String> MOLTEN_BLACKLIST_TCON = Lists.<String>newArrayList(
			"Iron", "Cobalt", "Ardite", "Gold", "Copper", "Tin", "Lead", "Nickel", "Silver", "Aluminum", "Zinc"
			);
	public static final ArrayList<String> MOLTEN_BLOCKLIST_EMBERS = Lists.<String>newArrayList(
			"Iron", "Gold", "Silver", "Copper", "Lead"
			);

	@Override
	public String getName() {
		return "molten";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		if(Loader.isModLoaded("tconstruct")) {
			MOLTEN_ENTRY.blacklist.addAll(MOLTEN_BLACKLIST_TCON);
		}
		if(Loader.isModLoaded("embers")) {
			MOLTEN_ENTRY.blacklist.addAll(MOLTEN_BLOCKLIST_EMBERS);
		}
		return Lists.<ItemEntry>newArrayList(MOLTEN_ENTRY);
	}

	@Override
	public void init() {}
}
