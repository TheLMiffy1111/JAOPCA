package thelm.jaopca.api.custom;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.google.common.base.Functions;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.functions.MaterialDoubleFunction;
import thelm.jaopca.api.functions.MaterialFunction;
import thelm.jaopca.api.functions.MaterialIntFunction;
import thelm.jaopca.api.functions.MaterialLongFunction;
import thelm.jaopca.api.functions.MaterialMappedFunction;
import thelm.jaopca.api.functions.MaterialPredicate;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.mixins.DeferredSoundTypeAccessor;

public class CustomCodecs {

	private CustomCodecs() {}

	public static <T> Function<Either<? extends T, ? extends T>, T> eitherIdentity() {
		return either->either.map(Function.identity(), Function.identity());
	}

	public static <T> Codec<Collection<T>> collectionOf(Codec<T> codec) {
		return codec.listOf().xmap(Function.identity(), List::copyOf);
	}

	public static <T> Codec<Set<T>> setOf(Codec<T> codec) {
		return codec.listOf().xmap(Set::copyOf, List::copyOf);
	}

	public static <T> Codec<List<T>> listOrSingle(Codec<T> codec) {
		return Codec.either(codec.listOf(), codec).
				xmap(either->either.map(Function.identity(), List::of), Either::left);
	}

	public static <T extends Enum<T>> Codec<T> enumByName(Class<T> enumClass) {
		return Codec.STRING.comapFlatMap(
				DataResult.partialGet(
						name->Arrays.stream(enumClass.getEnumConstants()).filter(value->value.name().equalsIgnoreCase(name)).findAny().orElse(null),
						()->"Unknown enum "),
				value->value.name().toLowerCase(Locale.US));
	}

	public static <T> Codec<Object2BooleanMap<T>> obj2BoolMap(Codec<T> codec) {
		return Codec.unboundedMap(codec, Codec.BOOL).
				xmap(Object2BooleanOpenHashMap::new, Object2BooleanOpenHashMap::new);
	}

	public static <T> Codec<Object2IntMap<T>> obj2IntMap(Codec<T> codec) {
		return Codec.unboundedMap(codec, Codec.INT).
				xmap(Object2IntOpenHashMap::new, Object2IntOpenHashMap::new);
	}

	public static <T> Codec<Object2DoubleMap<T>> obj2DoubleMap(Codec<T> codec) {
		return Codec.unboundedMap(codec, Codec.DOUBLE).
				xmap(Object2DoubleOpenHashMap::new, Object2DoubleOpenHashMap::new);
	}

	public static <T> Codec<Object2LongMap<T>> obj2LongMap(Codec<T> codec) {
		return Codec.unboundedMap(codec, Codec.LONG).
				xmap(Object2LongOpenHashMap::new, Object2LongOpenHashMap::new);
	}

	public static final <O> BuilderCodecBuilder<O> builder(Function<Instance<O>, App<Mu<O>, O>> base) {
		return BuilderCodecBuilder.of(base);
	}

	public static final Codec<MaterialType> MATERIAL_TYPE = StringRepresentable.fromValues(MaterialType::values);

	public static final Codec<IMaterial> MATERIAL = Codec.STRING.comapFlatMap(
			DataResult.partialGet(JAOPCAApi.instance()::getMaterial, ()->"Unknown material "), IMaterial::getName);

	public static final Codec<IFormType> FORM_TYPE = Codec.STRING.comapFlatMap(
			DataResult.partialGet(JAOPCAApi.instance()::getFormType, ()->"Unknown form type "), IFormType::getName);

	public static <R, T extends R> Codec<DeferredHolder<R, T>> deferredHolder(ResourceKey<? extends Registry<R>> registry) {
		return ResourceKey.codec(registry).xmap(DeferredHolder::create, DeferredHolder::getKey);
	}

	public static <R> Codec<Supplier<R>> deferredHolderSupplier(ResourceKey<? extends Registry<R>> registry) {
		return deferredHolder(registry).xmap(Function.identity(), supplier->{
			if(supplier instanceof DeferredHolder<?, ?>) {
				return (DeferredHolder<R, R>)supplier;
			}
			return DeferredHolder.create(registry, ((Registry<R>)BuiltInRegistries.REGISTRY.get(registry.registry())).getKey(supplier.get()));
		});
	}

	public static Codec<Predicate<IMaterial>> materialPredicate(boolean defaultValue) {
		return Codec.<Boolean, Predicate<IMaterial>>either(
				Codec.BOOL, RecordCodecBuilder.create(
						instance->instance.group(
								Codec.BOOL.optionalFieldOf("default", defaultValue).forGetter(f->f.defaultValue),
								obj2BoolMap(MATERIAL_TYPE).
								optionalFieldOf("materialTypes", Object2BooleanMaps.emptyMap()).
								forGetter(f->f.materialTypes),
								obj2BoolMap(MATERIAL).
								optionalFieldOf("materials", Object2BooleanMaps.emptyMap()).
								forGetter(f->f.materials),
								Codec.STRING.optionalFieldOf("path", "").forGetter(f->f.path),
								Codec.STRING.optionalFieldOf("comment", "").forGetter(f->f.comment)).
						apply(instance, MaterialPredicate::of)).
				xmap(Function.identity(), f->MaterialPredicate.of(defaultValue, f))).
				xmap(either->either.map(MaterialPredicate::of, Functions.identity()), Either::right);
	}

