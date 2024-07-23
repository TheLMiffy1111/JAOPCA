package thelm.jaopca.fluids;

import com.google.common.base.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathType;
import thelm.jaopca.api.custom.CustomCodecs;
import thelm.jaopca.api.custom.MapColorType;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.forms.IFormSettings;

public class FluidCustomCodecs {

	private FluidCustomCodecs() {};

	public static final Codec<IFormSettings> FLUID_FORM_SETTINGS =
			CustomCodecs.<IFluidFormSettings>builder(
					instance->RecordCodecBuilder.point(FluidFormType.INSTANCE.getNewSettings())).
			withField(
					CustomCodecs.materialIntFunction(8).optionalFieldOf("maxLevel"),
					s->s.getMaxLevelFunction(),
					(s, f)->s.setMaxLevelFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(5).optionalFieldOf("tickRate"),
					s->s.getTickRateFunction(),
					(s, f)->s.setTickRateFunction(f)).
			withField(
					CustomCodecs.materialDoubleFunction(100).optionalFieldOf("explosionResistance"),
					s->s.getExplosionResistanceFunction(),
					(s, f)->s.setExplosionResistanceFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(0).optionalFieldOf("lightValue"),
					s->s.getLightValueFunction(),
					(s, f)->s.setLightValueFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(1000).optionalFieldOf("density"),
					s->s.getDensityFunction(),
					(s, f)->s.setDensityFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(300).optionalFieldOf("temperature"),
					s->s.getTemperatureFunction(),
					(s, f)->s.setTemperatureFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(1000).optionalFieldOf("viscosity"),
					s->s.getViscosityFunction(),
					(s, f)->s.setViscosityFunction(f)).
			withField(
					CustomCodecs.materialEnumFunction(Rarity.class, Rarity.COMMON).optionalFieldOf("rarity"),
					s->s.getDisplayRarityFunction(),
					(s, f)->s.setDisplayRarityFunction(f)).
			withField(
					CustomCodecs.deferredHolderSupplier(Registries.SOUND_EVENT).optionalFieldOf("fillSound"),
					s->s.getFillSoundSupplier(),
					(s, f)->s.setFillSoundSupplier(f)).
			withField(
					CustomCodecs.deferredHolderSupplier(Registries.SOUND_EVENT).optionalFieldOf("emptySound"),
					s->s.getEmptySoundSupplier(),
					(s, f)->s.setEmptySoundSupplier(f)).
			withField(
					CustomCodecs.deferredHolderSupplier(Registries.SOUND_EVENT).optionalFieldOf("vaporizeSound"),
					s->s.getVaporizeSoundSupplier(),
					(s, f)->s.setVaporizeSoundSupplier(f)).
			withField(
					CustomCodecs.materialDoubleFunction(0.007/3).optionalFieldOf("motionScale"),
					s->s.getMotionScaleFunction(),
					(s, f)->s.setMotionScaleFunction(f)).
			withField(
					CustomCodecs.materialPredicate(true).optionalFieldOf("canPushEntity"),
					s->s.getCanPushEntityFunction(),
					(s, f)->s.setCanPushEntityFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("canSwim"),
					s->s.getCanSwimFunction(),
					(s, f)->s.setCanSwimFunction(f)).
			withField(
					CustomCodecs.materialDoubleFunction(0.5).optionalFieldOf("fallDistanceModifier"),
					s->s.getFallDistanceModifierFunction(),
					(s, f)->s.setFallDistanceModifierFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("canExtinguish"),
					s->s.getCanExtinguishFunction(),
					(s, f)->s.setCanExtinguishFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("canDrown"),
					s->s.getCanDrownFunction(),
					(s, f)->s.setCanDrownFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("supportsBoating"),
					s->s.getSupportsBoatingFunction(),
					(s, f)->s.setSupportsBoatingFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("canHydrate"),
					s->s.getCanHydrateFunction(),
					(s, f)->s.setCanHydrateFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("canConvertToSource"),
					s->s.getCanConvertToSourceFunction(),
					(s, f)->s.setCanConvertToSourceFunction(f)).
			withField(
					CustomCodecs.materialEnumFunction(PathType.class, PathType.WATER).optionalFieldOf("pathType"),
					s->s.getPathTypeFunction(),
					(s, f)->s.setPathTypeFunction(f)).
			withField(
					CustomCodecs.materialEnumFunction(PathType.class, PathType.WATER_BORDER).optionalFieldOf("adjacentPathType"),
					s->s.getAdjacentPathTypeFunction(),
					(s, f)->s.setAdjacentPathTypeFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(1).optionalFieldOf("levelDecreasePerBlock"),
					s->s.getLevelDecreasePerBlockFunction(),
					(s, f)->s.setLevelDecreasePerBlockFunction(f)).
			withField(
					CustomCodecs.materialMappedFunction(MapColorType::nameToMapColor, MapColorType::mapColorToName, MapColor.METAL).optionalFieldOf("mapColor"),
					s->s.getMapColorFunction(),
					(s, f)->s.setMapColorFunction(f)).
			withField(
					CustomCodecs.materialDoubleFunction(5).optionalFieldOf("blockHardness"),
					s->s.getBlockHardnessFunction(),
					(s, f)->s.setBlockHardnessFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(0).optionalFieldOf("flammability"),
					s->s.getFlammabilityFunction(),
					(s, f)->s.setFlammabilityFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(0).optionalFieldOf("fireSpreadSpeed"),
					s->s.getFireSpreadSpeedFunction(),
					(s, f)->s.setFireSpreadSpeedFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("isFireSource"),
					s->s.getIsFireSourceFunction(),
					(s, f)->s.setIsFireSourceFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(0).optionalFieldOf("fireTime"),
					s->s.getFireTimeFunction(),
					(s, f)->s.setFireTimeFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(64).optionalFieldOf("maxStackSize"),
					s->s.getMaxStackSizeFunction(),
					(s, f)->s.setMaxStackSizeFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("hasEffect"),
					s->s.getHasEffectFunction(),
					(s, f)->s.setHasEffectFunction(f)).
			build().
			flatComapMap(Functions.identity(),
					s->s instanceof IFluidFormSettings fs ? DataResult.success(fs) : DataResult.error(()->"Not fluid form settings"));
}
