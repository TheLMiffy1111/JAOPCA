package thelm.jaopca.forms;

import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.items.ItemFormType;

public class FormTypeHandler {

	private FormTypeHandler() {}

	private static final TreeMap<String, IFormType> FORM_TYPES = new TreeMap<>();

	public static boolean registerFormType(IFormType type) {
		Objects.requireNonNull(type);
		return FORM_TYPES.putIfAbsent(type.getName(), type) == null;
	}

	public static IFormType getFormType(String name) {
		return FORM_TYPES.get(name);
	}

	public static void registerMaterialForms() {
		BlockFormType.INSTANCE.registerMaterialForms();
		ItemFormType.INSTANCE.registerMaterialForms();
		FluidFormType.INSTANCE.registerMaterialForms();
		for(IFormType formType : FORM_TYPES.values()) {
			if(formType == BlockFormType.INSTANCE || formType == ItemFormType.INSTANCE || formType == FluidFormType.INSTANCE) {
				continue;
			}
			formType.registerMaterialForms();
		}
	}

	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		for(IFormType formType : FORM_TYPES.values()) {
			formType.onRegisterCapabilities(event);
		}
	}

	public static void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		for(IFormType formType : FORM_TYPES.values()) {
			formType.addToCreativeModeTab(parameters, output);
		}
	}

	public static void addBlockModelRemaps(Set<ResourceLocation> availableLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {
		for(IFormType formType : FORM_TYPES.values()) {
			formType.addBlockModelRemaps(availableLocations, output);
		}
	}

	public static void addItemModelRemaps(Set<ResourceLocation> availableLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {
		for(IFormType formType : FORM_TYPES.values()) {
			formType.addItemModelRemaps(availableLocations, output);
		}
	}
}
