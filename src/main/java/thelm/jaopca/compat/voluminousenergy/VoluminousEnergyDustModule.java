package thelm.jaopca.compat.voluminousenergy;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
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

@JAOPCAModule(modDependencies = "voluminousenergy")
public class VoluminousEnergyDustModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"bauxite", "cinnabar", "coal", "diamond", "emerald", "galena", "lapis", "quartz", "redstone",
			"rutile", "saltpeter", "sulfur"));

	@Override
	public String getName() {
		return "voluminousenergy_dust";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(1, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		VoluminousEnergyHelper helper = VoluminousEnergyHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			if(material.hasExtra(1)) {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "voluminousenergy.ore_to_material."+material.getName()),
						oreLocation, 1, materialLocation, 6, extraDustLocation, 1, 0.5F, 200);
			}
			else {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "voluminousenergy.ore_to_material."+material.getName()),
						oreLocation, 1, materialLocation, 6, 200);
			}
		}
	}
}
