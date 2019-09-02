package thelm.jaopca.api.items;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemCreator {

	IMaterialFormItem create(IForm form, IMaterial material, IItemFormSettings settings);
}
