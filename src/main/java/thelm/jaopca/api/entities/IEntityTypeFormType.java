package thelm.jaopca.api.entities;

import thelm.jaopca.api.forms.IFormType;

public interface IEntityTypeFormType extends IFormType {

	@Override
	IEntityTypeFormSettings getNewSettings();
}
