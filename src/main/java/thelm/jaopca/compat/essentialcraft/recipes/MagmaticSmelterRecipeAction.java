package thelm.jaopca.compat.essentialcraft.recipes;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import ec3.utils.common.EnumOreColoring;
import net.minecraftforge.common.util.EnumHelper;
import thelm.jaopca.api.recipes.IRecipeAction;

public class MagmaticSmelterRecipeAction implements IRecipeAction {

	public final String key;
	public final String input;
	public final String output;
	public final int count;
	public final Consumer<EnumOreColoring> callback;

	public MagmaticSmelterRecipeAction(String key, String input, String output, int count, Consumer<EnumOreColoring> callback) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
		this.callback = Objects.requireNonNull(callback);
	}

	@Override
	public boolean register() {
		callback.accept(EnumHelper.addEnum(EnumOreColoring.class, input.toUpperCase(Locale.US),
				new Class<?>[] {String.class, String.class, int.class, int.class},
				new Object[] {input, output, 0, count}));
		return true;
	}
}
