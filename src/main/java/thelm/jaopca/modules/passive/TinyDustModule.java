package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
public class TinyDustModule implements IModule {

	private final IForm tinyDustForm = ApiImpl.INSTANCE.newForm(this, "tiny_dust", ItemFormType.INSTANCE).
			setSecondaryName("dustTiny");

	public TinyDustModule() {
		ApiImpl.INSTANCE.registerUsedPlainPrefixes("dustTiny");
	}

	@Override
	public String getName() {
		return "tiny_dust";
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
		return Collections.singletonList(tinyDustForm.toRequest());
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : tinyDustForm.getMaterials()) {
			String tinyDustOredict = miscHelper.getOredictName("dustTiny", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			ApiImpl.INSTANCE.registerShapelessRecipe(
					miscHelper.getRecipeKey("tiny_dust.to_dust", material.getName()),
					dustOredict, 1, new Object[] {
							tinyDustOredict, tinyDustOredict, tinyDustOredict,
							tinyDustOredict, tinyDustOredict, tinyDustOredict,
							tinyDustOredict, tinyDustOredict, tinyDustOredict,
					});
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("dusttiny", "tiny_dust");
		return builder.build();
	}
}
