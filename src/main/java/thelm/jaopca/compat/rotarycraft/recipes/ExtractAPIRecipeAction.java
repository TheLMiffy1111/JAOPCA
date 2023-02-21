package thelm.jaopca.compat.rotarycraft.recipes;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.RotaryCraft.API.ExtractAPI;
import Reika.RotaryCraft.Auxiliary.CustomExtractLoader;
import thelm.jaopca.api.recipes.IRecipeAction;

public class ExtractAPIRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final String ore;
	public final String material;
	public final int count;
	public final boolean isRare;
	public final Consumer<CustomExtractLoader.CustomExtractEntry> callback;

	public ExtractAPIRecipeAction(String key, String ore, String material, int count, boolean isRare, Consumer<CustomExtractLoader.CustomExtractEntry> callback) {
		this.key = Objects.requireNonNull(key);
		this.ore = ore;
		this.material = material;
		this.count = count;
		this.isRare = isRare;
		this.callback = Objects.requireNonNull(callback);
	}

	@Override
	public boolean register() {
		ExtractAPI.addCustomExtractEntry(
				"", (isRare ? OreType.OreRarity.RARE : OreType.OreRarity.COMMON), "INGOT",
				material, count, 0, 0, null, ore);
		List<CustomExtractLoader.CustomExtractEntry> entries = CustomExtractLoader.instance.getEntries();
		callback.accept(entries.get(entries.size()-1));
		return true;
	}
}
