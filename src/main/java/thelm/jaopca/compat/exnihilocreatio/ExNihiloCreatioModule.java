package thelm.jaopca.compat.exnihilocreatio;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

@JAOPCAModule(modDependencies = "exnihilocreatio")
public class ExNihiloCreatioModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Ardite", "Beryllium", "Boron", "Cobalt", "Copper", "Gold", "Iron", "Lead",
			"Lithium", "Magnesium", "Nickel", "Silver", "Thorium", "Tin", "Titanium", "Tungsten", "Uranium",
			"Zinc", "Zirconium"));

	private static boolean registerOreOredict = true;

	static {
		if(Loader.isModLoaded("bigreactors")) {
			Collections.addAll(BLACKLIST, "Yellorium");
		}
		if(Loader.isModLoaded("draconicevolution")) {
			Collections.addAll(BLACKLIST, "Draconium");
		}
		if(Loader.isModLoaded("mekanism")) {
			Collections.addAll(BLACKLIST, "Osmium");
		}
		if(Loader.isModLoaded("thermalfoundation")) {
			Collections.addAll(BLACKLIST, "Platinum");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm pieceForm = ApiImpl.INSTANCE.newForm(this, "exnihilocreatio_piece", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("piece").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "exnihilocreatio_chunk", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("exnihilocreatio:chunk").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "exnihilocreatio";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this, pieceForm, chunkForm).setGrouped(true));
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		registerOreOredict = config.getDefinedBoolean("oredict.registerOreOredict", registerOreOredict, "Should the module register ore oredict for chunks.");
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onMaterialComputeComplete(IModuleData moduleData) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : chunkForm.getMaterials()) {
			IItemInfo chunkInfo = itemFormType.getMaterialFormInfo(chunkForm, material);
			if(registerOreOredict) {
				api.registerOredict(miscHelper.getOredictName("ore", material.getName()), chunkInfo.asItem());
			}
		}
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ExNihiloCreatioHelper helper = ExNihiloCreatioHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : pieceForm.getMaterials()) {
			IItemInfo pieceInfo = itemFormType.getMaterialFormInfo(pieceForm, material);

			IDynamicSpecConfig config = configs.get(material);
			String defaultOreBase;
			switch(material.getName().toLowerCase(Locale.US).hashCode() % 4) {
			case 0:
				defaultOreBase = "#crushedDiorite";
				break;
			case 1:
				defaultOreBase = "#crushedAndesite";
				break;
			case 2:
				defaultOreBase = "#crushedGranite";
				break;
			default:
				defaultOreBase = "#gravel";
				break;
			}
			String configOreBase = config.getDefinedString("exnihilocreatio.oreBase", defaultOreBase,
					this::isOredictOrItemValid, "The default base to sieve in Ex Nihilo Creatio to get ore pieces.");
			Object oreBase = getOredictOrItem(configOreBase);
			float sieveFlintChance = config.getDefinedFloat("exnihilocreatio.sieveFlintChance", 0.05F, 0F, 1F,
					"The output chance of the sieve recipes added using a flint mesh.");
			float sieveIronChance = config.getDefinedFloat("exnihilocreatio.sieveIronChance", 0.075F, 0F, 1F,
					"The output chance of the sieve recipes added using an iron mesh.");
			float sieveDiamondChance = config.getDefinedFloat("exnihilocreatio.sieveDiamondChance", 0.15F, 0F, 1F,
					"The output chance of the sieve recipes added using a diamond mesh.");

			if(sieveFlintChance > 0) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilocreatio.piece_sieve_flint."+material.getName()),
						oreBase, pieceInfo, sieveFlintChance, 2);
			}
			if(sieveIronChance > 0) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilocreatio.piece_sieve_iron."+material.getName()),
						oreBase, pieceInfo, sieveIronChance, 3);
			}
			if(sieveDiamondChance > 0) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilocreatio.piece_sieve_diamond."+material.getName()),
						oreBase, pieceInfo, sieveDiamondChance, 4);
			}
		}
		for(IMaterial material : chunkForm.getMaterials()) {
			String pieceOredict = miscHelper.getOredictName("piece", material.getName());
			IItemInfo chunkInfo = itemFormType.getMaterialFormInfo(chunkForm, material);
			String chunkOredict = miscHelper.getOredictName("exnihilocreatio:chunk", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("exnihilocreatio.piece_to_chunk", material.getName()),
					chunkInfo, 1, new Object[] {
							pieceOredict, pieceOredict,
							pieceOredict, pieceOredict,
					});
			if(!registerOreOredict) {
				api.registerSmeltingRecipe(
						miscHelper.getRecipeKey("exnihilocreatio.chunk_to_material", material.getName()),
						chunkInfo, materialOredict, 1, 0.7F);
			}
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("piece", "exnihilocreatio_piece");
		builder.put("hunk", "exnihilocreatio_chunk");
		builder.put("orepiece", "exnihilocreatio_piece");
		builder.put("orechunk", "exnihilocreatio_chunk");
		return builder.build();
	}

	public boolean isOredictOrItemValid(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return ApiImpl.INSTANCE.getOredict().contains(s.substring(1));
		}
		else {
			return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s.split("@(?=\\d*$)")[0]));
		}
	}

	public Object getOredictOrItem(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return s.substring(1);
		}
		else {
			return MiscHelper.INSTANCE.parseMetaItem(s);
		}
	}
}
