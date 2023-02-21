package thelm.jaopca.localization;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.StatCollector;
import thelm.jaopca.api.localization.ILocalizer;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class LocalizerDefault implements ILocalizer {

	private LocalizerDefault() {}

	public static final LocalizerDefault INSTANCE = new LocalizerDefault();

	@Override
	public String localizeMaterial(IMaterial material) {
		Map<String, String> locMap = ApiImpl.INSTANCE.currentMaterialLocalizationMap();
		String materialKey = "jaopca.material."+material.getName();
		if(StatCollector.canTranslate(materialKey)) {
			return StatCollector.translateToLocal(materialKey);
		}
		else if(locMap.containsKey(materialKey)) {
			return locMap.get(materialKey);
		}
		else {
			return splitAndCapitalize(material.getName());
		}
	}

	@Override
	public String localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey) {
		Map<String, String> locMap = ApiImpl.INSTANCE.currentMaterialLocalizationMap();
		if(StatCollector.canTranslate(overrideKey)) {
			return StatCollector.translateToLocal(overrideKey);
		}
		else if(locMap.containsKey(overrideKey)) {
			return locMap.get(overrideKey);
		}
		String materialName = localizeMaterial(material);
		if(StatCollector.canTranslate(formTranslationKey) || !locMap.containsKey(formTranslationKey)) {
			return StatCollector.translateToLocalFormatted(formTranslationKey, materialName);
		}
		else {
			return String.format(locMap.get(overrideKey), materialName);
		}
	}

	public static String splitAndCapitalize(String camelCase) {
		return Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(camelCase)).map(StringUtils::capitalize).collect(Collectors.joining(" "));
	}
}
