package thelm.jaopca.fluids;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluidBlock extends PlaceableFluidBlock implements IMaterialFormFluidBlock {

	private final IMaterialFormFluid fluid;
	protected final IFluidFormSettings settings;

	protected IntSupplier lightValue;
	protected DoubleSupplier explosionResistance;
	protected IntSupplier flammability;
	protected IntSupplier fireSpreadSpeed;
	protected BooleanSupplier isFireSource;
	protected IntSupplier fireTime;

	public JAOPCAFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(getProperties(fluid, settings), (PlaceableFluid)fluid.toFluid(),
				settings.getMaxLevelFunction().applyAsInt(fluid.getMaterial()));

		this.fluid = fluid;
		this.settings = settings;

		lightValue = MemoizingSuppliers.of(settings.getLightValueFunction(), fluid::getMaterial);
		explosionResistance = MemoizingSuppliers.of(settings.getExplosionResistanceFunction(), fluid::getMaterial);
		flammability = MemoizingSuppliers.of(settings.getFlammabilityFunction(), fluid::getMaterial);
		fireSpreadSpeed = MemoizingSuppliers.of(settings.getFireSpreadSpeedFunction(), fluid::getMaterial);
		isFireSource = MemoizingSuppliers.of(settings.getIsFireSourceFunction(), fluid::getMaterial);
		fireTime = MemoizingSuppliers.of(settings.getFireTimeFunction(), fluid::getMaterial);
	}

	public static Block.Properties getProperties(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		Block.Properties prop = Block.Properties.of(
				settings.getMaterialFunction().apply(fluid.getMaterial()),
				settings.getMaterialColorFunction().apply(fluid.getMaterial()));
		prop.strength((float)settings.getBlockHardnessFunction().applyAsDouble(fluid.getMaterial()));
		prop.lightLevel(state->settings.getLightValueFunction().applyAsInt(fluid.getMaterial()));
		prop.noCollission();
		prop.randomTicks();
		prop.noDrops();
		prop.noOcclusion();
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
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return lightValue.getAsInt();
	}

	@Override
	public float getExplosionResistance() {
		return (float)explosionResistance.getAsDouble();
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
	public void entityInside(BlockState blockState, World world, BlockPos pos, Entity entity) {
		int time = fireTime.getAsInt();
		if(time > 0) {
			entity.setSecondsOnFire(time);
		}
	}
}
