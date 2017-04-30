package thelm.jaopca.utils;

import net.minecraft.item.ItemStack;

public class JAOPCAUtils {
	
	public static ItemStack size(ItemStack stack, int size) {
		if(stack != null) {
			ItemStack ret = stack.copy();
			ret.stackSize = size;
			return ret;
		}
		return null;
	}
}
