package thelm.jaopca.api.blocks;

import java.util.function.Supplier;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IBlockCreator {

	IMaterialFormBlock create(IForm form, IMaterial material, Supplier<IBlockFormSettings> settings);
}
