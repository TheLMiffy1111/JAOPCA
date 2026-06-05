package thelm.jaopca.blocks;

import java.util.function.BooleanSupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlockItem extends BlockItem implements IMaterialFormBlockItem {

	protected final IBlockFormSettings settings;

	protected BooleanSupplier hasEffect;

	public JAOPCABlockItem(IMaterialFormBlock block, IBlockFormSettings settings, Identifier registryName) {
		super(block.toBlock(), getProperties(block, settings, registryName));
		this.settings = settings;

		hasEffect = MemoizingSuppliers.of(settings.getHasEffectFunction(), block::getMaterial);
	}

	public static Item.Properties getProperties(IMaterialFormBlock block, IBlockFormSettings settings, Identifier registryName) {
		Item.Properties prop = new Item.Properties();
		prop.setId(ResourceKey.create(Registries.ITEM, registryName));
		prop.stacksTo(settings.getMaxStackSizeFunction().applyAsInt(block.getMaterial()));
		prop.rarity(settings.getDisplayRarityFunction().apply(block.getMaterial()));
		return prop;
	}

	@Override
	public IForm getForm() {
		return ((IMaterialForm)getBlock()).getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return ((IMaterialForm)getBlock()).getMaterial();
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return hasEffect.getAsBoolean() || super.isFoil(stack);
	}

	@Override
	public Component getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+getForm().getName(), getMaterial(), getDescriptionId());
	}
}
