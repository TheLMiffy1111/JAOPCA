package thelm.jaopca.compat.skyresources;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
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

@JAOPCAModule(modDependencies = "skyresources")
public class SkyResourcesCrystalModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Agate", "Alexandrite", "Amber", "Amethyst", "Ametrine", "Apatite", "Aquamarine", "Beryl",
			"BlackDiamond", "BlueTopaz", "Carnelian", "CertusQuartz", "Chaos", "Citrine", "Dark", "Diamond",
			"Emerald", "EnderEssence", "Garnet", "GoldenBeryl", "Heliodor", "Indicolite", "Iolite", "Jasper",
			"Lapis", "Lepidolite", "Malachite", "Moldavite", "Moonstone", "Morganite", "Onyx", "Opal", "Peridot",
			"Quartz", "QuartzBlack", "RedGarnet", "Ruby", "Sapphire", "Spinel", "Tanzanite", "Topaz", "Turquoise",
			"VioletSapphire", "YellowGarnet"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm dirtyGemForm = ApiImpl.INSTANCE.newForm(this, "skyresources_dirty_gem", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.GEM, MaterialType.CRYSTAL).setSecondaryName("skyresources:dirtyGem").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "skyresources_crystal";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(dirtyGemForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL);
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
		SkyResourcesHelper helper = SkyResourcesHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : dirtyGemForm.getMaterials()) {
			IItemInfo dirtyGemInfo = ItemFormType.INSTANCE.getMaterialFormInfo(dirtyGemForm, material);

			IDynamicSpecConfig config = configs.get(material);
			float chance = config.getDefinedFloat("skyresources.rockGrinderChance", 0.015F, i->i>=0, "The output chance of Sky Resources' rock grinder recipe.");
			String configOreBase = config.getDefinedString("skyresources.oreBase", "minecraft:stone",
					this::isOredictOrItemValid, "The default base to grind with Sky Resources' rock grinder to get dirty gems.");
			Object oreBase = getOredictOrItem(configOreBase);

			helper.registerRockGrinderRecipe(
					miscHelper.getRecipeKey("skyresources.dirty_gem_rock_grinder", material.getName()),
					oreBase, dirtyGemInfo, 1, chance);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String dirtyGemOredict = miscHelper.getOredictName("skyresources:dirtyGem", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			helper.registerCauldronCleanRecipe(
					miscHelper.getRecipeKey("skyresources.dirty_gem_to_material", material.getName()),
					dirtyGemOredict, materialOredict, 1, 1F);
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("dirtygem", "skyresources_dirty_gem");
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
