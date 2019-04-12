package thelm.jaopca.modules.active;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.EnumMaterialType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule
public class ModuleStorageBlocks implements IModule {

	private final IForm storageBlockForm = JAOPCAApi.instance().newForm(this, "storage_blocks", JAOPCAApi.instance().blockFormType());

	@Override
	public String getName() {
		return "storage_blocks";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(storageBlockForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = JAOPCAApi.instance();
		for(IMaterial material : storageBlockForm.getMaterials()) {
			if(material.getType().isNone()) {
				continue;
			}
			ResourceLocation materialLocation = api.miscHelper().getTagLocation(material.getType().getFormName(), material.getName());
			IBlockInfo storageBlockInfo = api.blockFormType().getMaterialFormInfo(storageBlockForm, material);
			api.registerShapedRecipe(
					new ResourceLocation(JAOPCA.MOD_ID, "storage_blocks.to_storage_block."+material.getName()),
					storageBlockInfo, 1, new Object[] {
							"MMM",
							"MMM",
							"MMM",
							'M', materialLocation,
					});
			api.registerShapelessRecipe(
					new ResourceLocation(JAOPCA.MOD_ID, "storage_blocks.to_material."+material.getName()),
					materialLocation, 9, new Object[] {
							storageBlockInfo,
					});
		}
	}
}
