package thelm.jaopca.compat.crafttweaker;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.fluid.MCFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.MCItemStack;
import com.blamejared.crafttweaker.api.tag.CraftTweakerTagRegistry;
import com.blamejared.crafttweaker.api.tag.MCTag;
import com.blamejared.crafttweaker.api.tag.manager.ITagManager;
import com.google.common.collect.TreeBasedTable;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.blocks.IBlockLike;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

@ZenRegister
@ZenCodeType.Name("mods.jaopca.MaterialForm")
public class MaterialForm {

	private static final TreeBasedTable<IForm, IMaterial, MaterialForm> MATERIAL_FORM_WRAPPERS = TreeBasedTable.create();
	private final IMaterialFormInfo info;

	public static MaterialForm getMaterialFormWrapper(IForm form, IMaterial material) {
		MaterialForm materialForm = MATERIAL_FORM_WRAPPERS.get(form, material);
		if(materialForm == null) {
			IMaterialFormInfo info = form.getType().getMaterialFormInfo(form, material);
			materialForm = new MaterialForm(info);
		}
		return materialForm;
	}

	private MaterialForm(IMaterialFormInfo info) {
		this.info = info;
	}

	public IMaterialFormInfo getInternal() {
		return info;
	}

	@ZenCodeType.Getter("form")
	public Form getForm() {
		return Form.getFormWrapper(info.getMaterialForm().getForm());
	}

	@ZenCodeType.Getter("material")
	public Material getMaterial() {
		return Material.getMaterialWrapper(info.getMaterialForm().getMaterial());
	}

	@ZenCodeType.Method
	public MCTag asItemTag() {
		return asTag(Registry.ITEM_REGISTRY);
	}

	@ZenCodeType.Method
	public MCTag asFluidTag() {
		return asTag(Registry.FLUID_REGISTRY);
	}

	@ZenCodeType.Method
	public MCTag asTag(ResourceLocation registry) {
		return asTag(ResourceKey.createRegistryKey(registry));
	}

	public <T> MCTag asTag(ResourceKey<? extends Registry<T>> registry) {
		return asTag(CraftTweakerTagRegistry.INSTANCE.tagManager(registry));
	}

	@ZenCodeType.Method
	public MCTag asTag(ITagManager<?> tagManager) {
		return tagManager.tag(MiscHelper.INSTANCE.getTagLocation(
				info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getMaterial().getName(),
				info.getMaterialForm().getForm().getTagSeparator()));
	}

	@ZenCodeType.Method
	public IItemStack asItemStack(int count) {
		if(info instanceof ItemLike item) {
			return new MCItemStack(new ItemStack(item, count));
		}
		return MCItemStack.EMPTY.get();
	}

	@ZenCodeType.Method
	public IItemStack asItemStack() {
		return asItemStack(1);
	}

	@ZenCodeType.Method
	public IFluidStack asFluidStack(int amount) {
		if(info instanceof IFluidLike fluid) {
			return new MCFluidStack(new FluidStack(fluid.asFluid(), amount));
		}
		return MCFluidStack.EMPTY.get();
	}

	@ZenCodeType.Method
	public Block asBlock() {
		if(info instanceof IBlockLike block) {
			return block.asBlock();
		}
		return null;
	}

	@ZenCodeType.Method
	public BlockState asBlockState() {
		if(info instanceof IBlockLike block) {
			return block.asBlock().defaultBlockState();
		}
		return null;
	}
}
