package thelm.jaopca.compat.hephaestus;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "hephaestus")
public class HephaestusModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"cobalt", "copper", "gold", "iron", "netherite", "netherite_scrap", "steel"));

	private static boolean jaopcaOnly = true;

	static {
		if(ModList.get().isLoaded("allthemodium")) {
			Collections.addAll(BLACKLIST, "allthemodium", "unobtainium", "vibranium");
		}
		if(ModList.get().isLoaded("alltheores")) {
			Collections.addAll(BLACKLIST, "aluminium", "aluminum", "iridium", "lead", "nickel", "osmium", "platinum",
					"silver", "tin", "uranium", "zinc");
		}
		if(ModList.get().isLoaded("ftbarmory")) {
			Collections.addAll(BLACKLIST, "adamantite", "aeternium", "aurichalcum");
		}
		if(ModList.get().isLoaded("ftbmaterials")) {
			Collections.addAll(BLACKLIST, "aluminium", "aluminum", "chromium", "iridium", "lead", "nickel", "platinum",
					"silver", "tin", "titanium", "uranium", "zinc");
		}
		if(ModList.get().isLoaded("replication")) {
			Collections.addAll(BLACKLIST, "replica");
		}
	}

	@Override
	public String getName() {
		return "hephaestus";
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
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		HephaestusHelper helper = HephaestusHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getFluidType().getTemperature(stack)-300;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");

			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				helper.registerMeltingRecipe(
						miscHelper.getRecipeKey("hephaestus.raw_material_to_molten", material.getName()),
						rawMaterialLocation, null, 50, moltenLocation, 90, tempFunction, 150);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerMeltingRecipe(
							miscHelper.getRecipeKey("hephaestus.raw_storage_block_to_molten", material.getName()),
							rawStorageBlockLocation, null, 50, moltenLocation, 810, tempFunction, 250);
				}
			}
			else {
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
				helper.registerMeltingRecipe(
						miscHelper.getRecipeKey("hephaestus.ore_to_molten", material.getName()),
						oreLocation, null, 50, moltenLocation, 180, tempFunction, 150);

			}
		}
	}
}
