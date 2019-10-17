package thelm.jaopca.modules.compat.uselessmod;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
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
public class UselessModIngotModule implements IModule {

	private static final Set<String> BLACKLIST = Sets.newHashSet("gold", "iron", "super_useless", "useless");

	@Override
	public String getName() {
		return "uselessmod_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
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
			ResourceLocation oreLocation = api.miscHelper().getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = api.miscHelper().getTagLocation("dusts", material.getName());
			helper.registerCrusherRecipe(
					new ResourceLocation("jaopca", "uselessmod.ore_to_dust."+material.getName()),
					oreLocation, dustLocation, 2, 2.3F, 200);
		}
	}
}
