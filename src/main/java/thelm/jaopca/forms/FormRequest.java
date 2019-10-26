package thelm.jaopca.forms;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.modules.ModuleHandler;

public class FormRequest implements IFormRequest {

	private final IModule module;
	private final List<IForm> forms;
	private boolean grouped = false;

	public FormRequest(IModule module, IForm... forms) {
		this.module = Objects.requireNonNull(module);
		this.forms = Arrays.stream(Objects.requireNonNull(forms)).
				filter(Objects::nonNull).
				filter(form->form.getModule() == module).
				map(IForm::lock).collect(ImmutableList.toImmutableList());
		for(IForm form : this.forms) {
			form.setRequest(this);
		}
	}

	@Override
	public IModule getModule() {
		return module;
	}

	@Override
	public List<IForm> getForms() {
		return forms;
	}

	@Override
	public boolean isGrouped() {
		return grouped;
	}

	@Override
	public IFormRequest setGrouped(boolean grouped) {
		this.grouped = grouped;
		return this;
	}

	@Override
	public boolean isMaterialGroupValid(IMaterial material) {
		return !ModuleHandler.getModuleData(module).getRejectedMaterials().contains(material) &&
				forms.stream().filter(form->!form.skipGroupedCheck()).anyMatch(form->form.isMaterialValid(material));
	}
}
