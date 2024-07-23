package thelm.jaopca.compat.minestrapp;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "minestrapp")
public class MinestrappNonIngotModule implements IModule {

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "minestrapp_non_ingot";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return MinestrappModule.BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		MinestrappHelper helper = MinestrappHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("minestrapp.byproduct", "minestrapp:m_chunks@1",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in Minestrappolation's grinder.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			helper.registerCrusherRecipe(
					miscHelper.getRecipeKey("minestrapp.ore_to_dust", material.getName()),
					oreOredict, materialOredict, material.getType().isCrystalline() ? 2 : 5, byproduct, 1, 40, 0.2F);
		}
	}
}
