package thelm.jaopca.localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.JsonHelper;
import thelm.jaopca.utils.MiscHelper;

public class LocalizationRepoHandler {

	private LocalizationRepoHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int MAX_HTTP_REDIRECTS = Integer.getInteger("http.maxRedirects", 20);

	private static File configDir;
	private static File langDir;
	private static Map<String, String> currentLocalizationMap = ImmutableSortedMap.of();

	public static void setup(File modConfigDir) {
		configDir = new File(modConfigDir, "jaopca");
		langDir = new File(configDir, "lang");
		if(!langDir.exists() || !langDir.isDirectory()) {
			try {
				if(langDir.exists() && !langDir.isDirectory()) {
					LOGGER.warn("Directory {} is a file, deleting", langDir);
					langDir.delete();
				}
				if(!langDir.mkdir()) {
					throw new RuntimeException("Could not create directory "+langDir+", please create manually");
				}
			}
			catch(SecurityException e) {
				throw new RuntimeException("Could not create directory "+langDir+", please create manually", e);
			}
		}
		reload();
	}

	public static void reload() {
		MiscHelper.INSTANCE.submitAsyncTask(()->{
			JsonHelper jsonHelper = JsonHelper.INSTANCE;
			String language = LocalizationHandler.getLanguage();
			File langFile = new File(langDir, language+".json");
			if(ConfigHandler.checkL10nUpdates) {
				if(System.currentTimeMillis()-langFile.lastModified() > ConfigHandler.updateInterval*24*3600*1000) {
					try {
						LOGGER.info("Downloading localization file for language {}", language);
						URL url = new URL("https://raw.githubusercontent.com/TheLMiffy1111/JAOPCAMaterialLocalizations/v2/lang/"+language+".json");
						InputStream con = openUrlStream(url);
						byte[] data = ByteStreams.toByteArray(con);
						con.close();
						FileOutputStream out = new FileOutputStream(langFile);
						out.write(data);
						out.close();
						LOGGER.info("Downloaded localization file for language {}", language);
					}
					catch(FileNotFoundException e) {
						LOGGER.info("Unable to download localization file for language {}: File not found", language);
						currentLocalizationMap = ImmutableSortedMap.of();
					}
					catch(IOException e) {
						LOGGER.info("Unable to download localization file for language {}", language, e);
						currentLocalizationMap = ImmutableSortedMap.of();
					}
				}
			}
			if(langFile.exists()) {
				try(InputStreamReader reader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8)) {
					LOGGER.info("Reading localization file", language);
					JsonElement jsonElement = new JsonParser().parse(reader);
					JsonObject json = jsonHelper.getJsonObject(jsonElement, "file");
					ImmutableSortedMap.Builder<String, String> builder = ImmutableSortedMap.naturalOrder();
					for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
						if(entry.getValue().isJsonPrimitive()) {
							String string = jsonHelper.getString(entry.getValue(), entry.getKey());
							if(!string.isEmpty()) {
								builder.put(entry.getKey(), string);
							}
						}
						else {
							// currently unused
						}
					}
					currentLocalizationMap = builder.build();
					LOGGER.info("Finished reading localization file", language);
				}
				catch(Exception e) {
					LOGGER.info("Unable to read localization file", e);
					currentLocalizationMap = ImmutableSortedMap.of();
				}
			}
		});
	}

	public static Map<String, String> getCurrentLocalizationMap() {
		return currentLocalizationMap;
	}

	private static InputStream openUrlStream(URL url) throws IOException {
		URL currentUrl = url;
		for(int redirects = 0; redirects < MAX_HTTP_REDIRECTS; redirects++) {
			URLConnection c = currentUrl.openConnection();
			if(c instanceof HttpURLConnection) {
				HttpURLConnection huc = (HttpURLConnection)c;
				huc.setInstanceFollowRedirects(false);
				int responseCode = huc.getResponseCode();
				if(responseCode >= 300 && responseCode <= 399) {
					try {
						String loc = huc.getHeaderField("Location");
						currentUrl = new URL(currentUrl, loc);
						continue;
					}
					finally {
						huc.disconnect();
					}
				}
			}
			return c.getInputStream();
		}
		throw new IOException("Too many redirects while trying to fetch " + url);
	}
}
