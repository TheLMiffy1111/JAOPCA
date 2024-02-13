package thelm.jaopca.compat.rotarycraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import Reika.RotaryCraft.Auxiliary.SlotExtractor1;
import net.minecraft.item.ItemStack;
import thelm.jaopca.compat.rotarycraft.recipes.RotaryCraftRecipeHandler;

@Mixin(SlotExtractor1.class)
public class SlotExtractor1Mixin {

	@ModifyReturnValue(method = "isItemValid", at = @At("RETURN"))
	private boolean modifyIsItemValid(boolean original, ItemStack stack) {
		return original || RotaryCraftRecipeHandler.isExtractorOre(stack);
	}
}
