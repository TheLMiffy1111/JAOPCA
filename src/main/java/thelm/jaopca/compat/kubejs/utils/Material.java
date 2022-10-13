package thelm.jaopca.compat.kubejs.utils;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

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

	public String getName() {
		return material.getName();
	}

	public String getType() {
		return material.getType().getName();
	}

	public List<String> getAlternativeNames() {
		return material.getAlternativeNames().stream().collect(Collectors.toList());
	}

	public Material getExtra(int index) {
		return new Material(material.getExtra(index));
	}

	public boolean hasExtra(int index) {
		return material.hasExtra(index);
	}

	public boolean isSmallStorageBlock() {
		return material.isSmallStorageBlock();
	}

	public String getTag(String prefix) {
		return getTag(prefix, "/");
	}

	public String getTag(String prefix, String tagSeperator) {
		return MiscHelper.INSTANCE.getTagLocation(prefix, material.getName(), tagSeperator).toString();
	}

	public ItemStack getItemStack(String prefix, int count) {
		MiscHelper helper = MiscHelper.INSTANCE;
		return helper.getItemStack(helper.getTagLocation(prefix, material.getName()), count);
	}

	public ItemStack getItemStack(String prefix) {
		return getItemStack(prefix, 1);
	}

	public FluidStackJS getFluidStack(String prefix, int count) {
		MiscHelper helper = MiscHelper.INSTANCE;
		FluidStack stack = helper.getFluidStack(helper.getTagLocation(prefix, material.getName()), count);
		return FluidStackJS.of(FluidStackHooksForge.fromForge(stack));
	}

	public MaterialForm getMaterialForm(Form form) {
		if(!form.containsMaterial(this)) {
			return null;
		}
		return MaterialForm.getMaterialFormWrapper(form.getInternal(), material);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Material other)) {
			return false;
		}
		return material == other.material;
	}

	@Override
	public int hashCode() {
		return material.hashCode()+7;
	}
}
