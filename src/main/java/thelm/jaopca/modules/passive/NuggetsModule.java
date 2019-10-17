package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule
public class NuggetsModule implements IModule {

	private final IForm nuggetForm = JAOPCAApi.instance().newForm(this, "nuggets", JAOPCAApi.instance().itemFormType()).
			setMaterialTypes(MaterialType.NON_DUSTS);

	@Override
	public String getName() {
		return "nuggets";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(nuggetForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = JAOPCAApi.instance();
		for(IMaterial material : nuggetForm.getMaterials()) {
			ResourceLocation materialLocation = api.miscHelper().getTagLocation(material.getType().getFormName(), material.getName());
			IItemInfo nuggetInfo = api.itemFormType().getMaterialFormInfo(nuggetForm, material);
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "nuggets.to_material."+material.getName()),
					materialLocation, 1, new Object[] {
							nuggetInfo, nuggetInfo, nuggetInfo,
							nuggetInfo, nuggetInfo, nuggetInfo,
							nuggetInfo, nuggetInfo, nuggetInfo,
					});
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "nuggets.to_nugget."+material.getName()),
					nuggetInfo, 9, new Object[] {
							materialLocation,
					});
		}
	}
}
