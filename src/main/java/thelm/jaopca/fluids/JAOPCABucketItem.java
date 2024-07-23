package thelm.jaopca.fluids;

import java.util.function.BooleanSupplier;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABucketItem extends Item implements IMaterialFormBucketItem, DispensibleContainerItem {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	protected BooleanSupplier hasEffect;

	public JAOPCABucketItem(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(getProperties(fluid, settings));
		this.fluid = fluid;
		this.settings = settings;

		hasEffect = MemoizingSuppliers.of(settings.getHasEffectFunction(), fluid::getMaterial);
	}

	public static Item.Properties getProperties(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		Item.Properties prop = new Item.Properties();
		prop.stacksTo(settings.getMaxStackSizeFunction().applyAsInt(fluid.getMaterial()));
		prop.rarity(settings.getDisplayRarityFunction().apply(fluid.getMaterial()));
		prop.craftRemainder(Items.BUCKET);
		return prop;
	}

	@Override
	public IForm getForm() {
		return fluid.getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return fluid.getMaterial();
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return hasEffect.getAsBoolean() || super.isFoil(stack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult blockHitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
		if(blockHitResult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(stack);
		}
		else if(blockHitResult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(stack);
		}
		else {
			BlockPos resultPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getDirection();
			BlockPos offsetPos = resultPos.relative(blockHitResult.getDirection());
			if(world.mayInteract(player, resultPos) && player.mayUseItemAt(offsetPos, direction, stack)) {
				BlockState state = world.getBlockState(resultPos);
				BlockPos placePos = canBlockContainFluid(player, world, resultPos, state) ? resultPos : offsetPos;
				if(emptyContents(player, world, placePos, blockHitResult)) {
					checkExtraContent(player, world, stack, placePos);
					if(player instanceof ServerPlayer) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, placePos, stack);
					}
					player.awardStat(Stats.ITEM_USED.get(this));
					return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(stack, player), world.isClientSide);
				}
				else {
					return InteractionResultHolder.fail(stack);
				}
			}
			else {
				return InteractionResultHolder.fail(stack);
			}
		}
	}

	protected ItemStack getEmptySuccessItem(ItemStack stack, Player player) {
		return !player.getAbilities().instabuild ? new ItemStack(Items.BUCKET) : stack;
	}

	@Override
	public void checkExtraContent(Player player, Level world, ItemStack stack, BlockPos pos) {}

	@Override
	public boolean emptyContents(Player player, Level world, BlockPos pos, BlockHitResult blockHitResult) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		boolean flag = blockState.canBeReplaced(fluid.toFluid());
		boolean flag1 = blockState.isAir() || flag || (block instanceof LiquidBlockContainer container
				&& container.canPlaceLiquid(player, world, pos, blockState, fluid.toFluid()));
		if(!flag1) {
			return blockHitResult != null && emptyContents(player, world, blockHitResult.getBlockPos().relative(blockHitResult.getDirection()), null);
		}
		FluidStack stack = new FluidStack(fluid.toFluid(), FluidType.BUCKET_VOLUME);
		if(fluid.toFluid().getFluidType().isVaporizedOnPlacement(world, pos, stack)) {
			fluid.toFluid().getFluidType().onVaporize(player, world, pos, stack);
			return true;
		}
		if(block instanceof LiquidBlockContainer container && container.canPlaceLiquid(player, world, pos, blockState, fluid.toFluid())) {
			container.placeLiquid(world, pos, blockState, fluid.toFluid().defaultFluidState());
			playEmptySound(player, world, pos);
			return true;
		}
		if(!world.isClientSide && flag && !blockState.liquid()) {
			world.destroyBlock(pos, true);
		}
		if(!world.setBlock(pos, fluid.toFluid().getFluidType().getStateForPlacement(world, pos, stack).createLegacyBlock(), 11) && !blockState.getFluidState().isSource()) {
			return false;
		}
		playEmptySound(player, world, pos);
		return true;
	}

	protected void playEmptySound(Player player, LevelAccessor world, BlockPos pos) {
		SoundEvent soundEvent = fluid.toFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY);
		if(soundEvent == null) {
			soundEvent = fluid.toFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		}
		world.playSound(player, pos, soundEvent, SoundSource.BLOCKS, 1, 1);
		world.gameEvent(player, GameEvent.FLUID_PLACE, pos);
	}

	protected boolean canBlockContainFluid(Player player, Level worldIn, BlockPos posIn, BlockState blockstate) {
		return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)blockstate.getBlock()).canPlaceLiquid(player, worldIn, posIn, blockstate, fluid.toFluid());
	}

	@Override
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context)->new JAOPCAFluidHandlerItem(fluid, stack), this);
	}

	@Override
	public Component getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+getForm().getName(), getMaterial(), getDescriptionId(stack));
	}
}
