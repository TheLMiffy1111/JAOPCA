package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

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
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAFluidBlock extends BlockFluidClassic implements IMaterialFormFluidBlock {

	private final IMaterialFormFluid fluid;
	protected final IFluidFormSettings settings;

	protected Optional<Material> blockMaterial = Optional.empty();
	protected Optional<MapColor> mapColor = Optional.empty();
	protected OptionalDouble blockHardness = OptionalDouble.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected OptionalInt flammability = OptionalInt.empty();
	protected OptionalInt fireSpreadSpeed = OptionalInt.empty();
	protected Optional<Boolean> isFireSource = Optional.empty();

	public JAOPCAFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(fluid.toFluid(), Material.water);
		this.fluid = fluid;
		this.settings = settings;

		setQuantaPerBlock(settings.getMaxLevelFunction().applyAsInt(getIMaterial()));
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
		if(!blockMaterial.isPresent()) {
			blockMaterial = Optional.of(settings.getMaterialFunction().apply(getIMaterial()));
		}
		return blockMaterial.get();
	}

	@Override
	public MapColor getMapColor(int meta) {
		if(!mapColor.isPresent()) {
			mapColor = Optional.of(settings.getMapColorFunction().apply(getIMaterial()));
		}
		return mapColor.get();
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		if(!blockHardness.isPresent()) {
			blockHardness = OptionalDouble.of(settings.getBlockHardnessFunction().applyAsDouble(getIMaterial()));
		}
		return (float)blockHardness.getAsDouble();
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(getIMaterial()));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		if(!flammability.isPresent()) {
			flammability = OptionalInt.of(settings.getFireSpreadSpeedFunction().applyAsInt(getIMaterial()));
		}
		return flammability.getAsInt();
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		if(!fireSpreadSpeed.isPresent()) {
			fireSpreadSpeed = OptionalInt.of(settings.getFlammabilityFunction().applyAsInt(getIMaterial()));
		}
		return fireSpreadSpeed.getAsInt();
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		if(!isFireSource.isPresent()) {
			isFireSource = Optional.of(settings.getIsFireSourceFunction().test(getIMaterial()));
		}
		return isFireSource.get();
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
