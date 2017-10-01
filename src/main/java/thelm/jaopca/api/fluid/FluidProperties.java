package thelm.jaopca.api.fluid;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.IProperties;
import thelm.jaopca.api.block.BlockFluidBase;
import thelm.jaopca.api.block.IBlockFluidWithProperty;
import thelm.jaopca.api.item.IItemBlockFluidWithProperty;
import thelm.jaopca.api.item.ItemBlockFluidBase;

/**
 *
 * @author TheLMiffy1111
 */
public class FluidProperties implements IProperties {

	/**
	 * The default FluidProperties. DO NOT CALL ANY METHODS ON THIS FIELD.
	 */
	public static final FluidProperties DEFAULT = new FluidProperties();

	public ToIntFunction<IOreEntry> luminosFunc = entry->0;
	public ToIntFunction<IOreEntry> densityFunc = entry->1000;
	public ToIntFunction<IOreEntry> tempFunc = entry->300;
	public ToIntFunction<IOreEntry> viscosFunc = entry->1000;
	public Predicate<IOreEntry> gaseous = entry->densityFunc.applyAsInt(entry)<0;
	public EnumRarity rarity = EnumRarity.COMMON;
	public SoundEvent fillSound = SoundEvents.ITEM_BUCKET_FILL;
	public SoundEvent emptySound = SoundEvents.ITEM_BUCKET_EMPTY;
	public ToIntFunction<IOreEntry> opacityFunc = entry->255;
	public boolean hasBlock = true;
	public Material material = Material.WATER;
	public ToIntFunction<IOreEntry> quantaFunc = entry->8;
	public Class<? extends IFluidWithProperty> fluidClass = FluidBase.class;
	public Class<? extends IBlockFluidWithProperty> blockFluidClass = BlockFluidBase.class;
	public Class<? extends IItemBlockFluidWithProperty> itemBlockFluidClass = ItemBlockFluidBase.class;

	@Override
	public EnumEntryType getType() {
		return EnumEntryType.FLUID;
	}

	public FluidProperties setLuminosityFunc(ToIntFunction<IOreEntry> value) {
		luminosFunc = value;
		return this;
	}

	public FluidProperties setDensityFunc(ToIntFunction<IOreEntry> value) {
		densityFunc = value;
		return this;
	}

	public FluidProperties setTemperatureFunc(ToIntFunction<IOreEntry> value) {
		tempFunc = value;
		return this;
	}

	public FluidProperties setViscosityFunc(ToIntFunction<IOreEntry> value) {
		viscosFunc = value;
		return this;
	}

	public FluidProperties setGaseousPredicate(Predicate<IOreEntry> value) {
		gaseous = value;
		return this;
	}

	public FluidProperties setRarity(EnumRarity value) {
		rarity = value;
		return this;
	}

	public FluidProperties setFillSound(SoundEvent value) {
		fillSound = value;
		return this;
	}

	public FluidProperties setEmptySound(SoundEvent value) {
		emptySound = value;
		return this;
	}

	public FluidProperties setOpacityFunc(ToIntFunction<IOreEntry> value) {
		opacityFunc = value;
		return this;
	}

	public FluidProperties setHasBlock(boolean value) {
		hasBlock = value;
		return this;
	}

	public FluidProperties setMaterial(Material value) {
		material = value;
		return this;
	}

	public FluidProperties setQuantaFunc(ToIntFunction<IOreEntry> value) {
		quantaFunc = value;
		return this;
	}

	public FluidProperties setFluidClass(Class<? extends IFluidWithProperty> value) {
		fluidClass = value;
		return this;
	}

	public FluidProperties setBlockFluidClass(Class<? extends IBlockFluidWithProperty> value) {
		blockFluidClass = value;
		return this;
	}

	public FluidProperties setItemBlockFluidClass(Class<? extends IItemBlockFluidWithProperty> value) {
		itemBlockFluidClass = value;
		return this;
	}
}
