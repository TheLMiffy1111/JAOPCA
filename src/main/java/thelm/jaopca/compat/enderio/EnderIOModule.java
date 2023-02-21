package thelm.jaopca.compat.enderio;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.ItemStack;
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

@JAOPCAModule(modDependencies = "EnderIO")
public class EnderIOModule implements IModule {

	static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Adamantine", "Aluminium", "Aluminum", "Amber", "Amethyst", "Apatite", "Ardite", "AstralSilver",
			"Atlarus", "Carmot", "Ceruclase", "Coal", "Cobalt", "Copper", "DeepIron", "Diamond", "Emerald",
			"Gold", "Ignatius", "Infuscolium", "Iron", "Kalendrite", "Lapis", "Lead", "Lemurite", "Malachite",
			"Manganese", "Midasium", "Mithril", "NaturalAluminum", "NetherQuartz", "Nickel", "Orichalcum",
			"Osmium", "Oureclase", "Peridot", "Platinum", "Prometheum", "Quartz", "Redstone", "Rubracium",
			"Ruby", "Saltpeter", "Sanguinite", "Sapphire", "ShadowIron", "Silver", "Silver", "Sulfur",
			"Tanzanite", "Tin", "Topaz", "Vulcanite", "Vyroxeres", "Yellorite", "Zinc"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "enderio";
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
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
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
		EnderIOHelper helper = EnderIOHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("enderio.byproduct", "minecraft:cobblestone",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in Ender IO's sagmill.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			Object[] output = {
					dustOredict, 2, 1F,
					byproduct, 1, 0.15F,
			};
			if(material.hasExtra(1)) {
				String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
				output = ArrayUtils.addAll(output, extraDustOredict, 1, 0.1F);
			}
			if(material.hasExtra(2)) {
				String secondExtraDustOredict = miscHelper.getOredictName("dust", material.getExtra(2).getName());
				output = ArrayUtils.addAll(output, secondExtraDustOredict, 1, 0.05F);
			}
			helper.registerSagMillRecipe(
					miscHelper.getRecipeKey("enderio.ore_to_dust", material.getName()),
					oreOredict, 3600, "multiply_output", output);
		}
	}
}
