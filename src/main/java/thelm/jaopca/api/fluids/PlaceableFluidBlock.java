package thelm.jaopca.api.fluids;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class PlaceableFluidBlock extends Block implements IBucketPickupHandler {

	protected final StateContainer<Block, BlockState> stateContainer;

	protected final PlaceableFluid fluid;
	protected final int maxLevel;
	protected final IntegerProperty levelProperty;

	public PlaceableFluidBlock(Block.Properties properties, PlaceableFluid fluid, int maxLevel) {
		super(properties);

		this.fluid = fluid;
		this.maxLevel = maxLevel;
		levelProperty = IntegerProperty.create("level", 0, maxLevel);

		StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
		createBlockStateDefinition(builder);
		stateContainer = builder.create(Block::defaultBlockState, BlockState::new);
		registerDefaultState(stateContainer.any().setValue(levelProperty, maxLevel));
	}

	public IntegerProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	public void randomTick(BlockState blockState, ServerWorld world, BlockPos pos, Random random) {
		world.getFluidState(pos).randomTick(world, pos, random);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockState, IBlockReader reader, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isPathfindable(BlockState blockState, IBlockReader world, BlockPos pos, PathType type) {
		return !fluid.is(FluidTags.LAVA);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		IntegerProperty fluidLevelProperty = fluid.getLevelProperty();
		int blockLevel = blockState.getValue(levelProperty);
		int fluidLevel = blockLevel >= maxLevel ? maxLevel+1 : maxLevel-blockLevel;
		return fluid.defaultFluidState().setValue(fluidLevelProperty, fluidLevel);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean skipRendering(BlockState blockState, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getFluidState().getType().isSame(fluid);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState blockState) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public void onPlace(BlockState blockState, World world, BlockPos pos, BlockState oldBlockState, boolean isMoving) {
		if(reactWithNeighbors(world, pos, blockState)) {
			world.getLiquidTicks().scheduleTick(pos, blockState.getFluidState().getType(), fluid.getTickDelay(world));
		}
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if(blockState.getFluidState().isSource() || facingState.getFluidState().isSource()) {
			world.getLiquidTicks().scheduleTick(currentPos, blockState.getFluidState().getType(), fluid.getTickDelay(world));
		}
		return super.updateShape(blockState, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void neighborChanged(BlockState blockState, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if(reactWithNeighbors(world, pos, blockState)) {
			world.getLiquidTicks().scheduleTick(pos, blockState.getFluidState().getType(), fluid.getTickDelay(world));
		}
	}

	public boolean reactWithNeighbors(World world, BlockPos pos, BlockState blockState) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateContainer<Block, BlockState> getStateDefinition() {
		return stateContainer;
	}

	@Override
	public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState blockState) {
		if(blockState.getValue(levelProperty) == 0) {
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
			return fluid;
		}
		else {
			return Fluids.EMPTY;
		}
	}
}
