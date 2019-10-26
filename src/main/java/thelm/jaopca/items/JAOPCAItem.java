package thelm.jaopca.items;

import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCAItem extends Item implements IMaterialFormItem {

	private final IForm form;
	private final IMaterial material;
	protected final IItemFormSettings settings;

	protected OptionalInt itemStackLimit = OptionalInt.empty();
	protected Optional<Boolean> beaconPayment = Optional.empty();
	protected Optional<Boolean> hasEffect = Optional.empty();
	protected Optional<Rarity> rarity = Optional.empty();
	protected OptionalInt burnTime = OptionalInt.empty();

	public JAOPCAItem(IForm form, IMaterial material, IItemFormSettings settings) {
		super(new Item.Properties().group(ItemFormType.getItemGroup()));
		this.form = form;
		this.material = material;
		this.settings = settings;
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
		if(!itemStackLimit.isPresent()) {
			itemStackLimit = OptionalInt.of(settings.getItemStackLimitFunction().applyAsInt(material));
		}
		return itemStackLimit.getAsInt();
	}

	@Override
	public boolean isBeaconPayment(ItemStack stack) {
		if(!beaconPayment.isPresent()) {
			beaconPayment = Optional.of(settings.getIsBeaconPaymentFunction().test(material));
		}
		return beaconPayment.get();
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(material));
		}
		return hasEffect.get();
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(material));
		}
		return rarity.get();
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		if(!burnTime.isPresent()) {
			burnTime = OptionalInt.of(settings.getBurnTimeFunction().applyAsInt(material));
		}
		return burnTime.getAsInt();
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+form.getName(), material, getTranslationKey());
	}
}
