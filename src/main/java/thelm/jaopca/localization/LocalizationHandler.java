package thelm.jaopca.localization;

import java.util.Objects;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraftforge.fml.DistExecutor;
import thelm.jaopca.api.localization.ILocalizer;

public class LocalizationHandler {

	private static final TreeMap<String, ILocalizer> LOCALIZERS = new TreeMap<>();

	public static void registerLocalizer(ILocalizer localizer, String... languages) {
		Objects.requireNonNull(localizer);
		for(String language : Objects.requireNonNull(languages)) {
			LOCALIZERS.put(language, localizer);
		}
	}

	public static ILocalizer getCurrentLocalizer() {
		return LOCALIZERS.computeIfAbsent(getLanguage(), key->LocalizerDefault.INSTANCE);
	}

	public static String getLanguage() {
		return DistExecutor.unsafeRunForDist(()->()->{
			Minecraft mc = Minecraft.getInstance();
			if(mc != null) {
				Language lang = mc.getLanguageManager().getSelected();
				if(lang != null) {
					return lang.getCode();
				}
				return mc.options.languageCode;
			}
			return "en_us";
		}, ()->()->"en_us");
	}
}
