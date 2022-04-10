package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
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
		return Collections.singletonList(dustForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		for(IMaterial material : dustForm.getMaterials()) {
			if(material.getType().isIngot()) {
				IItemInfo dustInfo = ItemFormType.INSTANCE.getMaterialFormInfo(dustForm, material);
				ResourceLocation materialLocation = MiscHelper.INSTANCE.getTagLocation(material.getType().getFormName(), material.getName());
				api.registerSmeltingRecipe(
						new ResourceLocation("jaopca", "dusts.to_material."+material.getName()),
						dustInfo, materialLocation, 1, 0.7F, 200);
				api.registerBlastingRecipe(
						new ResourceLocation("jaopca", "dusts.to_material_blasting."+material.getName()),
						dustInfo, materialLocation, 1, 0.7F, 100);
			}
		}
	}
}
