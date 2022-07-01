package thelm.jaopca.compat.exnihilosequentia;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
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

@JAOPCAModule(modDependencies = "exnihilosequentia")
public class ExNihiloSequentiaNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "lapis", "quartz", "redstone"));

	static {
		if(ModList.get().isLoaded("exnihiloae")) {
			Collections.addAll(BLACKLIST, "certus_quartz");
		}
		if(ModList.get().isLoaded("exnihilomekanism")) {
			Collections.addAll(BLACKLIST, "fluorite");
		}
		if(ModList.get().isLoaded("exnihilothermal")) {
			Collections.addAll(BLACKLIST, "apatite", "cinnabar", "niter", "sulfur");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "exnihilosequentia_non_ingot";
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
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ExNihiloSequentiaHelper helper = ExNihiloSequentiaHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Item gravel = Items.GRAVEL;
		Item crushedNetherrack = ForgeRegistries.ITEMS.getValue(new ResourceLocation("exnihilosequentia:crushed_netherrack"));
		Item crushedEndstone = ForgeRegistries.ITEMS.getValue(new ResourceLocation("exnihilosequentia:crushed_end_stone"));
		Item dust = ForgeRegistries.ITEMS.getValue(new ResourceLocation("exnihilosequentia:dust"));
		Item soulSand = Items.SOUL_SAND;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			if(material.getType() != MaterialType.DUST) {
				IDynamicSpecConfig config = configs.get(material);
				boolean sieveGravel = config.getDefinedBoolean("exnihilosequentia.sieveGravel", true,
						"Should gravel sieve recipes be added.");
				boolean sieveCrushedNetherrack = config.getDefinedBoolean("exnihilosequentia.sieveCrushedNetherrack", false,
						"Should crushed netherrack sieve recipes be added.");
				boolean sieveCrushedEndstone = config.getDefinedBoolean("exnihilosequentia.sieveCrushedEndstone", false,
						"Should crushed end stone sieve recipes be added.");
				float sieveIronChance = config.getDefinedFloat("exnihilosequentia.sieveIronChance", 0F, 1F, 0.008F,
						"The output chance of the sieve recipes added using an iron mesh.");
				float sieveDiamondChance = config.getDefinedFloat("exnihilosequentia.sieveDiamondChance", 0F, 1F, 0.016F,
						"The output chance of the sieve recipes added using a diamond mesh.");

				if(sieveGravel) {
					helper.registerSieveRecipe(
							new ResourceLocation("jaopca", "exnihilosequentia.material_gravel."+material.getName()),
							gravel, materialLocation, 1, new Number[] {
									3, sieveIronChance,
									4, sieveDiamondChance,
							}, false);
				}
				if(sieveCrushedNetherrack) {
					helper.registerSieveRecipe(
							new ResourceLocation("jaopca", "exnihilosequentia.material_crushed_netherrack."+material.getName()),
							crushedNetherrack, materialLocation, 1, new Number[] {
									3, sieveIronChance,
									4, sieveDiamondChance,
							}, false);
				}
				if(sieveCrushedEndstone) {
					helper.registerSieveRecipe(
							new ResourceLocation("jaopca", "exnihilosequentia.material_crushed_end_stone."+material.getName()),
							crushedEndstone, materialLocation, 1, new Number[] {
									3, sieveIronChance,
									4, sieveDiamondChance,
							}, false);
				}
			}
			else {
				IDynamicSpecConfig config = configs.get(material);
				boolean sieveDust = config.getDefinedBoolean("exnihilosequentia.sieveGravel", true,
						"Should dust sieve recipes be added.");
				boolean sieveSoulSand = config.getDefinedBoolean("exnihilosequentia.sieveCrushedNetherrack", false,
						"Should soul sand sieve recipes be added.");
				float sieveIronChance = config.getDefinedFloat("exnihilosequentia.sieveIronChance", 0F, 1F, 0.0625F,
						"The output chance of the sieve recipes added using an iron mesh.");
				float sieveDiamondChance = config.getDefinedFloat("exnihilosequentia.sieveDiamondChance", 0F, 1F, 0.125F,
						"The output chance of the sieve recipes added using a diamond mesh.");

				if(sieveDust) {
					helper.registerSieveRecipe(
							new ResourceLocation("jaopca", "exnihilosequentia.material_dust."+material.getName()),
							gravel, materialLocation, 1, new Number[] {
									3, sieveIronChance,
									4, sieveDiamondChance,
							}, false);
				}
				if(sieveSoulSand) {
					helper.registerSieveRecipe(
							new ResourceLocation("jaopca", "exnihilosequentia.material_soul_sand."+material.getName()),
							crushedNetherrack, materialLocation, 1, new Number[] {
									3, sieveIronChance,
									4, sieveDiamondChance,
							}, false);
				}
			}
		}
	}
}
