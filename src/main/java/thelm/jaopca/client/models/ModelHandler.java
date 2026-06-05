package thelm.jaopca.client.models;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.MiscHelper;

public class ModelHandler {

	public static void registerFluidModels(RegisterFluidModelsEvent event) {
		for(IMaterialFormFluid materialFormFluid : FluidFormType.getFluids()) {
			Fluid fluid = materialFormFluid.toFluid();
			String modelType = materialFormFluid.getMaterial().getModelType();
			String formName = materialFormFluid.getForm().getName();
			Identifier key = BuiltInRegistries.FLUID.getKey(fluid);

			Material stillMaterial;
			if(MiscHelper.INSTANCE.hasResource(key.withPath("textures/fluid/%s_still.png"::formatted))) {
				stillMaterial = new Material(key.withPath("fluid/%s_still"::formatted));
			}
			else {
				stillMaterial = new Material(Identifier.fromNamespaceAndPath("jaopca", "fluid/%s/%s_still".formatted(modelType, formName)));
			}

			Material flowMaterial;
			if(MiscHelper.INSTANCE.hasResource(key.withPath("textures/fluid/%s_flow.png"::formatted))) {
				flowMaterial = new Material(key.withPath("fluid/%s_flow"::formatted));
			}
			else {
				flowMaterial = new Material(Identifier.fromNamespaceAndPath("jaopca", "fluid/%s/%s_flow".formatted(modelType, formName)));
			}

			event.register(new FluidModel.Unbaked(stillMaterial, flowMaterial, null, ColorHandler.FLUID_TINT), fluid);
		}
	}

	public static void remapItemModels(ModelEvent.ModifyBakingResult event) {
		Map<Identifier, ItemModel> itemModels = event.getBakingResult().itemStackModels();
		Map<Identifier, Identifier> itemModelRemaps = new LinkedHashMap<>();
		FormTypeHandler.addItemModelRemaps(Collections.unmodifiableSet(itemModels.keySet()), itemModelRemaps::putIfAbsent);
		itemModelRemaps.forEach((from, to) -> {
			if(itemModels.containsKey(to)) {
				itemModels.put(from, itemModels.get(to));
			}
		});
	}
}
