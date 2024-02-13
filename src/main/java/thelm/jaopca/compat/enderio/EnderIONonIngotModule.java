package thelm.jaopca.compat.enderio;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "EnderIO")
public class EnderIONonIngotModule implements IModule {

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "enderio_non_ingot";
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
		return EnderIOModule.BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		EnderIOHelper helper = EnderIOHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("enderio.byproduct", "minecraft:cobblestone",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in Ender IO's sagmill.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			Object[] output = material.getType().isCrystalline() ? new Object[] {
					materialOredict, 2, 1F,
					materialOredict, 1, 0.5F,
					byproduct, 1, 0.15F,
			} : new Object[] {
					materialOredict, 4, 1F,
					byproduct, 1, 0.15F,
			};
			if(material.hasExtra(1)) {
				String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
				output = ArrayUtils.addAll(output, extraDustOredict, 1, 0.1F);
			}
			helper.registerSagMillRecipe(
					miscHelper.getRecipeKey("enderio.ore_to_material", material.getName()),
					oreOredict, 3600, "multiply_output", output);
		}
	}
}
