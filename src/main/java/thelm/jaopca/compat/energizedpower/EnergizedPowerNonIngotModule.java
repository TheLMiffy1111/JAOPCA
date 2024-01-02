package thelm.jaopca.compat.energizedpower;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "energizedpower")
public class EnergizedPowerNonIngotModule implements IModule {

	private static final double[] CRYSTAL_CHANCES = {1, 0.67, 0.17};
	private static final double[] DUST_CHANCES = {1, 1, 1, 1, 0.5, 0.33, 0.17};
	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"coal", "diamond", "emerald", "lapis", "quartz", "redstone"));

	@Override
	public String getName() {
		return "energizedpower_non_ingot";
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
		EnergizedPowerHelper helper = EnergizedPowerHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			double[] outputChances = material.getType() != MaterialType.DUST ? CRYSTAL_CHANCES : DUST_CHANCES;
			helper.registerPulverizerRecipe(
					new ResourceLocation("jaopca", "energizedpower.ore_to_material."+material.getName()),
					oreLocation, materialLocation, outputChances);
		}
	}
}
