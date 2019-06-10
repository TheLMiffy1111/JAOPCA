package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreIngredient;
import teamroots.embers.RegistryManager;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.ItemMeltingRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
/*import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.ItemMeltingOreRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;*/
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleEmbers extends ModuleBase {

	@Override
	public String getName() {
		return "embers";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("molten");
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Iron", "Gold", "Silver", "Copper", "Lead", "Aluminium", "Nickel", "Tin"
				);
	}

	@Override
	public void init() {
		Ingredient stampBar = Ingredient.fromItem(RegistryManager.stamp_bar);
		Ingredient stampPlate = Ingredient.fromItem(RegistryManager.stamp_plate);
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(FluidRegistry.isFluidRegistered(Utils.to_under_score(entry.getOreName()))) {
				RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(
						new OreIngredient("ore"+entry.getOreName()), FluidRegistry.getFluidStack(Utils.to_under_score(entry.getOreName()), 288)).
						addBonusOutput(FluidRegistry.getFluidStack(Utils.to_under_score(entry.getOreName()), 16)));
				RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(
						new OreIngredient("ingot"+entry.getOreName()), FluidRegistry.getFluidStack(Utils.to_under_score(entry.getOreName()), 144)));
				RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(
						new OreIngredient("nugget"+entry.getOreName()), FluidRegistry.getFluidStack(Utils.to_under_score(entry.getOreName()), 16)));

				RecipeRegistry.stampingRecipes.add(
						new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack(Utils.to_under_score(entry.getOreName()), 144),
								stampBar, Utils.getOreStack("ingot", entry, 1)));
				if(Utils.doesOreNameExist("plate"+entry.getOreName())) {
					RecipeRegistry.stampingRecipes.add(
							new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack(Utils.to_under_score(entry.getOreName()), 144),
									stampPlate, Utils.getOreStack("plate", entry, 1)));
				}
			}
		}
	}
}
