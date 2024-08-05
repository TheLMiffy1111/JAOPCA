package thelm.jaopca.compat.railcraft;

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

@JAOPCAModule(modDependencies = "railcraft")
public class RailcraftNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"coal", "diamond", "emerald", "firestone", "lapis", "quartz", "redstone", "salt", "sulfur"));

	@Override
	public String getName() {
		return "railcraft_non_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(1, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		RailcraftHelper helper = RailcraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			if(BLACKLIST.contains(material.getName())) {
				continue;
			}
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			int mainOutputCount = material.getType() != MaterialType.DUST ? 1 : 3;
			if(material.hasExtra(1)) {
				helper.registerCrusherRecipe(
						miscHelper.getRecipeKey("railcraft.ore_to_material", material.getName()),
						oreLocation, 200, new Object[] {
								materialLocation, mainOutputCount, 1F,
								materialLocation, 1, 0.85F,
								materialLocation, 1, 0.25F,
								extraDustLocation, 1, 0.1F,
						});
			}
			else {
				helper.registerCrusherRecipe(
						miscHelper.getRecipeKey("railcraft.ore_to_material", material.getName()),
						oreLocation, 200, new Object[] {
								materialLocation, mainOutputCount, 1F,
								materialLocation, 1, 0.85F,
								materialLocation, 1, 0.25F,
						});
			}
		}
	}
}
