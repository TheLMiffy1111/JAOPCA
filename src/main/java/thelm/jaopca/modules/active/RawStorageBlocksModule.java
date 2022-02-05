package thelm.jaopca.modules.active;

import java.util.Collections;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule
public class RawStorageBlocksModule implements IModule {

	private final IForm rawStorageBlockForm = ApiImpl.INSTANCE.newForm(this, "raw_storage_blocks", BlockFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("storage_blocks/raw").setTagSeparator("_");

	@Override
	public String getName() {
		return "raw_storage_blocks";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(rawStorageBlockForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : rawStorageBlockForm.getMaterials()) {
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			IBlockInfo rawStorageBlockInfo = BlockFormType.INSTANCE.getMaterialFormInfo(rawStorageBlockForm, material);
			ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
			api.registerShapedRecipe(
					new ResourceLocation("jaopca", "raw_storage_blocks.to_raw_storage_block."+material.getName()),
					rawStorageBlockInfo, 1, new Object[] {
							"MMM",
							"MMM",
							"MMM",
							'M', rawMaterialLocation,
					});
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "raw_storage_blocks.to_raw_material."+material.getName()),
					rawMaterialLocation, 9, new Object[] {
							rawStorageBlockLocation,
					});
		}
	}
}
