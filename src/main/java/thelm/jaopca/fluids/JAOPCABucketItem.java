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
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.items.ItemFormType;

public class JAOPCABucketItem extends Item implements IMaterialFormBucketItem {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	private OptionalInt itemStackLimit = OptionalInt.empty();
	private Optional<Boolean> beaconPayment = Optional.empty();
	private Optional<Boolean> hasEffect = Optional.empty();
	private Optional<Rarity> rarity = Optional.empty();
	private OptionalInt burnTime = OptionalInt.empty();

	public JAOPCABucketItem(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(new Item.Properties().containerItem(Items.BUCKET).group(ItemFormType.getItemGroup()));
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
	public boolean isBeaconPayment(ItemStack stack) {
		if(!beaconPayment.isPresent()) {
			beaconPayment = Optional.of(settings.getIsBeaconPaymentFunction().test(getMaterial()));
		}
		return beaconPayment.get();
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(getMaterial()));
		}
		return hasEffect.get();
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
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		RayTraceResult rayTraceResult = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
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
			BlockPos resultPos = blockRayTraceResult.getPos();
			if(world.isBlockModifiable(player, resultPos) && player.canPlayerEdit(resultPos, blockRayTraceResult.getFace(), stack)) {
				BlockPos pos = blockRayTraceResult.getPos().offset(blockRayTraceResult.getFace());
				if(tryPlaceContainedLiquid(player, world, pos, blockRayTraceResult)) {
					onLiquidPlaced(world, stack, pos);
					if(player instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
					}
					player.addStat(Stats.ITEM_USED.get(this));
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
		return !player.abilities.isCreativeMode ? new ItemStack(Items.BUCKET) : stack;
	}

	public void onLiquidPlaced(World world, ItemStack stack, BlockPos pos) {}

	public boolean tryPlaceContainedLiquid(PlayerEntity player, World world, BlockPos pos, BlockRayTraceResult rayTraceResult) {
		BlockState blockState = world.getBlockState(pos);
		Material blockMaterial = blockState.getMaterial();
		boolean flag = !blockMaterial.isSolid();
		boolean flag1 = blockMaterial.isReplaceable();
		if(world.isAirBlock(pos) || flag || flag1) {
			FluidStack stack = new FluidStack(fluid.asFluid(), FluidAttributes.BUCKET_VOLUME);
			if(world.dimension.doesWaterVaporize() && fluid.asFluid().isIn(FluidTags.WATER)) {
				int i = pos.getX();
				int j = pos.getY();
				int k = pos.getZ();
				world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F+(world.rand.nextFloat()-world.rand.nextFloat())*0.8F);
				for(int l = 0; l < 8; ++l) {
					world.addOptionalParticle(ParticleTypes.LARGE_SMOKE, i+Math.random(), j+Math.random(), k+Math.random(), 0, 0, 0);
				}
			}
			else {
				if(!world.isRemote && (flag || flag1) && !blockMaterial.isLiquid()) {
					world.destroyBlock(pos, true);
				}
				playEmptySound(player, world, pos);
				world.setBlockState(pos, fluid.asFluid().getAttributes().getStateForPlacement(world, pos, stack).getBlockState(), 11);
			}
			return true;
		}
		else {
			return rayTraceResult == null ? false : tryPlaceContainedLiquid(player, world, rayTraceResult.getPos().offset(rayTraceResult.getFace()), null);
		}
	}

	protected void playEmptySound(PlayerEntity player, IWorld world, BlockPos pos) {
		SoundEvent soundEvent = fluid.asFluid().getAttributes().getEmptySound();
		if(soundEvent == null) {
			soundEvent = fluid.asFluid().isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
		}
		world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1, 1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new JAOPCAFluidHandlerItem(fluid, stack);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return JAOPCAApi.instance().currentLocalizer().localizeMaterialForm("item.jaopca."+getForm().getName(), getMaterial(), getTranslationKey());
	}
}
