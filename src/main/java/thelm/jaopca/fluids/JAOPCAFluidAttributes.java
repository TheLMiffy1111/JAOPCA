package thelm.jaopca.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAFluidAttributes extends FluidAttributes {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	public JAOPCAFluidAttributes(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(FluidAttributes.builder(
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_still"),
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_flow")).
				sound(settings.getFillSoundSupplier().get(), settings.getEmptySoundSupplier().get()).
				luminosity(settings.getLightValueFunction().applyAsInt(fluid.getMaterial())).
				density(settings.getDensityFunction().applyAsInt(fluid.getMaterial())).
				viscosity(settings.getViscosityFunction().applyAsInt(fluid.getMaterial())).
				temperature(settings.getTemperatureFunction().applyAsInt(fluid.getMaterial())).
				rarity(settings.getDisplayRarityFunction().apply(fluid.getMaterial())), fluid.asFluid());
		this.fluid = fluid;
		this.settings = settings;
	}

	@Override
	public int getColor() {
		return fluid.getMaterial().getColor();
	}

	@Override
	public FluidState getStateForPlacement(BlockAndTintGetter world, BlockPos pos, FluidStack stack) {
		return fluid.getSourceState();
	}

	@Override
	public ResourceLocation getStillTexture() {
		if(MiscHelper.INSTANCE.hasResource(
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"textures/fluid/"+fluid.asFluid().getRegistryName().getPath()+"_still.png"))) {
			return new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
					"fluid/"+fluid.asFluid().getRegistryName().getPath()+"_still");
		}
		return super.getStillTexture();
	}

	@Override
	public ResourceLocation getFlowingTexture() {
		if(MiscHelper.INSTANCE.hasResource(
				new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
						"textures/fluid/"+fluid.asFluid().getRegistryName().getPath()+"_flow.png"))) {
			return new ResourceLocation(fluid.asFluid().getRegistryName().getNamespace(),
					"fluid/"+fluid.asFluid().getRegistryName().getPath()+"_flow");
		}
		return super.getFlowingTexture();
	}

	@Override
	public Component getDisplayName(FluidStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("fluid.jaopca."+fluid.getForm().getName(), fluid.getMaterial(), getTranslationKey());
	}
}
