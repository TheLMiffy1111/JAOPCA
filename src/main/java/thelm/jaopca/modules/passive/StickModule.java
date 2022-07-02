package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
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
public class StickModule implements IModule {

	private final IForm stickForm = ApiImpl.INSTANCE.newForm(this, "stick", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS);

	private static boolean registerRodOredict = true;

	@Override
	public String getName() {
		return "stick";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(stickForm.toRequest());
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		registerRodOredict = config.getDefinedBoolean("oredict.registerRodOredict", registerRodOredict, "Should the module register rod oredict for sticks.");
	}

	@Override
	public void onMaterialComputeComplete(IModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : stickForm.getMaterials()) {
			IItemInfo stickInfo = itemFormType.getMaterialFormInfo(stickForm, material);
			if(registerRodOredict) {
				api.registerOredict(miscHelper.getOredictName("rod", material.getName()), stickInfo.asItem());
			}
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("stick", "stick");
		return builder.build();
	}
}
