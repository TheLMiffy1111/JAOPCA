package thelm.jaopca.modules.active;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule
public class BlockModule implements IModule {

	private final IForm storageBlockForm = ApiImpl.INSTANCE.newForm(this, "block", BlockFormType.INSTANCE);

	@Override
	public String getName() {
		return "block";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(storageBlockForm.toRequest());
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : storageBlockForm.getMaterials()) {
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			IBlockInfo storageBlockInfo = BlockFormType.INSTANCE.getMaterialFormInfo(storageBlockForm, material);
			String storageBlockOredict = miscHelper.getOredictName("block", material.getName());
			api.registerShapedRecipe(
					miscHelper.getRecipeKey("block.to_block", material.getName()),
					storageBlockInfo, 1, new Object[] {
							"MMM",
							"MMM",
							"MMM",
							'M', materialOredict,
					});
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("block.to_material", material.getName()),
					materialOredict, 9, new Object[] {
							storageBlockOredict,
					});
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("block", "block");
		return builder.build();
	}
}
