package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule
public class TinyDustsModule implements IModule {

	private final IForm tinyDustForm = JAOPCAApi.instance().newForm(this, "tiny_dusts", JAOPCAApi.instance().itemFormType());

	@Override
	public String getName() {
		return "tiny_dusts";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(tinyDustForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = JAOPCAApi.instance();
		for(IMaterial material : tinyDustForm.getMaterials()) {
			if(material.getType().isDummy()) {
				continue;
			}
			ResourceLocation dustLocation = api.miscHelper().getTagLocation("dusts", material.getName());
			IItemInfo tinyDustInfo = api.itemFormType().getMaterialFormInfo(tinyDustForm, material);
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "tiny_dusts.to_dust."+material.getName()),
					dustLocation, 1, new Object[] {
							tinyDustInfo, tinyDustInfo, tinyDustInfo,
							tinyDustInfo, tinyDustInfo, tinyDustInfo,
							tinyDustInfo, tinyDustInfo, tinyDustInfo,
					});
		}
	}
}
