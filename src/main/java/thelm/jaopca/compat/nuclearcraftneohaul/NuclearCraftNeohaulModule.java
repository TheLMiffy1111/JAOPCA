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

@JAOPCAModule(modDependencies = "nuclearcraftneohaul")
public class NuclearCraftNeohaulModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"boron", "copper", "iron", "lead", "lithium", "magnesium", "platinum", "thorium", "tin",
			"uranium", "zinc"));

	@Override
	public String getName() {
		return "nuclearcraftneohaul";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
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
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			helper.registerManufactoryRecipe(
					miscHelper.getRecipeKey("nuclearcraftneohaul.ore_to_dust", material.getName()),
					oreLocation, 1, dustLocation, 3, 0, 1, 1);
			if(material.getType() == MaterialType.INGOT) {
				helper.registerManufactoryRecipe(
						miscHelper.getRecipeKey("nuclearcraftneohaul.raw_material_to_dust", material.getName()),
						rawMaterialLocation, 1, dustLocation, 2, 33, 1, 0, 1, 1);
			}
		}
	}
}
