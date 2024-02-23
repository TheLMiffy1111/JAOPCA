package thelm.jaopca.compat.nuclearcraft;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

// Separate module because different blacklist
@JAOPCAModule(modDependencies = "nuclearcraft")
public class NuclearCraftMoltenModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "arsenic", "beryllium", "boron", "boron_arsenide", "bronze", "caesium_137",
			"carbon_manganese", "cobalt", "copper", "corium", "electrum", "enderium", "europium_155", "extreme",
			"ferroboron", "gold", "hard_carbon", "hsla_steel", "iron", "lapis", "lead", "lead_platinum",
			"lithium", "lithium_manganese_dioxide", "magnesium", "magnesium_diboride", "manganese",
			"manganese_dioxide", "manganese_oxide", "molybdenum", "obsidian", "palladium", "platinum", "polonium",
			"potassium_hydroxide", "potassium_iodide", "promethium_147", "purpur", "ruthenium_106", "shibuichi",
			"sic_sic_cmc", "silicon_carbide", "silver", "sodium_hydroxide", "steel", "strontium_90", "sulfur",
			"thermoconducting", "thorium", "tin", "tin_silver", "tough_alloy", "uranium", "zinc", "zircaloy",
			"zirconium_molybdenum", "zirconium"));

	@Override
	public String getName() {
		return "nuclearcraft_molten";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "molten");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		NuclearCraftHelper helper = NuclearCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");

			helper.registerMelterRecipe(
					new ResourceLocation("jaopca", "nuclearcraft.ore_to_molten."+material.getName()),
					oreLocation, 1, moltenLocation, 180, 1, 1, 1);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerMelterRecipe(
						new ResourceLocation("jaopca", "nuclearcraft.raw_material_to_molten."+material.getName()),
						rawMaterialLocation, 1, moltenLocation, 135, 1, 1, 1);
			}
		}
	}
}