	public static Codec<ToIntFunction<IMaterial>> materialIntFunction(int defaultValue) {
		return Codec.<Integer, ToIntFunction<IMaterial>>either(
				Codec.INT, RecordCodecBuilder.create(
						instance->instance.group(
								Codec.INT.optionalFieldOf("default", defaultValue).forGetter(f->f.defaultValue),
								obj2IntMap(MATERIAL_TYPE).
								optionalFieldOf("materialTypes", Object2IntMaps.emptyMap()).
								forGetter(f->f.materialTypes),
								obj2IntMap(MATERIAL).
								optionalFieldOf("materials", Object2IntMaps.emptyMap()).
								forGetter(f->f.materials),
								Codec.STRING.optionalFieldOf("path", "").forGetter(f->f.path),
								Codec.STRING.optionalFieldOf("comment", "").forGetter(f->f.comment)).
						apply(instance, MaterialIntFunction::of)).
				xmap(Function.identity(), f->MaterialIntFunction.of(defaultValue, f))).
				xmap(either->either.map(MaterialIntFunction::of, Functions.identity()), Either::right);
	}

	public static Codec<ToDoubleFunction<IMaterial>> materialDoubleFunction(double defaultValue) {
		return Codec.<Double, ToDoubleFunction<IMaterial>>either(
				Codec.DOUBLE, RecordCodecBuilder.create(
						instance->instance.group(
								Codec.DOUBLE.optionalFieldOf("default", defaultValue).forGetter(f->f.defaultValue),
								obj2DoubleMap(MATERIAL_TYPE).
								optionalFieldOf("materialTypes", Object2DoubleMaps.emptyMap()).
								forGetter(f->f.materialTypes),
								obj2DoubleMap(MATERIAL).
								optionalFieldOf("materials", Object2DoubleMaps.emptyMap()).
								forGetter(f->f.materials),
								Codec.STRING.optionalFieldOf("path", "").forGetter(f->f.path),
								Codec.STRING.optionalFieldOf("comment", "").forGetter(f->f.comment)).
						apply(instance, MaterialDoubleFunction::of)).
				xmap(Function.identity(), f->MaterialDoubleFunction.of(defaultValue, f))).
				xmap(either->either.map(MaterialDoubleFunction::of, Functions.identity()), Either::right);
	}

	public static Codec<ToLongFunction<IMaterial>> materialLongFunction(long defaultValue) {
		return Codec.<Long, ToLongFunction<IMaterial>>either(
				Codec.LONG, RecordCodecBuilder.create(
						instance->instance.group(
								Codec.LONG.optionalFieldOf("default", defaultValue).forGetter(f->f.defaultValue),
								obj2LongMap(MATERIAL_TYPE).
								optionalFieldOf("materialTypes", Object2LongMaps.emptyMap()).
								forGetter(f->f.materialTypes),
								obj2LongMap(MATERIAL).
								optionalFieldOf("materials", Object2LongMaps.emptyMap()).
								forGetter(f->f.materials),
								Codec.STRING.optionalFieldOf("path", "").forGetter(f->f.path),
								Codec.STRING.optionalFieldOf("comment", "").forGetter(f->f.comment)).
						apply(instance, MaterialLongFunction::of)).
				xmap(Function.identity(), f->MaterialLongFunction.of(defaultValue, f))).
				xmap(either->either.map(MaterialLongFunction::of, Functions.identity()), Either::right);
	}

	public static <T> Codec<Function<IMaterial, T>> materialMappedFunction(Function<String, T> nameToValue, Function<T, String> valueToName, T defaultValue) {
		return Codec.<T, Function<IMaterial, T>>either(
				Codec.STRING.xmap(nameToValue, valueToName), RecordCodecBuilder.create(
						instance->instance.group(
								Codec.STRING.xmap(nameToValue, valueToName).
								optionalFieldOf("default", defaultValue).
								forGetter(f->f.defaultValue),
								Codec.unboundedMap(MATERIAL_TYPE, Codec.STRING.xmap(nameToValue, valueToName)).
								optionalFieldOf("materialTypes", Map.of()).
								forGetter(f->f.materialTypes),
								Codec.unboundedMap(MATERIAL, Codec.STRING.xmap(nameToValue, valueToName)).
								optionalFieldOf("materials", Map.of()).
								forGetter(f->f.materials),
								Codec.STRING.optionalFieldOf("path", "").forGetter(f->f.path),
								Codec.STRING.optionalFieldOf("comment", "").forGetter(f->f.comment),
								Codec.unit(nameToValue).fieldOf("nameToValue").forGetter(f->f.nameToValue),
								Codec.unit(valueToName).fieldOf("valueToName").forGetter(f->f.valueToName)).
						apply(instance, MaterialMappedFunction::of)).
				xmap(Function.identity(), f->MaterialMappedFunction.of(defaultValue, f, nameToValue, valueToName))).
				xmap(either->either.map(v->MaterialMappedFunction.of(v, nameToValue, valueToName), Functions.identity()), Either::right);
	}

