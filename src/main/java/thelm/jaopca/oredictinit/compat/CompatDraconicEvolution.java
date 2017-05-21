package thelm.jaopca.oredictinit.compat;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.ICompat;

import static thelm.jaopca.oredictinit.registry.OreDictRegisCore.getBlock;
import static thelm.jaopca.oredictinit.registry.OreDictRegisCore.getItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CompatDraconicEvolution implements ICompat {

	@Override
	public void register() {
		try {
			Class<?> oreDictClass = Class.forName("com.brandon3055.draconicevolution.OreHandler");
			Method initMethod = oreDictClass.getDeclaredMethod("initialize");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "draconicevolution";
	}
}
