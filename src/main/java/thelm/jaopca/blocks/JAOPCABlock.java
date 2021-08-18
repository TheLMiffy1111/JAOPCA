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
import net.minecraft.world.level.block.state.BlockState;
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
	protected Optional<SoundType> soundType = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	//protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalDouble friction = OptionalDouble.empty();
	protected VoxelShape shape;
	protected VoxelShape interactionShape;
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(Block.Properties.of(settings.getMaterialFunction().apply(material),
				settings.getMaterialColorFunction().apply(material)).
				strength((float)settings.getBlockHardnessFunction().applyAsDouble(material)).
				lightLevel(state->settings.getLightValueFunction().applyAsInt(material)).
				noOcclusion());
		this.form = form;
		this.material = material;
		this.settings = settings;

		blocksMovement = settings.getBlocksMovement();
		shape = settings.getShape();
		interactionShape = settings.getInteractionShape();
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
	public Block asBlock() {
		return this;
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
	public MutableComponent getName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getDescriptionId());
	}
}
