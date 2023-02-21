package thelm.jaopca.compat.rotarycraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.mixinextras.injector.ModifyReturnValue;

import Reika.RotaryCraft.Auxiliary.SlotExtractor4;
import net.minecraft.item.ItemStack;
import thelm.jaopca.compat.rotarycraft.recipes.RotaryCraftRecipeHandler;

@Mixin(SlotExtractor4.class)
public class SlotExtractor4Mixin {

	@ModifyReturnValue(method = "isItemValid", at = @At("RETURN"))
	private boolean modifyIsItemValid(boolean original, ItemStack stack) {
		return original || RotaryCraftRecipeHandler.isExtractorSolution(stack);
	}
}
