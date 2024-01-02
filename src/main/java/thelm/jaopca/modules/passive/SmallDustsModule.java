package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
public class SmallDustsModule implements IModule {

	private final IForm smallDustForm = ApiImpl.INSTANCE.newForm(this, "small_dusts", ItemFormType.INSTANCE);

	@Override
	public String getName() {
		return "small_dusts";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(smallDustForm.toRequest());
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : smallDustForm.getMaterials()) {
			ResourceLocation smallDustLocation = miscHelper.getTagLocation("small_dusts", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ApiImpl.INSTANCE.registerShapelessRecipe(
					new ResourceLocation("jaopca", "small_dusts.to_dust."+material.getName()),
					dustLocation, 1, new Object[] {
							smallDustLocation, smallDustLocation,
							smallDustLocation, smallDustLocation,
					});
		}
	}
}
