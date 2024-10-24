package thelm.jaopca.compat.enderio;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "enderio_machines@[7.0.7,)")
public class EnderIOModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "enderio";
	}

	@Override
	public void onMaterialConfigAvailable(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		builder.put(1, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		EnderIOHelper helper = EnderIOHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("enderio.byproduct", "minecraft:cobblestone",
					s->BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(s)), "The default byproduct material to output in Ender IO's sagmill.");
			Item byproduct = BuiltInRegistries.ITEM.get(ResourceLocation.parse(configByproduct));

			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());

				helper.registerSagMillingRecipe(
						miscHelper.getRecipeKey("enderio.ore_to_raw_material", material.getName()),
						oreLocation, 2400, "multiply_output", new Object[] {
								rawMaterialLocation, 1, 1F,
								rawMaterialLocation, 1, 0.33F,
								byproduct, 1, 0.15F,
						});

				Object[] output = {
						dustLocation, 1, 1F,
						dustLocation, 1, 0.25F,
				};
				if(material.hasExtra(1)) {
					ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
					output = ArrayUtils.addAll(output, extraDustLocation, 1, 0.1F);
				}
				helper.registerSagMillingRecipe(
						miscHelper.getRecipeKey("enderio.raw_material_to_dust", material.getName()),
						rawMaterialLocation, 2400, "multiply_output", output);
			}
			else {
				Object[] output = {
						dustLocation, 2, 1F,
						byproduct, 1, 0.15F,
				};
				if(material.hasExtra(1)) {
					ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
					output = ArrayUtils.addAll(output, extraDustLocation, 1, 0.1F);
				}
				helper.registerSagMillingRecipe(
						miscHelper.getRecipeKey("enderio.ore_to_dust", material.getName()),
						oreLocation, 2400, "multiply_output", output);
			}
		}
	}
}
