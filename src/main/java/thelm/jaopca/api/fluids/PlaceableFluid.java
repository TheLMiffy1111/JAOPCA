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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class PlaceableFluid extends Fluid {

	public static final float EIGHT_NINTHS = 8/9F;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(()->{
		Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<>(200) {
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
	protected Vec3 getFlow(BlockGetter world, BlockPos pos, FluidState state) {
		double x = 0;
		double y = 0;
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
		for(Direction offset : Direction.Plane.HORIZONTAL) {
			mutablePos.setWithOffset(pos, offset);
			FluidState offsetState = world.getFluidState(mutablePos);
			if(affectsFlow(offsetState)) {
				float offsetHeight = offsetState.getOwnHeight();
				float heightDiff = 0;
				if(offsetHeight == 0) {
					if(!world.getBlockState(mutablePos).getMaterial().blocksMotion()) {
						BlockPos posDown = mutablePos.below();
						FluidState belowState = world.getFluidState(posDown);
						if(affectsFlow(belowState)) {
							offsetHeight = belowState.getOwnHeight();
							if(offsetHeight > 0) {
								heightDiff = state.getOwnHeight() - offsetHeight + EIGHT_NINTHS;
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
		Vec3 flow = new Vec3(x, 0, y);
		if(state.getValue(levelProperty).intValue() == 0) {
			for(Direction offset : Direction.Plane.HORIZONTAL) {
				mutablePos.setWithOffset(pos, offset);
				if(isSolidFace(world, mutablePos, offset) || isSolidFace(world, mutablePos.above(), offset)) {
					flow = flow.normalize().add(0, -6, 0);
					break;
				}
			}
		}
		return flow.normalize();
	}

	private boolean affectsFlow(FluidState otherState) {
		return otherState.isEmpty() || otherState.getType().isSame(this);
	}

	protected boolean isSolidFace(BlockGetter world, BlockPos pos, Direction face) {
		BlockState blockState = world.getBlockState(pos);
		FluidState fluidState = world.getFluidState(pos);
		return !fluidState.getType().isSame(this) && (face == Direction.UP ||
				(blockState.getMaterial() != Material.ICE && blockState.isFaceSturdy(world, pos, face)));
	}

	protected void spread(LevelAccessor world, BlockPos pos, FluidState fluidState) {
		if(!fluidState.isEmpty()) {
			BlockState blockState = world.getBlockState(pos);
			BlockPos downPos = pos.below();
			BlockState downBlockState = world.getBlockState(downPos);
			FluidState newFluidState = getNewLiquid(world, downPos, downBlockState);
			if(canSpreadTo(world, pos, blockState, Direction.DOWN, downPos, downBlockState, world.getFluidState(downPos), newFluidState.getType())) {
				spreadTo(world, downPos, downBlockState, Direction.DOWN, newFluidState);
				if(sourceNeighborCount(world, pos) >= 3) {
					spreadToSides(world, pos, fluidState, blockState);
				}
			}
			else if(fluidState.isSource() || !isWaterHole(world, newFluidState.getType(), pos, blockState, downPos, downBlockState)) {
				spreadToSides(world, pos, fluidState, blockState);
			}
		}
	}

	protected void spreadToSides(LevelAccessor world, BlockPos pos, FluidState fluidState, BlockState blockState) {
		int i = fluidState.getAmount() - getDropOff(world);
		if(i > 0) {
			Map<Direction, FluidState> map = getSpread(world, pos, blockState);
			for(Map.Entry<Direction, FluidState> entry : map.entrySet()) {
				Direction direction = entry.getKey();
				FluidState offsetFluidState = entry.getValue();
				BlockPos offsetPos = pos.relative(direction);
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				if(canSpreadTo(world, pos, blockState, direction, offsetPos, offsetBlockState, world.getFluidState(offsetPos), offsetFluidState.getType())) {
					spreadTo(world, offsetPos, offsetBlockState, direction, offsetFluidState);
				}
			}
		}
	}

	protected FluidState getNewLiquid(LevelReader world, BlockPos pos, BlockState blockState) {
		int i = 0;
		int j = 0;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.relative(direction);
			BlockState offsetBlockState = world.getBlockState(offsetPos);
			FluidState offsetFluidState = offsetBlockState.getFluidState();
			if(offsetFluidState.getType().isSame(this) && canPassThroughWall(direction, world, pos, blockState, offsetPos, offsetBlockState)) {
				if(offsetFluidState.isSource() && ForgeEventFactory.canCreateFluidSource(world, pos, blockState, offsetFluidState.canConvertToSource(world, offsetPos))) {
					++j;
				}
				i = Math.max(i, offsetFluidState.getAmount());
			}
		}
		if(j >= 2) {
			BlockState blockstate1 = world.getBlockState(pos.below());
			FluidState FluidState1 = blockstate1.getFluidState();
			if(blockstate1.getMaterial().isSolid() || isSourceBlockOfThisType(FluidState1)) {
				return defaultFluidState().setValue(levelProperty, maxLevel);
			}
		}
		BlockPos upPos = pos.above();
		BlockState upBlockState = world.getBlockState(upPos);
		FluidState upFluidState = upBlockState.getFluidState();
		if(!upFluidState.isEmpty() && upFluidState.getType().isSame(this) && canPassThroughWall(Direction.UP, world, pos, blockState, upPos, upBlockState)) {
			return defaultFluidState().setValue(levelProperty, maxLevel+1);
		}
		else {
			int k = i - getDropOff(world);
			if(k <= 0) {
				return Fluids.EMPTY.defaultFluidState();
			}
			else {
				return defaultFluidState().setValue(levelProperty, k);
			}
		}
	}

	protected boolean canPassThroughWall(Direction direction, BlockGetter world, BlockPos fromPos, BlockState fromBlockState, BlockPos toPos, BlockState toBlockState) {
		Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> cache;
		if(!fromBlockState.getBlock().hasDynamicShape() && !toBlockState.getBlock().hasDynamicShape()) {
			cache = OCCLUSION_CACHE.get();
		}
		else {
			cache = null;
		}
		Block.BlockStatePairKey cacheKey;
		if(cache != null) {
			cacheKey = new Block.BlockStatePairKey(fromBlockState, toBlockState, direction);
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

	protected Map<Direction, FluidState> getSpread(LevelReader world, BlockPos pos, BlockState blockState) {
		int i = 1000;
		Map<Direction, FluidState> map = new EnumMap<>(Direction.class);
		Short2ObjectMap<Pair<BlockState, FluidState>> stateMap = new Short2ObjectOpenHashMap<>();
		Short2BooleanMap isWaterHoleMap = new Short2BooleanOpenHashMap();
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.relative(direction);
			short key = getCacheKey(pos, offsetPos);
			Pair<BlockState, FluidState> offsetState = stateMap.computeIfAbsent(key, k->{
				BlockState offsetBlockState = world.getBlockState(offsetPos);
				return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
			});
			BlockState offsetBlockState = offsetState.getLeft();
			FluidState offsetFluidState = offsetState.getRight();
			FluidState newOffsetFluidState = getNewLiquid(world, offsetPos, offsetBlockState);
			if(canFlowSource(world, newOffsetFluidState.getType(), pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidState)) {
				boolean flag = isWaterHoleMap.computeIfAbsent(key, k->{
					BlockPos offsetDownPos = offsetPos.below();
					BlockState offsetDownState = world.getBlockState(offsetDownPos);
					return isWaterHole(world, this, offsetPos, offsetBlockState, offsetDownPos, offsetDownState);
				});
				int j = 0;
				if(!flag) {
					j = getSlopeDistance(world, offsetPos, 1, direction.getOpposite(), offsetBlockState, pos, stateMap, isWaterHoleMap);
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

	protected int getSlopeDistance(LevelReader world, BlockPos pos, int distance, Direction fromDirection, BlockState blockState, BlockPos startPos, Short2ObjectMap<Pair<BlockState, FluidState>> stateMap, Short2BooleanMap isWaterHoleMap) {
		int i = 1000;
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			if(direction != fromDirection) {
				BlockPos offsetPos = pos.relative(direction);
				short key = getCacheKey(startPos, offsetPos);
				Pair<BlockState, FluidState> pair = stateMap.computeIfAbsent(key, k->{
					BlockState offsetBlockState = world.getBlockState(offsetPos);
					return Pair.of(offsetBlockState, offsetBlockState.getFluidState());
				});
				BlockState offsetBlockState = pair.getLeft();
				FluidState offsetFluidstate = pair.getRight();
				if(canFlowSource(world, this, pos, blockState, direction, offsetPos, offsetBlockState, offsetFluidstate)) {
					boolean flag = isWaterHoleMap.computeIfAbsent(key, k->{
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
			return ((LiquidBlockContainer)block).canPlaceLiquid(world, pos, blockState, fluid);
		}
		if(block instanceof DoorBlock || blockState.is(BlockTags.SIGNS) || block == Blocks.LADDER || block == Blocks.SUGAR_CANE ||
				block == Blocks.BUBBLE_COLUMN) {
			return false;
		}
		Material blockMaterial = blockState.getMaterial();
		return blockMaterial != Material.PORTAL && blockMaterial != Material.STRUCTURAL_AIR && blockMaterial != Material.WATER_PLANT
				&& blockMaterial != Material.REPLACEABLE_WATER_PLANT && !blockMaterial.blocksMotion();
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

	protected int getDelay(Level world, BlockPos pos, FluidState fluidState, FluidState newFluidState) {
		return getTickDelay(world);
	}

	@Override
	public void tick(Level world, BlockPos pos, FluidState fluidState) {
		if(!fluidState.isSource()) {
			FluidState newFluidState = getNewLiquid(world, pos, world.getBlockState(pos));
			int delay = getDelay(world, pos, fluidState, newFluidState);
			if(newFluidState.isEmpty()) {
				fluidState = newFluidState;
				world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
			else if(!newFluidState.equals(fluidState)) {
				fluidState = newFluidState;
				BlockState blockState = fluidState.createLegacyBlock();
				world.setBlock(pos, blockState, 2);
				world.scheduleTick(pos, fluidState.getType(), delay);
				world.updateNeighborsAt(pos, blockState.getBlock());
			}
		}
		spread(world, pos, fluidState);
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
}
