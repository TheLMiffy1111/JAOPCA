package thelm.jaopca.compat.minetweaker;

import java.util.TreeMap;

import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import minetweaker.mc1710.liquid.MCLiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

@ZenClass("mods.jaopca.Material")
public class Material {

	private static final TreeMap<IMaterial, Material> MATERIAL_WRAPPERS = new TreeMap<>();
	private final IMaterial material;

	public static Material getMaterialWrapper(IMaterial material) {
		return MATERIAL_WRAPPERS.computeIfAbsent(material, Material::new);
	}

	private Material(IMaterial material) {
		this.material = material;
	}

	public IMaterial getInternal() {
		return material;
	}

	@ZenGetter("name")
	public String getName() {
		return material.getName();
	}

	@ZenGetter("type")
	public String getType() {
		return material.getType().getName();
	}

	@ZenGetter("alternativeNames")
	public String[] getAlternativeNames() {
		return material.getAlternativeNames().toArray(new String[0]);
	}

	@ZenMethod
	public Material getExtra(int index) {
		return new Material(material.getExtra(index));
	}

	@ZenMethod
	public boolean hasExtra(int index) {
		return material.hasExtra(index);
	}

	@ZenMethod
	public IOreDictEntry getOreDictEntry(String prefix) {
		return MineTweakerMC.getOreDict(MiscHelper.INSTANCE.getOredictName(prefix, material.getName()));
	}

	@ZenMethod
	public IItemStack getItemStack(String prefix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		ItemStack stack = helper.getItemStack(helper.getOredictName(prefix, material.getName()), count, false);
		return MineTweakerMC.getIItemStack(stack);
	}

	@ZenMethod
	public IItemStack getItemStack(String prefix) {
		return getItemStack(prefix, 1);
	}

	@ZenMethod
	public ILiquidStack getLiquidStack(String prefix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		FluidStack stack = helper.getFluidStack(helper.getOredictName(prefix, material.getName()), count);
		return new MCLiquidStack(stack);
	}

	@ZenMethod
	public MaterialForm getMaterialForm(Form form) {
		if(form.containsMaterial(this)) {
			return MaterialForm.getMaterialFormWrapper(form.getInternal(), material);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Material) {
			return material == ((Material)obj).material;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return material.hashCode()+7;
	}
}
