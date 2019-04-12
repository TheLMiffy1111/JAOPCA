package thelm.jaopca.items;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.ItemMaterialForm;
import thelm.jaopca.api.materials.IMaterial;

public class ItemJAOPCA extends ItemMaterialForm {

	private final IForm form;
	private final IMaterial material;
	private final Supplier<IItemFormSettings> settings;

	public ItemJAOPCA(IForm form, IMaterial material, Supplier<IItemFormSettings> settings) {
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

	private OptionalInt itemStackLimit = OptionalInt.empty();
	private Optional<Boolean> beaconPayment = Optional.empty();
	private Optional<Boolean> hasEffect = Optional.empty();
	private Optional<EnumRarity> rarity = Optional.empty();
	private OptionalInt burnTime = OptionalInt.empty();

	@Override
	public void settingsChanged() {
		itemStackLimit = OptionalInt.empty();
		beaconPayment = Optional.empty();
		hasEffect = Optional.empty();
		rarity = Optional.empty();
		burnTime = OptionalInt.empty();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		if(!itemStackLimit.isPresent()) {
			itemStackLimit = OptionalInt.of(settings.get().getItemStackLimitFunction().applyAsInt(material));
		}
		return itemStackLimit.getAsInt();
	}

	@Override
	public boolean isBeaconPayment(ItemStack stack) {
		if(!beaconPayment.isPresent()) {
			beaconPayment = Optional.of(settings.get().getIsBeaconPaymentFunction().test(material));
		}
		return beaconPayment.get();
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.get().getHasEffectFunction().test(material));
		}
		return hasEffect.get();
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.get().getRarityFunction().apply(material));
		}
		return rarity.get();
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		if(!burnTime.isPresent()) {
			burnTime = OptionalInt.of(settings.get().getBurnTimeFunction().applyAsInt(material));
		}
		return burnTime.getAsInt();
	}
}
