package thelm.jaopca.blocks;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.google.common.base.Strings;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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
	protected Supplier<Material> blockMaterial;
	protected Supplier<MapColor> mapColor;
	protected Supplier<SoundType> soundType;
	protected IntSupplier lightOpacity;
	protected IntSupplier lightValue;
	protected DoubleSupplier blockHardness;
	protected DoubleSupplier explosionResistance;
	protected DoubleSupplier slipperiness;
	protected AxisAlignedBB boundingBox;
	protected Supplier<String> harvestTool;
	protected IntSupplier harvestLevel;
	protected IntSupplier flammability;
	protected IntSupplier fireSpreadSpeed;
	protected BooleanSupplier isFireSource;
	protected BooleanSupplier isBeaconBase;
	protected Supplier<String> translationKey;

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(Material.IRON);
		this.form = form;
		this.material = material;
		this.settings = settings;

		blocksMovement = settings.getBlocksMovement();
		blockMaterial = MemoizingSuppliers.of(settings.getMaterialFunction(), ()->material);
		mapColor = MemoizingSuppliers.of(settings.getMapColorFunction(), ()->material);
		soundType = MemoizingSuppliers.of(settings.getSoundTypeFunction(), ()->material);
		lightOpacity = MemoizingSuppliers.of(settings.getLightOpacityFunction(), ()->material);
		lightValue = MemoizingSuppliers.of(settings.getLightValueFunction(), ()->material);
		blockHardness = MemoizingSuppliers.of(settings.getBlockHardnessFunction(), ()->material);
		explosionResistance = MemoizingSuppliers.of(settings.getExplosionResistanceFunction(), ()->material);
		slipperiness = MemoizingSuppliers.of(settings.getSlipperinessFunction(), ()->material);
		boundingBox = settings.getBoundingBox();
		harvestTool = MemoizingSuppliers.of(settings.getHarvestToolFunction(), ()->material);
		harvestLevel = MemoizingSuppliers.of(settings.getHarvestLevelFunction(), ()->material);
		flammability = MemoizingSuppliers.of(settings.getFlammabilityFunction(), ()->material);
		fireSpreadSpeed = MemoizingSuppliers.of(settings.getFireSpreadSpeedFunction(), ()->material);
		isFireSource = MemoizingSuppliers.of(settings.getIsFireSourceFunction(), ()->material);
		isBeaconBase = MemoizingSuppliers.of(settings.getIsBeaconBaseFunction(), ()->material);
		translationKey = MemoizingSuppliers.of(()->{
			ResourceLocation id = getRegistryName();
			return "block."+id.getNamespace()+"."+id.getPath().replace('/', '.');
		});
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
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
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
	public SoundType getSoundType() {
		return soundType.get();
	}

	@Override
	public int getLightOpacity(IBlockState state) {
		return lightOpacity.getAsInt();
	}

	@Override
	public int getLightValue(IBlockState state) {
		return lightValue.getAsInt();
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
	public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		return (float)slipperiness.getAsDouble();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return blocksMovement ? blockState.getBoundingBox(worldIn, pos) : NULL_AABB;
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		return Strings.emptyToNull(harvestTool.get());
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return harvestLevel.getAsInt();
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

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
		return isBeaconBase.getAsBoolean();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return FULL_BLOCK_AABB.equals(boundingBox);
	}

	@Override
	public String getTranslationKey() {
		return translationKey.get();
	}

	@Override
	public String getLocalizedName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getTranslationKey());
	}
}
