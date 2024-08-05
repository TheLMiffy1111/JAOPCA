package thelm.jaopca.compat.railcraft.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import mods.railcraft.world.item.crafting.RollingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class RollingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final Object[] input;

	public RollingRecipeSerializer(ResourceLocation key, Object output, int outputCount, int time, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public JsonElement get() {
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		List<String> pattern = new ArrayList<>();
		int idx = 0;
		if(input[idx] instanceof String[]) {
			String[] parts = ((String[])input[idx++]);
			for(String s : parts) {
				if(!pattern.isEmpty() && s.length() != pattern.get(0).length()) {
					throw new IllegalArgumentException("Invalid pattern in recipe "+key+"Pattern must be the same width on every line");
				}
				pattern.add(s);
			}
		}
		else {
			while(input[idx] instanceof String) {
				String s = (String)input[idx++];
				pattern.add(s);
			}
		}
		Map<Character, Ingredient> keyMap = new HashMap<>();
		for(; idx < input.length; idx += 2)  {
			Character chr = (Character)input[idx];
			Object in = input[idx+1];
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(keyMap.containsKey(chr)) {
				throw new IllegalArgumentException("Invalid key entry in recipe "+key+": Symbol '"+chr+"' is defined twice");
			}
			if(' ' == chr.charValue()) {
				throw new IllegalArgumentException("Invalid key entry in recipe "+key+": Symbol ' ' is reserved");
			}
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			keyMap.put(chr, ing);
		}
		if(pattern.isEmpty()) {
			throw new IllegalArgumentException("No pattern is defined for shaped recipe "+key);
		}
		Set<Character> set = new HashSet<>(keyMap.keySet());
		for(String str : pattern) {
			for(int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if(!keyMap.containsKey(Character.valueOf(c)) && c != ' ') {
					throw new IllegalArgumentException("Pattern in recipe "+key+" uses undefined symbol '"+c+"'");
				}
				set.remove(Character.valueOf(c));
			}
		}
		if(!set.isEmpty()) {
			throw new IllegalArgumentException("Ingredients are defined but not used in pattern for recipe "+key);
		}
		RollingRecipe recipe = new RollingRecipe(ShapedRecipePattern.of(keyMap, pattern), stack, time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
