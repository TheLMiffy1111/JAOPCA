package thelm.jaopca.compat.crossroads;

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

@JAOPCAModule(modDependencies = "crossroads")
public class CrossroadsCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iron", "tin"));
	private static final Set<String> TO_MOLTEN_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "copshowium", "gold", "iron", "tin"));
	private static final Set<String> TO_MATERIAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "copshowium", "gold", "iron", "tin"));

	@Override
	public String getName() {
		return "crossroads_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		CrossroadsHelper helper = CrossroadsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) && !TO_DUST_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerMillRecipe(
							new ResourceLocation("jaopca", "crossroads.material_to_dust."+material.getName()),
							materialLocation, dustLocation, 1);
				}
			}
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) && !TO_MOLTEN_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName());
				if(api.getFluidTags().contains(moltenLocation)) {
					helper.registerCrucibleRecipe(
							new ResourceLocation("jaopca", "crossroads.material_to_molten."+material.getName()),
							materialLocation, moltenLocation, 144);
					if(api.getItemTags().contains(dustLocation)) {
						helper.registerCrucibleRecipe(
								new ResourceLocation("jaopca", "crossroads.dust_to_molten."+material.getName()),
								dustLocation, moltenLocation, 144);
					}
					if(api.getItemTags().contains(nuggetLocation)) {
						helper.registerCrucibleRecipe(
								new ResourceLocation("jaopca", "crossroads.nugget_to_molten."+material.getName()),
								nuggetLocation, moltenLocation, 16);
					}
				}
			}
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) && !TO_MATERIAL_BLACKLIST.contains(material.getName())) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(api.getFluidTags().contains(moltenLocation)) {
					helper.registerFluidCoolingRecipe(
							new ResourceLocation("jaopca", "crossroads.molten_to_material."+material.getName()),
							moltenLocation, 144, materialLocation, 1, 1500, 100);
				}
			}
		}
	}
}
