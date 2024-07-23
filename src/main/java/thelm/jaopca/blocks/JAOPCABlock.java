package thelm.jaopca.blocks;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.google.common.base.Strings;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
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
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.client.renderer.JAOPCABlockRenderer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCABlock extends Block implements IMaterialFormBlock {

	public static final int RENDER_ID = MiscHelper.INSTANCE.conditionalSupplier(FMLCommonHandler.instance().getSide()::isClient,
			()->RenderingRegistry::getNextAvailableRenderId, ()->()->0).get();

	private final IForm form;
	private final IMaterial material;
	protected final IBlockFormSettings settings;

	protected boolean blocksMovement;
	protected Supplier<Material> blockMaterial;
	protected Supplier<MapColor> mapColor;
	protected IntSupplier lightOpacity;
	protected IntSupplier lightValue;
	protected DoubleSupplier blockHardness;
	protected DoubleSupplier explosionResistance;
	protected AxisAlignedBB boundingBox;
	protected Supplier<String> harvestTool;
	protected IntSupplier harvestLevel;
	protected IntSupplier flammability;
	protected IntSupplier fireSpreadSpeed;
	protected BooleanSupplier isFireSource;
	protected BooleanSupplier isBeaconBase;
	protected Supplier<String> translationKey;

	public JAOPCABlock(IForm form, IMaterial material, IBlockFormSettings settings) {
		super(Material.iron);
		this.form = form;
		this.material = material;
		this.settings = settings;

		stepSound = settings.getSoundTypeFunction().apply(material);
		slipperiness = (float)settings.getSlipperinessFunction().applyAsDouble(material);

		blocksMovement = settings.getBlocksMovement();
		blockMaterial = MemoizingSuppliers.of(settings.getMaterialFunction(), material);
		mapColor = MemoizingSuppliers.of(settings.getMapColorFunction(), material);
		lightOpacity = MemoizingSuppliers.of(settings.getLightOpacityFunction(), material);
		lightValue = MemoizingSuppliers.of(settings.getLightValueFunction(), material);
		blockHardness = MemoizingSuppliers.of(settings.getBlockHardnessFunction(), material);
		explosionResistance = MemoizingSuppliers.of(settings.getExplosionResistanceFunction(), material);
		boundingBox = settings.getBoundingBox();
		harvestTool = MemoizingSuppliers.of(settings.getHarvestToolFunction(), material);
		harvestLevel = MemoizingSuppliers.of(settings.getHarvestLevelFunction(), material);
		flammability = MemoizingSuppliers.of(settings.getFlammabilityFunction(), material);
		fireSpreadSpeed = MemoizingSuppliers.of(settings.getFireSpreadSpeedFunction(), material);
		isFireSource = MemoizingSuppliers.of(settings.getIsFireSourceFunction(), material);
		isBeaconBase = MemoizingSuppliers.of(settings.getIsBeaconBaseFunction(), material);
		translationKey = MemoizingSuppliers.of(()->{
			String name = blockRegistry.getNameForObject(JAOPCABlock.this);
			return "block."+name.replaceFirst(":", ".").replace('/', '.');
		});

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
	public Block toBlock() {
		return this;
	}

	@Override
	public Material getMaterial() {
		return blockMaterial.get();
	}

	@Override
	public MapColor getMapColor(int meta) {
		return mapColor.get();
	}

	@Override
	public int getLightOpacity() {
		return lightOpacity.getAsInt();
	}

	@Override
	public int getLightValue() {
		return lightValue.getAsInt();
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return blocksMovement ? super.getCollisionBoundingBoxFromPool(world, x, y, z) : AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	@Override
	public String getHarvestTool(int meta) {
		return Strings.emptyToNull(harvestTool.get());
	}

	@Override
	public int getHarvestLevel(int meta) {
		return harvestLevel.getAsInt();
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return isFireSource.getAsBoolean();
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
		return isBeaconBase.getAsBoolean();
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
		return translationKey.get();
	}

	@Override
	public String getLocalizedName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("block.jaopca."+form.getName(), material, getUnlocalizedName());
	}

	@Override
	public int getRenderType() {
		return RENDER_ID;
	}

	@SideOnly(Side.CLIENT)
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
