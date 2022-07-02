package thelm.jaopca.compat.teslathingies;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.ndrei.teslapoweredthingies.items.TeslifiedObsidian;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
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

@JAOPCAModule(modDependencies = "teslathingies")
public class TeslaThingiesModule implements IModule {

	public static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Iron", "Gold", "Coal", "Diamond", "Emerald", "Lapis", "Redstone", "Adamantine", "Antimony", "Aquarium",
			"Bismuth", "Brass", "Bronze", "Coldiron", "Copper", "Cupronickel", "Electrum", "Invar", "Lead", "Mercury",
			"Mithril", "Nickel", "Pewter", "Platinum", "Silver", "Starsteel", "Tin", "Zinc", "Aluminium", "AluminiumBrass",
			"Cadmium", "GalvanizedSteel", "Iridium", "Magnesium", "Manganese", "Nichrome", "Osmium", "Plutonium", "Rutile",
			"StainlessSteel", "Tantalum", "Titanium", "Tungsten", "Uranium", "Zirconium"));

	private final IForm teslaLumpForm = ApiImpl.INSTANCE.newForm(this, "teslathingies_tesla_lump", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("teslaLump").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm augmentedLumpForm = ApiImpl.INSTANCE.newForm(this, "teslathingies_augmented_lump", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("augmentedLump").setDefaultMaterialBlacklist(BLACKLIST);

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "teslathingies";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		builder.put(1, "dust");
		builder.put(2, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this, teslaLumpForm, augmentedLumpForm).
				setGrouped(true));
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.copyOf(Arrays.asList(MaterialType.ORE));
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TeslaThingiesHelper helper = TeslaThingiesHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : augmentedLumpForm.getMaterials()) {
			String teslaLumpOredict = miscHelper.getOredictName("teslaLump", material.getName());
			IItemInfo augmentedLumpInfo = itemFormType.getMaterialFormInfo(augmentedLumpForm, material);
			helper.registerCompoundMakerRecipe(
					miscHelper.getRecipeKey("teslathingies.tesla_lump_to_augmented_lump", material.getName()),
					new Object[] {
							teslaLumpOredict, 1,
					}, new Object[] {
							TeslifiedObsidian.INSTANCE, 1,
					}, augmentedLumpInfo, 1);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String augmentedLumpOredict = miscHelper.getOredictName("augmentedLump", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			int amount;
			switch(material.getType()) {
			default: case INGOT:
				amount = 2;
				break;
			case GEM: case CRYSTAL:
				amount = 3;
				break;
			case DUST:
				amount = 5;
				break;
			}
			Object[] output = {
					dustOredict, amount, 1F,
					dustOredict, 1, 0.24F,
			};
			if(material.hasExtra(1)) {
				String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
				output = ArrayUtils.addAll(output, extraDustOredict, 1, 0.24F);
			}
			helper.registerPowderMakerRecipe(
					miscHelper.getRecipeKey("teslathingies.augmented_lump_to_dust", material.getName()),
					augmentedLumpOredict, 1, output);
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("teslalump", "teslathingies_tesla_lump");
		builder.put("augmentedlump", "teslathingies_augmented_lump");
		return builder.build();
	}
}
