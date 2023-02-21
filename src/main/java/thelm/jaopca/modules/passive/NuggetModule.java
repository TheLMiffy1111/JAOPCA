package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
public class NuggetModule implements IModule {

	private final IForm nuggetForm = ApiImpl.INSTANCE.newForm(this, "nugget", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS);

	@Override
	public String getName() {
		return "nugget";
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
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : nuggetForm.getMaterials()) {
			IItemInfo nuggetInfo = ItemFormType.INSTANCE.getMaterialFormInfo(nuggetForm, material);
			String nuggetOredict = miscHelper.getOredictName("nugget", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("nugget.to_material", material.getName()),
					materialOredict, 1, new Object[] {
							nuggetOredict, nuggetOredict, nuggetOredict,
							nuggetOredict, nuggetOredict, nuggetOredict,
							nuggetOredict, nuggetOredict, nuggetOredict,
					});
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("nugget.to_nugget", material.getName()),
					nuggetInfo, 9, new Object[] {
							materialOredict,
					});
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("nugget", "nugget");
		return builder.build();
	}
}
