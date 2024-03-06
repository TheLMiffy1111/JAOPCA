package thelm.jaopca.registries;

import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Suppliers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegistryHandler {

	private RegistryHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<ResourceLocation, DeferredRegister<?>> DEFERRED_REGISTERS = new TreeMap<>();
	private static final Supplier<IEventBus> EVENT_BUS = Suppliers.memoize(
			()->((FMLModContainer)ModList.get().getModContainerById("jaopca").get()).getEventBus());

	public static <T, I extends T> DeferredHolder<T, I> registerRegistryEntry(ResourceKey<? extends Registry<T>> registry, String name, Supplier<I> entry) {
		Objects.requireNonNull(entry);
		return ((DeferredRegister<T>)DEFERRED_REGISTERS.computeIfAbsent(registry.location(), r->{
			DeferredRegister<?> register = DeferredRegister.create(registry, "jaopca");
			register.register(EVENT_BUS.get());
			return register;
		})).register(name, entry);
	}

	public static <T, I extends T> DeferredHolder<T, I> registerRegistryEntry(ResourceLocation registry, String name, Supplier<I> entry) {
		Objects.requireNonNull(entry);
		return ((DeferredRegister<T>)DEFERRED_REGISTERS.computeIfAbsent(registry, r->{
			DeferredRegister<?> register = DeferredRegister.create(registry, "jaopca");
			register.register(EVENT_BUS.get());
			return register;
		})).register(name, entry);
	}
}
