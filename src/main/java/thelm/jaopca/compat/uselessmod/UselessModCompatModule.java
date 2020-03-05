package thelm.jaopca.compat.uselessmod;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "uselessmod")
public class UselessModCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "gold", "iron", "super_useless", "useless"));

	static {
		if(ModList.get().isLoaded("silents_mechanisms")) {
			Collections.addAll(TO_DUST_BLACKLIST,
					"aluminum", "bismuth", "copper", "lead", "nickel", "platinum", "silver", "tin", "uranium", "zinc");
		}
	}

	@Override
	public String getName() {
		return "uselessmod_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		UselessModHelper helper = UselessModHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) && !TO_DUST_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerCrushingRecipe(
							new ResourceLocation("jaopca", "uselessmod.material_to_dust."+material.getName()),
							materialLocation, dustLocation, 1, 2.3F, 200);
				}
			}
		}
	}
}
