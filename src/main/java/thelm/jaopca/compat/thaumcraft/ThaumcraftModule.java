package thelm.jaopca.compat.thaumcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import thaumcraft.api.aspects.Aspect;
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

@JAOPCAModule(modDependencies = "thaumcraft")
public class ThaumcraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Cinnabar", "Copper", "Gold", "Iron", "Lead", "NetherQuartz", "Quartz", "Silver", "Tin"));

	private final IForm clusterForm = ApiImpl.INSTANCE.newForm(this, "thaumcraft_cluster", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("cluster").setDefaultMaterialBlacklist(BLACKLIST);

	private List<ResourceLocation> recipeKeys = new ArrayList<>();

	@Override
	public String getName() {
		return "thaumcraft";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "nugget");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(clusterForm.toRequest());
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
		ThaumcraftHelper helper = ThaumcraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : clusterForm.getMaterials()) {
			IItemInfo clusterInfo = itemFormType.getMaterialFormInfo(clusterForm, material);
			String clusterOredict = miscHelper.getOredictName("cluster", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String nuggetOredict = miscHelper.getOredictName("nugget", material.getName());

			helper.registerSpecialMiningRecipe(
					miscHelper.getRecipeKey("thaumcraft.ore_to_cluster_mining", material.getName()),
					oreOredict, clusterInfo, 1, 1F);
			ResourceLocation recipeKey = miscHelper.getRecipeKey("thaumcraft.ore_to_cluster_crucible", material.getName());
			recipeKeys.add(recipeKey);
			helper.registerCrucibleRecipe(
					recipeKey,
					"METALPURIFICATION", oreOredict, new Object[] {
							Aspect.METAL, 5, Aspect.ORDER, 5,
					}, clusterInfo, 1);

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("thaumcraft.cluster_to_material", material.getName()),
					clusterOredict, materialOredict, 2, 1F);
			helper.registerSmeltingBonusRecipe(
					miscHelper.getRecipeKey("thaumcraft.cluster_to_nugget", material.getName()),
					clusterOredict, nuggetOredict, 1, 0.33F);

		}
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String nuggetOredict = miscHelper.getOredictName("nugget", material.getName());
			helper.registerSmeltingBonusRecipe(
					miscHelper.getRecipeKey("thaumcraft.ore_to_nugget", material.getName()),
					oreOredict, nuggetOredict, 1, 0.33F);
		}
	}

	@Override
	public void onPostInit(IModuleData moduleData, FMLPostInitializationEvent event) {
		ThaumcraftHelper.INSTANCE.registerRecipesToResearch("METALPURIFICATION", recipeKeys);
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("cluster", "thaumcraft_cluster");
		return builder.build();
	}
}
