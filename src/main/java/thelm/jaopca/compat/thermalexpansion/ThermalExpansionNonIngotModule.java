package thelm.jaopca.compat.thermalexpansion;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "thermal_expansion@[1.3.0,)")
public class ThermalExpansionNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"apatite", "cinnabar", "coal", "diamond", "emerald", "lapis", "niter", "quartz", "redstone", "ruby",
			"sapphire", "sulfur"));
	private static final Set<String> PULVERIZER_BLACKLIST = new TreeSet<>();
	private static final Set<String> SMELTER_BLACKLIST = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("druidcraft")) {
			Collections.addAll(PULVERIZER_BLACKLIST, "amber", "fiery_glass", "moonstone", "rockroot");
		}
		if(ModList.get().isLoaded("mekanism")) {
			Collections.addAll(PULVERIZER_BLACKLIST, "fluorite");
		}
		if(ModList.get().isLoaded("rftools")) {
			Collections.addAll(PULVERIZER_BLACKLIST, "dimensional_shard");
		}
	}

	@Override
	public String getName() {
		return "thermal_expansion_non_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(1, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ThermalExpansionHelper helper = ThermalExpansionHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Item richSlag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:rich_slag"));
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			ResourceLocation extraMaterialLocation = miscHelper.getTagLocation(material.getExtra(1).getType().getFormName(), material.getExtra(1).getName());
			float pulverizerChance = material.getType() != MaterialType.DUST ? 2.5F : 6.5F;
			float smelterChance = material.getType() != MaterialType.DUST ? 1.5F : 3.5F;
			if(material.hasExtra(1)) {
				if(!PULVERIZER_BLACKLIST.contains(material.getName())) {
					helper.registerPulverizerRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.ore_to_material_pulverizer."+material.getName()),
							oreLocation, 1, new Object[] {
									materialLocation, pulverizerChance,
									extraDustLocation, 0.1F,
									Blocks.GRAVEL, 0.2F,
							}, 4000, 0.2F);
				}
				if(!SMELTER_BLACKLIST.contains(material.getName())) {
					helper.registerSmelterRecipe(
							new ResourceLocation("jaopca", "thermal_expansion.ore_to_material_smelter."+material.getName()), new Object[] {
									oreLocation,
							}, new Object[] {
									materialLocation, smelterChance,
									extraMaterialLocation, 0.2F,
									richSlag, 0.15F,
							}, 3200, 0.2F);
				}
			}
			else {
				helper.registerPulverizerRecipe(
						new ResourceLocation("jaopca", "thermal_expansion.ore_to_material_pulverizer."+material.getName()),
						oreLocation, 1, new Object[] {
								materialLocation, pulverizerChance,
								Blocks.GRAVEL, 0.2F,
						}, 4000, 0.2F);
				helper.registerSmelterRecipe(
						new ResourceLocation("jaopca", "thermal_expansion.ore_to_material_smelter."+material.getName()), new Object[] {
								oreLocation,
						}, new Object[] {
								materialLocation, smelterChance,
								richSlag, 0.15F,
						}, 3200, 0.2F);
			}
		}
	}
}
