package thelm.jaopca.modules.passive;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule
public class DustsModule implements IModule {

	private final IForm dustForm = ApiImpl.INSTANCE.newForm(this, "dusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS);

	@Override
	public String getName() {
		return "dusts";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(dustForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		for(IMaterial material : dustForm.getMaterials()) {
			if(material.getType().isIngot()) {
				ResourceLocation dustLocation = MiscHelper.INSTANCE.getTagLocation("dusts", material.getName());
				ResourceLocation materialLocation = MiscHelper.INSTANCE.getTagLocation(material.getType().getFormName(), material.getName());
				api.registerSmeltingRecipe(
						new ResourceLocation("jaopca", "dusts.to_material."+material.getName()),
						dustLocation, materialLocation, 1, 0.7F, 200);
				api.registerBlastingRecipe(
						new ResourceLocation("jaopca", "dusts.to_material_blasting."+material.getName()),
						dustLocation, materialLocation, 1, 0.7F, 100);
			}
		}
	}
}
