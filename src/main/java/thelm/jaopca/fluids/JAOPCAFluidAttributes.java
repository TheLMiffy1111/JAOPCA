package thelm.jaopca.fluids;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		super(getBuilder(fluid, settings), fluid.toFluid());
		this.fluid = fluid;
		this.settings = settings;
	}

	public static FluidAttributes.Builder getBuilder(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		FluidAttributes.Builder builder = FluidAttributes.builder(
				new ResourceLocation(fluid.toFluid().getRegistryName().getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_still"),
				new ResourceLocation(fluid.toFluid().getRegistryName().getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_flow"));
		builder.sound(settings.getFillSoundSupplier().get(), settings.getEmptySoundSupplier().get());
		builder.luminosity(settings.getLightValueFunction().applyAsInt(fluid.getMaterial()));
		builder.density(settings.getDensityFunction().applyAsInt(fluid.getMaterial()));
		builder.viscosity(settings.getViscosityFunction().applyAsInt(fluid.getMaterial()));
		builder.temperature(settings.getTemperatureFunction().applyAsInt(fluid.getMaterial()));
		builder.rarity(settings.getDisplayRarityFunction().apply(fluid.getMaterial()));
		return builder;
	}

	@Override
	public int getColor() {
		return fluid.getMaterial().getColor();
	}

	@Override
	public FluidState getStateForPlacement(IBlockDisplayReader world, BlockPos pos, FluidStack stack) {
		return fluid.getSourceState();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation getStillTexture() {
		if(MiscHelper.INSTANCE.hasResource(
				new ResourceLocation(fluid.toFluid().getRegistryName().getNamespace(),
						"textures/fluid/"+fluid.toFluid().getRegistryName().getPath()+"_still.png"))) {
			return new ResourceLocation(fluid.toFluid().getRegistryName().getNamespace(),
					"fluid/"+fluid.toFluid().getRegistryName().getPath()+"_still");
		}
		return super.getStillTexture();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation getFlowingTexture() {
		if(MiscHelper.INSTANCE.hasResource(
				new ResourceLocation(fluid.toFluid().getRegistryName().getNamespace(),
						"textures/fluid/"+fluid.toFluid().getRegistryName().getPath()+"_flow.png"))) {
			return new ResourceLocation(fluid.toFluid().getRegistryName().getNamespace(),
					"fluid/"+fluid.toFluid().getRegistryName().getPath()+"_flow");
		}
		return super.getFlowingTexture();
	}

	@Override
	public ITextComponent getDisplayName(FluidStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("fluid.jaopca."+fluid.getForm().getName(), fluid.getMaterial(), getTranslationKey());
	}
}
