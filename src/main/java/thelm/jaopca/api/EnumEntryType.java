package thelm.jaopca.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraftforge.common.util.EnumHelper;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.fluid.FluidProperties;
import thelm.jaopca.api.item.ItemProperties;

public enum EnumEntryType {

	BLOCK ("thelm.jaopca.registry.RegistryCore#checkEntry","thelm.jaopca.registry.RegistryCore#registerBlocks" ,BlockProperties.DEFAULT,"thelm.jaopca.custom.json.ItemRequestDeserializer#parseBlockPpt"),
	ITEM  ("thelm.jaopca.registry.RegistryCore#checkEntry","thelm.jaopca.registry.RegistryCore#registerItems"  ,ItemProperties.DEFAULT, "thelm.jaopca.custom.json.ItemRequestDeserializer#parseItemPpt" ),
	FLUID ("thelm.jaopca.registry.RegistryCore#checkEntry","thelm.jaopca.registry.RegistryCore#registerFluids" ,FluidProperties.DEFAULT,"thelm.jaopca.custom.json.ItemRequestDeserializer#parseFluidPpt"),
	CUSTOM("thelm.jaopca.registry.RegistryCore#checkEntry","thelm.jaopca.registry.RegistryCore#registerCustoms",null);

	public final BiPredicate<ItemEntry, IOreEntry> checker;
	public final Consumer<ItemEntry> registerer;
	public final IProperties defaultPpt;
	public final BiFunction<JsonObject, JsonDeserializationContext, IProperties> pptDeserializerContexted;
	@Deprecated
	public final Function<JsonObject, IProperties> pptDeserializer;

	EnumEntryType(BiPredicate<ItemEntry, IOreEntry> checker, Consumer<ItemEntry> registerer, IProperties defaultPpt) {
		this.checker = checker;
		this.registerer = registerer;
		this.defaultPpt = defaultPpt == null ? ()->this : defaultPpt;
		this.pptDeserializerContexted = (jsonObject,context)->this.defaultPpt;
		this.pptDeserializer = jsonObject->pptDeserializerContexted.apply(jsonObject, null);
	}

	EnumEntryType(BiPredicate<ItemEntry, IOreEntry> checker, Consumer<ItemEntry> registerer, IProperties defaultPpt, BiFunction<JsonObject, JsonDeserializationContext, IProperties> pptDeserializer) {
		this.checker = checker;
		this.registerer = registerer;
		this.defaultPpt = defaultPpt == null ? ()->this : defaultPpt;
		this.pptDeserializerContexted = pptDeserializer;
		this.pptDeserializer = jsonObject->pptDeserializerContexted.apply(jsonObject, null);
	}

	EnumEntryType(String checker, String registerer, IProperties defaultPpt) {
		this.checker = getChecker(checker);
		this.registerer = getRegisterer(registerer);
		this.defaultPpt = defaultPpt == null ? ()->this : defaultPpt;
		this.pptDeserializerContexted = (jsonObject,context)->this.defaultPpt;
		this.pptDeserializer = jsonObject->pptDeserializerContexted.apply(jsonObject, null);
	}

	EnumEntryType(String checker, String registerer, IProperties defaultPpt, String pptDeserializer) {
		this.checker = getChecker(checker);
		this.registerer = getRegisterer(registerer);
		this.defaultPpt = defaultPpt == null ? ()->this : defaultPpt;
		this.pptDeserializerContexted = getPptDeserializer(pptDeserializer, this.defaultPpt);
		this.pptDeserializer = jsonObject->pptDeserializerContexted.apply(jsonObject, null);
	}

	static Method getMethodUncatched(String methodName, Class<?>... classes) throws ClassNotFoundException, NoSuchMethodException {
		try {
			String[] methodNames = methodName.split("#");
			Class<?> methodClass = Class.forName(methodNames[0]);
			Method method = methodClass.getDeclaredMethod(methodNames[1], classes);
			method.setAccessible(true);
			return method;
		}
		catch(SecurityException e) {
			throw new IllegalArgumentException("not a valid method", e);
		}
	}

	static Method getMethod(String methodName, Class<?>... classes) throws ClassNotFoundException {
		try {
			return getMethodUncatched(methodName, classes);
		}
		catch(NoSuchMethodException e) {
			throw new IllegalArgumentException("not a valid method", e);
		}
	}

