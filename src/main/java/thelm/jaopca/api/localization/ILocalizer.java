package thelm.jaopca.api.localization;

import thelm.jaopca.api.materials.IMaterial;

public interface ILocalizer {

	String localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey);
}
