package thelm.jaopca.api.fluids;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.event.EventHooks;

public abstract class PlaceableFluid extends Fluid {

	public static final float EIGHT_NINTHS = 8/9F;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(()->{
		Object2ByteLinkedOpenHashMap<BlockStatePairKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<>(200) {
			@Override
			protected void rehash(int newN) {}
		};
		object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
		return object2bytelinkedopenhashmap;
	});

	protected final StateDefinition<Fluid, FluidState> stateDefinition;
	private final Map<FluidState, VoxelShape> shapeMap = new IdentityHashMap<>();

	protected final int maxLevel;
	protected final IntegerProperty levelProperty;

	public PlaceableFluid(int maxLevel) {
		this.maxLevel = maxLevel;
		levelProperty = IntegerProperty.create("level", 1, maxLevel+1);

		StateDefinition.Builder<Fluid, FluidState> builder = new StateDefinition.Builder<>(this);
		createFluidStateDefinition(builder);
		stateDefinition = builder.create(Fluid::defaultFluidState, FluidState::new);
		registerDefaultState(stateDefinition.any().setValue(levelProperty, maxLevel));
	}

	public IntegerProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
		if(levelProperty != null) {
			builder.add(levelProperty);
		}
	}

	@Override
	public StateDefinition<Fluid, FluidState> getStateDefinition() {
		return stateDefinition;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter world, BlockPos pos, Fluid fluid, Direction face) {
		return face == Direction.DOWN && !isSame(fluid);
	}

	protected abstract int getDropOff(LevelReader world);

	@Override
	protected BlockState createLegacyBlock(FluidState fluidState) {
		PlaceableFluidBlock block = getFluidBlock();
		IntegerProperty blockLevelProperty = block.getLevelProperty();
		int fluidLevel = fluidState.getValue(levelProperty);
		int blockLevel = fluidLevel > maxLevel ? maxLevel : maxLevel-fluidLevel;
		return block.defaultBlockState().setValue(blockLevelProperty, blockLevel);
	}

	protected abstract PlaceableFluidBlock getFluidBlock();

	@Override
	public Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState fluidState) {
		double x = 0;
		double y = 0;
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		for(Direction offset : Direction.Plane.HORIZONTAL) {
			blockPos.setWithOffset(pos, offset);
			FluidState neighbourFluid = level.getFluidState(blockPos);
			if(affectsFlow(neighbourFluid)) {
				float neighborHeight = neighbourFluid.getOwnHeight();
				float distance = 0;
				if(neighborHeight == 0) {
					if(!level.getBlockState(blockPos).blocksMotion()) {
						BlockPos neighborPos = blockPos.below();
						FluidState belowNeighborState = level.getFluidState(neighborPos);
						if(affectsFlow(belowNeighborState)) {
							neighborHeight = belowNeighborState.getOwnHeight();
							if(neighborHeight > 0) {
								distance = fluidState.getOwnHeight() - neighborHeight + EIGHT_NINTHS;
							}
						}
					}
				}
				else if(neighborHeight > 0) {
					distance = fluidState.getOwnHeight() - neighborHeight;
				}
				if(distance != 0) {
					x += offset.getStepX()*distance;
					y += offset.getStepZ()*distance;
				}
			}
		}
		Vec3 flow = new Vec3(x, 0, y);
		if(fluidState.getValue(levelProperty).intValue() == 0) {
			for(Direction offset : Direction.Plane.HORIZONTAL) {
				blockPos.setWithOffset(pos, offset);
				if(isSolidFace(level, blockPos, offset) || isSolidFace(level, blockPos.above(), offset)) {
					flow = flow.normalize().add(0, -6, 0);
					break;
				}
			}
		}
		return flow.normalize();
	}

	private boolean affectsFlow(FluidState neighbourFluid) {
		return neighbourFluid.isEmpty() || neighbourFluid.getType().isSame(this);
	}

	protected boolean isSolidFace(BlockGetter level, BlockPos pos, Direction direction) {
		BlockState state = level.getBlockState(pos);
		FluidState fluidState = level.getFluidState(pos);
		return !fluidState.getType().isSame(this) && (direction == Direction.UP ||
				(!(state.getBlock() instanceof IceBlock) && state.isFaceSturdy(level, pos, direction)));
	}

	protected void spread(ServerLevel level, BlockPos pos, BlockState state, FluidState fluidState) {
		if(!fluidState.isEmpty()) {
			BlockState blockState = level.getBlockState(pos);
			BlockPos belowPos = pos.below();
			BlockState belowState = level.getBlockState(belowPos);
			FluidState newBelowFluid = getNewLiquid(level, belowPos, belowState);
			if(canSpreadTo(level, pos, blockState, Direction.DOWN, belowPos, belowState, level.getFluidState(belowPos), newBelowFluid.getType())) {
				spreadTo(level, belowPos, belowState, Direction.DOWN, newBelowFluid);
				if(sourceNeighborCount(level, pos) >= 3) {
					spreadToSides(level, pos, fluidState, blockState);
				}
			}
			else if(fluidState.isSource() || !isWaterHole(level, newBelowFluid.getType(), pos, blockState, belowPos, belowState)) {
				spreadToSides(level, pos, fluidState, blockState);
			}
		}
	}

	protected void spreadToSides(ServerLevel level, BlockPos pos, FluidState fluidState, BlockState state) {
		int neighbor = fluidState.getAmount() - getDropOff(level);
		if(neighbor > 0) {
			Map<Direction, FluidState> spreads = getSpread(level, pos, state);
			for(Map.Entry<Direction, FluidState> entry : spreads.entrySet()) {
				Direction direction = entry.getKey();
				FluidState newNeighborFluid = entry.getValue();
				BlockPos neighborPos = pos.relative(direction);
				BlockState neighborState = level.getBlockState(neighborPos);
				if(canSpreadTo(level, pos, state, direction, neighborPos, neighborState, level.getFluidState(neighborPos), newNeighborFluid.getType())) {
					spreadTo(level, neighborPos, neighborState, direction, newNeighborFluid);
				}
			}
		}
	}

	protected FluidState getNewLiquid(ServerLevel level, BlockPos pos, BlockState state) {
		int highestNeighbor = 0;
		int neighbourSources = 0;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos relativePos = pos.relative(direction);
			BlockState blockState = level.getBlockState(relativePos);
			FluidState fluidState = blockState.getFluidState();
			if(fluidState.getType().isSame(this) && canPassThroughWall(direction, level, pos, state, relativePos, blockState)) {
				if(fluidState.isSource() && EventHooks.canCreateFluidSource(level, relativePos, blockState)) {// 172
					++neighbourSources;
				}
				highestNeighbor = Math.max(highestNeighbor, fluidState.getAmount());
			}
		}
		if(neighbourSources >= 2) {
			BlockState belowState = level.getBlockState(pos.below());
			FluidState belowFluid = belowState.getFluidState();
			if(belowState.isSolid() || isSourceBlockOfThisType(belowFluid)) {
				return defaultFluidState().setValue(levelProperty, maxLevel);
			}
		}
		BlockPos upPos = pos.above();
		BlockState upBlockState = level.getBlockState(upPos);
		FluidState upFluidState = upBlockState.getFluidState();
		if(!upFluidState.isEmpty() && upFluidState.getType().isSame(this) && canPassThroughWall(Direction.UP, level, pos, state, upPos, upBlockState)) {
			return defaultFluidState().setValue(levelProperty, maxLevel+1);
		}
		else {
			int k = highestNeighbor - getDropOff(level);
			if(k <= 0) {
				return Fluids.EMPTY.defaultFluidState();
			}
			else {
				return defaultFluidState().setValue(levelProperty, k);
			}
		}
	}

	protected boolean canPassThroughWall(Direction direction, BlockGetter world, BlockPos fromPos, BlockState fromBlockState, BlockPos toPos, BlockState toBlockState) {
		Object2ByteLinkedOpenHashMap<BlockStatePairKey> cache;
		if(!fromBlockState.getBlock().hasDynamicShape() && !toBlockState.getBlock().hasDynamicShape()) {
			cache = OCCLUSION_CACHE.get();
		}
		else {
			cache = null;
		}
		BlockStatePairKey cacheKey;
		if(cache != null) {
			cacheKey = new BlockStatePairKey(fromBlockState, toBlockState, direction);
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
		boolean flag = !Shapes.mergedFaceOccludes(fromShape, toShape, direction);
		if(cache != null) {
			if(cache.size() == 200) {
				cache.removeLastByte();
			}
			cache.putAndMoveToFirst(cacheKey, (byte)(flag ? 1 : 0));
		}
		return flag;
	}

	protected void spreadTo(LevelAccessor world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
		if(blockState.getBlock() instanceof LiquidBlockContainer) {
			((LiquidBlockContainer)blockState.getBlock()).placeLiquid(world, pos, blockState, fluidState);
		}
		else {
			if(!blockState.isAir()) {
				beforeDestroyingBlock(world, pos, blockState);
			}
			world.setBlock(pos, fluidState.createLegacyBlock(), 3);
		}
	}

	protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState blockState) {
		BlockEntity tile = blockState.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropResources(blockState, world, pos, tile);
	}

	protected static short getCacheKey(BlockPos pos, BlockPos otherPos) {
		int dx = otherPos.getX() - pos.getX();
		int dz = otherPos.getZ() - pos.getZ();
		return (short)((dx + 128 & 255) << 8 | dz + 128 & 255);
	}

	protected Map<Direction, FluidState> getSpread(ServerLevel level, BlockPos pos, BlockState blockState) {
		int i = 1000;
		Map<Direction, FluidState> result = new EnumMap<>(Direction.class);
		Short2ObjectMap<Pair<BlockState, FluidState>> stateMap = new Short2ObjectOpenHashMap<>();
		Short2BooleanMap isWaterHoleMap = new Short2BooleanOpenHashMap();
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos testPos = pos.relative(direction);
			short key = getCacheKey(pos, testPos);
			Pair<BlockState, FluidState> offsetState = stateMap.computeIfAbsent(key, _->{
				BlockState offsetBlockState = level.getBlockState(testPos);
				return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
			});
			BlockState testState = offsetState.getLeft();
			FluidState testFluidState = offsetState.getRight();
			FluidState newFluid = getNewLiquid(level, testPos, testState);
			if(canFlowSource(level, newFluid.getType(), pos, blockState, direction, testPos, testState, testFluidState)) {
				boolean flag = isWaterHoleMap.computeIfAbsent(key, _->{
					BlockPos offsetDownPos = testPos.below();
					BlockState offsetDownState = level.getBlockState(offsetDownPos);
					return isWaterHole(level, this, testPos, testState, offsetDownPos, offsetDownState);
				});
				int j = 0;
				if(!flag) {
					j = getSlopeDistance(level, testPos, 1, direction.getOpposite(), testState, pos, stateMap, isWaterHoleMap);
				}
				if(j < i) {
					result.clear();
				}
				if(j <= i) {
					result.put(direction, newFluid);
					i = j;
				}
			}
		}
		return result;
	}

	protected int getSlopeDistance(LevelReader world, BlockPos pos, int distance, Direction fromDirection, BlockState blockState, BlockPos startPos, Short2ObjectMap<Pair<BlockState, FluidState>> stateMap, Short2BooleanMap isWaterHoleMap) {
		int i = 1000;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			if(direction != fromDirection) {
				BlockPos offsetPos = pos.relative(direction);
				short key = getCacheKey(startPos, offsetPos);
				Pair<BlockState, FluidState> pair = stateMap.computeIfAbsent(key, _->{
					BlockState offsetBlockState = world.getBlockState(offsetPos);
					return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
				});
				BlockState offsetBlockState = pair.getLeft();
				FluidState offsetFluidstate = pair.getRight();
				if(canFlowSource(world, this, pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidstate)) {
					boolean flag = isWaterHoleMap.computeIfAbsent(key, _->{
						BlockPos offsetDownPos = offsetPos.below();
						BlockState offsetDownState = world.getBlockState(offsetDownPos);
						return isWaterHole(world, this, offsetPos, offsetBlockState, offsetDownPos, offsetDownState);
					});
					if(flag) {
						return distance;
					}
					if(distance < getSlopeFindDistance(world)) {
						int j = getSlopeDistance(world, offsetPos, distance+1, direction.getOpposite(), offsetBlockState, startPos, stateMap, isWaterHoleMap);
						if(j < i) {
							i = j;
						}
					}
				}
			}
		}
		return i;
	}

	protected boolean isSourceBlockOfThisType(FluidState fluidState) {
		return fluidState.getType().isSame(this) && fluidState.isSource();
	}

	protected int getSlopeFindDistance(LevelReader world) {
		return ceilDiv(ceilDiv(maxLevel, getDropOff(world)), 2);
	}

	protected int sourceNeighborCount(LevelReader world, BlockPos pos) {
		int count = 0;
		for(Direction offset : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.relative(offset);
			FluidState offsetState = world.getFluidState(offsetPos);
			if(isSourceBlockOfThisType(offsetState)) {
				++count;
			}
		}
		return count;
	}

	protected boolean canHoldFluid(BlockGetter world, BlockPos pos, BlockState blockState, Fluid fluid) {
		Block block = blockState.getBlock();
		if(block instanceof LiquidBlockContainer) {
			return ((LiquidBlockContainer)block).canPlaceLiquid(null, world, pos, blockState, fluid);
		}
		if(block instanceof DoorBlock || blockState.is(BlockTags.SIGNS) || block == Blocks.LADDER || block == Blocks.SUGAR_CANE || block == Blocks.BUBBLE_COLUMN) {
			return false;
		}
		return !blockState.is(Blocks.NETHER_PORTAL) && !blockState.is(Blocks.END_PORTAL) && !blockState.is(Blocks.END_GATEWAY) && !blockState.is(Blocks.STRUCTURE_VOID)
				&& !blockState.blocksMotion();
	}

	protected boolean canSpreadTo(BlockGetter world, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid) {
		return toFluidState.canBeReplacedWith(world, toPos, fluid, direction) && canPassThroughWall(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canHoldFluid(world, toPos, toBlockState, fluid);
	}

	protected boolean canFlowSource(BlockGetter world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState) {
		return !isSourceBlockOfThisType(toFluidState) && canPassThroughWall(direction, world, fromPos, fromBlockState, toPos, toBlockState) && canHoldFluid(world, toPos, toBlockState, fluid);
	}

	protected boolean isWaterHole(BlockGetter world, Fluid fluid, BlockPos fromPos, BlockState fromBlockState, BlockPos downPos, BlockState downState) {
		return canPassThroughWall(Direction.DOWN, world, fromPos, fromBlockState, downPos, downState)
				&& (downState.getFluidState().getType().isSame(this) || canHoldFluid(world, downPos, downState, fluid));
	}

	protected int getSpreadDelay(Level world, BlockPos pos, FluidState fluidState, FluidState newFluidState) {
		return getTickDelay(world);
	}

	@Override
	public void tick(ServerLevel level, BlockPos pos, BlockState blockState, FluidState fluidState) {
		if(!fluidState.isSource()) {
			FluidState newFluidState = getNewLiquid(level, pos, level.getBlockState(pos));
			int delay = getSpreadDelay(level, pos, fluidState, newFluidState);
			if(newFluidState.isEmpty()) {
				fluidState = newFluidState;
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
			else if(!newFluidState.equals(fluidState)) {
				fluidState = newFluidState;
				blockState = fluidState.createLegacyBlock();
				level.setBlock(pos, blockState, 2);
				level.scheduleTick(pos, fluidState.getType(), delay);
				level.updateNeighborsAt(pos, blockState.getBlock());
			}
		}
		spread(level, pos, blockState, fluidState);
	}

	protected int getLegacyLevel(FluidState fluidState) {
		int level = fluidState.getValue(levelProperty);
		if(level > maxLevel) {
			return maxLevel;
		}
		return maxLevel - Math.min(fluidState.getAmount(), maxLevel);
	}

	protected static boolean hasSameAbove(FluidState fluidState, BlockGetter world, BlockPos pos) {
		return fluidState.getType().isSame(world.getFluidState(pos.above()).getType());
	}

	@Override
	public float getHeight(FluidState fluidState, BlockGetter world, BlockPos pos) {
		return hasSameAbove(fluidState, world, pos) ? 1 : fluidState.getOwnHeight();
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
	public VoxelShape getShape(FluidState fluidState, BlockGetter world, BlockPos pos) {
		return shapeMap.computeIfAbsent(fluidState, s->Shapes.box(0, 0, 0, 1, s.getHeight(world, pos), 1));
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return Optional.ofNullable(getFluidType().getSound(SoundActions.BUCKET_FILL));
	}

	public static int ceilDiv(int x, int y) {
		int r = x/y;
		if((x^y) >= 0 && (r*y != x)) {
			r++;
		}
		return r;
	}

	private record BlockStatePairKey(BlockState first, BlockState second, Direction direction) {
	}
}
