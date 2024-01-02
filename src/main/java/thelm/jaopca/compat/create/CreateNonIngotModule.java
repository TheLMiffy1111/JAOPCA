package thelm.jaopca.compat.create;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "create")
public class CreateNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "lapis", "quartz", "redstone"));

	static {
		if(ModList.get().isLoaded("silentgems")) {
			Collections.addAll(BLACKLIST,
					"agate", "alexandrite", "amazonite", "amber", "amethyst", "ametrine", "ammolite", "apatite", "aquamarine",
					"benitoite", "black_diamond", "carnelian", "cats_eye", "chrysoprase", "citrine", "coral", "euclase",
					"fluorite", "garnet", "green_sapphire", "heliodor", "iolite", "jade", "jasper", "kunzite", "kyanite",
					"lepidoite", "malachite", "moldavite", "moonstone", "morganite", "onyx", "opal", "pearl", "peridot",
					"phosphophyllite", "pyrope", "rose_quartz", "ruby", "sapphire", "sodalite", "spinel", "sunstone",
					"tanzanite", "teklite", "topaz", "turquoise", "yellow_diamond", "zircon");
		}
		if(ModList.get().isLoaded("quark")) {
			Collections.addAll(BLACKLIST, "biotite");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "create_non_ingot";
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
		CreateHelper helper = CreateHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("create.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Create's crushing.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			if(material.getType() != MaterialType.DUST) {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "create.ore_to_material_crushing."+material.getName()),
						oreLocation, 500, new Object[] {
								materialLocation, 2,
								materialLocation, 1, 0.25F,
								byproduct, 1, 0.125F,
						});
			}
			else {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "create.ore_to_material_crushing."+material.getName()),
						oreLocation, 300, new Object[] {
								materialLocation, 4,
								materialLocation, 2, 0.25F,
								byproduct, 1, 0.125F,
						});
			}
		}
	}
}