	public static Codec<Function<IMaterial, String>> materialStringFunction(String defaultValue) {
		return materialMappedFunction(Function.identity(), Function.identity(), defaultValue);
	}

	public static <T extends Enum<T>> Codec<Function<IMaterial, T>> materialEnumFunction(Class<T> enumClass, T defaultValue) {
		return materialMappedFunction(
				name->Arrays.stream(enumClass.getEnumConstants()).filter(value->value.name().equalsIgnoreCase(name)).findAny().orElse(null),
				value->value == null ? "" : value.name().toLowerCase(Locale.US),
						defaultValue);
	}

	public static <T> Codec<Function<IMaterial, T>> materialFunction(Codec<T> codec, T defaultValue) {
		return Codec.<T, Function<IMaterial, T>>either(
				codec, RecordCodecBuilder.create(
						instance->instance.group(
								codec.optionalFieldOf("default", defaultValue).forGetter(f->f.defaultValue),
								Codec.unboundedMap(CustomCodecs.MATERIAL_TYPE, codec).
								optionalFieldOf("materialTypes", Map.of()).
								forGetter(f->f.materialTypes),
								Codec.unboundedMap(CustomCodecs.MATERIAL, codec).
								optionalFieldOf("materials", Map.of()).
								forGetter(f->f.materials)).
						apply(instance, MaterialFunction::of)).
				xmap(Function.identity(), f->MaterialFunction.of(defaultValue, f))).
				xmap(either->either.map(MaterialFunction::of, Functions.identity()), Either::right);
	}

	public static final Codec<MapColor> MAP_COLOR = StringRepresentable.fromValues(MapColorType::values).
			xmap(MapColorType::toMapColor, MapColorType::fromMapColor);

	public static Codec<Function<IMaterial, MapColor>> materialMapColorFunction(MapColor defaultValue) {
		return materialMappedFunction(MapColorType::nameToMapColor, MapColorType::mapColorToName, defaultValue);
	}

	public static final Codec<SoundType> VANILLA_SOUND_TYPE = StringRepresentable.fromValues(VanillaSoundType::values).
			xmap(VanillaSoundType::toSoundType, VanillaSoundType::fromSoundType);

	public static final Codec<DeferredSoundType> DEFERRED_SOUND_TYPE = RecordCodecBuilder.create(
			instance->instance.group(
					Codec.FLOAT.fieldOf("volume").forGetter(DeferredSoundType::getVolume),
					Codec.FLOAT.fieldOf("pitch").forGetter(DeferredSoundType::getPitch),
					deferredHolderSupplier(Registries.SOUND_EVENT).
					fieldOf("break").
					forGetter(st->((DeferredSoundTypeAccessor)st).breakSound()),
					deferredHolderSupplier(Registries.SOUND_EVENT).
					fieldOf("step").
					forGetter(st->((DeferredSoundTypeAccessor)st).stepSound()),
					deferredHolderSupplier(Registries.SOUND_EVENT).
					fieldOf("place").
					forGetter(st->((DeferredSoundTypeAccessor)st).placeSound()),
					deferredHolderSupplier(Registries.SOUND_EVENT).
					fieldOf("hit").
					forGetter(st->((DeferredSoundTypeAccessor)st).hitSound()),
					deferredHolderSupplier(Registries.SOUND_EVENT).
					fieldOf("fall").
					forGetter(st->((DeferredSoundTypeAccessor)st).fallSound())).
			apply(instance, DeferredSoundType::new));

	public static final Codec<SoundType> SOUND_TYPE = Codec.either(VANILLA_SOUND_TYPE, DEFERRED_SOUND_TYPE).
			xmap(eitherIdentity(), st->st instanceof DeferredSoundType dst ? Either.right(dst) : Either.left(st));

	public static Codec<Function<IMaterial, SoundType>> materialSoundTypeFunction(SoundType defaultValue) {
		return materialFunction(SOUND_TYPE, defaultValue);
	}

	public static final Codec<AABB> AABB = RecordCodecBuilder.create(
			instance->instance.group(
					Vec3.CODEC.fieldOf("from").forGetter(aabb->new Vec3(aabb.minX, aabb.minY, aabb.minZ)),
					Vec3.CODEC.fieldOf("to").forGetter(aabb->new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ))).
			apply(instance, AABB::new));

	public static final Codec<VoxelShape> VOXEL_SHAPE = listOrSingle(AABB).
			xmap(aabbs->aabbs.stream().map(Shapes::create).reduce(Shapes.empty(), Shapes::or), VoxelShape::toAabbs);
}
