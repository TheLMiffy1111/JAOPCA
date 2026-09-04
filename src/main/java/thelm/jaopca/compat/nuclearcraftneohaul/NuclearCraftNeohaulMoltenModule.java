package thelm.jaopca.compat.nuclearcraftneohaul;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "nuclearcraft")
public class NuclearCraftNeohaulMoltenModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "beryllium", "boron", "caesium_137", "copper", "dysprosium", "europium_155",
			"ferroboron", "gadolinium", "gold", "hard_carbon", "hastelloy", "holmium", "iron", "lead",
			"lead_platinium", "lithium", "magnesium", "manganese", "manganese_dioxide", "molybdenum",
			"nickel_oxide", "palladium", "promethium_147", "ruthenium_106", "silicon_carbide", "silver", "steel",
			"strontium_90", "thorium", "tin", "tough", "uranium", "zirconium"));

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
		NuclearCraftNeohaulHelper helper = NuclearCraftNeohaulHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
			ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");

			helper.registerMelterRecipe(
					miscHelper.getRecipeKey("nuclearcraftneohaul.ore_to_molten", material.getName()),
					oreLocation, 1, moltenLocation, 270, 0, 1.25, 1.5);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerMelterRecipe(
						miscHelper.getRecipeKey("nuclearcraftneohaul.raw_material_to_molten", material.getName()),
						rawMaterialLocation, 1, moltenLocation, 180, 25, 30, 90, 0, 1, 1);
				helper.registerMelterRecipe(
						miscHelper.getRecipeKey("nuclearcraftneohaul.raw_storage_block_to_molten", material.getName()),
						rawStorageBlockLocation, 1, moltenLocation, 810, 0, 9, 1);
			}
		}
	}
}
