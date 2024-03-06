package thelm.jaopca.compat.modernindustrialization;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "modern_industrialization")
public class ModernIndustrializationModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"antimony", "copper", "gold", "iridium", "iron", "lead", "nickel", "platinum", "silver", "tin", "titanium",
			"tungsten", "uranium"));

	static {
		if(ModList.get().isLoaded("mekanism")) {
			BLACKLIST.add("osmium");
		}
	}
	
	@Override
	public String getName() {
		return "modern_industrialization";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
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
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ModernIndustrializationHelper helper = ModernIndustrializationHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerMaceratorRecipe(
					new ResourceLocation("jaopca", "modern_industrialization.ore_to_raw_material."+material.getName()),
					oreLocation, 1, 1F, new Object[] {
							rawMaterialLocation, 3, 1F,
					}, 2, 200);
			helper.registerMaceratorRecipe(
					new ResourceLocation("jaopca", "modern_industrialization.raw_material_to_dust."+material.getName()),
					rawMaterialLocation, 1, 1F, new Object[] {
							dustLocation, 1, 1F,
							dustLocation, 1, 0.5F,
					}, 2, 100);
		}
		// Special case silver
		if(api.getMaterial("silver") != null) {
			helper.registerMaceratorRecipe(
					new ResourceLocation("jaopca", "modern_industrialization.ore_to_raw_material.silver"),
					miscHelper.getTagLocation("ores", "silver"), 1, 1F, new Object[] {
							miscHelper.getTagLocation("raw_materials", "silver"), 3, 1F,
					}, 2, 100);
		}
	}
}
