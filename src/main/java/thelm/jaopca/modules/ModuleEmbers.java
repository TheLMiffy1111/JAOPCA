package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.ItemMeltingOreRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
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
				"Iron", "Gold", "Silver", "Copper", "Lead"
				);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(FluidRegistry.isFluidRegistered(Utils.to_under_score(entry.getOreName()))) {
				RecipeRegistry.meltingOreRecipes.put("ore"+entry.getOreName(),
						new ItemMeltingOreRecipe("ore"+entry.getOreName(), new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 288)));
				RecipeRegistry.meltingOreRecipes.put("ingot"+entry.getOreName(),
						new ItemMeltingOreRecipe("ingot"+entry.getOreName(), new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144)));
				RecipeRegistry.meltingOreRecipes.put("nugget"+entry.getOreName(),
						new ItemMeltingOreRecipe("nugget"+entry.getOreName(), new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 16)));

				RecipeRegistry.stampingRecipes.add(
						new ItemStampingRecipe(null, new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144),
								EnumStampType.TYPE_BAR, Utils.getOreStack("ingot", entry, 1), false, false));
				if(Utils.doesOreNameExist("plate"+entry.getOreName())) {
					RecipeRegistry.stampingRecipes.add(
							new ItemStampingRecipe(null, new FluidStack(FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144),
									EnumStampType.TYPE_PLATE, Utils.getOreStack("plate", entry, 1), false, false));
				}
			}
		}
	}
}