	static BiPredicate<ItemEntry, IOreEntry> getChecker(String checker) {
		try {
			Method checkerMethod = getMethod(checker, ItemEntry.class, IOreEntry.class);
			if(!Boolean.TYPE.isAssignableFrom(checkerMethod.getReturnType())) {
				throw new IllegalArgumentException("not a valid method");
			}
			return (entry, ore)->{
				try {
					return (Boolean)checkerMethod.invoke(null, entry, ore);
				}
				catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		}
		catch(ClassNotFoundException e) {
			return (entry, ore)->false;
		}
	}

	static Consumer<ItemEntry> getRegisterer(String registerer) {
		try {
			Method registererMethod = getMethod(registerer, ItemEntry.class);
			return entry->{
				try {
					registererMethod.invoke(null, entry);
				}
				catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		}
		catch(ClassNotFoundException e) {
			return entry->{};
		}
	}

	static BiFunction<JsonObject, JsonDeserializationContext, IProperties> getPptDeserializer(String pptDeserializer, IProperties defaultPpt) {
		try {
			Method pptDeserializerMethod = getMethodUncatched(pptDeserializer, JsonObject.class, JsonDeserializationContext.class);
			if(!IProperties.class.isAssignableFrom(pptDeserializerMethod.getReturnType())) {
				throw new IllegalArgumentException("not a valid method");
			}
			return (jsonObject,context)->{
				try {
					return (IProperties)pptDeserializerMethod.invoke(null, jsonObject, context);
				}
				catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		}
		catch(ClassNotFoundException e) {
			return (jsonObject,context)->defaultPpt;
		}
		catch(NoSuchMethodException e) {
			try {
				Method pptDeserializerMethod = getMethod(pptDeserializer, JsonObject.class);
				if(!IProperties.class.isAssignableFrom(pptDeserializerMethod.getReturnType())) {
					throw new IllegalArgumentException("not a valid method");
				}
				return (jsonObject,context)->{
					try {
						return (IProperties)pptDeserializerMethod.invoke(null, jsonObject);
					}
					catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
						throw new RuntimeException(e1);
					}
				};
			}
			catch(ClassNotFoundException e1) {
				return (jsonObject,context)->defaultPpt;
			}
		}
	}

	public static EnumEntryType addEntryType(String name, BiPredicate<ItemEntry, IOreEntry> checker, Consumer<ItemEntry> registerer, IProperties defaultPpt) {
		return EnumHelper.<EnumEntryType>addEnum(EnumEntryType.class, name.toUpperCase(Locale.US), new Class<?>[] {BiPredicate.class, Consumer.class, IProperties.class}, new Object[] {checker, registerer, defaultPpt});
	}

	public static EnumEntryType addEntryType(String name, BiPredicate<ItemEntry, IOreEntry> checker, Consumer<ItemEntry> registerer, IProperties defaultPpt, BiFunction<JsonObject, JsonDeserializationContext, IProperties> pptDeserializer) {
		return EnumHelper.<EnumEntryType>addEnum(EnumEntryType.class, name.toUpperCase(Locale.US), new Class<?>[] {BiPredicate.class, Consumer.class, IProperties.class, BiFunction.class}, new Object[] {checker, registerer, defaultPpt, pptDeserializer});
	}

	public static EnumEntryType addEntryType(String name, String checker, String registerer, IProperties defaultPpt) {
		return EnumHelper.<EnumEntryType>addEnum(EnumEntryType.class, name.toUpperCase(Locale.US), new Class<?>[] {String.class, String.class, IProperties.class}, new Object[] {checker, registerer, defaultPpt});
	}

	public static EnumEntryType addEntryType(String name, String checker, String registerer, IProperties defaultPpt, String pptDeserializer) {
		return EnumHelper.<EnumEntryType>addEnum(EnumEntryType.class, name.toUpperCase(Locale.US), new Class<?>[] {String.class, String.class, IProperties.class, String.class}, new Object[] {checker, registerer, defaultPpt, pptDeserializer});
	}

	public static EnumEntryType fromName(String name) {
		for(EnumEntryType type : values()) {
			if(type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
