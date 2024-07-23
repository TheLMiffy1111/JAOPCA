package thelm.jaopca.compat.groovyscript;

import java.util.List;
import java.util.stream.Collectors;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictWildcardIngredient;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

public class FormExpansion {

	public static boolean containsMaterial(IForm form, IMaterial material) {
		return form.getMaterials().contains(material);
	}

	public static IIngredient ore(IForm form, String suffix) {
		String oreDict = MiscHelper.INSTANCE.getOredictName(form.getSecondaryName(), suffix);
		return oreDict.contains("*") ? OreDictWildcardIngredient.of(oreDict) : new OreDictIngredient(oreDict);
	}

	public static ItemStack item(IForm form, String suffix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		return helper.getItemStack(helper.getOredictName(form.getSecondaryName(), suffix), count);
	}

	public static ItemStack item(IForm form, String suffix) {
		return item(form, suffix, 1);
	}

	public static FluidStack fluid(IForm form, String suffix, int amount) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		return helper.getFluidStack(helper.getFluidName(form.getSecondaryName(), suffix), amount);
	}

	public static FluidStack liquid(IForm form, String suffix, int amount) {
		return fluid(form, suffix, amount);
	}

	public static IMaterialFormInfo getMaterialForm(IForm form, IMaterial material) {
		if(!containsMaterial(form, material)) {
			return null;
		}
		return form.getType().getMaterialFormInfo(form, material);
	}

	public static List<IMaterialFormInfo> getMaterialForms(IForm form) {
		return form.getMaterials().stream().map(m->form.getType().getMaterialFormInfo(form, m)).collect(Collectors.toList());
	}
}
