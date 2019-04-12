package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public class BlockJAOPCA extends BlockMaterialForm {

	private final IForm form;
	private final IMaterial material;
	private final Supplier<IBlockFormSettings> settings;

	public BlockJAOPCA(IForm form, IMaterial material, Supplier<IBlockFormSettings> settings) {
		super(Block.Properties.create(Material.IRON));
		this.form = form;
		this.material = material;
		this.settings = settings;
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	private Optional<Material> blockMaterial = Optional.empty();
	private Optional<MaterialColor> materialColor = Optional.empty();
	private Optional<Boolean> blocksMovement = Optional.empty();
	private Optional<SoundType> soundType = Optional.empty();
	private OptionalInt lightValue = OptionalInt.empty();
	private OptionalDouble blockHardness = OptionalDouble.empty();
	private OptionalDouble explosionResistance = OptionalDouble.empty();
	private OptionalDouble slipperiness = OptionalDouble.empty();
	private Optional<Boolean> isFull = Optional.empty();
	private Optional<VoxelShape> shape = Optional.empty();
	private Optional<VoxelShape> raytraceShape = Optional.empty();
	private Optional<BlockRenderLayer> renderLayer = Optional.empty();
	private Optional<ToolType> harvestTool = Optional.empty();
	private OptionalInt harvestLevel = OptionalInt.empty();
	private Optional<Boolean> isBeaconBase = Optional.empty();
	private OptionalInt flammability = OptionalInt.empty();
	private OptionalInt fireSpreadSpeed = OptionalInt.empty();
	private Optional<Boolean> isFireSource = Optional.empty();

	@Override
	public void settingsChanged() {
		blockMaterial = Optional.empty();
		materialColor = Optional.empty();
		blocksMovement = Optional.empty();
		soundType = Optional.empty();
		lightValue = OptionalInt.empty();
		blockHardness = OptionalDouble.empty();
		explosionResistance = OptionalDouble.empty();
		slipperiness = OptionalDouble.empty();
		isFull = Optional.empty();
		shape = Optional.empty();
		raytraceShape = Optional.empty();
		renderLayer = Optional.empty();
		harvestTool = Optional.empty();
		harvestLevel = OptionalInt.empty();
		isBeaconBase = Optional.empty();
		flammability = OptionalInt.empty();
		fireSpreadSpeed = OptionalInt.empty();
		isFireSource = Optional.empty();
	}

	@Override
	public Material getMaterial(IBlockState state) {
		if(!blockMaterial.isPresent()) {
			blockMaterial = Optional.of(settings.get().getMaterialFunction().apply(material));
		}
		return blockMaterial.get();
	}

	@Override
	public MaterialColor getMaterialColor(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		if(!materialColor.isPresent()) {
			materialColor = Optional.of(settings.get().getMaterialColorFunction().apply(material));
		}
		return materialColor.get();
	}

	@Override
	public SoundType getSoundType(IBlockState state, IWorldReader world, BlockPos pos, Entity entity) {
		if(!soundType.isPresent()) {
			soundType = Optional.of(settings.get().getSoundTypeFunction().apply(material));
		}
		return soundType.get();
	}

	@Override
	public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos) {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.get().getLightValueFunction().applyAsInt(material));
		}
		return lightValue.getAsInt();
	}

	@Override
	public float getBlockHardness(IBlockState blockState, IBlockReader worldIn, BlockPos pos) {
		if(!blockHardness.isPresent()) {
			blockHardness = OptionalDouble.of(settings.get().getBlockHardnessFunction().applyAsDouble(material));
		}
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getExplosionResistance(IBlockState state, IWorldReader world, BlockPos pos, Entity exploder, Explosion explosion) {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.get().getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public float getSlipperiness(IBlockState state, IWorldReader world, BlockPos pos, Entity entity) {
		if(!slipperiness.isPresent()) {
			slipperiness = OptionalDouble.of(settings.get().getSlipperinessFunction().applyAsDouble(material));
		}
		return (float)slipperiness.getAsDouble();
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		if(!isFull.isPresent()) {
			isFull = Optional.of(settings.get().getIsFull());
		}
		return isFull.get();
	}

	@Override
	public boolean isSolid(IBlockState state) {
		if(!blocksMovement.isPresent()) {
			blocksMovement = Optional.of(settings.get().getBlocksMovement());
		}
		return blocksMovement.get() && state.getBlock().getRenderLayer() == BlockRenderLayer.SOLID;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		if(!shape.isPresent()) {
			shape = Optional.of(settings.get().getShape());
		}
		return shape.get();
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		if(!blocksMovement.isPresent()) {
			blocksMovement = Optional.of(settings.get().getBlocksMovement());
		}
		return blocksMovement.get() ? state.getShape(worldIn, pos) : VoxelShapes.empty();
	}

	@Override
	public VoxelShape getRaytraceShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		if(!raytraceShape.isPresent()) {
			raytraceShape = Optional.of(settings.get().getRaytraceShape());
		}
		return raytraceShape.get();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		if(!renderLayer.isPresent()) {
			renderLayer = Optional.of(settings.get().getRenderLayer());
		}
		return renderLayer.get();
	}

	@Override
	public ToolType getHarvestTool(IBlockState state) {
		if(!harvestTool.isPresent()) {
			harvestTool = Optional.ofNullable(settings.get().getHarvestToolFunction().apply(material));
		}
		return harvestTool.orElse(null);
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		if(!harvestLevel.isPresent()) {
			harvestLevel = OptionalInt.of(settings.get().getHarvestLevelFunction().applyAsInt(material));
		}
		return harvestLevel.getAsInt();
	}

	@Override
	public boolean isBeaconBase(IBlockState state, IWorldReader world, BlockPos pos, BlockPos beacon) {
		if(!isBeaconBase.isPresent()) {
			isBeaconBase = Optional.of(settings.get().getIsBeaconBasePredicate().test(material));
		}
		return isBeaconBase.get();
	}

	@Override
	public int getFlammability(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing face) {
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.get().getFireSpreadSpeedFunction().applyAsInt(material));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.get().getFlammabilityFunction().applyAsInt(material));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.get().getIsFireSourcePredicate().test(material));
		}
		return isFireSource.get();
	}
}
