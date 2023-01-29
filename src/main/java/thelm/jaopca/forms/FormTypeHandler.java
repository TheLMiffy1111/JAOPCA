package thelm.jaopca.forms;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.google.common.base.Functions;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.FormDeserializer;
import thelm.jaopca.custom.json.FormRequestDeserializer;
import thelm.jaopca.custom.json.MaterialDoubleFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialIntFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialLongFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialPredicateDeserializer;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.items.ItemFormType;

public class FormTypeHandler {

	private FormTypeHandler() {}

	public static final Type PREDICATE_TYPE = new TypeToken<Predicate<IMaterial>>(){}.getType();
	public static final Type INT_FUNCTION_TYPE = new TypeToken<ToIntFunction<IMaterial>>(){}.getType();
	public static final Type DOUBLE_FUNCTION_TYPE = new TypeToken<ToDoubleFunction<IMaterial>>(){}.getType();
	public static final Type LONG_FUNCTION_TYPE = new TypeToken<ToLongFunction<IMaterial>>(){}.getType();
	public static final Type STRING_FUNCTION_TYPE = new TypeToken<Function<IMaterial, String>>(){}.getType();

	private static final TreeMap<String, IFormType> FORM_TYPES = new TreeMap<>();

	public static boolean registerFormType(IFormType type) {
		Objects.requireNonNull(type);
		return FORM_TYPES.putIfAbsent(type.getName(), type) == null;
	}

	public static IFormType getFormType(String name) {
		return FORM_TYPES.get(name);
	}

	public static void setupGson() {
		GsonBuilder builder = new GsonBuilder().
				registerTypeAdapter(IFormRequest.class, FormRequestDeserializer.INSTANCE).
				registerTypeAdapter(IForm.class, FormDeserializer.INSTANCE).
				registerTypeAdapter(MaterialType.class, EnumDeserializer.INSTANCE).
				registerTypeAdapter(PREDICATE_TYPE, MaterialPredicateDeserializer.INSTANCE).
				registerTypeAdapter(INT_FUNCTION_TYPE, MaterialIntFunctionDeserializer.INSTANCE).
				registerTypeAdapter(LONG_FUNCTION_TYPE, MaterialLongFunctionDeserializer.INSTANCE).
				registerTypeAdapter(DOUBLE_FUNCTION_TYPE, MaterialDoubleFunctionDeserializer.INSTANCE).
				registerTypeAdapter(STRING_FUNCTION_TYPE,
						new MaterialMappedFunctionDeserializer<>(Functions.identity(), Functions.identity()));
		for(IFormType formType : FORM_TYPES.values()) {
			builder = formType.configureGsonBuilder(builder);
		}
		CustomModule.instance.setGson(builder.create());
	}

	public static void registerMaterialForms() {
		BlockFormType.INSTANCE.registerMaterialForms();
		ItemFormType.INSTANCE.registerMaterialForms();
		FluidFormType.INSTANCE.registerMaterialForms();
		for(IFormType formType : FORM_TYPES.values()) {
			if(formType == BlockFormType.INSTANCE || formType == ItemFormType.INSTANCE || formType == FluidFormType.INSTANCE) {
				continue;
			}
			formType.registerMaterialForms();
		}
	}
}
