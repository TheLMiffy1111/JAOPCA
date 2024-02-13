package thelm.jaopca.compat.hydcraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.Loader;
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

@JAOPCAModule(modDependencies = "HydCraft")
public class HydCraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Ardite", "Cobalt", "Copper", "FzDarkIron", "Gold", "Iron", "Lead", "Nickel", "Silver", "Tin"));

	static {
		if(Loader.isModLoaded("IC2")) {
			Collections.addAll(BLACKLIST, "Uranium");
		}
	}

	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "hydcraft_chunk", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("chunk").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "hydcraft";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(chunkForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		HydCraftHelper helper = HydCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : chunkForm.getMaterials()) {
			IItemInfo chunkInfo = itemFormType.getMaterialFormInfo(chunkForm, material);
			String chunkOredict = miscHelper.getOredictName("chunk", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			helper.registerCrushingRecipe(
					miscHelper.getRecipeKey("hydcraft.ore_to_chunk", material.getName()),
					oreOredict, chunkInfo, 2, 1F, 200);

			helper.registerWasherRecipe(
					miscHelper.getRecipeKey("hydcraft.chunk_to_dust", material.getName()),
					chunkOredict, dustOredict, 1, 0.1F, 200);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("hydcraft.chunk_to_material", material.getName()),
					chunkInfo, materialOredict, 1, 0);
		}
		// Do other chunks as this is a single form module
		for(IMaterial material : Sets.difference(moduleData.getMaterials(), chunkForm.getMaterials())) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String chunkOredict = miscHelper.getOredictName("chunk", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			helper.registerCrushingRecipe(
					miscHelper.getRecipeKey("hydcraft.ore_to_chunk", material.getName()),
					oreOredict, chunkOredict, 2, 1F, 200);
			helper.registerWasherRecipe(
					miscHelper.getRecipeKey("hydcraft.chunk_to_dust", material.getName()),
					chunkOredict, dustOredict, 1, 0.1F, 200);
		}
	}
}
