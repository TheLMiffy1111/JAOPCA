package thelm.jaopca.compat.engtoolbox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
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

@JAOPCAModule(modDependencies = "eng_toolbox")
public class EngToolboxModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Ardite", "Cobalt", "Copper", "Gold", "Iron", "Lead", "NaturalAluminum",
			"Nickel", "Silver", "Tin"));

	private final IForm groundForm = ApiImpl.INSTANCE.newForm(this, "eng_toolbox_ground", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("ground").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm impureDustForm = ApiImpl.INSTANCE.newForm(this, "eng_toolbox_impure_dust", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("dustImpure").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, groundForm, impureDustForm).setGrouped(true);

	@Override
	public String getName() {
		return "eng_toolbox";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		builder.put(1, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(formRequest);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		EngToolboxHelper helper = EngToolboxHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo groundInfo = itemFormType.getMaterialFormInfo(groundForm, material);
			String groundOredict = miscHelper.getOredictName("ground", material.getName());
			IItemInfo impureDustInfo = itemFormType.getMaterialFormInfo(impureDustForm, material);
			String impureDustOredict = miscHelper.getOredictName("dustImpure", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			helper.registerGrinderRecipe(
					miscHelper.getRecipeKey("eng_toolbox.ore_to_ground", material.getName()),
					oreOredict, groundInfo, 1);

			helper.registerMultiSmelterRecipe(
					miscHelper.getRecipeKey("eng_toolbox.ground_to_impure_dust", material.getName()),
					groundOredict, "dustQuicklime", impureDustInfo, 3);

			helper.registerCentrifugeRecipe(
					miscHelper.getRecipeKey("eng_toolbox.impure_dust_to_dust", material.getName()),
					impureDustOredict, dustOredict, 1, extraDustOredict, 1, material.hasExtra(1) ? 10 : 33);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("eng_toolbox.ground_to_material", material.getName()),
					groundOredict, materialOredict, 2, 1F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("eng_toolbox.impure_dust_to_material", material.getName()),
					impureDustOredict, materialOredict, 1, 1F);
		}
	}
}
