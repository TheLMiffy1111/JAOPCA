package thelm.jaopca.compat.rotarycraft.recipes;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;

import Reika.DragonAPI.Interfaces.Registry.OreType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class JAOPCAExtractorRecipe implements OreType {

	public final Collection<ItemStack> ore;
	public final ItemStack dust;
	public final ItemStack slurry;
	public final ItemStack solution;
	public final ItemStack flakes;
	public final boolean isRare;

	JAOPCAExtractorRecipe(Collection<ItemStack> ore, ItemStack dust, ItemStack slurry, ItemStack solution, ItemStack flakes, boolean isRare) {
		this.ore = Objects.requireNonNull(ore);
		this.dust = Objects.requireNonNull(dust);
		this.slurry = Objects.requireNonNull(slurry);
		this.solution = Objects.requireNonNull(solution);
		this.flakes = Objects.requireNonNull(flakes);
		this.isRare = isRare;
	}

	@Override
	public OreRarity getRarity() {
		return isRare ? OreRarity.RARE : OreRarity.AVERAGE;
	}

	@Override
	public Collection<ItemStack> getAllOreBlocks() {
		return Collections.unmodifiableCollection(ore);
	}

	/* UNUSED */

	@Override
	public String[] getOreDictNames() {
		return new String[0];
	}

	@Override
	public String getProductOreDictName() {
		return "";
	}

	@Override
	public ItemStack getFirstOreBlock() {
		return null;
	}

	@Override
	public EnumSet<OreLocation> getOreLocations() {
		return EnumSet.noneOf(OreLocation.class);
	}

	@Override
	public boolean canGenerateIn(Block paramBlock) {
		return false;
	}

	@Override
	public int getDropCount() {
		return 1;
	}

	@Override
	public boolean existsInGame() {
		return true;
	}

	@Override
	public int ordinal() {
		return 0;
	}

	@Override
	public String name() {
		return "";
	}

	@Override
	public int getDisplayColor() {
		return 0xFFFFFF;
	}

	@Override
	public String getDisplayName() {
		return "";
	}
}
