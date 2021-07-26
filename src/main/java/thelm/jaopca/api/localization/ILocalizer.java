package thelm.jaopca.api.localization;

import net.minecraft.network.chat.MutableComponent;
import thelm.jaopca.api.materials.IMaterial;

public interface ILocalizer {

	MutableComponent localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey);
}
