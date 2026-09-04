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

@JAOPCAModule(modDependencies = "nuclearcraft")
public class NuclearCraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"boron", "cobalt", "copper", "gold", "iron", "lead", "lithium", "magnesium", "platinum", "silver",
			"thorium", "tin", "uranium", "zinc"));

	@Override
	public String getName() {
		return "nuclearcraft";
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
		NuclearCraftHelper helper = NuclearCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			if(material.getType() == MaterialType.INGOT) {
				helper.registerManufactoryRecipe(
						new ResourceLocation("jaopca", "nuclearcraft.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, 1, dustLocation, 2, 1, 1, 1);
			}
			else {
				helper.registerManufactoryRecipe(
						new ResourceLocation("jaopca", "nuclearcraft.ore_to_dust."+material.getName()),
						oreLocation, 1, dustLocation, 2, 1, 1, 1);
			}
		}
	}
}
