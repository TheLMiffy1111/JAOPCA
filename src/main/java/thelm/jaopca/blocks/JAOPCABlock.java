package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
	protected Optional<MapColor> mapColor = Optional.empty();
	protected Optional<SoundType> soundType = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalDouble friction = OptionalDouble.empty();
	protected VoxelShape shape;
	protected VoxelShape interactionShape;
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();
	protected Optional<PushReaction> pushReaction = Optional.empty();

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(getProperties(form, material, settings));
		this.form = form;
		this.material = material;
		this.settings = settings;

		blocksMovement = settings.getBlocksMovement();
		shape = settings.getShape();
		interactionShape = settings.getInteractionShape();
	}

	public static BlockBehaviour.Properties getProperties(IForm form, IMaterial material, IBlockFormSettings settings) {
		BlockBehaviour.Properties prop = Block.Properties.of();
		prop.strength((float)settings.getBlockHardnessFunction().applyAsDouble(material));
		prop.lightLevel(state->settings.getLightValueFunction().applyAsInt(material));
		if(settings.getReplaceable()) {
			prop.replaceable();
		}
		if(settings.getRequiresToolFunction().test(material)) {
			prop.requiresCorrectToolForDrops();
		}
		prop.instrument(settings.getInstrumentFunction().apply(material));
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
	public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
		if(!mapColor.isPresent()) {
			mapColor = Optional.of(settings.getMapColorFunction().apply(material));
		}
		return mapColor.get();
	}

	@Override
	public SoundType getSoundType(BlockState blockState) {
		if(!soundType.isPresent()) {
			soundType = Optional.of(settings.getSoundTypeFunction().apply(material));
		}
		return soundType.get();
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(material));
		}
		return lightValue.getAsInt();
	}

	@Override
	public float getExplosionResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public float getFriction(BlockState blockState, LevelReader world, BlockPos pos, Entity entity) {
		if(!friction.isPresent()) {
			friction = OptionalDouble.of(settings.getFrictionFunction().applyAsDouble(material));
		}
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
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.getFireSpreadSpeedFunction().applyAsInt(material));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(BlockState blockState, BlockGetter world, BlockPos pos, Direction face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.getFlammabilityFunction().applyAsInt(material));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(BlockState blockState, LevelReader world, BlockPos pos, Direction side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(material));
		}
		return isFireSource.get();
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		if(!pushReaction.isPresent()) {
			pushReaction = Optional.of(settings.getPushReactionFunction().apply(material));
		}
		return pushReaction.get();
	}

	@Override
	public MutableComponent getName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getDescriptionId());
	}
}
