package thelm.jaopca.forms;

import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.FormDeserializer;
import thelm.jaopca.custom.json.FormRequestDeserializer;
import thelm.jaopca.custom.json.MaterialDoubleFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialIntFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialLongFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialPredicateDeserializer;
import thelm.jaopca.modules.CustomModule;

public class FormTypeHandler {

	private FormTypeHandler() {}

	private static final TreeMap<String, IFormType<?>> FORM_TYPES = new TreeMap<>();

	public static boolean registerFormType(IFormType<?> type) {
		return FORM_TYPES.putIfAbsent(type.getName(), type) == null;
	}

	public static IFormType<?> getFormType(String name) {
		return FORM_TYPES.get(name);
	}

	public static void setupGson() {
		GsonBuilder builder = new GsonBuilder().
				registerTypeAdapter(IFormRequest.class, FormRequestDeserializer.INSTANCE).
				registerTypeAdapter(IForm.class, FormDeserializer.INSTANCE).
				registerTypeAdapter(MaterialType.class, EnumDeserializer.INSTANCE).
				registerTypeAdapter(new TypeToken<Predicate<IMaterial>>(){}.getType(), MaterialPredicateDeserializer.INSTANCE).
				registerTypeAdapter(new TypeToken<ToIntFunction<IMaterial>>(){}.getType(), MaterialIntFunctionDeserializer.INSTANCE).
				registerTypeAdapter(new TypeToken<ToLongFunction<IMaterial>>(){}.getType(), MaterialLongFunctionDeserializer.INSTANCE).
				registerTypeAdapter(new TypeToken<ToDoubleFunction<IMaterial>>(){}.getType(), MaterialDoubleFunctionDeserializer.INSTANCE);
		for(IFormType<?> formType : FORM_TYPES.values()) {
			builder = formType.configureGsonBuilder(builder);
		}
		CustomModule.instance.setGson(builder.create());
	}
}
