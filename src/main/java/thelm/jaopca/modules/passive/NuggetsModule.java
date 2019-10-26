package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule
public class NuggetsModule implements IModule {

	private final IForm nuggetForm = ApiImpl.INSTANCE.newForm(this, "nuggets", ItemFormType.INSTANCE).
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
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : nuggetForm.getMaterials()) {
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			IItemInfo nuggetInfo = ItemFormType.INSTANCE.getMaterialFormInfo(nuggetForm, material);
			ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "nuggets.to_material."+material.getName()),
					materialLocation, 1, new Object[] {
							nuggetLocation, nuggetLocation, nuggetLocation,
							nuggetLocation, nuggetLocation, nuggetLocation,
							nuggetLocation, nuggetLocation, nuggetLocation,
					});
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "nuggets.to_nugget."+material.getName()),
					nuggetInfo, 9, new Object[] {
							materialLocation,
					});
		}
	}
}
