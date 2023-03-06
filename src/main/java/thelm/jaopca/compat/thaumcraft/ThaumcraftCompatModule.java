package thelm.jaopca.compat.thaumcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thaumcraft.api.items.ItemsTC;
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

@JAOPCAModule(modDependencies = "thaumcraft")
public class ThaumcraftCompatModule implements IModule {

	private static final Set<String> ORE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Cinnabar", "Copper", "Diamond", "Emerald", "Gold", "Iron", "Lapis", "Lead", "NetherQuartz", "Quartz",
			"Redstone", "Silver", "Tin"));
	private static final Set<String> CLUSTER_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Cinnabar", "Copper", "Gold", "Iron", "Lead", "NetherQuartz", "Quartz", "Silver", "Tin"));
	private static Set<String> configOreBlacklist = new TreeSet<>();
	private static Set<String> configClusterBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "thaumcraft_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.oreMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have smelting bonus ore to rare earth recipes added."),
				configOreBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.clusterMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have smelting bonus cluster to rare earth recipes added."),
				configClusterBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ThaumcraftHelper helper = ThaumcraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		ItemStack rareEarths = new ItemStack(ItemsTC.nuggets, 1, 10);
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isOre() && !ORE_BLACKLIST.contains(name) && !configOreBlacklist.contains(name)) {
				String oreOredict = miscHelper.getOredictName("ore", material.getName());
				helper.registerSmeltingBonusRecipe(
						miscHelper.getRecipeKey("thaumcraft.ore_to_rare_earths", material.getName()),
						oreOredict, rareEarths, 1, 0.01F);
			}
			if(type == MaterialType.INGOT && !CLUSTER_BLACKLIST.contains(name) && !configClusterBlacklist.contains(name)) {
				String clusterOredict = miscHelper.getOredictName("cluster", material.getName());
				if(oredict.contains(clusterOredict)) {
					helper.registerSmeltingBonusRecipe(
							miscHelper.getRecipeKey("thaumcraft.cluster_to_rare_earths", material.getName()),
							clusterOredict, rareEarths, 1, 0.02F);
				}
			}
		}
	}
}
