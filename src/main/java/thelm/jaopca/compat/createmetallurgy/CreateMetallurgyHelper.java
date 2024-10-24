package thelm.jaopca.compat.createmetallurgy;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.compat.createmetallurgy.recipes.CastingBasinRecipeSerializer;
import thelm.jaopca.compat.createmetallurgy.recipes.CastingTableRecipeSerializer;
import thelm.jaopca.compat.createmetallurgy.recipes.MeltingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CreateMetallurgyHelper {

	public static final CreateMetallurgyHelper INSTANCE = new CreateMetallurgyHelper();

	private CreateMetallurgyHelper() {}

	public CastingOutput getCastingOutput(Object obj, int count) {
		return getCastingOutputResolved(obj, count).getLeft();
	}

	public Pair<CastingOutput, Set<Item>> getCastingOutputResolved(Object obj, int count) {
		CastingOutput out = CastingOutput.fromStack(ItemStack.EMPTY);
		Set<Item> items = new HashSet<>();
		if(obj instanceof Supplier<?>) {
			Pair<CastingOutput, Set<Item>> pair = getCastingOutputResolved(((Supplier<?>)obj).get(), count);
			out = pair.getLeft();
			items.addAll(pair.getRight());
		}
		else if(obj instanceof CastingOutput) {
			out = ((CastingOutput)obj);
			// We can't know what items the ingredient can have so assume all
			items.addAll(ForgeRegistries.ITEMS.getValues());
		}
		else if(obj instanceof ItemStack stack) {
			if(!stack.isEmpty()) {
				out = CastingOutput.fromStack(stack);
				items.add(stack.getItem());
			}
		}
		else if(obj instanceof ItemLike item) {
			if(item.asItem() != Items.AIR) {
				out = CastingOutput.fromStack(new ItemStack(item, count));
				items.add(item.asItem());
			}
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			out = CastingOutput.fromTag(MiscHelper.INSTANCE.getItemTagKey(location), count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			out = CastingOutput.fromTag(MiscHelper.INSTANCE.getItemTagKey(location), count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			out = CastingOutput.fromTag(key, count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(key.location()));
		}
		items.remove(Items.AIR);
		return Pair.of(items.isEmpty() ? null : out, items);
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object secondFluidInput, int secondFluidInputAmount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, itemInput, fluidInput, fluidInputAmount, secondFluidInput, secondFluidInputAmount, itemOutput, itemOutputCount, fluidOutput, fluidOutputAmount, time, heatLevel));
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object itemInput, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, itemInput, itemOutput, itemOutputCount, fluidOutput, fluidOutputAmount, time, heatLevel));
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object itemInput, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, itemInput, fluidOutput, fluidOutputAmount, time, heatLevel));
	}

	public boolean registerCastingTableRecipe(ResourceLocation key, Object mold, Object input, int inputAmount, Object output, int outputCount, int time, boolean consumeMold) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeSerializer(key, mold, input, inputAmount, output, outputCount, time, consumeMold));
	}

	public boolean registerCastingBasinRecipe(ResourceLocation key, Object mold, Object input, int inputAmount, Object output, int outputCount, int time, boolean consumeMold) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingBasinRecipeSerializer(key, mold, input, inputAmount, output, outputCount, time, consumeMold));
	}
}
