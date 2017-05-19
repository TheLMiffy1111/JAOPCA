package thelm.jaopca.api.fluid;

import java.util.Locale;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.modules.ModuleMolten;

public class FluidBase extends Fluid {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public FluidBase(ItemEntry itemEntry, IOreEntry oreEntry) {
		super((itemEntry == ModuleMolten.MOLTEN_ENTRY ? "" : itemEntry.name + "_") + Utils.to_under_score(oreEntry.getOreName()), new ResourceLocation("jaopca:fluids/"+itemEntry.prefix+"_still"), new ResourceLocation("jaopca:fluids/"+itemEntry.prefix+"_flowing"));
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
