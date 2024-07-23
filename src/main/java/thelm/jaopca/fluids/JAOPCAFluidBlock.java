package thelm.jaopca.fluids;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

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
		super(fluid.toFluid(), Material.water);
		this.fluid = fluid;
		this.settings = settings;

		setQuantaPerBlock(settings.getMaxLevelFunction().applyAsInt(getIMaterial()));

		blockMaterial = MemoizingSuppliers.of(settings.getMaterialFunction(), fluid::getIMaterial);
		mapColor = MemoizingSuppliers.of(settings.getMapColorFunction(), fluid::getIMaterial);
		blockHardness = MemoizingSuppliers.of(settings.getBlockHardnessFunction(), fluid::getIMaterial);
		explosionResistance = MemoizingSuppliers.of(settings.getExplosionResistanceFunction(), fluid::getIMaterial);
		flammability = MemoizingSuppliers.of(settings.getFlammabilityFunction(), fluid::getIMaterial);
		fireSpreadSpeed = MemoizingSuppliers.of(settings.getFireSpreadSpeedFunction(), fluid::getIMaterial);
		isFireSource = MemoizingSuppliers.of(settings.getIsFireSourceFunction(), fluid::getIMaterial);
	}

	@Override
	public IForm getForm() {
		return fluid.getForm();
	}

	@Override
	public IMaterial getIMaterial() {
		return fluid.getIMaterial();
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
	public float getBlockHardness(World world, int x, int y, int z) {
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return (float)explosionResistance.getAsDouble();
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

	@SideOnly(Side.CLIENT)
	protected IIcon flowIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/blocks/"+getForm().getName()+'.'+getIMaterial().getName()+"_still.png"))) {
			blockIcon = iconRegister.registerIcon("jaopca:"+getForm().getName()+'.'+getIMaterial().getName()+"_still");
		}
		else {
			blockIcon = iconRegister.registerIcon("jaopca:"+getIMaterial().getModelType()+'/'+getForm().getName()+"_still");
		}
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/blocks/"+getForm().getName()+'.'+getIMaterial().getName()+"_flow.png"))) {
			flowIcon = iconRegister.registerIcon("jaopca:"+getForm().getName()+'.'+getIMaterial().getName()+"_flow");
		}
		else {
			flowIcon = iconRegister.registerIcon("jaopca:"+getIMaterial().getModelType()+'/'+getForm().getName()+"_flow");
		}
		fluid.toFluid().setIcons(blockIcon, flowIcon);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 0 || side == 1 ? blockIcon : flowIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return getIMaterial().getColor();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderColor(int meta) {
		return getIMaterial().getColor();
	}
}
