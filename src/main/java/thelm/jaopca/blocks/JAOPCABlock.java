package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

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
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCABlock extends Block implements IMaterialFormBlock {

	private final IForm form;
	private final IMaterial material;
	protected final IBlockFormSettings settings;

	protected boolean blocksMovement;
	protected Optional<Material> blockMaterial = Optional.empty();
	protected Optional<MapColor> mapColor = Optional.empty();
	protected Optional<SoundType> soundType = Optional.empty();
	protected OptionalInt lightOpacity = OptionalInt.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalDouble slipperiness = OptionalDouble.empty();
	protected AxisAlignedBB boundingBox;
	protected Optional<String> harvestTool = Optional.empty();
	protected OptionalInt harvestLevel = OptionalInt.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();
	protected Optional<Boolean> isBeaconBase = Optional.empty();
	protected Optional<String> translationKey = Optional.empty();

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(Material.IRON);
		this.form = form;
		this.material = material;
		this.settings = settings;

		blocksMovement = settings.getBlocksMovement();
		boundingBox = settings.getBoundingBox();
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
		if(!blockMaterial.isPresent()) {
			blockMaterial = Optional.of(settings.getMaterialFunction().apply(material));
		}
		return blockMaterial.get();
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		if(!mapColor.isPresent()) {
			mapColor = Optional.of(settings.getMapColorFunction().apply(material));
		}
		return mapColor.get();
	}

	@Override
	public SoundType getSoundType() {
		if(!soundType.isPresent()) {
			soundType = Optional.of(settings.getSoundTypeFunction().apply(material));
		}
		return soundType.get();
	}

	@Override
	public int getLightOpacity(IBlockState state) {
		if(!lightOpacity.isPresent()) {
			lightOpacity = OptionalInt.of(settings.getLightOpacityFunction().applyAsInt(material));
		}
		return lightOpacity.getAsInt();
	}

	@Override
	public int getLightValue(IBlockState state) {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(material));
		}
		return lightValue.getAsInt();
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		if(!blockHardness.isPresent()) {
			blockHardness = OptionalDouble.of(settings.getBlockHardnessFunction().applyAsDouble(material));
		}
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		if(!slipperiness.isPresent()) {
			slipperiness = OptionalDouble.of(settings.getSlipperinessFunction().applyAsDouble(material));
		}
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
		if(!harvestTool.isPresent()) {
			harvestTool = Optional.of(settings.getHarvestToolFunction().apply(material));
		}
		return Strings.emptyToNull(harvestTool.get());
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		if(!harvestLevel.isPresent()) {
			harvestLevel = OptionalInt.of(settings.getHarvestLevelFunction().applyAsInt(material));
		}
		return harvestLevel.getAsInt();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.getFireSpreadSpeedFunction().applyAsInt(material));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.getFlammabilityFunction().applyAsInt(material));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(material));
		}
		return isFireSource.get();
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
		if(!isBeaconBase.isPresent()) {
			isBeaconBase = Optional.of(settings.getIsBeaconBaseFunction().test(material));
		}
		return isBeaconBase.get();
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
		if(!translationKey.isPresent()) {
			ResourceLocation id = getRegistryName();
			translationKey = Optional.of("block."+id.getNamespace()+"."+id.getPath().replace('/', '.'));
		}
		return translationKey.get();
	}

	@Override
	public String getLocalizedName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getTranslationKey());
	}
}
