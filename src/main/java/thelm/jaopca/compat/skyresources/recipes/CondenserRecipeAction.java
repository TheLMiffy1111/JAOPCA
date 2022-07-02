package thelm.jaopca.compat.skyresources.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bartz24.skyresources.recipe.ProcessRecipeManager;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CondenserRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final Object blockInput;
	public final boolean isFluid;
	public final Object output;
	public final int count;
	public final float usage;

	public CondenserRecipeAction(ResourceLocation key, Object itemInput, Object blockInput, boolean isFluid, Object output, int count, float usage) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.blockInput = blockInput;
		this.isFluid = isFluid;
		this.output = output;
		this.count = count;
		this.usage = usage;
	}

	@Override
	public boolean register() {
		List<Object> itemIn = new ArrayList<>();
		if(itemInput instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(itemInput)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
			}
			itemIn.add(itemInput);
		}
		else {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
			}
			for(ItemStack in : ing.getMatchingStacks()) {
				itemIn.add(in.copy());
			}
		}
		List<Object> blockIn = new ArrayList<>();
		if(isFluid) {
			FluidStack ing = MiscHelper.INSTANCE.getFluidStack(blockInput, 1000);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+blockInput);
			}
			if(!ing.getFluid().canBePlacedInWorld()) {
				throw new IllegalArgumentException("Non-block ingredient in recipe "+key+": "+blockInput);
			}
			blockIn.add(ing);
		}
		else if(blockInput instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(blockInput)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+blockInput);
			}
			if(OreDictionary.getOres((String)blockInput, false).stream().
					noneMatch(in->ForgeRegistries.BLOCKS.containsKey(in.getItem().getRegistryName()))) {
				throw new IllegalArgumentException("Non-block ingredient in recipe "+key+": "+blockInput);
			}
			blockIn.add(blockInput);
		}
		else {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(blockInput);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+blockInput);
			}
			for(ItemStack in : ing.getMatchingStacks()) {
				if(ForgeRegistries.BLOCKS.containsKey(in.getItem().getRegistryName())) {
					blockIn.add(in.copy());
				}
			}
			if(blockIn.isEmpty()) {
				throw new IllegalArgumentException("Non-block ingredient in recipe "+key+": "+blockInput);
			}
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(List<Object> in : Lists.cartesianProduct(Arrays.asList(itemIn, blockIn))) {
			ProcessRecipeManager.condenserRecipes.addRecipe(stack, usage, in);
		}
		return true;
	}
}
