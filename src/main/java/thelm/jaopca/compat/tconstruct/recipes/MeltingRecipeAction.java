package thelm.jaopca.compat.tconstruct.recipes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object render;
	public final Object output;
	public final int outputAmount;
	public final int temperature;

	public MeltingRecipeAction(String key, Object input, Object render, Object output, int outputAmount, int temperature) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.render = render;
		this.output = output;
		this.outputAmount = outputAmount;
		this.temperature = temperature;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, false);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Block defaultBlock = TinkerSmeltery.glueBlock;
		int defaultMeta = 0;
		ItemStack ren = MiscHelper.INSTANCE.getPreferredItemStack(
				MiscHelper.INSTANCE.getItemStacks(render, 1, false).stream().
				filter(s->s.getItem() instanceof ItemBlock).collect(Collectors.toList()), 1);
		if(ren != null) {
			defaultBlock = ((ItemBlock)ren.getItem()).field_150939_a;
			defaultMeta = ren.getItemDamage();
		}
		else {
			LOGGER.debug("No valid specified render blocks in recipe {}, defaulting to glue", new Object[] {key});
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(ItemStack in : ing) {
			Block block = defaultBlock;
			int meta = defaultMeta;
			if(in.getItem() instanceof ItemBlock) {
				block = ((ItemBlock)in.getItem()).field_150939_a;
				meta = in.getItemDamage();
			}
			Smeltery.addMelting(in, block, meta, temperature, stack);
		}
		return true;
	}
}
