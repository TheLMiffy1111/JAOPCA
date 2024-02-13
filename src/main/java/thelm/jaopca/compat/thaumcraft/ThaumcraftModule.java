package thelm.jaopca.compat.thaumcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
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

@JAOPCAModule(modDependencies = "Thaumcraft")
public class ThaumcraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Cinnabar", "Copper", "Gold", "Iron", "Lead", "Silver", "Tin"));

	private final IForm clusterForm = ApiImpl.INSTANCE.newForm(this, "thaumcraft_cluster", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("cluster").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "thaumcraft";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
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
			helper.registerCrucibleRecipe(
					miscHelper.getRecipeKey("thaumcraft.ore_to_cluster_crucible", material.getName()),
					"JAOPCA_PUREMETAL", oreOredict, new Object[] {
							Aspect.METAL, 1, Aspect.ORDER, 1,
					}, clusterInfo, 1);

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("thaumcraft.cluster_to_material", material.getName()),
					clusterOredict, materialOredict, 2, 1F);
			helper.registerSmeltingBonusRecipe(
					miscHelper.getRecipeKey("thaumcraft.cluster_to_nugget", material.getName()),
					clusterOredict, nuggetOredict, 1);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String nuggetOredict = miscHelper.getOredictName("nugget", material.getName());
			helper.registerSmeltingBonusRecipe(
					miscHelper.getRecipeKey("thaumcraft.ore_to_nugget", material.getName()),
					oreOredict, nuggetOredict, 1);
		}
	}

	@Override
	public void onLoadComplete(IModuleData moduleData, FMLLoadCompleteEvent event) {
		ArrayList<ResearchPage> pages = new ArrayList<ResearchPage>();
		pages.add(new ResearchPage("tc.research_page.JAOPCA_PUREMETAL.1"));
		((List<Object>)ThaumcraftApi.getCraftingRecipes()).stream().
		filter(o->o instanceof CrucibleRecipe).
		map(o->(CrucibleRecipe)o).
		filter(r->r.key.equals("JAOPCA_PUREMETAL")).
		forEach(r->pages.add(new ResearchPage(r)));
		if(pages.size() > 1) {
			ResourceLocation texture = new ResourceLocation("jaopca:textures/items/metallic/thaumcraft_cluster_research.png");
			ResearchCategories.registerCategory("JAOPCA", texture, new ResourceLocation("thaumcraft:textures/gui/gui_researchback.png"));
			new ResearchItem("JAOPCA_PUREMETAL", "JAOPCA", new AspectList().add(Aspect.METAL, 5).add(Aspect.ORDER, 2), 0, 0, 1, texture).
			setPages(pages.toArray(new ResearchPage[pages.size()])).
			setSecondary().
			setParents("PUREIRON", "PUREGOLD").
			registerResearchItem();
		}
	}
}
