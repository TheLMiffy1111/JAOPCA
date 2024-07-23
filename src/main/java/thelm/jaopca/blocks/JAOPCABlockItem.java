package thelm.jaopca.blocks;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlockItem extends ItemBlock implements IMaterialFormBlockItem {

	protected final IBlockFormSettings settings;

	protected IntSupplier itemStackLimit;
	protected BooleanSupplier hasEffect;
	protected Supplier<EnumRarity> rarity;
	protected IntSupplier burnTime;

	public JAOPCABlockItem(IMaterialFormBlock block, IBlockFormSettings settings) {
		super(block.toBlock());
		this.settings = settings;

		itemStackLimit = MemoizingSuppliers.of(settings.getItemStackLimitFunction(), block::getMaterial);
		hasEffect = MemoizingSuppliers.of(settings.getHasEffectFunction(), block::getMaterial);
		rarity = MemoizingSuppliers.of(settings.getDisplayRarityFunction(), block::getMaterial);
		burnTime = MemoizingSuppliers.of(settings.getBurnTimeFunction(), block::getMaterial);
	}

	@Override
	public IForm getForm() {
		return ((IMaterialForm)getBlock()).getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return ((IMaterialForm)getBlock()).getMaterial();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return itemStackLimit.getAsInt();
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return hasEffect.getAsBoolean() || super.hasEffect(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity.get();
	}

	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		return burnTime.getAsInt();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+getForm().getName(), getMaterial(), getTranslationKey(stack));
	}
}
