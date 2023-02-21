package thelm.jaopca.modules.passive;

import java.util.List;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule
public class TinyDustsModule implements IModule {

	private final IForm tinyDustForm = ApiImpl.INSTANCE.newForm(this, "tiny_dusts", ItemFormType.INSTANCE);

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
		return List.of(tinyDustForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : tinyDustForm.getMaterials()) {
			ResourceLocation tinyDustLocation = miscHelper.getTagLocation("tiny_dusts", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ApiImpl.INSTANCE.registerShapelessRecipe(
					new ResourceLocation("jaopca", "tiny_dusts.to_dust."+material.getName()),
					dustLocation, 1, new Object[] {
							tinyDustLocation, tinyDustLocation, tinyDustLocation,
							tinyDustLocation, tinyDustLocation, tinyDustLocation,
							tinyDustLocation, tinyDustLocation, tinyDustLocation,
					});
		}
	}
}
