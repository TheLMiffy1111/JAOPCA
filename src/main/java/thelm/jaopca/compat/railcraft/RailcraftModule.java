package thelm.jaopca.compat.railcraft;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "railcraft")
public class RailcraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "lead", "nickel", "silver"));

	@Override
	public String getName() {
		return "railcraft";
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
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		RailcraftHelper helper = RailcraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			if(BLACKLIST.contains(material.getName())) {
				continue;
			}

			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());

			if(material.hasExtra(1)) {
				helper.registerCrusherRecipe(
						new ResourceLocation("jaopca", "railcraft.ore_to_dust."+material.getName()),
						oreLocation, 200, new Object[] {
								dustLocation, 2, 1F,
								extraDustLocation, 1, 0.1F,
						});
			}
			else {
				helper.registerCrusherRecipe(
						new ResourceLocation("jaopca", "railcraft.ore_to_dust."+material.getName()),
						oreLocation, 200, new Object[] {
								dustLocation, 2, 1F,
								extraDustLocation, 1, 0.1F,
						});
			}

			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");

				helper.registerCrusherRecipe(
						new ResourceLocation("jaopca", "railcraft.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, 200, new Object[] {
								dustLocation, 1, 1F,
								dustLocation, 1, 0.35F,
						});
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerCrusherRecipe(
							new ResourceLocation("jaopca", "railcraft.raw_storage_block_to_dust."+material.getName()),
							rawMaterialLocation, 200, new Object[] {
									dustLocation, 12, 1F,
							});
				}
			}
		}
	}
}
