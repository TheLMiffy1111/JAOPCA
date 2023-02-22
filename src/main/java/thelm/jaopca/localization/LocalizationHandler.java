package thelm.jaopca.localization;

import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import thelm.jaopca.api.localization.ILocalizer;
import thelm.jaopca.utils.MiscHelper;

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
		return MiscHelper.INSTANCE.conditionalSupplier(FMLCommonHandler.instance().getSide()::isClient, ()->()->{
			Minecraft mc = Minecraft.getMinecraft();
			if(mc != null) {
				Language lang = mc.getLanguageManager().getCurrentLanguage();
				if(lang != null) {
					return lang.getLanguageCode().toLowerCase(Locale.US);
				}
				return mc.gameSettings.language.toLowerCase(Locale.US);
			}
			return "en_us";
		}, ()->()->"en_us").get();
	}
}
