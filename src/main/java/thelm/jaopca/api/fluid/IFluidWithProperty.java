package thelm.jaopca.api.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;

public interface IFluidWithProperty extends IObjectWithProperty {

	default IFluidWithProperty setLuminosity(int luminosity) {
		return this;
	}

	default IFluidWithProperty setTemperature(int temperature) {
		return this;
	}

	default IFluidWithProperty setDensity(int density) {
		return this;
	}

	default IFluidWithProperty setViscosity(int viscosity) {
		return this;
	}

	default IFluidWithProperty setGaseous(boolean gaseous) {
		return this;
	}

	default IFluidWithProperty setRarity(EnumRarity rarity) {
		return this;
	}

	default IFluidWithProperty setFillSound(SoundEvent fillSound) {
		return this;
	}

	default IFluidWithProperty setEmptySound(SoundEvent emptySound) {
		return this;
	}

	default IFluidWithProperty setOpacity(int opacity) {
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {}
}
