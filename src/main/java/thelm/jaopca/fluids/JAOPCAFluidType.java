package thelm.jaopca.fluids;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

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
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAFluidType extends FluidType implements IMaterialFormFluidType {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	private IntSupplier lightValue;
	private IntSupplier density;
	private IntSupplier temperature;
	private IntSupplier viscosity;
	private Supplier<Rarity> rarity;
	private DoubleSupplier motionScale;
	private BooleanSupplier canPushEntity;
	private BooleanSupplier canSwim;
	private DoubleSupplier fallDistanceModifier;
	private BooleanSupplier canExtinguish;
	private BooleanSupplier canDrown;
	private BooleanSupplier supportsBoating;
	private BooleanSupplier canHydrate;
	private BooleanSupplier canConvertToSource;

	public JAOPCAFluidType(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(getProperties(fluid, settings));
		this.fluid = fluid;
		this.settings = settings;

		lightValue = MemoizingSuppliers.of(settings.getLightValueFunction(), fluid::getMaterial);
		density = MemoizingSuppliers.of(settings.getDensityFunction(), fluid::getMaterial);
		temperature = MemoizingSuppliers.of(settings.getTemperatureFunction(), fluid::getMaterial);
		viscosity = MemoizingSuppliers.of(settings.getViscosityFunction(), fluid::getMaterial);
		rarity = MemoizingSuppliers.of(settings.getDisplayRarityFunction(), fluid::getMaterial);
		motionScale = MemoizingSuppliers.of(settings.getMotionScaleFunction(), fluid::getMaterial);
		canPushEntity = MemoizingSuppliers.of(settings.getCanPushEntityFunction(), fluid::getMaterial);
		canSwim = MemoizingSuppliers.of(settings.getCanSwimFunction(), fluid::getMaterial);
		fallDistanceModifier = MemoizingSuppliers.of(settings.getFallDistanceModifierFunction(), fluid::getMaterial);
		canExtinguish = MemoizingSuppliers.of(settings.getCanExtinguishFunction(), fluid::getMaterial);
		canDrown = MemoizingSuppliers.of(settings.getCanDrownFunction(), fluid::getMaterial);
		supportsBoating = MemoizingSuppliers.of(settings.getSupportsBoatingFunction(), fluid::getMaterial);
		canHydrate = MemoizingSuppliers.of(settings.getCanHydrateFunction(), fluid::getMaterial);
		canConvertToSource = MemoizingSuppliers.of(settings.getCanConvertToSourceFunction(), fluid::getMaterial);
	}

	public static FluidType.Properties getProperties(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		FluidType.Properties prop = FluidType.Properties.create();
		prop.pathType(settings.getPathTypeFunction().apply(fluid.getMaterial()));
		prop.adjacentPathType(settings.getAdjacentPathTypeFunction().apply(fluid.getMaterial()));
		prop.sound(SoundActions.BUCKET_FILL, settings.getFillSoundSupplier().get());
		prop.sound(SoundActions.BUCKET_EMPTY, settings.getEmptySoundSupplier().get());
		prop.sound(SoundActions.FLUID_VAPORIZE, settings.getVaporizeSoundSupplier().get());
		return prop;
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
		return lightValue.getAsInt();
	}

	@Override
	public int getDensity() {
		return density.getAsInt();
	}

	@Override
	public int getTemperature() {
		return temperature.getAsInt();
	}

	@Override
	public int getViscosity() {
		return viscosity.getAsInt();
	}

	@Override
	public Rarity getRarity() {
		return rarity.get();
	}

	@Override
	public double motionScale(Entity entity) {
		return motionScale.getAsDouble();
	}

	@Override
	public boolean canPushEntity(Entity entity) {
		return canPushEntity.getAsBoolean();
	}

	@Override
	public boolean canSwim(Entity entity) {
		return canSwim.getAsBoolean();
	}

	@Override
	public float getFallDistanceModifier(Entity entity) {
		return (float)fallDistanceModifier.getAsDouble();
	}

	@Override
	public boolean canExtinguish(Entity entity) {
		return canExtinguish.getAsBoolean();
	}

	@Override
	public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos) {
		return canExtinguish.getAsBoolean();
	}

	@Override
	public boolean canDrownIn(LivingEntity entity) {
		return canDrown.getAsBoolean();
	}

	@Override
	public boolean supportsBoating(Boat boat) {
		return supportsBoating.getAsBoolean();
	}

	@Override
	public boolean canHydrate(Entity entity) {
		return canHydrate.getAsBoolean();
	}

	@Override
	public boolean canHydrate(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos) {
		return canHydrate.getAsBoolean();
	}

	@Override
	public boolean canHydrate(FluidStack stack) {
		return canHydrate.getAsBoolean();
	}

	@Override
	public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
		return canConvertToSource.getAsBoolean();
	}

	@Override
	public boolean canConvertToSource(FluidStack stack) {
		return canConvertToSource.getAsBoolean();
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
						ResourceLocation.fromNamespaceAndPath(location.getNamespace(),
								"textures/fluid/"+location.getPath()+"_still.png"))) {
					return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "fluid/"+location.getPath()+"_still");
				}
				return ResourceLocation.fromNamespaceAndPath(location.getNamespace(),
						"fluid/"+fluid.getMaterial().getModelType()+'/'+fluid.getForm().getName()+"_still");
			}
			@Override
			public ResourceLocation getFlowingTexture() {
				ResourceLocation location = BuiltInRegistries.FLUID.getKey(fluid.toFluid());
				if(MiscHelper.INSTANCE.hasResource(
						ResourceLocation.fromNamespaceAndPath(location.getNamespace(),
								"textures/fluid/"+location.getPath()+"_flow.png"))) {
					return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "fluid/"+location.getPath()+"_flow");
				}
				return ResourceLocation.fromNamespaceAndPath(location.getNamespace(),
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
