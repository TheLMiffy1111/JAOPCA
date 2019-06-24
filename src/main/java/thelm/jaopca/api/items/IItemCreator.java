package thelm.jaopca.api.items;

import java.util.function.Supplier;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemCreator {

	MaterialFormItem create(IForm form, IMaterial material, Supplier<IItemFormSettings> settings);
}
