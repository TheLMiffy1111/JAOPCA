package thelm.jaopca.compat.rotarycraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.RotaryCraft.ModInterface.NEI.ExtractorHandler;
import net.minecraft.item.ItemStack;
import thelm.jaopca.compat.rotarycraft.recipes.JAOPCAExtractorRecipe;

@Mixin(ExtractorHandler.ExtractorRecipe.class)
public class ExtractorHandlerRecipeMixin {

	@Shadow(remap = false)
	public OreType type;

	@ModifyReturnValue(method = "getDust", at = @At("RETURN"), remap = false)
	private ItemStack modifyGetDust(ItemStack original) {
		if(original == null && type instanceof JAOPCAExtractorRecipe) {
			return ((JAOPCAExtractorRecipe)type).dust;
		}
		return original;
	}

	@ModifyReturnValue(method = "getSlurry", at = @At("RETURN"), remap = false)
	private ItemStack modifyGetSlurry(ItemStack original) {
		if(original == null && type instanceof JAOPCAExtractorRecipe) {
			return ((JAOPCAExtractorRecipe)type).slurry;
		}
		return original;
	}

	@ModifyReturnValue(method = "getSolution", at = @At("RETURN"), remap = false)
	private ItemStack modifyGetSolution(ItemStack original) {
		if(original == null && type instanceof JAOPCAExtractorRecipe) {
			return ((JAOPCAExtractorRecipe)type).solution;
		}
		return original;
	}

	@ModifyReturnValue(method = "getFlakes", at = @At("RETURN"), remap = false)
	private ItemStack modifyGetFlakes(ItemStack original) {
		if(original == null && type instanceof JAOPCAExtractorRecipe) {
			return ((JAOPCAExtractorRecipe)type).flakes;
		}
		return original;
	}
}
