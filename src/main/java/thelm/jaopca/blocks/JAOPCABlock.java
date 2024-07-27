package thelm.jaopca.blocks;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlock extends Block implements IMaterialFormBlock {

	private final IForm form;
	private final IMaterial material;
	protected final IBlockFormSettings settings;

	protected boolean blocksMovement;
	protected Supplier<SoundType> soundType;
	protected IntSupplier lightOpacity;
	protected IntSupplier lightValue;
	protected DoubleSupplier explosionResistance;
	protected DoubleSupplier slipperiness;
	protected VoxelShape shape;
	protected VoxelShape raytraceShape;
	protected Supplier<ToolType> harvestTool;
	protected IntSupplier harvestLevel;
	protected IntSupplier flammability;
	protected IntSupplier fireSpreadSpeed;
	protected BooleanSupplier isFireSource;

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(getProperties(form, material, settings));
		this.form = form;
		this.material = material;
		this.settings = settings;

		blocksMovement = settings.getBlocksMovement();
		soundType = MemoizingSuppliers.of(settings.getSoundTypeFunction(), ()->material);
		lightOpacity = MemoizingSuppliers.of(settings.getLightOpacityFunction(), ()->material);
		lightValue = MemoizingSuppliers.of(settings.getLightValueFunction(), ()->material);
		explosionResistance = MemoizingSuppliers.of(settings.getExplosionResistanceFunction(), ()->material);
		slipperiness = MemoizingSuppliers.of(settings.getSlipperinessFunction(), ()->material);
		shape = settings.getShape();
		raytraceShape = settings.getRaytraceShape();
		harvestTool = MemoizingSuppliers.of(settings.getHarvestToolFunction(), ()->material);
		harvestLevel = MemoizingSuppliers.of(settings.getHarvestLevelFunction(), ()->material);
		flammability = MemoizingSuppliers.of(settings.getFlammabilityFunction(), ()->material);
		fireSpreadSpeed = MemoizingSuppliers.of(settings.getFireSpreadSpeedFunction(), ()->material);
		isFireSource = MemoizingSuppliers.of(settings.getIsFireSourceFunction(), ()->material);
	}

	public static Block.Properties getProperties(IForm form, IMaterial material, IBlockFormSettings settings) {
		Block.Properties prop = Block.Properties.of(
				settings.getMaterialFunction().apply(material),
				settings.getMaterialColorFunction().apply(material));
		prop.strength((float)settings.getBlockHardnessFunction().applyAsDouble(material));
		prop.lightLevel(state->settings.getLightValueFunction().applyAsInt(material));
		if(settings.getRequiresToolFunction().test(material)) {
			prop.requiresCorrectToolForDrops();
		}
		prop.noOcclusion();
		return prop;
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
		return soundType.get();
	}

	@Override
	public int getLightBlock(BlockState state, IBlockReader world, BlockPos pos) {
		return lightOpacity.getAsInt();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return lightValue.getAsInt();
	}

	@Override
	public float getExplosionResistance() {
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public float getSlipperiness(BlockState blockState, IWorldReader world, BlockPos pos, Entity entity) {
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
	public VoxelShape getInteractionShape(BlockState blockState, IBlockReader world, BlockPos pos) {
		return raytraceShape;
	}

	@Override
	public ToolType getHarvestTool(BlockState blockState) {
		return harvestTool.get();
	}

	@Override
	public int getHarvestLevel(BlockState blockState) {
		return harvestLevel.getAsInt();
	}

	@Override
	public int getFlammability(BlockState blockState, IBlockReader world, BlockPos pos, Direction face) {
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(BlockState blockState, IBlockReader world, BlockPos pos, Direction face) {
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(BlockState blockState, IWorldReader world, BlockPos pos, Direction side) {
		return isFireSource.getAsBoolean();
	}

	@Override
	public IFormattableTextComponent getName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getDescriptionId());
	}
}
