package thelm.jaopca.compat.thaumcraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class CrucibleRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String researchReq;
	public final Object input;
	public final Object[] aspects;
	public final Object output;
	public final int count;

	public CrucibleRecipeAction(ResourceLocation key, String researchReq, Object input, Object[] aspects, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.researchReq = researchReq;
		this.input = input;
		this.aspects = aspects;
		this.output = output;
		this.count = count;
	}

	@Override
	public boolean register() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		AspectList aspectList = new AspectList();
		int i = 0;
		while(i < aspects.length) {
			String in = aspects[i].toString();
			++i;
			Integer inc = 1;
			if(i < aspects.length && aspects[i] instanceof Integer) {
				inc = (Integer)aspects[i];
				++i;
			}
			Aspect aspect = Aspect.getAspect(in);
			if(aspect == null) {
				throw new IllegalArgumentException("Non-existent aspect in recipe "+key+": "+in);
			}
			aspectList.add(aspect, inc);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ThaumcraftApi.addCrucibleRecipe(key, new CrucibleRecipe(researchReq, stack, ing, aspectList));
		return true;
	}
}
