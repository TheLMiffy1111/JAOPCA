package thelm.jaopca.compat.immersiveengineering;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "immersiveengineering")
public class ImmersiveEngineeringModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "ardite", "cobalt", "copper", "gold", "iron", "lead", "netherite",
			"netherite_scrap", "nickel", "osmium", "platinum", "silver", "tin", "tungsten", "uranium", "zinc"));

	@Override
	public String getName() {
		return "immersiveengineering";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		builder.put(1, "dusts");
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
		JAOPCAApi api = ApiImpl.INSTANCE;
		ImmersiveEngineeringHelper helper = ImmersiveEngineeringHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Item hammer = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:hammer"));
		Item slag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering:slag"));
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ResourceLocation extraDustLocation = miscHelper.getTagLocation("dusts", material.getExtra(1).getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "immersiveengineering.ore_to_dust_hammer."+material.getName()),
					dustLocation, 1, new Object[] {
							oreLocation, hammer,
					});
			if(material.hasExtra(1)) {
				helper.registerCrusherRecipe(
						new ResourceLocation("jaopca", "immersiveengineering.ore_to_dust_crusher."+material.getName()),
						oreLocation, new Object[] {
								dustLocation, 2,
								extraDustLocation, 1, 0.1F,
						}, 6000);
			}
			else {
				helper.registerCrusherRecipe(
						new ResourceLocation("jaopca", "immersiveengineering.ore_to_dust_crusher."+material.getName()),
						oreLocation, new Object[] {
								dustLocation, 2,
						}, 6000);
			}
			helper.registerArcFurnaceRecipe(
					new ResourceLocation("jaopca", "immersiveengineering.ore_to_ingot."+material.getName()),
					new Object[] {
							oreLocation, 1,
					}, slag, 1, new Object[] {
							materialLocation, 2,
					}, 200, 102400);
		}
	}
}
