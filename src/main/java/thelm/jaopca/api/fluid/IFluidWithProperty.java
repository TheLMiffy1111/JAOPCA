package thelm.jaopca.api.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;

public interface IFluidWithProperty {
	
	IFluidWithProperty setLuminosity(int luminosity);
	IFluidWithProperty setTemperature(int temperature);
	IFluidWithProperty setDensity(int density);
	IFluidWithProperty setViscosity(int viscosity);
	IFluidWithProperty setGaseous(boolean gaseous);
	IFluidWithProperty setRarity(EnumRarity rarity);
	IFluidWithProperty setFillSound(SoundEvent fillSound);
	IFluidWithProperty setEmptySound(SoundEvent emptySound);
}
