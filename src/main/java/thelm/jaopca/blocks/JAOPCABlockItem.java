package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeType;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlockItem extends BlockItem implements IMaterialFormBlockItem {

	protected final IBlockFormSettings settings;

	protected OptionalInt maxStackSize = OptionalInt.empty();
	protected Optional<Boolean> hasEffect = Optional.empty();
	protected Optional<Rarity> rarity = Optional.empty();
	protected OptionalInt burnTime = OptionalInt.empty();

	public JAOPCABlockItem(IMaterialFormBlock block, IBlockFormSettings settings) {
		super(block.toBlock(), new Item.Properties());
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

	@Override
	public int getMaxStackSize(ItemStack stack) {
		if(!maxStackSize.isPresent()) {
			maxStackSize = OptionalInt.of(settings.getMaxStackSizeFunction().applyAsInt(getMaterial()));
		}
		return maxStackSize.getAsInt();
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(getMaterial()));
		}
		return hasEffect.get() || super.isFoil(stack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(getMaterial()));
		}
		return rarity.get();
	}

	@Override
	public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
		if(!burnTime.isPresent()) {
			burnTime = OptionalInt.of(settings.getBurnTimeFunction().applyAsInt(getMaterial()));
		}
		return burnTime.getAsInt();
	}

	@Override
	public Component getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+getForm().getName(), getMaterial(), getDescriptionId(stack));
	}
}
