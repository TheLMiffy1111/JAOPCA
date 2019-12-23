package thelm.jaopca.localization;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.LanguageMap;
import thelm.jaopca.api.localization.ILocalizer;
import thelm.jaopca.api.materials.IMaterial;

public class LocalizerDefault implements ILocalizer {

	private LocalizerDefault() {}

	public static final LocalizerDefault INSTANCE = new LocalizerDefault();

	@Override
	public ITextComponent localizeMaterialForm(String formTranslationKey, IMaterial material, String overrideKey) {
		LanguageMap languageMap = LanguageMap.getInstance();
		if(languageMap.exists(overrideKey)) {
			return new TranslationTextComponent(overrideKey);
		}
		String materialName;
		String materialKey = "jaopca.material."+material.getName();
		if(languageMap.exists(materialKey)) {
			materialName = languageMap.translateKey(materialKey);
		}
		else {
			materialName = splitAndCapitalize(material.getName());
		}
		return new TranslationTextComponent(formTranslationKey, materialName);
	}

	public static String splitAndCapitalize(String underscore) {
		return Arrays.stream(StringUtils.split(underscore, '_')).map(StringUtils::capitalize).reduce((s1, s2)->s1+' '+s2).orElse("");
	}
}
