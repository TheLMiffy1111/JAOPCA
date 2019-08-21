package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.items.ItemFormType;

public class JAOPCABlockItem extends BlockItem implements IMaterialFormBlockItem {

	private final Supplier<IBlockFormSettings> settings;

	public JAOPCABlockItem(IMaterialFormBlock block, Supplier<IBlockFormSettings> settings) {
		super(block.asBlock(), new Item.Properties().group(block.getMaterial().getType().isDummy() ? null : ItemFormType.getItemGroup()));
		this.settings = settings;
	}

	@Override
	public IForm getForm() {
		return ((IMaterialForm)getBlock()).getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return ((IMaterialForm)getBlock()).getMaterial();
	}

	private OptionalInt itemStackLimit = OptionalInt.empty();
	private Optional<Boolean> beaconPayment = Optional.empty();
	private Optional<Boolean> hasEffect = Optional.empty();
	private Optional<Rarity> rarity = Optional.empty();
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
			itemStackLimit = OptionalInt.of(settings.get().getItemStackLimitFunction().applyAsInt(getMaterial()));
		}
		return itemStackLimit.getAsInt();
	}

	@Override
	public boolean isBeaconPayment(ItemStack stack) {
		if(!beaconPayment.isPresent()) {
			beaconPayment = Optional.of(settings.get().getIsBeaconPaymentFunction().test(getMaterial()));
		}
		return beaconPayment.get();
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.get().getHasEffectFunction().test(getMaterial()));
		}
		return hasEffect.get();
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.get().getDisplayRarityFunction().apply(getMaterial()));
		}
		return rarity.get();
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		if(!burnTime.isPresent()) {
			burnTime = OptionalInt.of(settings.get().getBurnTimeFunction().applyAsInt(getMaterial()));
		}
		return burnTime.getAsInt();
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return JAOPCAApi.instance().currentLocalizer().localizeMaterialForm(getForm(), getMaterial(), getTranslationKey());
	}
}
