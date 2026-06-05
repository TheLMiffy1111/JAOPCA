package thelm.jaopca.api.fluids;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;

public abstract class PlaceableFluidBlock extends Block implements BucketPickup {

	protected final StateDefinition<Block, BlockState> stateDefinition;

	protected final PlaceableFluid fluid;
	protected final int maxLevel;
	protected final IntegerProperty levelProperty;

	public PlaceableFluidBlock(Block.Properties properties, PlaceableFluid fluid, int maxLevel) {
		super(properties);

		this.fluid = fluid;
		this.maxLevel = maxLevel;
		levelProperty = IntegerProperty.create("level", 0, maxLevel);

		StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
		createBlockStateDefinition(builder);
		stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
		registerDefaultState(stateDefinition.any().setValue(levelProperty, maxLevel));
	}

	public IntegerProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	public boolean isRandomlyTicking(BlockState blockState) {
		return blockState.getFluidState().isRandomlyTicking();
	}

	@Override
	public void randomTick(BlockState blockState, ServerLevel world, BlockPos pos, RandomSource random) {
		world.getFluidState(pos).randomTick(world, pos, random);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return false;
	}

	@Override
	public boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return !fluid.is(FluidTags.LAVA);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		IntegerProperty fluidLevelProperty = fluid.getLevelProperty();
		int blockLevel = blockState.getValue(levelProperty);
		int fluidLevel = blockLevel >= maxLevel ? maxLevel+1 : maxLevel-blockLevel;
		return fluid.defaultFluidState().setValue(fluidLevelProperty, fluidLevel);
	}

	@Override
	public boolean skipRendering(BlockState blockState, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getFluidState().getType().isSame(fluid);
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return List.of();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public void onPlace(BlockState blockState, Level world, BlockPos pos, BlockState oldBlockState, boolean isMoving) {
		if(!FluidInteractionRegistry.canInteract(world, pos)) {
			world.scheduleTick(pos, blockState.getFluidState().getType(), fluid.getTickDelay(world));
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		if(state.getFluidState().isSource() || neighbourState.getFluidState().isSource()) {
			ticks.scheduleTick(pos, neighbourState.getFluidState().getType(), fluid.getTickDelay(level));
		}
		return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston) {
		if(!FluidInteractionRegistry.canInteract(level, pos)) {
			level.scheduleTick(pos, state.getFluidState().getType(), fluid.getTickDelay(level));
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateDefinition<Block, BlockState> getStateDefinition() {
		return stateDefinition;
	}

	@Override
	public ItemStack pickupBlock(LivingEntity user, LevelAccessor level, BlockPos pos, BlockState blockState) {
		if(blockState.getValue(levelProperty) == 0) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
			return new ItemStack(fluid.getBucket());
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return fluid.getPickupSound();
	}
}
