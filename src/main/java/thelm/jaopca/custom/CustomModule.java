package thelm.jaopca.custom;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import thelm.jaopca.api.custom.CustomCodecs;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.forms.Form;
import thelm.jaopca.forms.FormRequest;

@JAOPCAModule
public class CustomModule implements IModule {

	private static final Logger LOGGER = LogManager.getLogger();
	public static CustomModule instance;

	private final Codec<IForm> formCodec = CustomCodecs.FORM_TYPE.dispatch(
			"type", IForm::getType,
			type->CustomCodecs.<IForm>builder(
					kind->kind.group(Codec.STRING.fieldOf("name").forGetter(IForm::getName)).
					apply(kind, name->new Form(this, name, type))).
			withField(
					Codec.STRING.optionalFieldOf("secondaryName"),
					IForm::getSecondaryName,
					IForm::setSecondaryName).
			withField(
					CustomCodecs.setOf(CustomCodecs.MATERIAL_TYPE).optionalFieldOf("materialTypes"),
					IForm::getMaterialTypes,
					IForm::setMaterialTypes).
			withField(
					CustomCodecs.setOf(Codec.STRING).optionalFieldOf("defaultMaterialBlacklist"),
					IForm::getDefaultMaterialBlacklist,
					IForm::setDefaultMaterialBlacklist).
			withField(
					Codec.BOOL.optionalFieldOf("skipGroupedCheck"),
					IForm::skipGroupedCheck,
					IForm::setSkipGroupedCheck).
			withField(
					Codec.STRING.optionalFieldOf("tagSeparator"),
					IForm::getTagSeparator,
					IForm::setTagSeparator).
			withField(
					type.formSettingsCodec().optionalFieldOf("settings"),
					IForm::getSettings,
					IForm::setSettings).
			build());
	private final Codec<IFormRequest> formRequestCodec = Codec.either(formCodec, formCodec.listOf()).
			xmap(either->either.map(IForm::toRequest, forms->new FormRequest(this, forms.toArray(new IForm[0]))),
					request->Either.right(request.getForms()));

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

	public void setCustomFormConfigFile(Path customFormConfigFile) {
		formRequests.clear();
		try(InputStreamReader reader = new InputStreamReader(Files.newInputStream(customFormConfigFile), StandardCharsets.UTF_8)) {
			JsonElement json = JsonParser.parseReader(reader);
			if(!json.isJsonNull()) {
				List<IFormRequest> requests = CustomCodecs.listOrSingle(formRequestCodec).parse(JsonOps.INSTANCE, json).
						resultOrPartial(LOGGER::warn).orElse(List.of());
				formRequests.addAll(requests);
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
}
