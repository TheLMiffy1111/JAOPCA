package thelm.jaopca.compat.futurepack;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

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

@JAOPCAModule(modDependencies = "fp")
public class FuturepackNonIngotModule implements IModule {

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "fp_non_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(1, "dust");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return FuturepackModule.BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		FuturepackHelper helper = FuturepackHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("fp.byproduct", "minecraft:cobblestone",
					miscHelper.metaItemPredicate(), "The byproduct material to output in Futurepack's Centrifuge.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			Object[] output = {
					materialOredict, (material.getType().isCrystalline() ? 12 : 24),
					byproduct, 3,
			};
			if(material.hasExtra(1)) {
				String exOredict;
				switch(material.getExtra(1).getType()) {
				case GEM: case GEM_PLAIN:
					exOredict = miscHelper.getOredictName("gem", material.getExtra(1).getName());
					break;
				case CRYSTAL:
					exOredict = miscHelper.getOredictName("crystal", material.getExtra(1).getName());
					break;
				default:
					exOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
					break;
				}
				output = ArrayUtils.addAll(output, exOredict, 2);
			}
			helper.registerZentrifugeRecipe(
					miscHelper.getRecipeKey("fp.ore_to_material", material.getName()),
					oreOredict, 4, material.getType().isCrystalline() ? 8 : 6, 200, output);
		}
	}
}
