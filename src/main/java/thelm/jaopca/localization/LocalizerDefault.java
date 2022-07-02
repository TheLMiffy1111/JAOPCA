package thelm.jaopca.localization;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.text.translation.I18n;
import thelm.jaopca.api.localization.ILocalizer;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class LocalizerDefault implements ILocalizer {

	private LocalizerDefault() {}

	public static final LocalizerDefault INSTANCE = new LocalizerDefault();

	@Override
	public String localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey) {
		Map<String, String> locMap = ApiImpl.INSTANCE.currentMaterialLocalizationMap();
		if(I18n.canTranslate(overrideKey)) {
			return I18n.translateToLocal(overrideKey);
		}
		else if(locMap.containsKey(overrideKey)) {
			return locMap.get(overrideKey);
		}
		String materialName;
		String materialKey = "jaopca.material."+material.getName();
		if(I18n.canTranslate(materialKey)) {
			materialName = I18n.translateToLocal(materialKey);
		}
		else if(locMap.containsKey(materialKey)) {
			materialName = locMap.get(materialKey);
		}
		else {
			materialName = splitAndCapitalize(material.getName());
		}
		if(I18n.canTranslate(formTranslationKey) || !locMap.containsKey(formTranslationKey)) {
			return I18n.translateToLocalFormatted(formTranslationKey, materialName);
		}
		else {
			return String.format(locMap.get(overrideKey), materialName);
		}
	}

	public static String splitAndCapitalize(String camelCase) {
		return Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(camelCase)).map(StringUtils::capitalize).collect(Collectors.joining(" "));
	}
}
