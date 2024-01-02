package thelm.jaopca.compat.cyclic;

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

@JAOPCAModule(modDependencies = "cyclic")
public class CyclicNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"coal", "diamond", "emerald", "inferium", "lapis", "prosperity", "quartz", "redstone", "soulium"));

	@Override
	public String getName() {
		return "cyclic_non_ingot";
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
		CyclicHelper helper = CyclicHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			boolean isCrystal = material.getType().isCrystalline();
			helper.registerCrusherRecipe(
					new ResourceLocation("jaopca", "cyclic.ore_to_material."+material.getName()),
					oreLocation, materialLocation, 3,
					materialLocation, (isCrystal ? 2 : 4), (isCrystal ? 50 : 60),
					(isCrystal ? 600 : 300), 40);
		}
	}
}
