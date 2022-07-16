package thelm.jaopca.api.fluids;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidInteractionRegistry;

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
	public boolean propagatesSkylightDown(BlockState blockState, BlockGetter reader, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isPathfindable(BlockState blockState, BlockGetter world, BlockPos pos, PathComputationType type) {
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
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Collections.emptyList();
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
	public BlockState updateShape(BlockState blockState, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if(blockState.getFluidState().isSource() || facingState.getFluidState().isSource()) {
			world.scheduleTick(currentPos, blockState.getFluidState().getType(), fluid.getTickDelay(world));
		}
		return super.updateShape(blockState, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void neighborChanged(BlockState blockState, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if(!FluidInteractionRegistry.canInteract(world, pos)) {
			world.scheduleTick(pos, blockState.getFluidState().getType(), fluid.getTickDelay(world));
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
	public ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState blockState) {
		if(blockState.getValue(levelProperty) == 0) {
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
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
