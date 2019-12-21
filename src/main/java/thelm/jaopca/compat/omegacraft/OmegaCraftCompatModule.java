package thelm.jaopca.compat.omegacraft;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "omegacraft")
public class OmegaCraftCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "bronze", "copper", "electrum", "gold", "iron", "lead", "magmite", "silver", "steel", "tin"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "bronze", "cave_magmite", "copper", "electrum", "gold", "iron", "lead", "magmite", "silver",
			"steel", "tin"));
	private static final Set<String> COMPACT_TO_BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "bronze", "cave_magmite", "copper", "electrum", "gold", "iron", "lead", "magmite", "silver",
			"steel", "tin"));
	private static final Set<String> COMPACT_TO_INGOT_BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "bronze", "cave_magmite", "copper", "electrum", "gold", "iron", "lead", "magmite", "silver",
			"steel", "tin"));

	private boolean recipeRegistered = false;

	@Override
	public String getName() {
		return "omegacraft_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void onRecipeInjectComplete(IModuleData moduleData, IResourceManager resourceManager) {
		if(!recipeRegistered) {
			JAOPCAApi api = ApiImpl.INSTANCE;
			OmegaCraftHelper helper = OmegaCraftHelper.INSTANCE;
			IMiscHelper miscHelper = MiscHelper.INSTANCE;
			for(IMaterial material : moduleData.getMaterials()) {
				MaterialType type = material.getType();
				if(ArrayUtils.contains(MaterialType.INGOTS, type)) {
					if(!TO_DUST_BLACKLIST.contains(material.getName())) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
						ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
						if(api.getItemTags().contains(dustLocation)) {
							helper.registerCrusherRecipe(materialLocation, 1, dustLocation, 1);
						}
					}
					if(!TO_PLATE_BLACKLIST.contains(material.getName())) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
						if(api.getItemTags().contains(plateLocation)) {
							helper.registerPlateFormerRecipe(materialLocation, 1, plateLocation, 1);
						}
					}
					if(!COMPACT_TO_BLOCK_BLACKLIST.contains(material.getName())) {
						ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
						ResourceLocation blockLocation = miscHelper.getTagLocation("blocks", material.getName());
						if(api.getItemTags().contains(blockLocation)) {
							helper.registerBlockCompactorRecipe(materialLocation, 9, blockLocation, 1);
						}
					}
					if(!COMPACT_TO_INGOT_BLACKLIST.contains(material.getName())) {
						ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
						ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
						if(api.getItemTags().contains(plateLocation)) {
							helper.registerBlockCompactorRecipe(plateLocation, 1, materialLocation, 1);
						}
					}
				}
			}
			recipeRegistered = true;
		}
	}
}
