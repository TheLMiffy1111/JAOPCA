package thelm.jaopca.localization;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import thelm.jaopca.api.localization.ILocalizer;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class LocalizerDefault implements ILocalizer {

	private LocalizerDefault() {}

	public static final LocalizerDefault INSTANCE = new LocalizerDefault();

	@Override
	public MutableComponent localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey) {
		Language languageMap = Language.getInstance();
		Map<String, String> locMap = ApiImpl.INSTANCE.currentMaterialLocalizationMap();
		if(languageMap.has(overrideKey)) {
			return Component.translatable(overrideKey);
		}
		else if(locMap.containsKey(overrideKey)) {
			return Component.literal(locMap.get(overrideKey));
		}
		String materialName;
		String materialKey = "jaopca.material."+material.getName();
		if(languageMap.has(materialKey)) {
			materialName = languageMap.getOrDefault(materialKey);
		}
		else if(locMap.containsKey(materialKey)) {
			materialName = locMap.get(materialKey);
		}
		else {
			materialName = splitAndCapitalize(material.getName());
		}
		if(languageMap.has(formTranslationKey) || !locMap.containsKey(formTranslationKey)) {
			return Component.translatable(formTranslationKey, materialName);
		}
		else {
			return Component.literal(String.format(locMap.get(overrideKey), materialName));
		}
	}

	public static String splitAndCapitalize(String underscore) {
		return Arrays.stream(StringUtils.split(underscore, '_')).map(StringUtils::capitalize).reduce((s1, s2)->s1+' '+s2).orElse("");
	}
}
