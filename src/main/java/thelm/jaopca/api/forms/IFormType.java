package thelm.jaopca.api.forms;

import java.util.Set;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;

public interface IFormType extends Comparable<IFormType> {

	String getName();

	void addForm(IForm form);

	Set<IForm> getForms();

	boolean shouldRegister(IForm form, IMaterial material);

	IFormSettings getNewSettings();

	Codec<IFormSettings> formSettingsCodec();

	IMaterialFormInfo getMaterialFormInfo(IForm form, IMaterial material);

	default void registerMaterialForms() {}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}

	default void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {}

	default void addBlockModelRemaps(Set<ResourceLocation> allLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {}

	default void addItemModelRemaps(Set<ResourceLocation> allLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {}

	@Override
	default int compareTo(IFormType other) {
		return getName().compareTo(other.getName());
	}
}
