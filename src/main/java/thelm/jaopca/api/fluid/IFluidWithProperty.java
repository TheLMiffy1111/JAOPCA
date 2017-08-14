package thelm.jaopca.api.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;
import thelm.jaopca.api.IObjectWithProperty;

public interface IFluidWithProperty extends IObjectWithProperty {
	
	IFluidWithProperty setLuminosity(int luminosity);
	IFluidWithProperty setTemperature(int temperature);
	IFluidWithProperty setDensity(int density);
	IFluidWithProperty setViscosity(int viscosity);
	IFluidWithProperty setGaseous(boolean gaseous);
	IFluidWithProperty setRarity(EnumRarity rarity);
	IFluidWithProperty setFillSound(SoundEvent fillSound);
	IFluidWithProperty setEmptySound(SoundEvent emptySound);
	IFluidWithProperty setOpacity(int opacity);
}
