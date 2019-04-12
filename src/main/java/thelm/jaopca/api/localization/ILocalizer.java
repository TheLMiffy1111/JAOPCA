package thelm.jaopca.api.localization;

import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface ILocalizer {

	ITextComponent localizeMaterialForm(IForm form, IMaterial material, String defaultKey);
}
