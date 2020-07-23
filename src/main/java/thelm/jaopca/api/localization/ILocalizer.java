package thelm.jaopca.api.localization;

import net.minecraft.util.text.IFormattableTextComponent;
import thelm.jaopca.api.materials.IMaterial;

public interface ILocalizer {

	IFormattableTextComponent localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey);
}
