package thelm.jaopca.localization;

import java.util.Objects;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLEnvironment;
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
		return MiscHelper.INSTANCE.conditionalSupplier(FMLEnvironment.dist::isClient, ()->()->{
			Minecraft mc = Minecraft.getInstance();
			if(mc != null) {
				String lang = mc.getLanguageManager().getSelected();
				return lang != null ? lang : mc.options.languageCode;
			}
			return "en_us";
		}, ()->()->"en_us").get();
	}
}
