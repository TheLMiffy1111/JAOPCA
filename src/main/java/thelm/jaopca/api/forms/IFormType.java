package thelm.jaopca.api.forms;

import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import net.minecraft.world.item.CreativeModeTab;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;

public interface IFormType extends Comparable<IFormType> {

	String getName();

	void addForm(IForm form);

	Set<IForm> getForms();

	boolean shouldRegister(IForm form, IMaterial material);

	IFormSettings getNewSettings();

	GsonBuilder configureGsonBuilder(GsonBuilder builder);

	IFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context);

	IMaterialFormInfo getMaterialFormInfo(IForm form, IMaterial material);

	default void registerMaterialForms() {}

	default void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {}

	@Override
	default int compareTo(IFormType other) {
		return getName().compareTo(other.getName());
	}
}
