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
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
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

	protected final StateContainer<Fluid, FluidState> stateContainer;
	private final Map<FluidState, VoxelShape> shapeMap = new IdentityHashMap<>();

	protected final int maxLevel;
	protected final IntegerProperty levelProperty;

	public PlaceableFluid(int maxLevel) {
		this.maxLevel = maxLevel;
		levelProperty = IntegerProperty.create("level", 1, maxLevel+1);

		StateContainer.Builder<Fluid, FluidState> builder = new StateContainer.Builder<>(this);
		createFluidStateDefinition(builder);
		stateContainer = builder.create(Fluid::defaultFluidState, FluidState::new);
		registerDefaultState(stateContainer.any().setValue(levelProperty, maxLevel));
	}

	public IntegerProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateContainer<Fluid, FluidState> getStateDefinition() {
		return stateContainer;
	}

	protected abstract boolean canSourcesMultiply();

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, IBlockReader world, BlockPos pos, Fluid fluid, Direction face) {
		return face == Direction.DOWN && !isSame(fluid);
	}

	protected abstract int getLevelDecreasePerBlock(IWorldReader world);

	@Override
	protected BlockState createLegacyBlock(FluidState fluidState) {
		PlaceableFluidBlock block = getFluidBlock();
		IntegerProperty blockLevelProperty = block.getLevelProperty();
		int fluidLevel = fluidState.getValue(levelProperty);
		int blockLevel = fluidLevel > maxLevel ? maxLevel : maxLevel-fluidLevel;
		return block.defaultBlockState().setValue(blockLevelProperty, blockLevel);
	}

	protected abstract PlaceableFluidBlock getFluidBlock();

	/**
	 * getFlow
	 */
	@Override
	protected Vector3d getFlow(IBlockReader world, BlockPos pos, FluidState state) {
		double x = 0;
		double y = 0;
		BlockPos.Mutable mutablePos = new BlockPos.Mutable();
		for(Direction offset : Direction.Plane.HORIZONTAL) {
			mutablePos.setWithOffset(pos, offset);
			FluidState offsetState = world.getFluidState(mutablePos);
			if(isSameOrEmpty(offsetState)) {
				float offsetHeight = offsetState.getOwnHeight();
				float heightDiff = 0;
				if(offsetHeight == 0) {
					if(!world.getBlockState(mutablePos).getMaterial().blocksMotion()) {
						BlockPos posDown = mutablePos.below();
						FluidState belowState = world.getFluidState(posDown);
						if(isSameOrEmpty(belowState)) {
							offsetHeight = belowState.getOwnHeight();
							if(offsetHeight > 0) {
								heightDiff = state.getOwnHeight()-(offsetHeight-EIGHT_NINTHS);
							}
						}
					}
				}
				else if(offsetHeight > 0) {
					heightDiff = state.getOwnHeight() - offsetHeight;
				}
				if(heightDiff != 0) {
					x += offset.getStepX()*heightDiff;
					y += offset.getStepZ()*heightDiff;
				}
			}
		}
		Vector3d flow = new Vector3d(x, 0, y);
		if(state.getValue(levelProperty).intValue() == 0) {
			for(Direction offset : Direction.Plane.HORIZONTAL) {
				mutablePos.setWithOffset(pos, offset);
				if(causesDownwardCurrent(world, mutablePos, offset) || causesDownwardCurrent(world, mutablePos.above(), offset)) {
					flow = flow.normalize().add(0, -6, 0);
					break;
				}
			}
		}
		return flow.normalize();
	}

	private boolean isSameOrEmpty(FluidState otherState) {
		return otherState.isEmpty() || otherState.getType().isSame(this);
	}

	protected boolean causesDownwardCurrent(IBlockReader world, BlockPos pos, Direction face) {
		BlockState blockState = world.getBlockState(pos);
		FluidState fluidState = world.getFluidState(pos);
		return !fluidState.getType().isSame(this) && (face == Direction.UP ||
				(blockState.getMaterial() != Material.ICE && blockState.isFaceSturdy(world, pos, face)));
	}

	protected void flowAround(IWorld world, BlockPos pos, FluidState fluidState) {
		if(!fluidState.isEmpty()) {
			BlockState blockState = world.getBlockState(pos);
			BlockPos downPos = pos.below();
			BlockState downBlockState = world.getBlockState(downPos);
			FluidState newFluidState = calculateCorrectState(world, downPos, downBlockState);
			if(canFlow(world, pos, blockState, Direction.DOWN, downPos, downBlockState, world.getFluidState(downPos), newFluidState.getType())) {
				flowInto(world, downPos, downBlockState, Direction.DOWN, newFluidState);
				if(getAdjacentSourceCount(world, pos) >= 3) {
					flowAdjacent(world, pos, fluidState, blockState);
				}
			}
			else if(fluidState.isSource() || !canFlowDown(world, newFluidState.getType(), pos, blockState, downPos, downBlockState)) {
				flowAdjacent(world, pos, fluidState, blockState);
			}
		}
	}

	protected void flowAdjacent(IWorld world, BlockPos pos, FluidState fluidState, BlockState blockState) {
		int i = fluidState.getAmount() - getLevelDecreasePerBlock(world);
		if(i > 0) {
			Map<Direction, FluidState> map = calculateAdjacentStates(world, pos, blockState);
			for(Map.Entry<Direction, FluidState> entry : map.entrySet()) {
				Direction direction = entry.getKey();
				FluidState offsetFluidState = entry.getValue();
				BlockPos offsetPos = pos.relative(direction);
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				if(canFlow(world, pos, blockState, direction, offsetPos, offsetBlockState, world.getFluidState(offsetPos), offsetFluidState.getType())) {
					flowInto(world, offsetPos, offsetBlockState, direction, offsetFluidState);
				}
			}
		}
	}

	protected FluidState calculateCorrectState(IWorldReader world, BlockPos pos, BlockState blockState) {
		int i = 0;
		int j = 0;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.relative(direction);
			BlockState offsetBlockState = world.getBlockState(offsetPos);
			FluidState offsetFluidState = offsetBlockState.getFluidState();
			if(offsetFluidState.getType().isSame(this) && doShapesFillSquare(direction, world, pos, blockState, offsetPos, offsetBlockState)) {
				if(offsetFluidState.isSource()) {
					++j;
				}
				i = Math.max(i, offsetFluidState.getAmount());
			}
		}
		if(canSourcesMultiply() && j >= 2) {
			BlockState blockstate1 = world.getBlockState(pos.below());
			FluidState FluidState1 = blockstate1.getFluidState();
			if(blockstate1.getMaterial().isSolid() || isSameSource(FluidState1)) {
				return defaultFluidState().setValue(levelProperty, maxLevel);
			}
		}
		BlockPos upPos = pos.above();
		BlockState upBlockState = world.getBlockState(upPos);
		FluidState upFluidState = upBlockState.getFluidState();
		if(!upFluidState.isEmpty() && upFluidState.getType().isSame(this) && doShapesFillSquare(Direction.UP, world, pos, blockState, upPos, upBlockState)) {
			return defaultFluidState().setValue(levelProperty, maxLevel+1);
		}
		else {
			int k = i - getLevelDecreasePerBlock(world);
			if(k <= 0) {
				return Fluids.EMPTY.defaultFluidState();
			}
			else {
				return defaultFluidState().setValue(levelProperty, k);
			}
		}
	}

	protected boolean doShapesFillSquare(Direction direction, IBlockReader world, BlockPos fromPos, BlockState fromBlockState, BlockPos toPos, BlockState toBlockState) {
		Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> cache;
		if(!fromBlockState.getBlock().hasDynamicShape() && !toBlockState.getBlock().hasDynamicShape()) {
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
		boolean flag = !VoxelShapes.mergedFaceOccludes(fromShape, toShape, direction);
		if(cache != null) {
			if(cache.size() == 200) {
				cache.removeLastByte();
			}
			cache.putAndMoveToFirst(cacheKey, (byte)(flag ? 1 : 0));
		}
		return flag;
	}

	protected void flowInto(IWorld world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
		if(blockState.getBlock() instanceof ILiquidContainer) {
			((ILiquidContainer)blockState.getBlock()).placeLiquid(world, pos, blockState, fluidState);
		}
		else {
			if(!blockState.isAir(world, pos)) {
				beforeReplacingBlock(world, pos, blockState);
			}
			world.setBlock(pos, fluidState.createLegacyBlock(), 3);
		}
	}

	protected void beforeReplacingBlock(IWorld world, BlockPos pos, BlockState blockState) {
		TileEntity tile = blockState.hasTileEntity() ? world.getBlockEntity(pos) : null;
		Block.dropResources(blockState, world, pos, tile);
	}

	protected static short getPosKey(BlockPos pos, BlockPos otherPos) {
		int dx = otherPos.getX() - pos.getX();
		int dz = otherPos.getZ() - pos.getZ();
		return (short)((dx + 128 & 255) << 8 | dz + 128 & 255);
	}

	protected Map<Direction, FluidState> calculateAdjacentStates(IWorldReader world, BlockPos pos, BlockState blockState) {
		int i = 1000;
		Map<Direction, FluidState> map = new EnumMap<>(Direction.class);
		Short2ObjectMap<Pair<BlockState, FluidState>> stateMap = new Short2ObjectOpenHashMap<>();
		Short2BooleanMap canFlowDownMap = new Short2BooleanOpenHashMap();
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.relative(direction);
			short key = getPosKey(pos, offsetPos);
			Pair<BlockState, FluidState> offsetState = stateMap.computeIfAbsent(key, k->{
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
			});
			BlockState offsetBlockState = offsetState.getLeft();
			FluidState offsetFluidState = offsetState.getRight();
			FluidState newOffsetFluidState = calculateCorrectState(world, offsetPos, offsetBlockState);
			if(canFlowSource(world, newOffsetFluidState.getType(), pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidState)) {
				boolean flag = canFlowDownMap.computeIfAbsent(key, k->{
					BlockPos offsetDownPos = offsetPos.below();
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

	protected int getFlowDistance(IWorldReader world, BlockPos pos, int distance, Direction fromDirection, BlockState blockState, BlockPos startPos, Short2ObjectMap<Pair<BlockState, FluidState>> stateMap, Short2BooleanMap canFlowDownMap) {
		int i = 1000;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			if(direction != fromDirection) {
				BlockPos offsetPos = pos.relative(direction);
				short key = getPosKey(startPos, offsetPos);
				Pair<BlockState, FluidState> pair = stateMap.computeIfAbsent(key, k->{
					BlockState offsetBlockState = world.getBlockState(offsetPos);
					return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
				});
				BlockState offsetBlockState = pair.getLeft();
				FluidState offsetFluidstate = pair.getRight();
				if(canFlowSource(world, this, pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidstate)) {
					boolean flag = canFlowDownMap.computeIfAbsent(key, k->{
						BlockPos offsetDownPos = offsetPos.below();
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

	protected boolean isSameSource(FluidState fluidState) {
		return fluidState.getType().isSame(this) && fluidState.isSource();
	}

	protected int getSlopeFindDistance(IWorldReader world) {
		return ceilDiv(ceilDiv(maxLevel, getLevelDecreasePerBlock(world)), 2);
	}

	protected int getAdjacentSourceCount(IWorldReader world, BlockPos pos) {
		int count = 0;
		for(Direction offset : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.relative(offset);
			FluidState offsetState = world.getFluidState(offsetPos);
			if(isSameSource(offsetState)) {
				++count;
			}
		}
		return count;
	}

	protected boolean canFlowIntoBlock(IBlockReader world, BlockPos pos, BlockState blockState, Fluid fluid) {
		Block block = blockState.getBlock();
		if(block instanceof ILiquidContainer) {
			return ((ILiquidContainer)block).canPlaceLiquid(world, pos, blockState, fluid);
		}
		if(block instanceof DoorBlock || block.is(BlockTags.SIGNS) || block == Blocks.LADDER || block == Blocks.SUGAR_CANE ||
				block == Blocks.BUBBLE_COLUMN) {
			return false;
		}
		Material blockMaterial = blockState.getMaterial();
		return blockMaterial != Material.PORTAL && blockMaterial != Material.STRUCTURAL_AIR && blockMaterial != Material.WATER_PLANT
				&& blockMaterial != Material.REPLACEABLE_WATER_PLANT && !blockMaterial.blocksMotion();
	}

	protected boolean canFlow(IBlockReader world, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid) {
		return toFluidState.canBeReplacedWith(world, toPos, fluid, direction) && this.doShapesFillSquare(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canFlowIntoBlock(world, toPos, toBlockState, fluid);
	}

	protected boolean canFlowSource(IBlockReader world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState) {
		return !isSameSource(toFluidState) && doShapesFillSquare(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canFlowIntoBlock(world, toPos, toBlockState, fluid);
	}

	protected boolean canFlowDown(IBlockReader world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, BlockPos downPos, BlockState downState) {
		return doShapesFillSquare(Direction.DOWN, world, fromPos, fromBlockState, downPos, downState)
				&& (downState.getFluidState().getType().isSame(this) || canFlowIntoBlock(world, downPos, downState, fluid));
	}

	protected int getDelay(World world, BlockPos pos, FluidState fluidState, FluidState newFluidState) {
		return getTickDelay(world);
	}

	@Override
	public void tick(World world, BlockPos pos, FluidState fluidState) {
		if(!fluidState.isSource()) {
			FluidState newFluidState = calculateCorrectState(world, pos, world.getBlockState(pos));
			int delay = getDelay(world, pos, fluidState, newFluidState);
			if(newFluidState.isEmpty()) {
				fluidState = newFluidState;
				world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
			else if(!newFluidState.equals(fluidState)) {
				fluidState = newFluidState;
				BlockState blockState = fluidState.createLegacyBlock();
				world.setBlock(pos, blockState, 2);
				world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), delay);
				world.updateNeighborsAt(pos, blockState.getBlock());
			}
		}
		flowAround(world, pos, fluidState);
	}

	protected int getBlockLevelFromState(FluidState fluidState) {
		int level = fluidState.getValue(levelProperty);
		if(level > maxLevel) {
			return maxLevel;
		}
		return maxLevel - Math.min(fluidState.getAmount(), maxLevel);
	}

	protected static boolean isFluidAboveSame(FluidState fluidState, IBlockReader world, BlockPos pos) {
		return fluidState.getType().isSame(world.getFluidState(pos.above()).getType());
	}

	@Override
	public float getHeight(FluidState fluidState, IBlockReader world, BlockPos pos) {
		return isFluidAboveSame(fluidState, world, pos) ? 1 : fluidState.getOwnHeight();
	}

	@Override
	public float getOwnHeight(FluidState fluidState) {
		return 0.9F*fluidState.getAmount()/maxLevel;
	}

	@Override
	public boolean isSource(FluidState fluidState) {
		return fluidState.getValue(levelProperty).intValue() == maxLevel;
	}

	@Override
	public int getAmount(FluidState fluidState) {
		return Math.min(maxLevel, fluidState.getValue(levelProperty));
	}

	@Override
	public VoxelShape getShape(FluidState fluidState, IBlockReader world, BlockPos pos) {
		return shapeMap.computeIfAbsent(fluidState, s->VoxelShapes.box(0, 0, 0, 1, s.getHeight(world, pos), 1));
	}

	public static int ceilDiv(int x, int y) {
		int r = x/y;
		if((x^y) >= 0 && (r*y != x)) {
			r++;
		}
		return r;
	}
}
