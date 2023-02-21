package thelm.jaopca.modules.active;

import java.util.Collections;
import java.util.List;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule
public class DustModule implements IModule {

	private final IForm dustForm = ApiImpl.INSTANCE.newForm(this, "dust", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS);

	@Override
	public String getName() {
		return "dust";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(dustForm.toRequest());
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : dustForm.getMaterials()) {
			if(material.getType().isIngot()) {
				String dustOredict = miscHelper.getOredictName("dust", material.getName());
				String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
				api.registerSmeltingRecipe(
						miscHelper.getRecipeKey("dust.to_material", material.getName()),
						dustOredict, materialOredict, 1, 0.7F);
			}
		}
	}
}
