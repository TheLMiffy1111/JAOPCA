package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.ItemBlockMaterialForm;
import thelm.jaopca.items.ItemFormType;

public class ItemBlockJAOPCA extends ItemBlockMaterialForm {

	private final Supplier<IBlockFormSettings> settings;

	public ItemBlockJAOPCA(BlockMaterialForm block, Supplier<IBlockFormSettings> settings) {
		super(block, new Item.Properties().group(ItemFormType.getItemGroup()));
		this.settings = settings;
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
	public EnumRarity getRarity(ItemStack stack) {
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
