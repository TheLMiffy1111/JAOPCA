package thelm.jaopca.api.items;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemCreator {

	IMaterialFormItem create(IForm form, IMaterial material, Supplier<IItemFormSettings> settings);
}
