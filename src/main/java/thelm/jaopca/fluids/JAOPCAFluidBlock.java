package thelm.jaopca.fluids;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluidBlock extends BlockFluidClassic implements IMaterialFormFluidBlock {

	private final IMaterialFormFluid fluid;
	protected final IFluidFormSettings settings;

	protected Supplier<Material> blockMaterial;
	protected Supplier<MapColor> mapColor;
	protected DoubleSupplier blockHardness;
	protected DoubleSupplier explosionResistance;
	protected IntSupplier flammability;
	protected IntSupplier fireSpreadSpeed;
	protected BooleanSupplier isFireSource;

	public JAOPCAFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(fluid.toFluid(), Material.WATER);
		this.fluid = fluid;
		this.settings = settings;

		setQuantaPerBlock(settings.getMaxLevelFunction().applyAsInt(fluid.getMaterial()));

		blockMaterial = MemoizingSuppliers.of(settings.getMaterialFunction(), fluid::getMaterial);
		mapColor = MemoizingSuppliers.of(settings.getMapColorFunction(), fluid::getMaterial);
		blockHardness = MemoizingSuppliers.of(settings.getBlockHardnessFunction(), fluid::getMaterial);
		explosionResistance = MemoizingSuppliers.of(settings.getExplosionResistanceFunction(), fluid::getMaterial);
		flammability = MemoizingSuppliers.of(settings.getFlammabilityFunction(), fluid::getMaterial);
		fireSpreadSpeed = MemoizingSuppliers.of(settings.getFireSpreadSpeedFunction(), fluid::getMaterial);
		isFireSource = MemoizingSuppliers.of(settings.getIsFireSourceFunction(), fluid::getMaterial);
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
	public Material getMaterial(IBlockState state) {
		return blockMaterial.get();
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return mapColor.get();
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		return isFireSource.getAsBoolean();
	}
}
