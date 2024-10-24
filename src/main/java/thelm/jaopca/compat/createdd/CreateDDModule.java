package thelm.jaopca.compat.createdd;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "create_dd")
public class CreateDDModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "zinc"));

	static {
		if(ModList.get().isLoaded("galosphere")) {
			Collections.addAll(BLACKLIST, "silver");
		}
		if(ModList.get().isLoaded("ic2")) {
			Collections.addAll(BLACKLIST, "aluminium", "aluminum", "silver", "tin", "uranium");
		}
		if(ModList.get().isLoaded("iceandfire")) {
			Collections.addAll(BLACKLIST, "silver");
		}
		if(ModList.get().isLoaded("immersiveengineering")) {
			Collections.addAll(BLACKLIST, "aluminium", "aluminum", "lead", "nickel", "silver", "uranium");
		}
		if(ModList.get().isLoaded("mekanism")) {
			Collections.addAll(BLACKLIST, "lead", "osmium", "tin", "uranium");
		}
		if(ModList.get().isLoaded("oreganized")) {
			Collections.addAll(BLACKLIST, "lead");
		}
		if(ModList.get().isLoaded("thermal")) {
			Collections.addAll(BLACKLIST, "lead", "nickel", "silver", "tin");
		}
	}

	@Override
	public String getName() {
		return "create_dd";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "create");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		CreateDDHelper helper = CreateDDHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation crushedLocation = miscHelper.getTagLocation("create:crushed_raw_materials", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			helper.registerSeethingRecipe(
					new ResourceLocation("jaopca", "create_dd.crushed_to_ingot."+material.getName()),
					crushedLocation, new Object[] {
							materialLocation, 1, 1F,
							materialLocation, 1, 0.5F,
					});
		}
	}
}
