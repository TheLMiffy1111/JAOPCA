package thelm.jaopca.api.custom;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;

public class BuilderCodecBuilder<O> implements Function<Instance<O>, App<Mu<O>, O>> {

	private Function<Instance<O>, App<Mu<O>, O>> function;

	private BuilderCodecBuilder(Function<Instance<O>, App<Mu<O>, O>> function) {
		this.function = function;
	}

	public static <O> BuilderCodecBuilder<O> of(Function<Instance<O>, App<Mu<O>, O>> function) {
		return new BuilderCodecBuilder<>(function);
	}

	@Override
	public App<Mu<O>, O> apply(Instance<O> t) {
		return function.apply(t);
	}

	public <F> BuilderCodecBuilder<O> withField(MapCodec<Optional<F>> field, Function<O, F> getter, BiFunction<O, F, O> setter) {
		function = instance->instance.apply2(
				(o, f1)->(f1.isPresent() ? setter.apply(o, f1.get()) : o),
				function.apply(instance),
				field.forGetter(o->Optional.ofNullable(getter.apply(o))));
		return this;
	}

	public Codec<O> build() {
		return RecordCodecBuilder.create(this);
	}
}
