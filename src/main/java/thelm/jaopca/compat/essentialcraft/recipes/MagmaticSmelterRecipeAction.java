package thelm.jaopca.compat.essentialcraft.recipes;

import java.util.Objects;
import java.util.function.Consumer;

import essentialcraft.api.OreSmeltingRecipe;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;

public class MagmaticSmelterRecipeAction implements IRecipeAction {

	public final ResourceLocation key;
	public final String input;
	public final String output;
	public final int count;
	public final Consumer<OreSmeltingRecipe> callback;

	public MagmaticSmelterRecipeAction(ResourceLocation key, String input, String output, int count, Consumer<OreSmeltingRecipe> callback) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
		this.callback = callback;
	}

	@Override
	public boolean register() {
		callback.accept(OreSmeltingRecipe.addRecipe(input, output, 0, count));
		return true;
	}
}
