package thelm.jaopca.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public class FluidBase extends Fluid {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public FluidBase(ItemEntry itemEntry, IOreEntry oreEntry) {
		super(itemEntry.prefix+"_"+oreEntry.getOreName(), new ResourceLocation("jaopca:fluids/"+itemEntry.prefix+"_still"), new ResourceLocation("jaopca:fluids/"+itemEntry.prefix+"_flowing"));
		this.setUnlocalizedName("jaopca."+itemEntry.name);
		this.oreEntry = oreEntry;
		this.itemEntry = itemEntry;
	}

	@Override
	public int getColor() {
		return oreEntry.getColor();
	}

	@Override
	public String getLocalizedName(FluidStack stack) {
		return String.format(super.getLocalizedName(stack), I18n.canTranslate("jaopca.entry."+oreEntry.getOreName()) ? I18n.translateToLocal("jaopca.entry."+oreEntry.getOreName()) : oreEntry.getOreName());
	}
}
