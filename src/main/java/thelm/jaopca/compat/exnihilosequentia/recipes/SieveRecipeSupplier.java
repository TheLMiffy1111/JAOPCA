package thelm.jaopca.compat.exnihilosequentia.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import novamachina.exnihilosequentia.api.crafting.sieve.MeshWithChance;
import novamachina.exnihilosequentia.api.crafting.sieve.SieveRecipe;
import novamachina.exnihilosequentia.common.item.mesh.EnumMesh;
import thelm.jaopca.utils.MiscHelper;

public class SieveRecipeSupplier implements Supplier<SieveRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Number[] meshChances;
	public final boolean waterlogged;

	public SieveRecipeSupplier(ResourceLocation key, Object input, Object output, int outputCount, Number[] meshChances, boolean waterlogged) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.meshChances = meshChances;
		this.waterlogged = waterlogged;
	}

	@Override
	public SieveRecipe get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.hasNoMatchingItems()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		List<MeshWithChance> chances = new ArrayList<>();
		int i = 0;
		while(i+1 < meshChances.length) {
			int id = meshChances[i].intValue();
			float chance = meshChances[i+1].floatValue();
			i += 2;
			chances.add(new MeshWithChance(EnumMesh.values()[id], chance));
		}
		return new SieveRecipe(key, ing, stack, chances, waterlogged);
	}
}
