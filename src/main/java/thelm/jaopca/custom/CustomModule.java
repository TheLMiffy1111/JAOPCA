package thelm.jaopca.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule
public class CustomModule implements IModule {

	private static final Logger LOGGER = LogManager.getLogger();
	public static CustomModule instance;

	private Gson gson = new Gson();
	private final List<BiConsumer<IMaterial, IDynamicSpecConfig>> customConfigDefiners = new ArrayList<>();
	private final List<IFormRequest> formRequests = new ArrayList<>();

	public CustomModule() {
		if(instance == null) {
			instance = this;
		}
	}

	@Override
	public String getName() {
		return "custom";
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public void addCustomConfigDefiner(BiConsumer<IMaterial, IDynamicSpecConfig> customConfigDefiner) {
		customConfigDefiners.add(customConfigDefiner);
	}

	public void setCustomFormConfigFile(File customFormConfigFile) {
		formRequests.clear();
		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(customFormConfigFile), StandardCharsets.UTF_8)) {
			IFormRequest[] requests = gson.fromJson(reader, IFormRequest[].class);
			if(requests != null) {
				Collections.addAll(formRequests, requests);
			}
		}
		catch(Exception e) {
			LOGGER.error("Unable to read custom json", e);
		}
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return formRequests;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		for(BiConsumer<IMaterial, IDynamicSpecConfig> customConfigDefiner : customConfigDefiners) {
			for(IMaterial material : moduleData.getMaterials()) {
				customConfigDefiner.accept(material, configs.get(material));
			}
		}
	}
}
