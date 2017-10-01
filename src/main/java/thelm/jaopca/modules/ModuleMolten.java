package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.fluid.FluidProperties;

public class ModuleMolten extends ModuleBase {

	public static final FluidProperties MOLTEN_PROPERTIES = new FluidProperties().
			setDensityFunc(entry->2000).
			setViscosityFunc(entry->10000).
			setTemperatureFunc(entry->Math.min(1000, (int)(500*Math.sqrt(entry.getEnergyModifier())))).
			setLuminosityFunc(entry->10).
			setRarity(EnumRarity.UNCOMMON).
			setFillSound(SoundEvents.ITEM_BUCKET_FILL_LAVA).
			setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY_LAVA).
			setMaterial(Material.LAVA);

	public static final ItemEntry MOLTEN_ENTRY = new ItemEntry(EnumEntryType.FLUID, "molten", new ModelResourceLocation("jaopca:fluids/molten#normal")).
			setProperties(MOLTEN_PROPERTIES).
			setOreTypes(EnumOreType.INGOTS);

	public static final ArrayList<String> MOLTEN_BLACKLIST_TCON = Lists.<String>newArrayList(
			"Iron", "Cobalt", "Ardite", "Gold", "Copper", "Tin", "Lead", "Nickel", "Silver", "Aluminium", "Zinc", "BrickSeared"
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
