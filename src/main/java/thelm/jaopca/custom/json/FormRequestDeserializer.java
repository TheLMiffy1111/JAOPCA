package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.forms.FormRequest;
import thelm.jaopca.utils.JsonHelper;

public class FormRequestDeserializer implements JsonDeserializer<IFormRequest> {

	public static final FormRequestDeserializer INSTANCE = new FormRequestDeserializer();

	private FormRequestDeserializer() {}

	@Override
	public IFormRequest deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if(jsonElement.isJsonObject()) {
			return context.<IForm>deserialize(jsonElement, IForm.class).toRequest();
		}
		else if(jsonElement.isJsonArray()) {
			return new FormRequest(CustomModule.instance, context.<IForm[]>deserialize(jsonElement, IForm.class)).setGrouped(true);
		}
		throw new JsonParseException("Unable to deserialize "+JsonHelper.INSTANCE.toSimpleString(jsonElement)+" into a form request");
	}
}
