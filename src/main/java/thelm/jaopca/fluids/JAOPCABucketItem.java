package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABucketItem extends Item implements IMaterialFormBucketItem {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	private OptionalInt itemStackLimit = OptionalInt.empty();
	private Optional<Boolean> hasEffect = Optional.empty();
	private Optional<Rarity> rarity = Optional.empty();
	private OptionalInt burnTime = OptionalInt.empty();

	public JAOPCABucketItem(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(new Item.Properties().craftRemainder(Items.BUCKET).tab(ItemFormType.getItemGroup()));
		this.fluid = fluid;
		this.settings = settings;
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
	public int getItemStackLimit(ItemStack stack) {
		if(!itemStackLimit.isPresent()) {
			itemStackLimit = OptionalInt.of(settings.getItemStackLimitFunction().applyAsInt(getMaterial()));
		}
		return itemStackLimit.getAsInt();
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
	public int getBurnTime(ItemStack itemStack) {
		if(!burnTime.isPresent()) {
			burnTime = OptionalInt.of(settings.getBurnTimeFunction().applyAsInt(getMaterial()));
		}
		return burnTime.getAsInt();
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		RayTraceResult rayTraceResult = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
		ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, stack, rayTraceResult);
		if(ret != null) {
			return ret;
		}
		if(rayTraceResult.getType() == RayTraceResult.Type.MISS) {
			return new ActionResult<>(ActionResultType.PASS, stack);
		}
		else if(rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
			return new ActionResult<>(ActionResultType.PASS, stack);
		}
		else {
			BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
			BlockPos resultPos = blockRayTraceResult.getBlockPos();
			if(world.mayInteract(player, resultPos) && player.mayUseItemAt(resultPos, blockRayTraceResult.getDirection(), stack)) {
				BlockPos pos = blockRayTraceResult.getBlockPos().relative(blockRayTraceResult.getDirection());
				if(tryPlaceContainedLiquid(player, world, pos, blockRayTraceResult)) {
					onLiquidPlaced(world, stack, pos);
					if(player instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
					}
					player.awardStat(Stats.ITEM_USED.get(this));
					return new ActionResult<>(ActionResultType.SUCCESS, emptyBucket(stack, player));
				}
				else {
					return new ActionResult<>(ActionResultType.FAIL, stack);
				}
			}
			else {
				return new ActionResult<>(ActionResultType.FAIL, stack);
			}
		}
	}

	protected ItemStack emptyBucket(ItemStack stack, PlayerEntity player) {
		return !player.abilities.instabuild ? new ItemStack(Items.BUCKET) : stack;
	}

	public void onLiquidPlaced(World world, ItemStack stack, BlockPos pos) {}

	public boolean tryPlaceContainedLiquid(PlayerEntity player, World world, BlockPos pos, BlockRayTraceResult rayTraceResult) {
		BlockState blockState = world.getBlockState(pos);
		Material blockMaterial = blockState.getMaterial();
		boolean flag = !blockMaterial.isSolid();
		boolean flag1 = blockMaterial.isReplaceable();
		if(world.isEmptyBlock(pos) || flag || flag1) {
			FluidStack stack = new FluidStack(fluid.toFluid(), FluidAttributes.BUCKET_VOLUME);
			if(world.dimensionType().ultraWarm() && fluid.toFluid().is(FluidTags.WATER)) {
				int i = pos.getX();
				int j = pos.getY();
				int k = pos.getZ();
				world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F+(world.random.nextFloat()-world.random.nextFloat())*0.8F);
				for(int l = 0; l < 8; ++l) {
					world.addParticle(ParticleTypes.LARGE_SMOKE, i+Math.random(), j+Math.random(), k+Math.random(), 0, 0, 0);
				}
			}
			else {
				if(!world.isClientSide && (flag || flag1) && !blockMaterial.isLiquid()) {
					world.destroyBlock(pos, true);
				}
				playEmptySound(player, world, pos);
				world.setBlock(pos, fluid.toFluid().getAttributes().getStateForPlacement(world, pos, stack).createLegacyBlock(), 11);
			}
			return true;
		}
		else {
			return rayTraceResult == null ? false : tryPlaceContainedLiquid(player, world, rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection()), null);
		}
	}

	protected void playEmptySound(PlayerEntity player, IWorld world, BlockPos pos) {
		SoundEvent soundEvent = fluid.toFluid().getAttributes().getEmptySound();
		if(soundEvent == null) {
			soundEvent = fluid.toFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		}
		world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1, 1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new JAOPCAFluidHandlerItem(fluid, stack);
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+getForm().getName(), getMaterial(), getDescriptionId(stack));
	}
}
