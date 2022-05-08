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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class RegistryHandler {

	private RegistryHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<ResourceLocation, DeferredRegister<?>> DEFERRED_REGISTERS = new TreeMap<>();
	private static final Supplier<IEventBus> EVENT_BUS = Suppliers.memoize(
			()->((FMLModContainer)ModList.get().getModContainerById("jaopca").get()).getEventBus());

	public static <T extends IForgeRegistryEntry<T>, I extends T> RegistryObject<I> registerForgeRegistryEntry(ResourceKey<? extends Registry<T>> registry, String name, Supplier<I> entry) {
		Objects.requireNonNull(entry);
		return ((DeferredRegister<T>)DEFERRED_REGISTERS.computeIfAbsent(registry.location(), r->{
			DeferredRegister<?> register = DeferredRegister.create(registry, "jaopca");
			register.register(EVENT_BUS.get());
			return register;
		})).register(name, entry);
	}

	public static <T extends IForgeRegistryEntry<T>, I extends T> RegistryObject<I> registerForgeRegistryEntry(ResourceLocation registry, String name, Supplier<I> entry) {
		Objects.requireNonNull(entry);
		return ((DeferredRegister<T>)DEFERRED_REGISTERS.computeIfAbsent(registry, r->{
			DeferredRegister<?> register = DeferredRegister.create(registry, "jaopca");
			register.register(EVENT_BUS.get());
			return register;
		})).register(name, entry);
	}
}
