package thelm.jaopca.compat.groovyscript;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictWildcardIngredient;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

public class MaterialExpansion {

	public static IIngredient ore(IMaterial material, String prefix) {
		String oreDict = MiscHelper.INSTANCE.getOredictName(prefix, material.getName());
		return oreDict.contains("*") ? OreDictWildcardIngredient.of(oreDict) : new OreDictIngredient(oreDict);
	}
	
	public static ItemStack item(IMaterial material, String prefix, int count) {
		MiscHelper helper = MiscHelper.INSTANCE;
		return helper.getItemStack(helper.getOredictName(prefix, material.getName()), count);
	}
	
	public static ItemStack item(IMaterial material, String prefix) {
		return item(material, prefix, 1);
	}

	public static FluidStack fluid(IMaterial material, String prefix, int count) {
		MiscHelper helper = MiscHelper.INSTANCE;
		return helper.getFluidStack(helper.getOredictName(prefix, material.getName()), count);
	}

	public static FluidStack liquid(IMaterial material, String prefix, int count) {
		return fluid(material, prefix, count);
	}

	public static IMaterialFormInfo getMaterialForm(IMaterial material, IForm form) {
		if(!FormExpansion.containsMaterial(form, material)) {
			return null;
		}
		return form.getType().getMaterialFormInfo(form, material);
	}
}
