package thelm.jaopca.api.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.modules.ModuleMolten;

public class FluidBase extends Fluid implements IFluidWithProperty {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public FluidBase(ItemEntry itemEntry, IOreEntry oreEntry) {
		super((itemEntry.name.equals("molten")?"":Utils.to_under_score(itemEntry.name)+"_")+Utils.to_under_score(oreEntry.getOreName()), new ResourceLocation("jaopca:fluids/"+Utils.to_under_score(itemEntry.prefix)+"_still"), new ResourceLocation("jaopca:fluids/"+Utils.to_under_score(itemEntry.prefix)+"_flowing"));
		this.setUnlocalizedName("jaopca."+itemEntry.name);
		this.oreEntry = oreEntry;
		this.itemEntry = itemEntry;
	}

	@Override
	public IOreEntry getOreEntry() {
		return oreEntry;
	}

	@Override
	public ItemEntry getItemEntry() {
		return itemEntry;
	}

	@Override
	public FluidBase setLuminosity(int luminosity) {
		super.setLuminosity(luminosity);
		return this;
	}

	@Override
	public FluidBase setDensity(int density) {
		super.setDensity(density);
		return this;
	}

	@Override
	public FluidBase setTemperature(int temperature) {
		super.setTemperature(temperature);
		return this;
	}

	@Override
	public FluidBase setViscosity(int viscosity) {
		super.setViscosity(viscosity);
		return this;
	}

	@Override
	public FluidBase setGaseous(boolean isGaseous) {
		super.setGaseous(isGaseous);
		return this;
	}

	@Override
	public FluidBase setRarity(EnumRarity rarity) {
		super.setRarity(rarity);
		return this;
	}

	@Override
	public FluidBase setFillSound(SoundEvent fillSound) {
		super.setFillSound(fillSound);
		return this;
	}

	@Override
	public FluidBase setEmptySound(SoundEvent emptySound) {
		super.setEmptySound(emptySound);
		return this;
	}

	protected int opacity = 255;

	@Override
	public FluidBase setOpacity(int opacity) {
		this.opacity = opacity;
		return this;
	}

	@Override
	public int getColor() {
		return (oreEntry.getColor()&0xFFFFFF) | (opacity<<24);
	}

	@Override
	public String getLocalizedName(FluidStack stack) {
		return String.format(super.getLocalizedName(stack), I18n.canTranslate("jaopca.entry."+oreEntry.getOreName()) ? I18n.translateToLocal("jaopca.entry."+oreEntry.getOreName()) : oreEntry.getOreName());
	}
}
