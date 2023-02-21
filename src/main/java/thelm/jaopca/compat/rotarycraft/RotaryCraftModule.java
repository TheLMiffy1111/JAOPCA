package thelm.jaopca.compat.rotarycraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

/**
 * RotaryCraft extractor support currently requires GTHNMixins for the following reasons:<br>
 * 1. RotaryCraft doesn't do localization on custom (and mod) extracts.<br>
 * 2. RotaryCraft adds duplicate material items for custom (and mod) extracts.<br>
 * 3. Adding custom items whenever possible is more in-line with JAOPCA's behavior on newer versions.<br>
 * 4.
 */
@JAOPCAModule(modDependencies = {"RotaryCraft", "gtnhmixins"})
public class RotaryCraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Amber", "Amethyst", "Ammonium", "Apatite", "Ardite", "Bauxite", "BlueTopaz",
			"Cadmium", "Calcite", "Cassiterite", "CertusQuartz", "ChargedCertusQuartz", "Chimerite", "Chromite",
			"Cinnabar", "Coal", "Cobalt", "Cooperite", "Copper", "Diamond", "Dilithium", "Draconium",
			"Electrotine", "Emerald", "Endium", "Essence", "Eximite", "Firestone", "Fluorite", "Force",
			"FzDarkIron", "Galena", "Gold", "HeeEndium", "Indium", "InfusedAir", "InfusedEarth", "InfusedEntropy",
			"InfusedFire", "InfusedOrder", "InfusedWater", "Iridium", "Iron", "Lapis", "Lead", "Magmanite",
			"Magnetite", "Mana", "Meutoite", "Mimichite", "Monazit", "Moonstone", "NaturalAluminum", "NetherQuartz",
			"Nickel", "Nikolite", "Osmium", "Pentlandite", "Peridot", "Pitchblende", "Platinum", "Pyrite",
			"Quantum", "Quartz", "Redstone", "Ruby", "Rutile", "Saltpeter", "Sapphire", "Silicon", "Silver",
			"Sodalite", "Sphalerite", "Sulfur", "Sunstone", "Teslatite", "Tetrahedrite", "Thorianite", "Thorite",
			"Thorium", "Tin", "Titanium", "Tungstate", "Tungsten", "Uraninite", "Uranium", "Vinteum", "Yellorite",
			"Yellorium", "Zinc"));

	private final IForm dustForm = ApiImpl.INSTANCE.newForm(this, "rotarycraft_dust", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("RotaryCraft:dust").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm slurryForm = ApiImpl.INSTANCE.newForm(this, "rotarycraft_slurry", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("RotaryCraft:slurry").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm solutionForm = ApiImpl.INSTANCE.newForm(this, "rotarycraft_solution", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("RotaryCraft:solution").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm flakesForm = ApiImpl.INSTANCE.newForm(this, "rotarycraft_flakes", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("RotaryCraft:flakes").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this,
			dustForm, slurryForm, solutionForm, flakesForm).setGrouped(true);

	@Override
	public String getName() {
		return "rotarycraft";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(formRequest);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		RotaryCraftHelper helper = RotaryCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo dustInfo = itemFormType.getMaterialFormInfo(dustForm, material);
			IItemInfo slurryInfo = itemFormType.getMaterialFormInfo(slurryForm, material);
			IItemInfo solutionInfo = itemFormType.getMaterialFormInfo(solutionForm, material);
			IItemInfo flakesInfo = itemFormType.getMaterialFormInfo(flakesForm, material);
			String flakesOredict = miscHelper.getOredictName("RotaryCraft:flakes", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			helper.registerExtractorRecipe(
					miscHelper.getRecipeKey("rotarycraft.ore_extractor", material.getName()),
					oreOredict, dustInfo, slurryInfo, solutionInfo, flakesInfo, false);

			helper.registerGrinderRecipe(
					miscHelper.getRecipeKey("rotarycraft.ore_to_flakes", material.getName()),
					oreOredict, flakesInfo, 3);

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("rotarycraft.flakes_to_material", material.getName()),
					flakesOredict, materialOredict, material.getType().isDust() ? 2 : 1, 1F);
		}
	}
}
