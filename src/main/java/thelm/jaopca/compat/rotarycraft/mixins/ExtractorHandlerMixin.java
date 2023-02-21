package thelm.jaopca.compat.rotarycraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import Reika.RotaryCraft.ModInterface.NEI.ExtractorHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.item.ItemStack;
import thelm.jaopca.compat.rotarycraft.recipes.JAOPCAExtractorRecipe;
import thelm.jaopca.compat.rotarycraft.recipes.RotaryCraftRecipeHandler;

@Mixin(ExtractorHandler.class)
public abstract class ExtractorHandlerMixin extends TemplateRecipeHandler {

	@Inject(method = "loadCraftingRecipes(Ljava/lang/String;[Ljava/lang/Object;)V", at = @At("RETURN"), remap = false)
	private void afterLoadCraftingRecipes(String outputId, Object[] results, CallbackInfo info) {
		if("rcextract".equals(outputId)) {
			for(JAOPCAExtractorRecipe recipe : RotaryCraftRecipeHandler.getExtractorRecipes()) {
				arecipes.add(((ExtractorHandler)(Object)this).new ExtractorRecipe(recipe));
			}
		}
	}

	@Inject(method = "loadCraftingRecipes(Lnet/minecraft/item/ItemStack;)V", at = @At("RETURN"), remap = false)
	private void afterLoadCraftingRecipes(ItemStack result, CallbackInfo info) {
		JAOPCAExtractorRecipe recipe = RotaryCraftRecipeHandler.getExtractorRecipeFromResult(result);
		if(recipe != null) {
			arecipes.add(((ExtractorHandler)(Object)this).new ExtractorRecipe(recipe));
		}
	}

	@Inject(method = "loadUsageRecipes(Lnet/minecraft/item/ItemStack;)V", at = @At("RETURN"), remap = false)
	private void loadUsageRecipes(ItemStack result, CallbackInfo info) {
		JAOPCAExtractorRecipe recipe = RotaryCraftRecipeHandler.getExtractorRecipe(result);
		if(recipe != null) {
			arecipes.add(((ExtractorHandler)(Object)this).new ExtractorRecipe(recipe));
		}
	}
}
