package thelm.jaopca.compat.skyresources.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bartz24.skyresources.recipe.ProcessRecipeManager;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class RockGrinderRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int count;
	public final float chance;

	public RockGrinderRecipeAction(ResourceLocation key, Object input, Object output, int count, float chance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
		this.chance = chance;
	}

	@Override
	public boolean register() {
		List<Object> ins = new ArrayList<>();
		if(input instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(input)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			if(OreDictionary.getOres((String)input, false).stream().
					noneMatch(in->ForgeRegistries.BLOCKS.containsKey(in.getItem().getRegistryName()))) {
				throw new IllegalArgumentException("Non-block ingredient in recipe "+key+": "+input);
			}
			ins.add(input);
		}
		else {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			for(ItemStack in : ing.getMatchingStacks()) {
				if(ForgeRegistries.BLOCKS.containsKey(in.getItem().getRegistryName())) {
					ins.add(in.copy());
				}
			}
			if(ins.isEmpty()) {
				throw new IllegalArgumentException("Non-block ingredient in recipe "+key+": "+input);
			}
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(Object in : ins) {
			ProcessRecipeManager.rockGrinderRecipes.addRecipe(stack, chance, in);
		}
		return true;
	}
}
