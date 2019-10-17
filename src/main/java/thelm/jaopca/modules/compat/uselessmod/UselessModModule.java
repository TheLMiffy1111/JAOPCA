package thelm.jaopca.modules.compat.uselessmod;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule(modDependencies = "uselessmod")
public class UselessModModule implements IModule {

	private static final Set<String> BLACKLIST = Sets.newHashSet("coal", "diamond", "emerald", "gold", "iron", "redstone", "super_useless", "useless");
	private static final Set<String> ORE_BLACKLIST = Sets.newHashSet("lapis", "quartz");

	@Override
	public String getName() {
		return "uselessmod";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.complementOf(EnumSet.of(MaterialType.DUST_PLAIN));
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = JAOPCAApi.instance();
		UselessModHelper helper = UselessModHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation materialLocation = api.miscHelper().getTagLocation(material.getType().getFormName(), material.getName());
			switch(material.getType()) {
			case DUST:
				if(!ORE_BLACKLIST.contains(material.getName())) {
					ResourceLocation oreLocation = api.miscHelper().getTagLocation("ores", material.getName());
					helper.registerCrusherRecipe(
							new ResourceLocation("jaopca", "uselessmod.ore_to_material."+material.getName()),
							oreLocation, materialLocation, 6, 0, 200);
				}
				break;
			case GEM:
			case CRYSTAL:
				if(!ORE_BLACKLIST.contains(material.getName())) {
					ResourceLocation oreLocation = api.miscHelper().getTagLocation("ores", material.getName());
					helper.registerCrusherRecipe(
							new ResourceLocation("jaopca", "uselessmod.ore_to_material."+material.getName()),
							oreLocation, materialLocation, 2, 0.5F, 200);
				}
			case GEM_PLAIN:
			case CRYSTAL_PLAIN:
			case INGOT:
			case INGOT_PLAIN:
			default:
				ResourceLocation dustLocation = api.miscHelper().getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerCrusherRecipe(
							new ResourceLocation("jaopca", "uselessmod.material_to_dust."+material.getName()),
							materialLocation, dustLocation, 1, 2.3F, 200);
				}
				break;
			}
		}
	}
}
