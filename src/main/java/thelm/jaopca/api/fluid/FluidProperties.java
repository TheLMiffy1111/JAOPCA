package thelm.jaopca.api.fluid;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;
import thelm.jaopca.api.IOreEntry;

/**
 * 
 * @author TheLMiffy1111
 */
public class FluidProperties {

	/**
	 * The default FluidProperties. DO NOT CALL ANY METHODS ON THIS FIELD.
	 */
	public static final FluidProperties DEFAULT = new FluidProperties();

	public ToIntFunction<IOreEntry> luminosFunc = (entry)->{return 0;};
	public ToIntFunction<IOreEntry> densityFunc = (entry)->{return 1000;};
	public ToIntFunction<IOreEntry> tempFunc = (entry)->{return 300;};
	public ToIntFunction<IOreEntry> viscosFunc = (entry)->{return 1000;};
	public Predicate<IOreEntry> gaseous = (entry)->{return densityFunc.applyAsInt(entry)<0;};
	public EnumRarity rarity = EnumRarity.COMMON;
	public SoundEvent fillSound = SoundEvents.ITEM_BUCKET_FILL;
	public SoundEvent emptySound = SoundEvents.ITEM_BUCKET_EMPTY;
	public Class<? extends IFluidWithProperty> fluidClass = FluidBase.class;

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

	public FluidProperties setFluidClass(Class<? extends IFluidWithProperty> value) {
		fluidClass = value;
		return this;
	}
}
