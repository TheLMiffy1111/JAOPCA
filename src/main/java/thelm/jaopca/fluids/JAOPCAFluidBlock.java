package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.fluids.PlaceableFluid;
import thelm.jaopca.api.fluids.PlaceableFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluidBlock extends PlaceableFluidBlock implements IMaterialFormFluidBlock {

	private final IMaterialFormFluid fluid;
	protected final IFluidFormSettings settings;

	protected Optional<MapColor> mapColor = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();
	protected OptionalInt fireTime = OptionalInt.empty();

	public JAOPCAFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(getProperties(fluid, settings), (PlaceableFluid)fluid.toFluid(),
				settings.getMaxLevelFunction().applyAsInt(fluid.getMaterial()));
		this.fluid = fluid;
		this.settings = settings;
	}

	public static BlockBehaviour.Properties getProperties(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		BlockBehaviour.Properties prop = BlockBehaviour.Properties.of();
		prop.strength((float)settings.getBlockHardnessFunction().applyAsDouble(fluid.getMaterial()));
		prop.lightLevel(state->settings.getLightValueFunction().applyAsInt(fluid.getMaterial()));
		prop.noCollission();
		prop.randomTicks();
		prop.noLootTable();
		prop.noOcclusion();
		prop.replaceable();
		prop.liquid();
		prop.pushReaction(PushReaction.DESTROY);
		return prop;
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
	public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
		if(!mapColor.isPresent()) {
			mapColor = Optional.of(settings.getMapColorFunction().apply(getMaterial()));
		}
		return mapColor.get();
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(getMaterial()));
		}
		return lightValue.getAsInt();
	}

	@Override
	public float getExplosionResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(getMaterial()));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public int getFlammability(BlockState blockState, BlockGetter world, BlockPos pos, Direction face) {
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.getFireSpreadSpeedFunction().applyAsInt(getMaterial()));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(BlockState blockState, BlockGetter world, BlockPos pos, Direction face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.getFlammabilityFunction().applyAsInt(getMaterial()));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(BlockState blockState, LevelReader world, BlockPos pos, Direction side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(getMaterial()));
		}
		return isFireSource.get();
	}

	@Override
	public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
		if(!fireTime.isPresent()) {
			fireTime = OptionalInt.of(settings.getFireTimeFunction().applyAsInt(getMaterial()));
		}
		int time = fireTime.getAsInt();
		if(time > 0) {
			entity.setSecondsOnFire(time);
		}
	}
}
