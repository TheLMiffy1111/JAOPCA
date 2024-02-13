package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalInt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlockItem extends ItemBlock implements IMaterialFormBlockItem {

	protected final IBlockFormSettings settings;

	protected OptionalInt itemStackLimit = OptionalInt.empty();
	protected Optional<Boolean> hasEffect = Optional.empty();
	protected Optional<EnumRarity> rarity = Optional.empty();

	public JAOPCABlockItem(IMaterialFormBlock block, IBlockFormSettings settings) {
		super(block.toBlock());
		this.settings = settings;
	}

	@Override
	public IForm getForm() {
		return ((IMaterialForm)field_150939_a).getForm();
	}

	@Override
	public IMaterial getIMaterial() {
		return ((IMaterialForm)field_150939_a).getIMaterial();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		if(!itemStackLimit.isPresent()) {
			itemStackLimit = OptionalInt.of(settings.getItemStackLimitFunction().applyAsInt(getIMaterial()));
		}
		return itemStackLimit.getAsInt();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(getIMaterial()));
		}
		return hasEffect.get() || super.hasEffect(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(getIMaterial()));
		}
		return rarity.get();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+getForm().getName(), getIMaterial(), getUnlocalizedName(stack));
	}
}
