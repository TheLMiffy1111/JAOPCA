package thelm.jaopca.compat.magneticraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
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

@JAOPCAModule(modDependencies = "Magneticraft")
public class MagneticraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Ardite", "Bismuth", "Chromium", "Cobalt", "Copper", "Galena", "Gold",
			"Iridium", "Iron", "Lead", "Lithium", "Manganese", "Mithril", "NaturalAluminum", "Nickel", "Osmium",
			"Platinum", "Silver", "Thorium", "Tin", "Titanium", "Tungsten", "Uranium", "Zinc"));

	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "magneticraft_chunk", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("chunk").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm rubbleForm = ApiImpl.INSTANCE.newForm(this, "magneticraft_rubble", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("rubble").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm pebblesForm = ApiImpl.INSTANCE.newForm(this, "magneticraft_pebbles", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("pebbles").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this,
			chunkForm, rubbleForm, pebblesForm).setGrouped(true);

	@Override
	public String getName() {
		return "magneticraft";
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
		MagneticraftHelper helper = MagneticraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo chunkInfo = itemFormType.getMaterialFormInfo(chunkForm, material);
			String chunkOredict = miscHelper.getOredictName("chunk", material.getName());
			IItemInfo rubbleInfo = itemFormType.getMaterialFormInfo(rubbleForm, material);
			String rubbleOredict = miscHelper.getOredictName("rubble", material.getName());
			IItemInfo pebblesInfo = itemFormType.getMaterialFormInfo(pebblesForm, material);
			String pebblesOredict = miscHelper.getOredictName("pebbles", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			helper.registerHammerTableRecipe(
					miscHelper.getRecipeKey("magneticraft.ore_to_chunk_hammer", material.getName()),
					oreOredict, chunkInfo, 1);
			if(material.hasExtra(1)) {
				helper.registerCrusherRecipe(
						miscHelper.getRecipeKey("magneticraft.ore_to_chunk_crusher", material.getName()),
						oreOredict, chunkInfo, 1, dustOredict, 1, 0.05F, extraDustOredict, 1, 0.05F);
			}
			else {
				helper.registerCrusherRecipe(
						miscHelper.getRecipeKey("magneticraft.ore_to_chunk_crusher", material.getName()),
						oreOredict, chunkInfo, 1, dustOredict, 1, 0.05F);
			}

			if(material.hasExtra(1)) {
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("magneticraft.chunk_to_rubble", material.getName()),
						chunkOredict, rubbleInfo, 1, dustOredict, 1, 0.05F, extraDustOredict, 1, 0.05F);
			}
			else {
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("magneticraft.chunk_to_rubble", material.getName()),
						chunkOredict, rubbleInfo, 1, dustOredict, 1, 0.05F);
			}


			if(material.hasExtra(1)) {
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("magneticraft.rubble_to_pebbles", material.getName()),
						rubbleOredict, pebblesInfo, 1, dustOredict, 1, 0.05F, extraDustOredict, 1, 0.05F);
			}
			else {
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("magneticraft.rubble_to_pebbles", material.getName()),
						rubbleOredict, pebblesInfo, 1, dustOredict, 1, 0.05F);
			}

			if(material.hasExtra(1)) {
				helper.registerSifterRecipe(
						miscHelper.getRecipeKey("magneticraft.pebbles_to_dust", material.getName()),
						pebblesOredict, dustOredict, 3, extraDustOredict, 1, 0.05F);
			}
			else {
				helper.registerSifterRecipe(
						miscHelper.getRecipeKey("magneticraft.pebbles_to_dust", material.getName()),
						pebblesOredict, dustOredict, 3);
			}
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("magneticraft.chunk_to_material", material.getName()),
					chunkInfo, materialOredict, 2, 1F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("magneticraft.rubble_to_material", material.getName()),
					rubbleOredict, materialOredict, 2, 1F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("magneticraft.pebbles_to_material", material.getName()),
					pebblesOredict, materialOredict, 2, 1F);
		}
	}
}
