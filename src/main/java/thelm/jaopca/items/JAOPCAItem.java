package thelm.jaopca.items;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeType;
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

	protected IntSupplier maxStackSize;
	protected BooleanSupplier hasEffect;
	protected Supplier<Rarity> rarity;
	protected IntSupplier burnTime;

	public JAOPCAItem(IForm form, IMaterial material, IItemFormSettings settings) {
		super(new Item.Properties().tab(ItemFormType.getCreativeTab()));
		this.form = form;
		this.material = material;
		this.settings = settings;

		maxStackSize = MemoizingSuppliers.of(settings.getMaxStackSizeFunction(), material);
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
	public int getMaxStackSize(ItemStack stack) {
		return maxStackSize.getAsInt();
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
	public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
		return burnTime.getAsInt();
	}

	@Override
	public Component getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+form.getName(), material, getDescriptionId(stack));
	}
}
