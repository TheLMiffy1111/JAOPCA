package thelm.jaopca.items;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCAItem extends Item implements IMaterialFormItem {

	private final IForm form;
	private final IMaterial material;
	protected final IItemFormSettings settings;

	protected IntSupplier itemStackLimit;
	protected BooleanSupplier hasEffect;
	protected Supplier<Rarity> rarity;
	protected IntSupplier burnTime;

	public JAOPCAItem(IForm form, IMaterial material, IItemFormSettings settings) {
		super(new Item.Properties().tab(ItemFormType.getItemGroup()));
		this.form = form;
		this.material = material;
		this.settings = settings;

		itemStackLimit = MemoizingSuppliers.of(settings.getItemStackLimitFunction(), material);
		hasEffect = MemoizingSuppliers.of(settings.getHasEffectFunction(), material);
		rarity = MemoizingSuppliers.of(settings.getDisplayRarityFunction(), material);
		burnTime = MemoizingSuppliers.of(settings.getBurnTimeFunction(), material);
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
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
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+form.getName(), material, getDescriptionId());
	}
}
