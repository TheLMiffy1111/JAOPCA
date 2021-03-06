package thelm.jaopca.compat.futurepack;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "futurepack")
public class FuturepackNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"amethyst", "apatite", "coal", "diamond", "emerald", "lapis", "magnetite", "olivine", "pyrite", "quartz",
			"redstone", "ruby", "salt", "sulfur", "sulphur"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "futurepack_non_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(1, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
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
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FuturepackHelper helper = FuturepackHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("futurepack.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Futurepack's Centrifuge.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			int outputCount = material.getType() != MaterialType.DUST ? 12 : 24;
			Object[] output = {
					materialLocation, outputCount,
					byproduct, 3,
			};
			if(material.hasExtra(1)) {
				ResourceLocation exLocation;
				switch(material.getExtra(1).getType()) {
				case CRYSTAL:
				case CRYSTAL_PLAIN:
				case GEM:
				case GEM_PLAIN:
					exLocation = miscHelper.getTagLocation("gems", material.getExtra(1).getName());
					break;
				default:
					exLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
					break;
				}
				output = ArrayUtils.addAll(output, exLocation, 2);
			}

			if(material.getType() != MaterialType.DUST) {
				helper.registerZentrifugeRecipe(oreLocation, 4, 8, 200, output);
			}
			else {
				helper.registerZentrifugeRecipe(oreLocation, 4, 6, 200, output);
			}
		}
	}
}
