package thelm.jaopca.blocks;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.client.renderer.JAOPCABlockRenderer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCABlock extends Block implements IMaterialFormBlock {

	private final IForm form;
	private final IMaterial material;
	protected final IBlockFormSettings settings;

	protected boolean blocksMovement;
	protected Optional<Material> blockMaterial = Optional.empty();
	protected Optional<MapColor> mapColor = Optional.empty();
	protected Optional<Block.SoundType> soundType = Optional.empty();
	protected OptionalInt lightOpacity = OptionalInt.empty();
	protected OptionalInt lightValue = OptionalInt.empty();
	protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected AxisAlignedBB boundingBox;
	protected Optional<String> harvestTool = Optional.empty();
	protected OptionalInt harvestLevel = OptionalInt.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();
	protected Optional<Boolean> isBeaconBase = Optional.empty();
	protected Optional<String> translationKey = Optional.empty();

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(Material.iron);
		this.form = form;
		this.material = material;
		this.settings = settings;

		stepSound = settings.getSoundTypeFunction().apply(material);
		slipperiness = (float)settings.getSlipperinessFunction().applyAsDouble(material);

		blocksMovement = settings.getBlocksMovement();
		boundingBox = settings.getBoundingBox();
		minX = boundingBox.minX;
		minY = boundingBox.minY;
		minZ = boundingBox.minZ;
		maxX = boundingBox.maxX;
		maxY = boundingBox.maxY;
		maxZ = boundingBox.maxZ;
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getIMaterial() {
		return material;
	}

	@Override
	public Block asBlock() {
		return this;
	}

	@Override
	public Material getMaterial() {
		if(!blockMaterial.isPresent()) {
			blockMaterial = Optional.of(settings.getMaterialFunction().apply(material));
		}
		return blockMaterial.get();
	}

	@Override
	public MapColor getMapColor(int meta) {
		if(!mapColor.isPresent()) {
			mapColor = Optional.of(settings.getMapColorFunction().apply(material));
		}
		return mapColor.get();
	}

	@Override
	public int getLightOpacity() {
		if(!lightOpacity.isPresent()) {
			lightOpacity = OptionalInt.of(settings.getLightOpacityFunction().applyAsInt(material));
		}
		return lightOpacity.getAsInt();
	}

	@Override
	public int getLightValue() {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(material));
		}
		return lightValue.getAsInt();
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return blocksMovement ? super.getCollisionBoundingBoxFromPool(world, x, y, z) : AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	@Override
	public String getHarvestTool(int meta) {
		if(!harvestTool.isPresent()) {
			harvestTool = Optional.of(settings.getHarvestToolFunction().apply(material));
		}
		return Strings.emptyToNull(harvestTool.get());
	}

	@Override
	public int getHarvestLevel(int meta) {
		if(!harvestLevel.isPresent()) {
			harvestLevel = OptionalInt.of(settings.getHarvestLevelFunction().applyAsInt(material));
		}
		return harvestLevel.getAsInt();
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.getFireSpreadSpeedFunction().applyAsInt(material));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.getFlammabilityFunction().applyAsInt(material));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(material));
		}
		return isFireSource.get();
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
		if(!isBeaconBase.isPresent()) {
			isBeaconBase = Optional.of(settings.getIsBeaconBaseFunction().test(material));
		}
		return isBeaconBase.get();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isNormalCube() {
		return minX == 0 && minY == 0 && minZ == 0 && maxX == 1 && maxY == 1 && maxZ == 1;
	}

	@Override
	public String getUnlocalizedName() {
		if(!translationKey.isPresent()) {
			String name = blockRegistry.getNameForObject(this);
			translationKey = Optional.of("block."+name.replaceFirst(":", ".").replace('/', '.'));
		}
		return translationKey.get();
	}

	@Override
	public String getLocalizedName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getUnlocalizedName());
	}

	@Override
	public int getRenderType() {
		return JAOPCABlockRenderer.RENDER_ID;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	protected IIcon overlayIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/blocks/"+form.getName()+'.'+material.getName()+".png"))) {
			blockIcon = iconRegister.registerIcon("jaopca:"+form.getName()+'.'+material.getName());
		}
		else {
			blockIcon = iconRegister.registerIcon("jaopca:"+material.getModelType()+'/'+form.getName());
		}
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/blocks/"+form.getName()+'.'+material.getName()+"_overlay.png"))) {
			overlayIcon = iconRegister.registerIcon("jaopca:"+form.getName()+'.'+material.getName());
		}
		else if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/blocks/"+material.getModelType()+'/'+form.getName()+"_overlay.png"))) {
			overlayIcon = iconRegister.registerIcon("jaopca:"+material.getModelType()+'/'+form.getName()+"_overlay");
		}
		else {
			overlayIcon = null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasOverlay() {
		return overlayIcon != null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return JAOPCABlockRenderer.INSTANCE.renderingOverlay ? overlayIcon : blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return JAOPCABlockRenderer.INSTANCE.renderingOverlay ? 0xFFFFFF : material.getColor();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderColor(int meta) {
		return JAOPCABlockRenderer.INSTANCE.renderingOverlay ? 0xFFFFFF : material.getColor();
	}
}
