package thelm.jaopca.api.fluids;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class PlaceableFluid extends Fluid {

	public static final float EIGHT_NINTHS = 8/9F;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> CACHE = ThreadLocal.withInitial(()->{
		Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
			@Override
			protected void rehash(int newN) {}
		};
		object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
		return object2bytelinkedopenhashmap;
	});

	protected final StateContainer<Fluid, IFluidState> stateContainer;
	private final Map<IFluidState, VoxelShape> shapeMap = new IdentityHashMap<>();

	protected final int maxLevel;
	protected final IntegerProperty levelProperty;

	public PlaceableFluid(int maxLevel) {
		this.maxLevel = maxLevel;
		levelProperty = IntegerProperty.create("level", 1, maxLevel+1);

		StateContainer.Builder<Fluid, IFluidState> builder = new StateContainer.Builder<>(this);
		fillStateContainer(builder);
		stateContainer = builder.create(FluidState::new);
		setDefaultState(stateContainer.getBaseState().with(levelProperty, maxLevel));
	}

	public IntegerProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	protected void fillStateContainer(Builder<Fluid, IFluidState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateContainer<Fluid, IFluidState> getStateContainer() {
		return stateContainer;
	}

	protected abstract boolean canSourcesMultiply();

	@Override
	protected boolean canDisplace(IFluidState fluidState, IBlockReader world, BlockPos pos, Fluid fluid, Direction face) {
		return face == Direction.DOWN && !isEquivalentTo(fluid);
	}

	protected abstract int getLevelDecreasePerBlock(IWorldReader world);

	@Override
	protected BlockState getBlockState(IFluidState fluidState) {
		PlaceableFluidBlock block = getFluidBlock();
		IntegerProperty blockLevelProperty = block.getLevelProperty();
		int fluidLevel = fluidState.get(levelProperty);
		int blockLevel = fluidLevel > maxLevel ? maxLevel : maxLevel-fluidLevel;
		return block.getDefaultState().with(blockLevelProperty, blockLevel);
	}

	protected abstract PlaceableFluidBlock getFluidBlock();

	@Override
	protected Vec3d getFlow(IBlockReader world, BlockPos pos, IFluidState state) {
		double x = 0;
		double y = 0;
		try(BlockPos.PooledMutableBlockPos mutablePos = BlockPos.PooledMutableBlockPos.retain()) {
			for(Direction offset : Direction.Plane.HORIZONTAL) {
				mutablePos.setPos(pos).move(offset);
				IFluidState offsetState = world.getFluidState(mutablePos);
				if(!isSameOrEmpty(offsetState)) {
					continue;
				}
				float offsetHeight = offsetState.getHeight();
				float heightDiff = 0;
				if(offsetHeight == 0) {
					if(!world.getBlockState(mutablePos).getMaterial().blocksMovement()) {
						BlockPos posDown = mutablePos.down();
						final IFluidState belowState = world.getFluidState(posDown);
						if(isSameOrEmpty(belowState)) {
							offsetHeight = belowState.getHeight();
							if(offsetHeight > 0) {
								heightDiff = state.getHeight()-(offsetHeight-EIGHT_NINTHS);
							}
						}
					}
				}
				else if(offsetHeight > 0) {
					heightDiff = state.getHeight() - offsetHeight;
				}
				if(heightDiff == 0) {
					continue;
				}
				x += offset.getXOffset()*heightDiff;
				y += offset.getZOffset()*heightDiff;
			}
			Vec3d flow = new Vec3d(x, 0, y);
			if(state.get(levelProperty).intValue() == 0) {
				for(Direction offset : Direction.Plane.HORIZONTAL) {
					mutablePos.setPos(pos).move(offset);
					if(causesDownwardCurrent(world, mutablePos, offset) || causesDownwardCurrent(world, mutablePos.up(), offset)) {
						flow = flow.normalize().add(0, -6, 0);
						break;
					}
				}
			}
			return flow.normalize();
		}
	}
	private boolean isSameOrEmpty(IFluidState otherState) {
		return otherState.isEmpty() || otherState.getFluid().isEquivalentTo(this);
	}

	protected boolean causesDownwardCurrent(IBlockReader world, BlockPos pos, Direction face) {
		BlockState blockState = world.getBlockState(pos);
		IFluidState fluidState = world.getFluidState(pos);
		return !fluidState.getFluid().isEquivalentTo(this) && (face == Direction.UP ||
				(blockState.getMaterial() != Material.ICE && blockState.func_224755_d(world, pos, face)));
	}

	protected void flowAround(IWorld world, BlockPos pos, IFluidState fluidState) {
		if(!fluidState.isEmpty()) {
			BlockState blockState = world.getBlockState(pos);
			BlockPos downPos = pos.down();
			BlockState downBlockState = world.getBlockState(downPos);
			IFluidState newFluidState = calculateCorrectState(world, downPos, downBlockState);
			if(canFlow(world, pos, blockState, Direction.DOWN, downPos, downBlockState, world.getFluidState(downPos), newFluidState.getFluid())) {
				flowInto(world, downPos, downBlockState, Direction.DOWN, newFluidState);
				if(getAdjacentSourceCount(world, pos) >= 3) {
					flowAdjacent(world, pos, fluidState, blockState);
				}
			}
			else if(fluidState.isSource() || !canFlowDown(world, newFluidState.getFluid(), pos, blockState, downPos, downBlockState)) {
				flowAdjacent(world, pos, fluidState, blockState);
			}
		}
	}

	protected void flowAdjacent(IWorld world, BlockPos pos, IFluidState fluidState, BlockState blockState) {
		int i = fluidState.getLevel() - getLevelDecreasePerBlock(world);
		if(i > 0) {
			Map<Direction, IFluidState> map = calculateAdjacentStates(world, pos, blockState);
			for(Map.Entry<Direction, IFluidState> entry : map.entrySet()) {
				Direction direction = entry.getKey();
				IFluidState offsetFluidState = entry.getValue();
				BlockPos offsetPos = pos.offset(direction);
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				if(canFlow(world, pos, blockState, direction, offsetPos, offsetBlockState, world.getFluidState(offsetPos), offsetFluidState.getFluid())) {
					flowInto(world, offsetPos, offsetBlockState, direction, offsetFluidState);
				}
			}
		}
	}

	protected IFluidState calculateCorrectState(IWorldReader world, BlockPos pos, BlockState blockState) {
		int i = 0;
		int j = 0;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(direction);
			BlockState offsetBlockState = world.getBlockState(offsetPos);
			IFluidState offsetFluidState = offsetBlockState.getFluidState();
			if(offsetFluidState.getFluid().isEquivalentTo(this) && doShapesFillSquare(direction, world, pos, blockState, offsetPos, offsetBlockState)) {
				if(offsetFluidState.isSource()) {
					++j;
				}
				i = Math.max(i, offsetFluidState.getLevel());
			}
		}
		if(canSourcesMultiply() && j >= 2) {
			BlockState blockstate1 = world.getBlockState(pos.down());
			IFluidState ifluidstate1 = blockstate1.getFluidState();
			if(blockstate1.getMaterial().isSolid() || isSameSource(ifluidstate1)) {
				return getDefaultState().with(levelProperty, maxLevel);
			}
		}
		BlockPos upPos = pos.up();
		BlockState upBlockState = world.getBlockState(upPos);
		IFluidState upFluidState = upBlockState.getFluidState();
		if(!upFluidState.isEmpty() && upFluidState.getFluid().isEquivalentTo(this) && doShapesFillSquare(Direction.UP, world, pos, blockState, upPos, upBlockState)) {
			return getDefaultState().with(levelProperty, maxLevel+1);
		}
		else {
			int k = i - getLevelDecreasePerBlock(world);
			if(k <= 0) {
				return Fluids.EMPTY.getDefaultState();
			}
			else {
				return getDefaultState().with(levelProperty, k);
			}
		}
	}

	protected boolean doShapesFillSquare(Direction direction, IBlockReader world, BlockPos fromPos, BlockState fromBlockState, BlockPos toPos, BlockState toBlockState) {
		Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> cache;
		if(!fromBlockState.getBlock().isVariableOpacity() && !toBlockState.getBlock().isVariableOpacity()) {
			cache = CACHE.get();
		}
		else {
			cache = null;
		}
		Block.RenderSideCacheKey cacheKey;
		if(cache != null) {
			cacheKey = new Block.RenderSideCacheKey(fromBlockState, toBlockState, direction);
			byte b0 = cache.getAndMoveToFirst(cacheKey);
			if(b0 != 127) {
				return b0 != 0;
			}
		}
		else {
			cacheKey = null;
		}
		VoxelShape fromShape = fromBlockState.getCollisionShape(world, fromPos);
		VoxelShape toShape = toBlockState.getCollisionShape(world, toPos);
		boolean flag = !VoxelShapes.doAdjacentCubeSidesFillSquare(fromShape, toShape, direction);
		if(cache != null) {
			if(cache.size() == 200) {
				cache.removeLastByte();
			}
			cache.putAndMoveToFirst(cacheKey, (byte)(flag ? 1 : 0));
		}
		return flag;
	}

	protected void flowInto(IWorld world, BlockPos pos, BlockState blockState, Direction direction, IFluidState fluidState) {
		if(blockState.getBlock() instanceof ILiquidContainer) {
			((ILiquidContainer)blockState.getBlock()).receiveFluid(world, pos, blockState, fluidState);
		}
		else {
			if(!blockState.isAir(world, pos)) {
				beforeReplacingBlock(world, pos, blockState);
			}
			world.setBlockState(pos, fluidState.getBlockState(), 3);
		}
	}

	protected void beforeReplacingBlock(IWorld world, BlockPos pos, BlockState blockState) {
		TileEntity tile = blockState.hasTileEntity() ? world.getTileEntity(pos) : null;
		Block.spawnDrops(blockState, world.getWorld(), pos, tile);
	}

	protected static short getPosKey(BlockPos pos, BlockPos otherPos) {
		int dx = otherPos.getX() - pos.getX();
		int dz = otherPos.getZ() - pos.getZ();
		return (short)((dx + 128 & 255) << 8 | dz + 128 & 255);
	}

	protected Map<Direction, IFluidState> calculateAdjacentStates(IWorldReader world, BlockPos pos, BlockState blockState) {
		int i = 1000;
		Map<Direction, IFluidState> map = new EnumMap(Direction.class);
		Short2ObjectMap<Pair<BlockState, IFluidState>> stateMap = new Short2ObjectOpenHashMap();
		Short2BooleanMap canFlowDownMap = new Short2BooleanOpenHashMap();
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(direction);
			short key = getPosKey(pos, offsetPos);
			Pair<BlockState, IFluidState> offsetState = stateMap.computeIfAbsent(key, k->{
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
			});
			BlockState offsetBlockState = offsetState.getLeft();
			IFluidState offsetFluidState = offsetState.getRight();
			IFluidState newOffsetFluidState = calculateCorrectState(world, offsetPos, offsetBlockState);
			if(canFlowSource(world, newOffsetFluidState.getFluid(), pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidState)) {
				boolean flag = canFlowDownMap.computeIfAbsent(key, k->{
					BlockPos offsetDownPos = offsetPos.down();
					BlockState offsetDownState = world.getBlockState(offsetDownPos);
					return canFlowDown(world, this, offsetPos, offsetBlockState, offsetDownPos, offsetDownState);
				});
				int j = 0;
				if(!flag) {
					j = getFlowDistance(world, offsetPos, 1, direction.getOpposite(), offsetBlockState, pos, stateMap, canFlowDownMap);
				}
				if(j < i) {
					map.clear();
				}
				if(j <= i) {
					map.put(direction, newOffsetFluidState);
					i = j;
				}
			}
		}
		return map;
	}

	protected int getFlowDistance(IWorldReader world, BlockPos pos, int distance, Direction fromDirection, BlockState blockState, BlockPos startPos, Short2ObjectMap<Pair<BlockState, IFluidState>> stateMap, Short2BooleanMap canFlowDownMap) {
		int i = 1000;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			if(direction != fromDirection) {
				BlockPos offsetPos = pos.offset(direction);
				short key = getPosKey(startPos, offsetPos);
				Pair<BlockState, IFluidState> pair = stateMap.computeIfAbsent(key, k->{
					BlockState offsetBlockState = world.getBlockState(offsetPos);
					return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
				});
				BlockState offsetBlockState = pair.getLeft();
				IFluidState offsetFluidstate = pair.getRight();
				if(canFlowSource(world, this, pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidstate)) {
					boolean flag = canFlowDownMap.computeIfAbsent(key, k->{
						BlockPos offsetDownPos = offsetPos.down();
						BlockState offsetDownState = world.getBlockState(offsetDownPos);
						return canFlowDown(world, this, offsetPos, offsetBlockState, offsetDownPos, offsetDownState);
					});
					if(flag) {
						return distance;
					}
					if(distance < getSlopeFindDistance(world)) {
						int j = getFlowDistance(world, offsetPos, distance+1, direction.getOpposite(), offsetBlockState, startPos, stateMap, canFlowDownMap);
						if(j < i) {
							i = j;
						}
					}
				}
			}
		}
		return i;
	}

	protected boolean isSameSource(IFluidState fluidState) {
		return fluidState.getFluid().isEquivalentTo(this) && fluidState.isSource();
	}

	protected int getSlopeFindDistance(IWorldReader world) {
		return ceilDiv(ceilDiv(maxLevel, getLevelDecreasePerBlock(world)), 2);
	}

	protected int getAdjacentSourceCount(IWorldReader world, BlockPos pos) {
		int count = 0;
		for(Direction offset : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(offset);
			IFluidState offsetState = world.getFluidState(offsetPos);
			if(isSameSource(offsetState)) {
				++count;
			}
		}
		return count;
	}

	protected boolean canFlowIntoBlock(IBlockReader world, BlockPos pos, BlockState blockState, Fluid fluid) {
		Block block = blockState.getBlock();
		if(block instanceof ILiquidContainer) {
			return ((ILiquidContainer)block).canContainFluid(world, pos, blockState, fluid);
		}
		if(block instanceof DoorBlock || block.isIn(BlockTags.SIGNS) || block == Blocks.LADDER || block == Blocks.SUGAR_CANE ||
				block == Blocks.BUBBLE_COLUMN) {
			return false;
		}
		Material blockMaterial = blockState.getMaterial();
		return blockMaterial != Material.PORTAL && blockMaterial != Material.STRUCTURE_VOID && blockMaterial != Material.OCEAN_PLANT
				&& blockMaterial != Material.SEA_GRASS && !blockMaterial.blocksMovement();
	}

	protected boolean canFlow(IBlockReader world, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, IFluidState toFluidState, Fluid fluid) {
		return toFluidState.canDisplace(world, toPos, fluid, direction) && this.doShapesFillSquare(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canFlowIntoBlock(world, toPos, toBlockState, fluid);
	}

	protected boolean canFlowSource(IBlockReader world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, IFluidState toFluidState) {
		return !isSameSource(toFluidState) && doShapesFillSquare(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canFlowIntoBlock(world, toPos, toBlockState, fluid);
	}

	protected boolean canFlowDown(IBlockReader world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, BlockPos downPos, BlockState downState) {
		return doShapesFillSquare(Direction.DOWN, world, fromPos, fromBlockState, downPos, downState)
				&& (downState.getFluidState().getFluid().isEquivalentTo(this) || canFlowIntoBlock(world, downPos, downState, fluid));
	}

	protected int getDelay(World world, BlockPos pos, IFluidState fluidState, IFluidState newFluidState) {
		return getTickRate(world);
	}

	@Override
	public void tick(World world, BlockPos pos, IFluidState fluidState) {
		if(!fluidState.isSource()) {
			IFluidState newFluidState = calculateCorrectState(world, pos, world.getBlockState(pos));
			int delay = getDelay(world, pos, fluidState, newFluidState);
			if(newFluidState.isEmpty()) {
				fluidState = newFluidState;
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			}
			else if(!newFluidState.equals(fluidState)) {
				fluidState = newFluidState;
				BlockState blockState = fluidState.getBlockState();
				world.setBlockState(pos, blockState, 2);
				world.getPendingFluidTicks().scheduleTick(pos, fluidState.getFluid(), delay);
				world.notifyNeighborsOfStateChange(pos, blockState.getBlock());
			}
		}
		flowAround(world, pos, fluidState);
	}

	protected int getBlockLevelFromState(IFluidState fluidState) {
		int level = fluidState.get(levelProperty);
		if(level > maxLevel) {
			return maxLevel;
		}
		return maxLevel - Math.min(fluidState.getLevel(), maxLevel);
	}

	protected static boolean isFluidAboveSame(IFluidState fluidState, IBlockReader world, BlockPos pos) {
		return fluidState.getFluid().isEquivalentTo(world.getFluidState(pos.up()).getFluid());
	}

	/**
	 * getFluidHeight
	 */
	@Override
	public float getActualHeight(IFluidState fluidState, IBlockReader world, BlockPos pos) {
		return fluidState.getHeight();
	}

	/**
	 * getFluidHeight
	 */
	@Override
	public float getHeight(IFluidState fluidState) {
		return 0.9F*fluidState.getLevel()/maxLevel;
	}

	@Override
	public boolean isSource(IFluidState fluidState) {
		return fluidState.get(levelProperty).intValue() == maxLevel;
	}

	@Override
	public int getLevel(IFluidState fluidState) {
		return Math.min(maxLevel, fluidState.get(levelProperty));
	}

	@Override
	public VoxelShape func_215664_b(IFluidState fluidState, IBlockReader world, BlockPos pos) {
		return shapeMap.computeIfAbsent(fluidState, s->VoxelShapes.create(0, 0, 0, 1, s.getActualHeight(world, pos), 1));
	}

	public static int ceilDiv(int x, int y) {
		int r = x/y;
		if((x^y) >= 0 && (r*y != x)) {
			r++;
		}
		return r;
	}
}
