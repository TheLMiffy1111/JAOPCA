package thelm.jaopca.api.custom;

import java.util.ArrayList;
import java.util.List;
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

	private final Function<Instance<O>, App<Mu<O>, O>> function;
	private final List<BiFunction<Instance<O>, App<Mu<O>, O>, App<Mu<O>, O>>> fields = new ArrayList<>();

	private BuilderCodecBuilder(Function<Instance<O>, App<Mu<O>, O>> function) {
		this.function = function;
	}

	public static <O> BuilderCodecBuilder<O> of(Function<Instance<O>, App<Mu<O>, O>> function) {
		return new BuilderCodecBuilder<>(function);
	}

	@Override
	public App<Mu<O>, O> apply(Instance<O> instance) {
		App<Mu<O>, O> app = function.apply(instance);
		for(var field : fields) {
			app = field.apply(instance, app);
		}
		return app;
	}

	public <F> BuilderCodecBuilder<O> withField(MapCodec<Optional<F>> field, Function<O, F> getter, BiFunction<O, F, O> setter) {
		fields.add((instance, app)->instance.apply2(
				(o, f)->(f.isPresent() ? setter.apply(o, f.get()) : o),
				app, field.forGetter(o->Optional.ofNullable(getter.apply(o)))));
		return this;
	}

	public Codec<O> build() {
		return RecordCodecBuilder.create(this);
	}
}
