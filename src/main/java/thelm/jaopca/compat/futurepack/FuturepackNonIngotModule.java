package thelm.jaopca.compat.futurepack;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

@JAOPCAModule(modDependencies = "futurepack")
public class FuturepackNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"adrite", "amethyst", "apatite", "bauxite", "beryllium", "cinnabar", "coal", "cobalt", "copper",
			"devilsiron", "diamond", "emerald", "gold", "iridium", "iron", "lapis", "lead", "magnesium",
			"magnetite", "manganese", "molybdenum", "naquadah", "nickel", "olivine", "platinum", "pyrite",
			"quartz", "redstone", "ruby", "salt", "silver", "sulfur", "sulphur", "tin", "titanium", "tungsten",
			"unobtanium", "wulfenit", "zinc"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "futurepack_non_ingot";
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
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		FuturepackHelper helper = FuturepackHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("futurepack.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Futurepack's Centrifuge.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			Object[] output = {
					materialLocation, (material.getType().isCrystalline() ? 12 : 24),
					byproduct, 3,
			};
			if(material.hasExtra(1)) {
				ResourceLocation exLocation = switch(material.getExtra(1).getType()) {
				case GEM, GEM_PLAIN -> miscHelper.getTagLocation("gems", material.getExtra(1).getName());
				case CRYSTAL, CRYSTAL_PLAIN -> miscHelper.getTagLocation("crystals", material.getExtra(1).getName());
				default -> miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
				};
				output = ArrayUtils.addAll(output, exLocation, 2);
			}

			helper.registerCentrifugeRecipe(
					new ResourceLocation("jaopca", "futurepack.ore_to_material."+material.getName()),
					oreLocation, 4, material.getType().isCrystalline() ? 8 : 6, 200, output);
		}
	}
}
