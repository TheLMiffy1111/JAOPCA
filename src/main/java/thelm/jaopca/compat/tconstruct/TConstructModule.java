package thelm.jaopca.compat.tconstruct;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "tconstruct")
public class TConstructModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "cobalt", "copper", "debris", "diamond", "emerald", "gold", "iron",
			"lead", "netherite", "netherite_scrap", "nickel", "osmium", "platinum", "quartz", "silver",
			"tin", "tungsten", "uranium", "zinc"));

	private static boolean jaopcaOnly = true;

	static {
		if(ModList.get().isLoaded("allthemodium")) {
			Collections.addAll(BLACKLIST, "allthemodium", "unobtainium", "vibranium");
		}
		if(ModList.get().isLoaded("materialis")) {
			Collections.addAll(BLACKLIST, "cloggrum", "froststeel", "iesnium", "quicksilver", "regalium", "starmetal", "utherium");
		}
		if(ModList.get().isLoaded("bettercompat")) {
			Collections.addAll(BLACKLIST, "amethyst", "black_opal", "certus_quartz", "moonstone", "thallasium");
		}
		if(ModList.get().isLoaded("natureminerals")) {
			Collections.addAll(BLACKLIST, "astrite", "kunzite", "stibnite", "uvarovite");
		}
		if(ModList.get().isLoaded("taiga")) {
			Collections.addAll(BLACKLIST, "abyssum", "aurorium", "dilithium", "duranite", "eezo", "jauxum", "karmesine",
					"osram", "ovium", "palladium", "prometheum", "tiberium", "uru", "valyrium", "vibranium");
		}
	}

	@Override
	public String getName() {
		return "tconstruct";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "molten");
		builder.put(1, "molten");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getAttributes().getTemperature(stack)-300;
		ToIntFunction<FluidStack> timeFunction = stack->IMeltingRecipe.calcTime(tempFunction.applyAsInt(stack), 1.5F);
		for(IMaterial material : moduleData.getMaterials()) {
			if(!jaopcaOnly || moltenMaterials.contains(material)) {
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				ResourceLocation extraMoltenLocation = miscHelper.getTagLocation("molten", material.getExtra(1).getName(), "_");
				if(material.hasExtra(1)) {
					helper.registerMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten."+material.getName()),
							oreLocation, moltenLocation, 144,
							tempFunction, timeFunction, true,
							extraMoltenLocation, 48);
				}
				else {
					helper.registerMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten."+material.getName()),
							oreLocation, moltenLocation, 144,
							tempFunction, timeFunction, true);
				}
			}
		}
	}
}
