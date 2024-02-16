package thelm.jaopca.api.localization;

import thelm.jaopca.api.materials.IMaterial;

public interface ILocalizer {

	String localizeMaterial(IMaterial material);

	String localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey);
}
