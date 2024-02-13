package thelm.jaopca.compat.techreborn;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import ic2.api.item.IC2Items;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidRegistry;
import techreborn.init.ModFluids;
import techreborn.init.ModItems;
import techreborn.items.ItemCells;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "techreborn")
public class TechRebornDustModule implements IModule {

	private static Set<String> configWaterBlacklist = new TreeSet<>();
	private static Set<String> configSodiumPersulfateBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "techreborn_dust";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(1, "small_dust");
		builder.put(2, "small_dust");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return TechRebornModule.BLACKLIST;
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.waterMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have water industrial grinder recipes added."),
				configWaterBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.sodiumPersulfateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have sodium persulfate	 industrial grinder recipes added."),
				configSodiumPersulfateBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String extraSmallDustOredict = miscHelper.getOredictName("dustSmall", material.getExtra(1).getName());
			String secondExtraSmallDustOredict = miscHelper.getOredictName("dustSmall", material.getExtra(2).getName());
			if(!configWaterBlacklist.contains(material.getName())) {
				Object[] output = {
						materialOredict, 2,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraSmallDustOredict, 1);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustOredict, 1);
				}
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_water_fluid", material.getName()),
						new Object[] {
								oreOredict, 1,
						}, FluidRegistry.WATER, 1000, output, 100, 120);
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_water_bucket", material.getName()),
						new Object[] {
								oreOredict, 1,
								Items.water_bucket, 1,
						}, null, 0, ArrayUtils.addAll(output, Items.bucket, 1), 100, 120);
				if(Loader.isModLoaded("IC2")) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("techreborn.ore_to_material_water_cell", material.getName()),
							new Object[] {
									oreOredict, 1,
									IC2Items.getItem("waterCell"), 1,
							}, null, 0, ArrayUtils.addAll(output, IC2Items.getItem("cell"), 1), 100, 120);
				}
			}
			if(!configSodiumPersulfateBlacklist.contains(material.getName())) {
				Object[] output = {
						materialOredict, 3,
				};
				if(material.hasExtra(1)) {
					output = ArrayUtils.addAll(output, extraSmallDustOredict, 1);
				}
				if(material.hasExtra(2)) {
					output = ArrayUtils.addAll(output, secondExtraSmallDustOredict, 1);
				}
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_sodium_persulfate_fluid", material.getName()),
						new Object[] {
								oreOredict, 1,
						}, ModFluids.BlockFluidSodiumpersulfate, 1000, output, 100, 120);
				helper.registerGrinderRecipe(
						miscHelper.getRecipeKey("techreborn.ore_to_material_sodium_persulfate_bucket", material.getName()),
						new Object[] {
								oreOredict, 1,
								ModItems.bucketSodiumpersulfate, 1,
						}, null, 0, ArrayUtils.addAll(output, Items.bucket, 1), 100, 120);
				if(Loader.isModLoaded("IC2")) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("techreborn.ore_to_material_sodium_persulfate_cell", material.getName()),
							new Object[] {
									oreOredict, 1,
									ItemCells.getCellByName("sodiumPersulfate"), 1,
							}, null, 0, ArrayUtils.addAll(output, IC2Items.getItem("cell"), 1), 100, 120);
				}
			}
		}
	}
}
