package thelm.jaopca.fluids;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
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
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluidBlock extends Block implements IMaterialFormFluidBlock {

	private final IMaterialFormFluid fluid;
	protected final IFluidFormSettings settings;

	protected final StateContainer<Block, BlockState> stateContainer;

	protected final int maxLevel;
	protected final IntegerProperty levelProperty;

	protected Optional<Material> blockMaterial = Optional.empty();
	protected Optional<MaterialColor> materialColor = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();

	public JAOPCAFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(Block.Properties.create(Material.WATER).lightValue(settings.getLightValueFunction().applyAsInt(fluid.getMaterial())).
				doesNotBlockMovement().tickRandomly().noDrops());
		this.fluid = fluid;
		this.settings = settings;

		maxLevel = settings.getMaxLevelFunction().applyAsInt(getMaterial());
		levelProperty = IntegerProperty.create("level", 0, maxLevel);

		StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
		fillStateContainer(builder);
		stateContainer = builder.create(BlockState::new);
		setDefaultState(stateContainer.getBaseState());
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
	public IntegerProperty getLevelProperty() {
		return levelProperty;
	}

	@Override
	public Material getMaterial(BlockState blockState) {
		if(!blockMaterial.isPresent()) {
			blockMaterial = Optional.of(settings.getMaterialFunction().apply(getMaterial()));
		}
		return blockMaterial.get();
	}

	@Override
	public MaterialColor getMaterialColor(BlockState blockState, IBlockReader world, BlockPos pos) {
		if(!materialColor.isPresent()) {
			materialColor = Optional.of(settings.getMaterialColorFunction().apply(getMaterial()));
		}
		return materialColor.get();
	}

	@Override
	public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(getMaterial()));
		}
		return lightValue.getAsInt();
	}

	@Override
	public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
		if(!blockHardness.isPresent()) {
			blockHardness = OptionalDouble.of(settings.getBlockHardnessFunction().applyAsDouble(getMaterial()));
		}
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getExplosionResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(getMaterial()));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public int getFlammability(BlockState blockState, IBlockReader world, BlockPos pos, Direction face) {
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.getFireSpreadSpeedFunction().applyAsInt(getMaterial()));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(BlockState blockState, IBlockReader world, BlockPos pos, Direction face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.getFlammabilityFunction().applyAsInt(getMaterial()));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(getMaterial()));
		}
		return isFireSource.get();
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
		return !fluid.asFluid().isIn(FluidTags.LAVA);
	}

	@Override
	public IFluidState getFluidState(BlockState blockState) {
		IntegerProperty fluidLevelProperty = fluid.getLevelProperty();
		int blockLevel = blockState.get(levelProperty);
		int fluidLevel = blockLevel >= maxLevel ? maxLevel+1 : maxLevel-blockLevel;
		return fluid.asFluid().getDefaultState().with(fluidLevelProperty, fluidLevel);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getFluidState().getFluid().isEquivalentTo(fluid.asFluid()) || super.isSolid(blockState);
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
		return fluid.asFluid().getTickRate(world);
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
			return fluid.asFluid();
		}
		else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
		if(fluid.asFluid().isIn(FluidTags.LAVA)) {
			entity.setInLava();
		}
	}
}
