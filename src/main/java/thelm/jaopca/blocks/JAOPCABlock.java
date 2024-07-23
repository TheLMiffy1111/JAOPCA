package thelm.jaopca.blocks;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
	protected DoubleSupplier friction;
	protected VoxelShape shape;
	protected VoxelShape interactionShape;
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
		friction = MemoizingSuppliers.of(settings.getFrictionFunction(), ()->material);
		shape = settings.getShape();
		interactionShape = settings.getInteractionShape();
		flammability = MemoizingSuppliers.of(settings.getFlammabilityFunction(), ()->material);
		fireSpreadSpeed = MemoizingSuppliers.of(settings.getFireSpreadSpeedFunction(), ()->material);
		isFireSource = MemoizingSuppliers.of(settings.getIsFireSourceFunction(), ()->material);
	}

	public static BlockBehaviour.Properties getProperties(IForm form, IMaterial material, IBlockFormSettings settings) {
		BlockBehaviour.Properties prop = BlockBehaviour.Properties.of(
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
	public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
		return lightOpacity.getAsInt();
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return lightValue.getAsInt();
	}

	@Override
	public float getExplosionResistance() {
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public float getFriction(BlockState blockState, LevelReader world, BlockPos pos, Entity entity) {
		return (float)friction.getAsDouble();
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter world, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter world, BlockPos pos, CollisionContext context) {
		return blocksMovement ? blockState.getShape(world, pos) : Shapes.empty();
	}

	@Override
	public VoxelShape getInteractionShape(BlockState blockState, BlockGetter world, BlockPos pos) {
		return interactionShape;
	}

	@Override
	public int getFlammability(BlockState blockState, BlockGetter world, BlockPos pos, Direction face) {
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(BlockState blockState, BlockGetter world, BlockPos pos, Direction face) {
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(BlockState blockState, LevelReader world, BlockPos pos, Direction side) {
		return isFireSource.getAsBoolean();
	}

	@Override
	public MutableComponent getName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getDescriptionId());
	}
}
