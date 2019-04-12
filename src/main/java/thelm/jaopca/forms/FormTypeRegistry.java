package thelm.jaopca.forms;

import java.util.HashMap;

import thelm.jaopca.api.forms.IFormType;

public class FormTypeRegistry {

	private static final HashMap<String, IFormType<?>> FORM_TYPES = new HashMap<>();
	
	public static boolean registerFormType(IFormType<?> type) {
		return FORM_TYPES.putIfAbsent(type.getName(), type) == null;
	}
	
	public static IFormType<?> getFormType(String name) {
		return FORM_TYPES.get(name);
	}
}
