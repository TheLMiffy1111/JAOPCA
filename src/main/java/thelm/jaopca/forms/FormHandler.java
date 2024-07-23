package thelm.jaopca.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

public class FormHandler {

	private FormHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<String, IForm> FORMS = new TreeMap<>();
	private static final List<IFormRequest> FORM_REQUESTS = new ArrayList<>();

	public static Map<String, IForm> getFormMap() {
		return FORMS;
	}

	public static Collection<IForm> getForms() {
		return FORMS.values();
	}

	public static IForm getForm(String name) {
		return FORMS.get(name);
	}

	public static boolean containsForm(String name) {
		return FORMS.containsKey(name);
	}

	public static void collectForms() {
		for(IModule module : ModuleHandler.getModuleMap().values()) {
			List<IFormRequest> list = module.getFormRequests();
			if(list != null && !list.isEmpty()) {
				list.stream().filter(request->request.getModule() == module).forEach(FORM_REQUESTS::add);
			}
		}
		for(IFormRequest request : FORM_REQUESTS) {
			for(IForm form : request.getForms()) {
				if(FORMS.putIfAbsent(form.getName(), form) != null) {
					throw new IllegalStateException(String.format("Form name conflict: %s for modules %s and %s",
							form.getName(), FORMS.get(form.getName()).getModule().getName(), form.getModule().getName()));
				}
				form.getType().addForm(form);
			}
		}
	}

	public static void computeValidMaterials() {
		for(IFormRequest request : FORM_REQUESTS) {
			if(request.isGrouped()) {
				List<IMaterial> materials = MaterialHandler.getMaterials().stream().filter(request::isMaterialGroupValid).collect(Collectors.toList());
				for(IForm form : request.getForms()) {
					form.setMaterials(materials);
				}
				request.setMaterials(materials);
			}
			else {
				for(IForm form : request.getForms()) {
					List<IMaterial> materials = MaterialHandler.getMaterials().stream().filter(form::isMaterialValid).collect(Collectors.toList());
					form.setMaterials(materials);
				}
			}
		}
	}
}
