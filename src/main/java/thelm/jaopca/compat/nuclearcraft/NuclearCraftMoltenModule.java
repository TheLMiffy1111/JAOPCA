package thelm.jaopca.compat.nuclearcraft;

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
public class NuclearCraftMoltenModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"boron", "cobalt", "copper", "gold", "iron", "lead", "lithium", "magnesium", "platinum", "silver",
			"thorium", "tin", "uranium", "zinc"));

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
					miscHelper.getRecipeKey("nuclearcraft.ore_to_molten", material.getName()),
					oreLocation, 1, moltenLocation, 240, 200, 50);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerMelterRecipe(
						miscHelper.getRecipeKey("nuclearcraft.raw_material_to_molten", material.getName()),
						rawMaterialLocation, 1, moltenLocation, 120, 200, 50);
			}
		}
	}
}
