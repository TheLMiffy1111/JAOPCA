package thelm.jaopca.compat.abyssalcraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;

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

@JAOPCAModule(modDependencies = "abyssalcraft")
public class AbyssalCraftModule implements IModule {

	private static final Set<String> FORM_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Carbon", "Copper", "Coralium", "Dreadium", "Gold", "Iron",
			"LiquifiedCoralium", "Magnesium", "NaturalAluminum", "Potassium", "Silicon", "Tin", "Zinc"));
	private static final Set<String> ORE_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Copper", "Coralium", "Gold", "Iron", "LiquifiedCoralium",
			"NaturalAluminum", "Tin", "Zinc"));
	private static final Set<String> TRANSMUTE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Copper", "Coralium", "Dreadium", "Gold", "Iron",
			"LiquifiedCoralium", "Magnesium", "NaturalAluminum", "Tin", "Zinc"));

	public AbyssalCraftModule() {
		AbyssalCraftAPI.registerFuelHandler(AbyssalCraftHelper.INSTANCE, AbyssalCraftAPI.FuelType.CRYSTALLIZER);
		AbyssalCraftAPI.registerFuelHandler(AbyssalCraftHelper.INSTANCE, AbyssalCraftAPI.FuelType.TRANSMUTATOR);
	}

	private final IForm crystalShardForm = ApiImpl.INSTANCE.newForm(this, "abyssalcraft_crystal_shard", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystalShard").setDefaultMaterialBlacklist(FORM_BLACKLIST);
	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "abyssalcraft_crystal", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystal").setDefaultMaterialBlacklist(FORM_BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, crystalShardForm, crystalForm).setGrouped(true);

	@Override
	public String getName() {
		return "abyssalcraft";
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
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AbyssalCraftHelper helper = AbyssalCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo crystalShardInfo = itemFormType.getMaterialFormInfo(crystalShardForm, material);
			String crystalShardOredict = miscHelper.getOredictName("crystalShard", material.getName());
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			String crystalOredict = miscHelper.getOredictName("crystal", material.getName());

			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_to_crystal_shard", material.getName()),
					crystalShardInfo, 9, new Object[] {
							crystalOredict,
					});
			helper.registerCrystal(crystalShardInfo);
			helper.registerFuel(crystalShardInfo, 150);

			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_shard_to_crystal", material.getName()),
					crystalInfo, 1, new Object[] {
							crystalShardOredict, crystalShardOredict, crystalShardOredict,
							crystalShardOredict, crystalShardOredict, crystalShardOredict,
							crystalShardOredict, crystalShardOredict, crystalShardOredict,
					});
			helper.registerCrystal(crystalInfo);
			helper.registerFuel(crystalInfo, 1350);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String name = material.getName();
			if(!ORE_TO_CRYSTAL_BLACKLIST.contains(name)) {
				String oreOredict = miscHelper.getOredictName("ore", name);
				String crystalShardOredict = miscHelper.getOredictName("crystalShard", name);
				if(oredict.contains(crystalShardOredict)) {
					helper.registerCrystallizationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.ore_to_crystal_shard", name),
							oreOredict, crystalShardOredict, 4, 0.1F);
				}
			}
			if(!TRANSMUTE_BLACKLIST.contains(name)) {
				String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), name);
				String crystalOredict = miscHelper.getOredictName("crystal", name);
				if(oredict.contains(crystalOredict)) {
					helper.registerTransmutationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.crystal_to_material", name),
							crystalOredict, materialOredict, 1, 0.2F);
				}
			}
		}
	}
}
