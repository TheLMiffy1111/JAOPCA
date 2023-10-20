package thelm.jaopca.compat.enderio;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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

@JAOPCAModule(modDependencies = "enderio")
public class EnderIOModule implements IModule {

	static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Adamantine", "Agate", "Alexandrite", "Aluminium", "Aluminum", "Amber", "Amethyst", "Ametrine",
			"Ammolite", "Apatite", "Aquamarine", "Ardite", "AstralSilver", "AstralStarmetal", "Atlarus", "Bauxite",
			"Beyrl", "BlackDiamond", "BlueTopaz", "Boron", "Carmot", "Carnelian", "CatsEye", "CertusQuartz",
			"Ceruclase", "Chaos", "ChargedCertusQuartz", "Chrysoprase", "Cinnibar", "Citrine", "Coal", "Cobalt",
			"Copper", "Coral", "DeepIron", "Diamond", "Dilithium", "DimensionalShard", "Draconium", "Emerald",
			"Ender", "EnderBiotite", "EnderEssence", "Fluorite", "Galena", "Garnet", "Gold", "GoldenBeryl",
			"Heliodor", "Ignatius", "Indicolite", "Infuscolium", "Iolite", "Iridium", "Iron", "Jade", "Jasper",
			"Kalendrite", "Kunzite", "Kyanite", "Lapis", "Lava", "Lead", "Lemurite", "Lepidolite", "Lithium",
			"Magnesium", "Malachite", "Midasium", "Mithril", "Moonstone", "Morganite", "NaturalAluminum",
			"NetherQuartz", "Nickel", "Niter", "Onyx", "Opal", "Orichalcum", "Osmium", "Oureclase", "Pearl",
			"Peridot", "Platinum", "Prometheum", "Prosperity", "Pyrite", "Pyrope", "Quartz", "QuartzBlack",
			"Redstone", "RoseQuartz", "Rubracium", "Ruby", "Rutile", "Saltpeter", "Sanguinite", "Sapphire",
			"ShadowIron", "Sheldonite", "Silver", "Sodalite", "Sphalerite", "Spinel", "Sulfur", "Sunstone",
			"Tanzanite", "Tektite", "Thorium", "Tin", "Titanium", "Topaz", "Tritanium", "Tungsten", "Turquoise",
			"Uranium", "VioletSapphire", "Vulcanite", "Vyroxeres", "Yellorium", "Zinc", "Zircon"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "enderio";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
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
					oreOredict, 3600, "multiply_output", "ignore", output);
		}
	}
}
