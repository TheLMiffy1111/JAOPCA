package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
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
public class SmallDustModule implements IModule {

	public SmallDustModule() {
		ApiImpl.INSTANCE.registerUsedPlainPrefixes("dustSmall");
	}

	private final IForm smallDustForm = ApiImpl.INSTANCE.newForm(this, "small_dust", ItemFormType.INSTANCE).
			setSecondaryName("dustSmall");

	@Override
	public String getName() {
		return "small_dust";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(smallDustForm.toRequest());
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : smallDustForm.getMaterials()) {
			String smallDustOredict = miscHelper.getOredictName("dustSmall", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			ApiImpl.INSTANCE.registerShapelessRecipe(
					miscHelper.getRecipeKey("small_dust.to_dust", material.getName()),
					dustOredict, 1, new Object[] {
							smallDustOredict, smallDustOredict,
							smallDustOredict, smallDustOredict,
					});
		}
	}
}
