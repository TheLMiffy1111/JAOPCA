package thelm.jaopca.compat.bcoreprocessing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.ndrei.bcoreprocessing.lib.fluids.FluidsRegistry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "bcoreprocessing")
public class BCOreProcessingModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Adamantine", "Aluminium", "Aluminum", "AluminumBrass", "Antimony", "Aquarium", "Bismuth", "Brass",
			"Bronze", "Cadmium", "Coldiron", "Copper", "Cupronickel", "Electrum", "GalvanizedSteel", "Gold",
			"Invar", "Iridium", "Iron", "Lead", "Magnesium", "Manganese", "Mercury", "Mithril", "Nichrome",
			"Nickel", "Osmium", "Pewter", "Platinum", "Plutonium", "Rutile", "Silver", "StainlessSteel",
			"Starsteel", "Tantalum", "Tin", "Titanium", "Tungsten", "Uranium", "Zinc", "Zirconium"));

	private final IForm searingForm = ApiImpl.INSTANCE.newForm(this, "bcoreprocessing_searing", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(FluidFormType.INSTANCE.getNewSettings().
					setTemperatureFunction(material->1300).setMaterialFunction(material->Material.LAVA));
	private final IForm hotForm = ApiImpl.INSTANCE.newForm(this, "bcoreprocessing_hot", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(FluidFormType.INSTANCE.getNewSettings().
					setTemperatureFunction(material->800).setMaterialFunction(material->Material.LAVA));
	private final IForm coolForm = ApiImpl.INSTANCE.newForm(this, "bcoreprocessing_cool", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setDefaultMaterialBlacklist(BLACKLIST).
			setSettings(FluidFormType.INSTANCE.getNewSettings().
					setTemperatureFunction(material->300).setMaterialFunction(material->Material.LAVA));
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, searingForm, hotForm, coolForm).setGrouped(true);

	@Override
	public String getName() {
		return "bcoreprocessing";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(formRequest);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		BCOreProcessingHelper helper = BCOreProcessingHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IFluidFormType fluidFormType = FluidFormType.INSTANCE;
		Fluid searingLava = FluidsRegistry.GASEOUS_LAVA[2];
		for(IMaterial material : formRequest.getMaterials()) {
			IFluidInfo hotInfo = fluidFormType.getMaterialFormInfo(hotForm, material);
			String hotName = miscHelper.getFluidName("bcoreprocessing_hot", material.getName());
			IFluidInfo searingInfo = fluidFormType.getMaterialFormInfo(searingForm, material);
			String searingName = miscHelper.getFluidName("bcoreprocessing_searing", material.getName());
			IFluidInfo coolInfo = fluidFormType.getMaterialFormInfo(coolForm, material);
			String coolName = miscHelper.getFluidName("bcoreprocessing_cool", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			helper.registerOreProcessorRecipe(
					miscHelper.getRecipeKey("bcoreprocessing.ore_to_searing", material.getName()),
					oreOredict, 1, searingInfo, 1000, searingLava, 125, 40);
			helper.registerHeatableRecipe(
					miscHelper.getRecipeKey("bcoreprocessing.hot_to_searing", material.getName()),
					hotName, 100, searingInfo, 100, 1, 2);
			helper.registerCoolableRecipe(

					miscHelper.getRecipeKey("bcoreprocessing.searing_to_hot", material.getName()),
					searingName, 100, hotInfo, 100, 2, 1);
			helper.registerHeatableRecipe(
					miscHelper.getRecipeKey("bcoreprocessing.cool_to_hot", material.getName()),
					coolName, 100, hotInfo, 100, 0, 1);

			helper.registerCoolableRecipe(
					miscHelper.getRecipeKey("bcoreprocessing.hot_to_cool", material.getName()),
					hotName, 100, coolInfo, 100, 1, 0);

			helper.registerFluidProcessorRecipe(
					miscHelper.getRecipeKey("bcoreprocessing.searing_to_material", material.getName()),
					searingName, 1000, materialOredict, 1, searingLava, 50, 40);
			helper.registerFluidProcessorRecipe(
					miscHelper.getRecipeKey("bcoreprocessing.hot_to_material", material.getName()),
					hotName, 1000, materialOredict, 2, searingLava, 25, 40);
			helper.registerFluidProcessorRecipe(
					miscHelper.getRecipeKey("bcoreprocessing.cool_to_material", material.getName()),
					coolName, 1000, materialOredict, 3, searingLava, 10, 40);
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("searingmolten", "bcoreprocessing_searing");
		builder.put("hotmolten", "bcoreprocessing_hot");
		builder.put("coolmolten", "bcoreprocessing_cool");
		return builder.build();
	}
}
