package thelm.jaopca.compat.astralsorcery;

import java.util.function.Supplier;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.compat.astralsorcery.recipes.InfusionRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class AstralSorceryHelper {

	public static final AstralSorceryHelper INSTANCE = new AstralSorceryHelper();

	private AstralSorceryHelper() {}

	public ItemHandle getItemHandle(Object obj) {
		if(obj instanceof Supplier<?>) {
			return getItemHandle(((Supplier<?>)obj).get());
		}
		if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return new ItemHandle((String)obj);
			}
		}
		if(obj instanceof ItemStack) {
			return new ItemHandle((ItemStack)obj);
		}
		if(obj instanceof Item) {
			return new ItemHandle(new ItemStack((Item)obj));
		}
		if(obj instanceof FluidStack) {
			return new ItemHandle((FluidStack)obj);
		}
		if(obj instanceof Fluid) {
			return new ItemHandle((Fluid)obj);
		}
		if(obj instanceof Ingredient) {
			return ItemHandle.of((Ingredient)obj);
		}
		if(obj instanceof ItemHandle) {
			return (ItemHandle)obj;
		}
		return null;
	}

	public boolean registerInfusionRecipe(ResourceLocation key, Object input, Object output, int outputCount, float consumptionChance, boolean consumeMultiple, boolean acceptsChalices) {
		return ApiImpl.INSTANCE.registerRecipe(key, new InfusionRecipeAction(key, input, output, outputCount, consumptionChance, consumeMultiple, acceptsChalices));
	}
}
