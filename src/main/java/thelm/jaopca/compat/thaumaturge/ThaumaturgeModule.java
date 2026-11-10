package thelm.jaopca.compat.thaumaturge;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.leclowndu93150.thaumaturge.api.aspect.TTAspects;
import com.leclowndu93150.thaumaturge.api.recipe.ResearchGate;
import com.leclowndu93150.thaumaturge.content.infernalfurnace.InfernalBonus;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantInt;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
import thelm.jaopca.compat.thaumaturge.recipes.AspectInstance;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "thaumaturge")
public class ThaumaturgeModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron"/*, "lead", "silver", "tin"*/));

	private final IForm clusterForm = ApiImpl.INSTANCE.newForm(this, "thaumaturge_clusters", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("thaumaturge:clusters").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "thaumaturge";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "nuggets");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(clusterForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public void onMaterialComputeComplete(IModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : clusterForm.getMaterials()) {
			IItemInfo clusterInfo = itemFormType.getMaterialFormInfo(clusterForm, material);
			api.registerItemTag(Identifier.parse("thaumaturge:rare_earth_chance_normal"), clusterInfo);
		}
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ThaumaturgeHelper helper = ThaumaturgeHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		List<AspectInstance> aspects = List.of(AspectInstance.of(TTAspects.METALLUM, 5), AspectInstance.of(TTAspects.ORDO, 5));
		ResearchGate research = new ResearchGate(Identifier.parse("thaumaturge:metal_purification"), Optional.empty(), false);
		for(IMaterial material : clusterForm.getMaterials()) {
			IItemInfo clusterInfo = itemFormType.getMaterialFormInfo(clusterForm, material);
			Identifier clusterLocation = miscHelper.getTagLocation("thaumaturge:clusters", material.getName());
			Identifier materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			if(material.getType() == MaterialType.INGOT) {
				Identifier rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerCrucibleRecipe(
						miscHelper.getRecipeKey("thaumaturge.raw_material_to_cluster", material.getName()),
						rawMaterialLocation, aspects, clusterInfo, 1, research);
			}
			else {
				Identifier oreLocation = miscHelper.getTagLocation("ores", material.getName());
				helper.registerCrucibleRecipe(
						miscHelper.getRecipeKey("thaumaturge.ore_to_cluster", material.getName()),
						oreLocation, aspects, clusterInfo, 1, research);
			}

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("thaumaturge.cluster_to_material_smelting", material.getName()),
					clusterLocation, materialLocation, 2, 1, 200);
			api.registerBlastingRecipe(
					miscHelper.getRecipeKey("thaumaturge.cluster_to_material_blasting", material.getName()),
					clusterLocation, materialLocation, 2, 1, 100);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			Identifier rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			Identifier oreLocation = miscHelper.getTagLocation("ores", material.getName());
			Identifier nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());

			api.registerDataMapEntry(InfernalBonus.DATA_MAP, oreLocation,
					()->List.of(new InfernalBonus(
							HolderSet.emptyNamed(BuiltInRegistries.ITEM, miscHelper.getItemTagKey(nuggetLocation)),
							ConstantInt.of(2), 0.33F)));
			if(material.getType() == MaterialType.INGOT) {
				api.registerDataMapEntry(InfernalBonus.DATA_MAP, rawMaterialLocation,
						()->List.of(new InfernalBonus(
								HolderSet.emptyNamed(BuiltInRegistries.ITEM, miscHelper.getItemTagKey(nuggetLocation)),
								ConstantInt.of(1), 0.33F)));
			}
		}
	}
}
