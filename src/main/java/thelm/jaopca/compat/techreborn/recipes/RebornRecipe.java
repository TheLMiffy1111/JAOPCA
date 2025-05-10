package thelm.jaopca.compat.techreborn.recipes;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public record RebornRecipe(List<SizedIngredient> ingredients, List<ItemStack> outputs, int power, int time) {

	public static final Codec<RebornRecipe> CODEC = RecordCodecBuilder.create(instance->instance.group(
			SizedIngredient.FLAT_CODEC.listOf().fieldOf("ingredients").forGetter(RebornRecipe::ingredients),
			ItemStack.CODEC.listOf().fieldOf("outputs").forGetter(RebornRecipe::outputs),
			Codec.INT.fieldOf("power").forGetter(RebornRecipe::power),
			Codec.INT.fieldOf("time").forGetter(RebornRecipe::time)).
			apply(instance, RebornRecipe::new));
}
