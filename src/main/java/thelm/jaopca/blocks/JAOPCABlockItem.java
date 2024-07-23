package thelm.jaopca.blocks;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlockItem extends BlockItem implements IMaterialFormBlockItem {

	protected final IBlockFormSettings settings;

	protected IntSupplier itemStackLimit;
	protected BooleanSupplier hasEffect;
	protected Supplier<Rarity> rarity;
	protected IntSupplier burnTime;

	public JAOPCABlockItem(IMaterialFormBlock block, IBlockFormSettings settings) {
		super(block.toBlock(), new Item.Properties().tab(ItemFormType.getItemGroup()));
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
	public boolean isFoil(ItemStack stack) {
		return hasEffect.getAsBoolean() || super.isFoil(stack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return rarity.get();
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		return burnTime.getAsInt();
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+getForm().getName(), getMaterial(), getDescriptionId());
	}
}
