package thelm.jaopca.api.localization;

import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.materials.IMaterial;

public interface ILocalizer {

	ITextComponent localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey);
}
