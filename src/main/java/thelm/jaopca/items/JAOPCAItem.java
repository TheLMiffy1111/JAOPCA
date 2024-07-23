package thelm.jaopca.items;

import java.util.function.BooleanSupplier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

	protected BooleanSupplier hasEffect;

	public JAOPCAItem(IForm form, IMaterial material, IItemFormSettings settings) {
		super(getProperties(form, material, settings));
		this.form = form;
		this.material = material;
		this.settings = settings;

		hasEffect = MemoizingSuppliers.of(settings.getHasEffectFunction(), ()->material);
	}

	public static Item.Properties getProperties(IForm form, IMaterial material, IItemFormSettings settings) {
		Item.Properties prop = new Item.Properties();
		prop.rarity(settings.getDisplayRarityFunction().apply(material));
		return prop;
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
	public boolean isFoil(ItemStack stack) {
		return hasEffect.getAsBoolean() || super.isFoil(stack);
	}

	@Override
	public Component getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+form.getName(), material, getDescriptionId(stack));
	}
}
