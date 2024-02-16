package thelm.jaopca.compat.steamcraft.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import flaxbeard.steamcraft.tile.TileEntitySmasher;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class SmasherRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object output;
	public final int count;

	public SmasherRecipeAction(String key, Object input, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
	}

	@Override
	public boolean register() {
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		if(input instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(input)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			TileEntitySmasher.REGISTRY.registerSmashable((String)input, stack);
		}
		else {
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, false);
			if(ing.isEmpty()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			for(ItemStack in : ing) {
				TileEntitySmasher.REGISTRY.registerSmashable(in, stack);
			}
		}
		return true;
	}
}
