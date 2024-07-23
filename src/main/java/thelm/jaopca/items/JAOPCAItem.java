package thelm.jaopca.items;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
	protected Supplier<EnumRarity> rarity;
	protected IntSupplier burnTime;
	protected Supplier<String> translationKey;

	public JAOPCAItem(IForm form, IMaterial material, IItemFormSettings settings) {
		this.form = form;
		this.material = material;
		this.settings = settings;

		itemStackLimit = MemoizingSuppliers.of(settings.getItemStackLimitFunction(), material);
		hasEffect = MemoizingSuppliers.of(settings.getHasEffectFunction(), material);
		rarity = MemoizingSuppliers.of(settings.getDisplayRarityFunction(), material);
		burnTime = MemoizingSuppliers.of(settings.getBurnTimeFunction(), material);
		translationKey = MemoizingSuppliers.of(()->{
			ResourceLocation id = getRegistryName();
			return "item."+id.getNamespace()+"."+id.getPath().replace('/', '.');
		});
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
	public String getTranslationKey() {
		return translationKey.get();
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return getTranslationKey();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+getForm().getName(), getMaterial(), getTranslationKey(stack));
	}
}
