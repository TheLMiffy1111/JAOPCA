package thelm.jaopca.compat.theurgy.items;

import java.util.List;

import com.klikli_dev.theurgy.content.item.sulfur.AlchemicalSulfurItem;
import com.klikli_dev.theurgy.content.item.sulfur.AlchemicalSulfurType;
import com.klikli_dev.theurgy.registry.DataComponentRegistry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.theurgy.TheurgyModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAAlchemicalSulfurItem extends AlchemicalSulfurItem implements IMaterialFormItem {

	private final IForm form;
	private final IMaterial material;

	public JAOPCAAlchemicalSulfurItem(IForm form, IMaterial material, IItemFormSettings settings) {
		super(getProperties(form, material, settings));
		this.form = form;
		this.material = material;
		tier = TheurgyModule.TIER_FUNCTION.apply(material);
		type = switch(material.getType()) {
		case INGOT, INGOT_LEGACY -> AlchemicalSulfurType.METALS;
		case GEM, CRYSTAL -> AlchemicalSulfurType.GEMS;
		default -> AlchemicalSulfurType.OTHER_MINERALS;
		};
	}

	public static Item.Properties getProperties(IForm form, IMaterial material, IItemFormSettings settings) {
		Item.Properties prop = new Item.Properties();
		ResourceLocation tagLocation = MiscHelper.INSTANCE.getTagLocation(material.getType().getFormName(), material.getName());
		prop.component(DataComponentRegistry.SOURCE_TAG.get(), MiscHelper.INSTANCE.getItemTagKey(tagLocation));
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
	public MutableComponent getSourceName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("%s", material, "%s").
				withStyle(Style.EMPTY.withColor(tier.color()).withItalic(true));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		tooltipComponents.add(Component.translatable("item.jaopca.theurgy_alchemical_sulfurs.tooltip",
				getSourceName(stack),
				ComponentUtils.wrapInSquareBrackets(
						Component.translatable(tier.descriptionId()).
						withStyle(Style.EMPTY.withColor(tier.color).withItalic(true))),
				ComponentUtils.wrapInSquareBrackets(
						Component.translatable(type.descriptionId()).
						withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true)))).
				withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable("item.jaopca.theurgy_alchemical_sulfurs",
				ComponentUtils.wrapInSquareBrackets(getSourceName(stack)));
	}
}
