package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlock extends Block implements IMaterialFormBlock {

	private final IForm form;
	private final IMaterial material;
	protected final IBlockFormSettings settings;

	protected boolean blocksMovement;
	protected Optional<SoundType> soundType = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	//protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalDouble slipperiness = OptionalDouble.empty();
	protected VoxelShape shape;
	protected VoxelShape raytraceShape;
	protected Optional<ToolType> harvestTool = Optional.empty();
	protected OptionalInt harvestLevel = OptionalInt.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(Block.Properties.create(settings.getMaterialFunction().apply(material),
				settings.getMaterialColorFunction().apply(material)).
				hardnessAndResistance((float)settings.getBlockHardnessFunction().applyAsDouble(material)).
				setLightLevel(state->settings.getLightValueFunction().applyAsInt(material)).
				notSolid());
		this.form = form;
		this.material = material;
		this.settings = settings;

		blocksMovement = settings.getBlocksMovement();
		shape = settings.getShape();
		raytraceShape = settings.getRaytraceShape();
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public SoundType getSoundType(BlockState blockState) {
		if(!soundType.isPresent()) {
			soundType = Optional.of(settings.getSoundTypeFunction().apply(material));
		}
		return soundType.get();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(material));
		}
		return lightValue.getAsInt();
	}

	//@Override
	//public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
	//	if(!blockHardness.isPresent()) {
	//		blockHardness = OptionalDouble.of(settings.getBlockHardnessFunction().applyAsDouble(material));
	//	}
	//	return (float)blockHardness.getAsDouble();
	//}

	@Override
	public float getExplosionResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public float getSlipperiness(BlockState blockState, IWorldReader world, BlockPos pos, Entity entity) {
		if(!slipperiness.isPresent()) {
			slipperiness = OptionalDouble.of(settings.getSlipperinessFunction().applyAsDouble(material));
		}
		return (float)slipperiness.getAsDouble();
	}

	@Override
	public VoxelShape getShape(BlockState blockState, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return shape;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return blocksMovement ? blockState.getShape(world, pos) : VoxelShapes.empty();
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState blockState, IBlockReader world, BlockPos pos) {
		return raytraceShape;
	}

	@Override
	public ToolType getHarvestTool(BlockState blockState) {
		if(!harvestTool.isPresent()) {
			harvestTool = Optional.ofNullable(settings.getHarvestToolFunction().apply(material));
		}
		return harvestTool.orElse(null);
	}

	@Override
	public int getHarvestLevel(BlockState blockState) {
		if(!harvestLevel.isPresent()) {
			harvestLevel = OptionalInt.of(settings.getHarvestLevelFunction().applyAsInt(material));
		}
		return harvestLevel.getAsInt();
	}

	@Override
	public int getFlammability(BlockState blockState, IBlockReader world, BlockPos pos, Direction face) {
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.getFireSpreadSpeedFunction().applyAsInt(material));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(BlockState blockState, IBlockReader world, BlockPos pos, Direction face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.getFlammabilityFunction().applyAsInt(material));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(BlockState blockState, IWorldReader world, BlockPos pos, Direction side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(material));
		}
		return isFireSource.get();
	}

	@Override
	public IFormattableTextComponent getTranslatedName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getTranslationKey());
	}
}
