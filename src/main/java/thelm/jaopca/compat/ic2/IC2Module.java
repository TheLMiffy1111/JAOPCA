package thelm.jaopca.compat.ic2;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "ic2@[1.19.2-2.0.9,1.20)")
public class IC2Module implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "copper", "gold", "iron", "silver", "tin", "uranium"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	public IC2Module() {
		IC2DataInjector.init();
	}

	@Override
	public String getName() {
		return "ic2";
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
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IC2Helper helper = IC2Helper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		Fluid biofuel = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("ic2:bio_fuel"));
		Fluid alcohol = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("ic2:alcohol"));
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerMaceratorRecipe(
					new ResourceLocation("jaopca", "ic2.ore_to_dust."+material.getName()),
					oreLocation, 1,
					dustLocation, 2,
					1, 1, 0.5F);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				double multiplier = configs.get(material).getDefinedDouble("ic2.multiplier", 2, "The multiplier of this material's recipes in the refinery with respect to gold.");
				helper.registerMaceratorRecipe(
						new ResourceLocation("jaopca", "ic2.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, 1,
						dustLocation, 2,
						1, 1, 0.5F);
				helper.registerRefineryRecipe(
						new ResourceLocation("jaopca", "ic2.ore_to_raw_material_lava."+material.getName()),
						oreLocation, 1,
						Fluids.LAVA, Mth.ceil(25*multiplier),
						rawMaterialLocation, Mth.ceil(1*multiplier), Mth.ceil(3*multiplier),
						0.5, 1);
				helper.registerRefineryRecipe(
						new ResourceLocation("jaopca", "ic2.ore_to_raw_material_bio_fuel."+material.getName()),
						oreLocation, 1,
						biofuel, Mth.ceil(50*multiplier),
						rawMaterialLocation, Mth.ceil(3*multiplier), Mth.ceil(5*multiplier),
						0.5, 1);
				helper.registerRefineryRecipe(
						new ResourceLocation("jaopca", "ic2.ore_to_raw_material_alcohol."+material.getName()),
						oreLocation, 1,
						alcohol, Mth.ceil(25*multiplier),
						rawMaterialLocation, Mth.ceil(5*multiplier), Mth.ceil(7*multiplier),
						0.5, 1);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerMaceratorRecipe(
							new ResourceLocation("jaopca", "ic2.raw_storage_block_to_dust."+material.getName()),
							rawStorageBlockLocation, 1,
							dustLocation, 18,
							4, 1, 4.5F);
				}
			}
		}
	}
}
