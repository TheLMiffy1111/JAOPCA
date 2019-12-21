package thelm.jaopca.api.fluids;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
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
		fillStateContainer(builder);
		stateContainer = builder.create(BlockState::new);
		setDefaultState(stateContainer.getBaseState());
	}

	public IntegerProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	public void randomTick(BlockState blockState, World world, BlockPos pos, Random random) {
		world.getFluidState(pos).randomTick(world, pos, random);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockState, IBlockReader reader, BlockPos pos) {
		return false;
	}

	@Override
	public boolean allowsMovement(BlockState blockState, IBlockReader world, BlockPos pos, PathType type) {
		return !fluid.isIn(FluidTags.LAVA);
	}

	@Override
	public IFluidState getFluidState(BlockState blockState) {
		IntegerProperty fluidLevelProperty = fluid.getLevelProperty();
		int blockLevel = blockState.get(levelProperty);
		int fluidLevel = blockLevel >= maxLevel ? maxLevel+1 : maxLevel-blockLevel;
		return fluid.getDefaultState().with(fluidLevelProperty, fluidLevel);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getFluidState().getFluid().isEquivalentTo(fluid) || super.isSolid(blockState);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
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
	public int tickRate(IWorldReader world) {
		return fluid.getTickRate(world);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos pos, BlockState oldBlockState, boolean isMoving) {
		if(reactWithNeighbors(world, pos, blockState)) {
			world.getPendingFluidTicks().scheduleTick(pos, blockState.getFluidState().getFluid(), tickRate(world));
		}
	}

	@Override
	public BlockState updatePostPlacement(BlockState blockState, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if(blockState.getFluidState().isSource() || facingState.getFluidState().isSource()) {
			world.getPendingFluidTicks().scheduleTick(currentPos, blockState.getFluidState().getFluid(), tickRate(world));
		}
		return super.updatePostPlacement(blockState, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void neighborChanged(BlockState blockState, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if(reactWithNeighbors(world, pos, blockState)) {
			world.getPendingFluidTicks().scheduleTick(pos, blockState.getFluidState().getFluid(), tickRate(world));
		}
	}

	public boolean reactWithNeighbors(World world, BlockPos pos, BlockState blockState) {
		return true;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateContainer<Block, BlockState> getStateContainer() {
		return stateContainer;
	}

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState blockState) {
		if(blockState.get(levelProperty) == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			return fluid;
		}
		else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
		if(fluid.isIn(FluidTags.LAVA)) {
			entity.setInLava();
		}
	}
}
