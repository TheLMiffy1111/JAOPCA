package thelm.jaopca.compat.exnihilocreatio;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "exnihilocreatio")
public class ExNihiloCreatioNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Coal", "Diamond", "Emerald", "Lapis", "Quartz", "Redstone"));

	static {
		if(Loader.isModLoaded("actuallyadditions")) {
			Collections.addAll(BLACKLIST, "QuartzBlack");
		}
		if(Loader.isModLoaded("forestry")) {
			Collections.addAll(BLACKLIST, "Apatite");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "exnihilocretaio_non_ingot";
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
		ExNihiloCreatioHelper helper = ExNihiloCreatioHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			boolean isCrystal = material.getType().isCrystalline();
			IDynamicSpecConfig config = configs.get(material);
			String configOreBase = config.getDefinedString("exnihilocreatio.oreBase", isCrystal ? "#gravel" : "#dust",
					this::isOredictOrItemValid, "The default base to sieve in Ex Nihilo Creatio to get this material.");
			Object oreBase = getOredictOrItem(configOreBase);
			float sieveIronChance = config.getDefinedFloat("exnihilosequentia.sieveIronChance", isCrystal ? 0.008F : 0.0625F, 0F, 1F,
					"The output chance of the sieve recipes added using an iron mesh.");
			float sieveDiamondChance = config.getDefinedFloat("exnihilosequentia.sieveDiamondChance", isCrystal ? 0.016F : 0.125F, 0F, 1F,
					"The output chance of the sieve recipes added using a diamond mesh.");

			if(sieveIronChance > 0) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilocreatio.material_sieve_iron."+material.getName()),
						oreBase, materialOredict, sieveIronChance, 3);
			}
			if(sieveDiamondChance > 0) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilocreatio.material_sieve_diamond."+material.getName()),
						oreBase, materialOredict, sieveDiamondChance, 4);
			}
		}
	}

	public boolean isOredictOrItemValid(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return ApiImpl.INSTANCE.getOredict().contains(s.substring(1));
		}
		else {
			return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s.split("@(?=\\d+)")[0]));
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
