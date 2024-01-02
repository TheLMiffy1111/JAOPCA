package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
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

	protected Optional<Material> blockMaterial = Optional.empty();
	protected Optional<MaterialColor> materialColor = Optional.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	//protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();
	protected OptionalInt fireTime = OptionalInt.empty();

	public JAOPCAFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(Block.Properties.of(settings.getMaterialFunction().apply(fluid.getMaterial()),
				settings.getMaterialColorFunction().apply(fluid.getMaterial())).
				strength((float)settings.getBlockHardnessFunction().applyAsDouble(fluid.getMaterial())).
				lightLevel(state->settings.getLightValueFunction().applyAsInt(fluid.getMaterial())).
				noCollission().randomTicks().noDrops().noOcclusion(), (PlaceableFluid)fluid.toFluid(),
				settings.getMaxLevelFunction().applyAsInt(fluid.getMaterial()));

		this.fluid = fluid;
		this.settings = settings;
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
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(getMaterial()));
		}
		return lightValue.getAsInt();
	}

	//@Override
	//public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
	//	if(!blockHardness.isPresent()) {
	//		blockHardness = OptionalDouble.of(settings.getBlockHardnessFunction().applyAsDouble(getMaterial()));
	//	}
	//	return (float)blockHardness.getAsDouble();
	//}

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
	public boolean isFireSource(BlockState blockState, IWorldReader world, BlockPos pos, Direction side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(getMaterial()));
		}
		return isFireSource.get();
	}

	@Override
	public void entityInside(BlockState blockState, World world, BlockPos pos, Entity entity) {
		if(!fireTime.isPresent()) {
			fireTime = OptionalInt.of(settings.getFireTimeFunction().applyAsInt(getMaterial()));
		}
		int time = fireTime.getAsInt();
		if(time > 0) {
			entity.setSecondsOnFire(time);
		}
	}
}
