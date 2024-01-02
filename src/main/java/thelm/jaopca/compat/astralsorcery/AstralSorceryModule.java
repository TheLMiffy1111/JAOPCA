package thelm.jaopca.compat.astralsorcery;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "astralsorcery")
public class AstralSorceryModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "gold", "iron", "lapis", "netherite", "netherite_scrap",
			"redstone"));

	@Override
	public String getName() {
		return "astralsorcery";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		AstralSorceryHelper helper = AstralSorceryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Fluid liquidStarlight = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("astralsorcery:liquid_starlight"));
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			int outputCount;
			switch(material.getType()) {
			case INGOT:
			default:
				outputCount = 3;
				break;
			case GEM:
			case CRYSTAL:
				outputCount = 4;
				break;
			case DUST:
				outputCount = 5;
				break;
			}
			helper.registerInfuserRecipe(
					new ResourceLocation("jaopca", "astralsorcery.ore_to_material."+material.getName()),
					liquidStarlight, oreLocation, materialLocation, outputCount,
					100, 0.1F, false, true, false);
		}
	}
}
