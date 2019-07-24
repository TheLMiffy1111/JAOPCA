package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
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
				api.registerFurnaceRecipe(
						new ResourceLocation(JAOPCA.MOD_ID, "dusts.to_material."+material.getName()), 
						api.itemFormType().getMaterialFormInfo(dustForm, material),
						api.miscHelper().getTagLocation(material.getType().getFormName(), material.getName()), 1, 0.1F, 200);
			}
		}
	}
}
