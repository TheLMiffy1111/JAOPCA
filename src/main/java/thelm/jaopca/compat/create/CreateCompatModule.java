package thelm.jaopca.compat.create;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.ResourceLocation;
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

@JAOPCAModule(modDependencies = "create")
public class CreateCompatModule implements IModule {

	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"brass", "copper", "gold", "iron"));

	@Override
	public String getName() {
		return "create_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		CreateHelper helper = CreateHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			if(ArrayUtils.contains(MaterialType.INGOTS, type) && !TO_PLATE_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(api.getItemTags().contains(plateLocation)) {
					helper.registerPressingRecipe(
							new ResourceLocation("jaopca", "create.material_to_plate."+material.getName()),
							materialLocation, plateLocation, 1);
				}
			}
		}
	}
}
