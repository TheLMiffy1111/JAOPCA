package thelm.jaopca.items;

import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeType;
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
	protected Optional<Boolean> hasEffect = Optional.empty();
	protected Optional<Rarity> rarity = Optional.empty();
	protected OptionalInt burnTime = OptionalInt.empty();

	public JAOPCAItem(IForm form, IMaterial material, IItemFormSettings settings) {
		super(new Item.Properties().tab(ItemFormType.getCreativeTab()));
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
	public boolean isFoil(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(material));
		}
		return hasEffect.get() || super.isFoil(stack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(material));
		}
		return rarity.get();
	}

	@Override
	public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
		if(!burnTime.isPresent()) {
			burnTime = OptionalInt.of(settings.getBurnTimeFunction().applyAsInt(material));
		}
		return burnTime.getAsInt();
	}

	@Override
	public Component getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+form.getName(), material, getDescriptionId(stack));
	}
}
