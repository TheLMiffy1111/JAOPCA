package thelm.jaopca.api.blocks;

import net.minecraft.resources.Identifier;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IBlockCreator {

	IMaterialFormBlock create(IForm form, IMaterial material, IBlockFormSettings settings, Identifier registryName);
}
