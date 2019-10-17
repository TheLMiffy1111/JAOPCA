package thelm.jaopca.modules.compat.silentsmechanisms;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Sets;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule(modDependencies = "silents_mechanisms")
public class SilentsMechanismsModule implements IModule {

	private static final Set<String> BLACKLIST = Sets.newHashSet(
			"coal", "diamond", "dimensional", "emerald", "lapis", "quartz", "redstone");

	static {
		if(ModList.get().isLoaded("silentgems")) {
			Collections.addAll(BLACKLIST, "agate", "alexandrite", "amazonite", "amber", "amethyst", "ametrine", "ammolite",
					"apatite", "aquamarine", "benitoite", "black_diamond", "carnelian", "cats_eye", "chaos", "chrysoprase",
					"citrine", "coral", "euclase", "fluorite", "garnet", "green_sapphire", "heliodor", "iolite", "jade",
					"jasper", "kunzite", "kyanite", "lepidoite", "malachite", "moldavite", "moonstone", "morganite", "onyx",
					"opal", "pearl", "peridot", "phosphophyllite", "pyrope", "rose_quartz", "ruby", "sapphire", "sodalite",
					"spinel", "sunstone", "tanzanite", "teklite", "topaz", "turquoise", "yellow_diamond", "zircon");
		}
	}

	private static final TreeMap<IMaterial, Item> BYPRODUCTS = new TreeMap<>();

	@Override
	public String getName() {
		return "silents_mechanisms";
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
		for(IMaterial material : moduleData.getMaterials()) {
			if(material.getType() != MaterialType.DUST) {
				IDynamicSpecConfig config = configs.get(material);
				String byproduct = config.getDefinedString("silents_mechanisms.byproduct", "minecraft:cobblestone",
						s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Silent's Mechanisms' Crusher.");
				BYPRODUCTS.put(material, ForgeRegistries.ITEMS.getValue(new ResourceLocation(byproduct)));
			}
		}
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = JAOPCAApi.instance();
		SilentsMechanismsHelper helper = SilentsMechanismsHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = api.miscHelper().getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = api.miscHelper().getTagLocation(material.getType().getFormName(), material.getName());
			if(material.getType() != MaterialType.DUST) {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "silents_mechanisms.ore_to_material."+material.getName()), oreLocation, 400, new Object[] {
								materialLocation, 2,
								materialLocation, 1, 0.1F,
								BYPRODUCTS.get(material), 1, 0.1F,
						});
			}
			else {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "silents_mechanisms.ore_to_material."+material.getName()), oreLocation, 400, new Object[] {
								materialLocation, 6,
						});
			}
		}
	}
}
