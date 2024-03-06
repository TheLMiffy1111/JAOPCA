package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidType;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAFluidType extends FluidType implements IMaterialFormFluidType {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	private OptionalInt lightValue = OptionalInt.empty();
	private OptionalInt density = OptionalInt.empty();
	private OptionalInt temperature = OptionalInt.empty();
	private OptionalInt viscosity = OptionalInt.empty();
	private Optional<Rarity> rarity = Optional.empty();
	private OptionalDouble motionScale = OptionalDouble.empty();
	private Optional<Boolean> canPushEntity = Optional.empty();
	private Optional<Boolean> canSwim = Optional.empty();
	private OptionalDouble fallDistanceModifier = OptionalDouble.empty();
	private Optional<Boolean> canExtinguish = Optional.empty();
	private Optional<Boolean> canDrown = Optional.empty();
	private Optional<Boolean> supportsBoating = Optional.empty();
	private Optional<Boolean> canHydrate = Optional.empty();
	private Optional<Boolean> canConvertToSource = Optional.empty();

	public JAOPCAFluidType(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(FluidType.Properties.create().
				pathType(settings.getPathTypeFunction().apply(fluid.getMaterial())).
				adjacentPathType(settings.getAdjacentPathTypeFunction().apply(fluid.getMaterial())).
				sound(SoundActions.BUCKET_FILL, settings.getFillSoundSupplier().get()).
				sound(SoundActions.BUCKET_EMPTY, settings.getEmptySoundSupplier().get()).
				sound(SoundActions.FLUID_VAPORIZE, settings.getVaporizeSoundSupplier().get()));
		this.fluid = fluid;
		this.settings = settings;
	}

	@Override
	public IForm getForm() {
		return fluid.getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return fluid.getMaterial();
	}

	@Override
	public FluidState getStateForPlacement(BlockAndTintGetter world, BlockPos pos, FluidStack stack) {
		return fluid.getSourceState();
	}

	@Override
	public int getLightLevel() {
		if(!lightValue.isPresent()) {
			lightValue = OptionalInt.of(settings.getLightValueFunction().applyAsInt(getMaterial()));
		}
		return lightValue.getAsInt();
	}

	@Override
	public int getDensity() {
		if(!density.isPresent()) {
			density = OptionalInt.of(settings.getDensityFunction().applyAsInt(getMaterial()));
		}
		return density.getAsInt();
	}

	@Override
	public int getTemperature() {
		if(!temperature.isPresent()) {
			temperature = OptionalInt.of(settings.getTemperatureFunction().applyAsInt(getMaterial()));
		}
		return temperature.getAsInt();
	}

	@Override
	public int getViscosity() {
		if(!viscosity.isPresent()) {
			viscosity = OptionalInt.of(settings.getViscosityFunction().applyAsInt(getMaterial()));
		}
		return viscosity.getAsInt();
	}

	@Override
	public Rarity getRarity() {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(getMaterial()));
		}
		return rarity.get();
	}

	@Override
	public double motionScale(Entity entity) {
		if(!motionScale.isPresent()) {
			motionScale = OptionalDouble.of(settings.getMotionScaleFunction().applyAsDouble(getMaterial()));
		}
		return motionScale.getAsDouble();
	}

	@Override
	public boolean canPushEntity(Entity entity) {
		if(!canPushEntity.isPresent()) {
			canPushEntity = Optional.of(settings.getCanPushEntityFunction().test(getMaterial()));
		}
		return canPushEntity.get();
	}

	@Override
	public boolean canSwim(Entity entity) {
		if(!canSwim.isPresent()) {
			canSwim = Optional.of(settings.getCanSwimFunction().test(getMaterial()));
		}
		return canSwim.get();
	}

	@Override
	public float getFallDistanceModifier(Entity entity) {
		if(!fallDistanceModifier.isPresent()) {
			fallDistanceModifier = OptionalDouble.of(settings.getFallDistanceModifierFunction().applyAsDouble(getMaterial()));
		}
		return (float)fallDistanceModifier.getAsDouble();
	}

	@Override
	public boolean canExtinguish(Entity entity) {
		if(!canExtinguish.isPresent()) {
			canExtinguish = Optional.of(settings.getCanExtinguishFunction().test(getMaterial()));
		}
		return canExtinguish.get();
	}

	@Override
	public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos) {
		if(!canExtinguish.isPresent()) {
			canExtinguish = Optional.of(settings.getCanExtinguishFunction().test(getMaterial()));
		}
		return canExtinguish.get();
	}

	@Override
	public boolean canDrownIn(LivingEntity entity) {
		if(!canDrown.isPresent()) {
			canDrown = Optional.of(settings.getCanDrownFunction().test(getMaterial()));
		}
		return canDrown.get();
	}

	@Override
	public boolean supportsBoating(Boat boat) {
		if(!supportsBoating.isPresent()) {
			supportsBoating = Optional.of(settings.getSupportsBoatingFunction().test(getMaterial()));
		}
		return supportsBoating.get();
	}

	@Override
	public boolean canHydrate(Entity entity) {
		if(!canHydrate.isPresent()) {
			canHydrate = Optional.of(settings.getCanHydrateFunction().test(getMaterial()));
		}
		return canHydrate.get();
	}

	@Override
	public boolean canHydrate(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos) {
		if(!canHydrate.isPresent()) {
			canHydrate = Optional.of(settings.getCanHydrateFunction().test(getMaterial()));
		}
		return canHydrate.get();
	}

	@Override
	public boolean canHydrate(FluidStack stack) {
		if(!canHydrate.isPresent()) {
			canHydrate = Optional.of(settings.getCanHydrateFunction().test(getMaterial()));
		}
		return canHydrate.get();
	}

	@Override
	public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
		if(!canConvertToSource.isPresent()) {
			canConvertToSource = Optional.of(settings.getCanConvertToSourceFunction().test(getMaterial()));
		}
		return canConvertToSource.get();
	}

	@Override
	public boolean canConvertToSource(FluidStack stack) {
		if(!canConvertToSource.isPresent()) {
			canConvertToSource = Optional.of(settings.getCanConvertToSourceFunction().test(getMaterial()));
		}
		return canConvertToSource.get();
	}

	@Override
	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public int getTintColor() {
				return fluid.getMaterial().getColor();
			}
			@Override
			public ResourceLocation getStillTexture() {
				ResourceLocation location = BuiltInRegistries.FLUID.getKey(fluid.toFluid());
				if(MiscHelper.INSTANCE.hasResource(
						new ResourceLocation(location.getNamespace(),
								"textures/fluid/"+location.getPath()+"_still.png"))) {
					return new ResourceLocation(location.getNamespace(), "fluid/"+location.getPath()+"_still");
				}
				return new ResourceLocation(location.getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_still");
			}
			@Override
			public ResourceLocation getFlowingTexture() {
				ResourceLocation location = BuiltInRegistries.FLUID.getKey(fluid.toFluid());
				if(MiscHelper.INSTANCE.hasResource(
						new ResourceLocation(location.getNamespace(),
								"textures/fluid/"+location.getPath()+"_flow.png"))) {
					return new ResourceLocation(location.getNamespace(), "fluid/"+location.getPath()+"_flow");
				}
				return new ResourceLocation(location.getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_flow");
			}
		});
	}

	@Override
	public Component getDescription() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("fluid_type.jaopca."+fluid.getForm().getName(), fluid.getMaterial(), getDescriptionId());
	}

	@Override
	public Component getDescription(FluidStack stack) {
		return getDescription();
	}
}
