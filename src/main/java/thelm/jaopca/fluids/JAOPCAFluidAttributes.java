package thelm.jaopca.fluids;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCAFluidAttributes extends FluidAttributes {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	public JAOPCAFluidAttributes(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(FluidAttributes.builder(
				new ResourceLocation(ForgeRegistries.FLUIDS.getKey(fluid.asFluid()).getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_still"),
				new ResourceLocation(ForgeRegistries.FLUIDS.getKey(fluid.asFluid()).getNamespace(),
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

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation getStillTexture() {
		ResourceLocation location = ForgeRegistries.FLUIDS.getKey(fluid.asFluid());
		if(Minecraft.getInstance().getResourceManager().getResource(
				new ResourceLocation(location.getNamespace(),
						"textures/fluid/"+location.getPath()+"_still.png")).isPresent()) {
			return new ResourceLocation(location.getNamespace(), "fluid/"+location.getPath()+"_still");
		}
		return super.getStillTexture();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation getFlowingTexture() {
		ResourceLocation location = ForgeRegistries.FLUIDS.getKey(fluid.asFluid());
		if(Minecraft.getInstance().getResourceManager().getResource(
				new ResourceLocation(location.getNamespace(),
						"textures/fluid/"+location.getPath()+"_flow.png")).isPresent()) {
			return new ResourceLocation(location.getNamespace(), "fluid/"+location.getPath()+"_flow");
		}
		return super.getFlowingTexture();
	}

	@Override
	public Component getDisplayName(FluidStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("fluid.jaopca."+fluid.getForm().getName(), fluid.getMaterial(), getTranslationKey());
	}
}
