package thelm.jaopca.fluids;

import net.minecraft.client.Minecraft;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;

public class JAOPCAFluidAttributes extends FluidAttributes {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	public JAOPCAFluidAttributes(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(FluidAttributes.builder(fluid.asFluid().getRegistryName().toString(),
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"fluid/"+fluid.getMaterial().getTextureType().getRegistryName()+fluid.getForm().getName()+"_still"),
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"fluid/"+fluid.getMaterial().getTextureType().getRegistryName()+fluid.getForm().getName()+"_flow")).
				translationKey(fluid.asFluid().getRegistryName().toString().replace(':', '.')).
				sound(settings.getFillSoundSupplier().get(), settings.getEmptySoundSupplier().get()).
				luminosity(settings.getLightValueFunction().applyAsInt(fluid.getMaterial())).
				density(settings.getDensityFunction().applyAsInt(fluid.getMaterial())).
				viscosity(settings.getViscosityFunction().applyAsInt(fluid.getMaterial())).
				temperature(settings.getTemperatureFunction().applyAsInt(fluid.getMaterial())).
				rarity(settings.getDisplayRarityFunction().apply(fluid.getMaterial())));
		this.fluid = fluid;
		this.settings = settings;
	}

	@Override
	public int getColor() {
		return 0xFFFFFF & fluid.getMaterial().getColor();
	}

	@Override
	public IFluidState getStateForPlacement(IEnviromentBlockReader world, BlockPos pos, FluidStack stack) {
		return fluid.getSourceState();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation getStillTexture() {
		if(Minecraft.getInstance().getResourceManager().hasResource(
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"textures/fluid/"+fluid.asFluid().getRegistryName().getPath()+"_still.png"))) {
			return new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
					"fluid/"+fluid.asFluid().getRegistryName().getPath()+"_still");
		}
		return super.getStillTexture();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation getFlowingTexture() {
		if(Minecraft.getInstance().getResourceManager().hasResource(
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"textures/fluid/"+fluid.asFluid().getRegistryName().getPath()+"_flow.png"))) {
			return new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
					"fluid/"+fluid.asFluid().getRegistryName().getPath()+"_flow");
		}
		return super.getFlowingTexture();
	}

	@Override
	public ITextComponent getDisplayName(FluidStack stack) {
		return JAOPCAApi.instance().currentLocalizer().localizeMaterialForm("fluid.jaopca."+fluid.getForm().getName(), fluid.getMaterial(), getTranslationKey());
	}
}
