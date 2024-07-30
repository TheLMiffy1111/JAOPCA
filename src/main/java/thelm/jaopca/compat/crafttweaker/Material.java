package thelm.jaopca.compat.crafttweaker;

import java.util.TreeMap;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.tag.CraftTweakerTagRegistry;
import com.blamejared.crafttweaker.api.tag.MCTag;
import com.blamejared.crafttweaker.api.tag.manager.ITagManager;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

@ZenRegister
@ZenCodeType.Name("mods.jaopca.Material")
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

	@ZenCodeType.Getter("name")
	public String getName() {
		return material.getName();
	}

	@ZenCodeType.Getter("type")
	public String getType() {
		return material.getType().getName();
	}

	@ZenCodeType.Getter("alternativeNames")
	public String[] getAlternativeNames() {
		return material.getAlternativeNames().toArray(new String[0]);
	}

	@ZenCodeType.Method
	public Material getExtra(int index) {
		return new Material(material.getExtra(index));
	}

	@ZenCodeType.Method
	public boolean hasExtra(int index) {
		return material.hasExtra(index);
	}

	@ZenCodeType.Getter("isSmallStorageBlock")
	public boolean isSmallStorageBlock() {
		return material.isSmallStorageBlock();
	}

	@ZenCodeType.Method
	public MCTag getItemTag(String prefix) {
		return getItemTag(prefix, "/");
	}

	@ZenCodeType.Method
	public MCTag getItemTag(String prefix, String tagSeperator) {
		return getTag(Registry.ITEM_REGISTRY, prefix, tagSeperator);
	}

	@ZenCodeType.Method
	public MCTag getFluidTag(String prefix) {
		return getFluidTag(prefix, "/");
	}

	@ZenCodeType.Method
	public MCTag getFluidTag(String prefix, String tagSeperator) {
		return getTag(Registry.FLUID_REGISTRY, prefix, tagSeperator);
	}

	@ZenCodeType.Method
	public MCTag getTag(ResourceLocation registry, String prefix) {
		return getTag(registry, prefix, "/");
	}

	@ZenCodeType.Method
	public MCTag getTag(ResourceLocation registry, String prefix, String tagSeperator) {
		return getTag(ResourceKey.createRegistryKey(registry), prefix, tagSeperator);
	}

	public <T> MCTag getTag(ResourceKey<? extends Registry<T>> registry, String prefix) {
		return getTag(registry, prefix, "/");
	}

	public <T> MCTag getTag(ResourceKey<? extends Registry<T>> registry, String prefix, String tagSeperator) {
		return getTag(CraftTweakerTagRegistry.INSTANCE.tagManager(registry), prefix, tagSeperator);
	}

	@ZenCodeType.Method
	public MCTag getTag(ITagManager<?> tagManager, String prefix) {
		return getTag(tagManager, prefix, "/");
	}

	@ZenCodeType.Method
	public MCTag getTag(ITagManager<?> tagManager, String prefix, String tagSeperator) {
		return tagManager.tag(MiscHelper.INSTANCE.getTagLocation(prefix, material.getName(), tagSeperator));
	}

	@ZenCodeType.Method
	public IItemStack getItemStack(String prefix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		ItemStack stack = helper.getItemStack(helper.getTagLocation(prefix, material.getName()), count);
		return IItemStack.of(stack);
	}

	@ZenCodeType.Method
	public IItemStack getItemStack(String prefix) {
		return getItemStack(prefix, 1);
	}

	@ZenCodeType.Method
	public IFluidStack getFluidStack(String prefix, int count) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		FluidStack stack = helper.getFluidStack(helper.getTagLocation(prefix, material.getName()), count);
		return IFluidStack.of(stack);
	}

	@ZenCodeType.Method
	public MaterialForm getMaterialForm(Form form) {
		if(form.containsMaterial(this)) {
			return MaterialForm.getMaterialFormWrapper(form.getInternal(), material);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Material other) {
			return material == other.material;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return material.hashCode()+7;
	}
}
