package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

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
public class DustsModule implements IModule {

	private final IForm dustForm = JAOPCAApi.instance().newForm(this, "dusts", JAOPCAApi.instance().itemFormType()).
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
		JAOPCAApi api = JAOPCAApi.instance();
		for(IMaterial material : dustForm.getMaterials()) {
			if(ArrayUtils.contains(MaterialType.INGOTS, material.getType())) {
				IItemInfo dustInfo = api.itemFormType().getMaterialFormInfo(dustForm, material);
				ResourceLocation materialLocation = api.miscHelper().getTagLocation(material.getType().getFormName(), material.getName());
				api.registerFurnaceRecipe(
						new ResourceLocation("jaopca", "dusts.to_material."+material.getName()),
						dustInfo, materialLocation, 1, 0.7F, 200);
				api.registerBlastingRecipe(
						new ResourceLocation("jaopca", "dusts.to_material_blasting."+material.getName()),
						dustInfo, materialLocation, 1, 0.7F, 100);
			}
		}
	}
}
