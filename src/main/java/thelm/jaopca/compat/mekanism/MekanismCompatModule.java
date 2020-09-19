package thelm.jaopca.compat.mekanism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.tags.ItemTags;
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

@JAOPCAModule(modDependencies = "mekanism@[9.9.20,)")
public class MekanismCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"bronze", "charcoal", "coal", "copper", "diamond", "emerald", "gold", "iron", "lapis", "osmium", "quartz",
			"refined_glowstone", "refined_obsidian", "steel", "tin"));
	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"charcoal", "coal", "diamond", "emerald", "lapis", "quartz"));
	private static final Set<String> TO_ORE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "copper", "diamond", "emerald", "gold", "iron", "lapis", "osmium", "quartz", "redstone", "tin"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();
	private static Set<String> configToOreBlacklist = new TreeSet<>();

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "mekanism_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crushing recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have enriching recipes added."),
				configToCrystalBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toOreMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have combining recipes added."),
				configToOreBlacklist);
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		MekanismHelper helper = MekanismHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					!TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerCrushingRecipe(
							new ResourceLocation("jaopca", "mekanism.material_to_dust."+material.getName()),
							materialLocation, 1, dustLocation, 1);
				}
			}
			if((ArrayUtils.contains(MaterialType.GEMS, type) || ArrayUtils.contains(MaterialType.CRYSTALS, type)) &&
					!TO_CRYSTAL_BLACKLIST.contains(material.getName()) && !configToCrystalBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerEnrichingRecipe(
							new ResourceLocation("jaopca", "mekanism.dust_to_material."+material.getName()),
							dustLocation, 1, materialLocation, 1);
				}
			}
			if(ArrayUtils.contains(MaterialType.ORE, type) &&
					!TO_ORE_BLACKLIST.contains(material.getName()) && !configToOreBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					IDynamicSpecConfig config = configs.get(material);
					String configOreBase = config.getDefinedString("mekanism.ore_base", "#forge:cobblestone",
							this::isTagOrItemValid, "The base to use in Mekanism's Combiner to recreate ores.");
					Object oreBase = getTagOrItem(configOreBase);

					int inputCount;
					switch(type) {
					case INGOT:
					case DUST:
					default:
						inputCount = 8;
						break;
					case GEM:
					case CRYSTAL:
						inputCount = 5; // I feel like 5 is more balanced
						break;
					}
					helper.registerCombiningRecipe(
							new ResourceLocation("jaopca", "mekanism.dust_to_ore."+material.getName()),
							dustLocation, inputCount, oreBase, 1, oreLocation, 1);
				}
			}
		}
	}

	public boolean isTagOrItemValid(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return ApiImpl.INSTANCE.getItemTags().contains(new ResourceLocation(s.substring(1)));
		}
		else {
			return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s));
		}
	}

	public Object getTagOrItem(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return new ItemTags.Wrapper(new ResourceLocation(s.substring(1)));
		}
		else {
			return ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
		}
	}
}
