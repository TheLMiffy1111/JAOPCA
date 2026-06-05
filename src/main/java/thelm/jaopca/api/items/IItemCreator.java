package thelm.jaopca.api.items;

import net.minecraft.resources.Identifier;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemCreator {

	IMaterialFormItem create(IForm form, IMaterial material, IItemFormSettings settings, Identifier registryName);
}
