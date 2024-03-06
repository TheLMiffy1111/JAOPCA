package thelm.jaopca.modules.active;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
public class StorageBlocksModule implements IModule {

	private final IForm storageBlockForm = ApiImpl.INSTANCE.newForm(this, "storage_blocks", BlockFormType.INSTANCE);

	@Override
	public String getName() {
		return "storage_blocks";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(storageBlockForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : storageBlockForm.getMaterials()) {
			IBlockInfo storageBlockInfo = BlockFormType.INSTANCE.getMaterialFormInfo(storageBlockForm, material);
			ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			if(material.isSmallStorageBlock()) {
				api.registerShapedRecipe(
						new ResourceLocation("jaopca", "storage_blocks.to_storage_block."+material.getName()),
						CraftingBookCategory.BUILDING,
						storageBlockInfo, 1, new Object[] {
								"MM",
								"MM",
								'M', materialLocation,
						});
			}
			else {
				api.registerShapedRecipe(
						new ResourceLocation("jaopca", "storage_blocks.to_storage_block."+material.getName()),
						CraftingBookCategory.BUILDING,
						storageBlockInfo, 1, new Object[] {
								"MMM",
								"MMM",
								"MMM",
								'M', materialLocation,
						});
			}
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "storage_blocks.to_material."+material.getName()),
					materialLocation, material.isSmallStorageBlock() ? 4 : 9, new Object[] {
							storageBlockLocation,
					});
		}
	}
}
