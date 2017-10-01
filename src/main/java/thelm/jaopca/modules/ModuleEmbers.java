package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.ItemMeltingOreRecipe;
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
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(FluidRegistry.isFluidRegistered(Utils.to_under_score(entry.getOreName()))) {
				RecipeRegistry.meltingOreRecipes.add(new ItemMeltingOreRecipe("ore"+entry.getOreName(), new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 288)));
				RecipeRegistry.meltingOreRecipes.add(new ItemMeltingOreRecipe("ingot"+entry.getOreName(), new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144)));
				RecipeRegistry.meltingOreRecipes.add(new ItemMeltingOreRecipe("nugget"+entry.getOreName(), new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 16)));

				RecipeRegistry.stampingRecipes.add(
						new ItemStampingRecipe(ItemStack.EMPTY, new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144),
								EnumStampType.TYPE_BAR, Utils.getOreStack("ingot", entry, 1), false, false));
				if(Utils.doesOreNameExist("plate"+entry.getOreName())) {
					RecipeRegistry.stampingRecipes.add(
							new ItemStampingRecipe(ItemStack.EMPTY, new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144),
									EnumStampType.TYPE_PLATE, Utils.getOreStack("plate", entry, 1), false, false));
				}
			}
		}
	}
}
