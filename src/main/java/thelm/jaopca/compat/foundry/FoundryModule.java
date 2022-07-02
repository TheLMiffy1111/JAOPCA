package thelm.jaopca.compat.foundry;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import exter.foundry.api.FoundryAPI;
import exter.foundry.fluid.LiquidMetalRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "foundry@[3,)")
public class FoundryModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();

	@Override
	public String getName() {
		return "foundry";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "foundry_liquid");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		if(BLACKLIST.isEmpty()) {
			BLACKLIST.addAll(LiquidMetalRegistry.INSTANCE.getFluidNames());
			Collections.addAll(BLACKLIST, "Aluminum", "Constantan");
		}
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FoundryHelper helper = FoundryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		int oreAmount = FoundryAPI.getAmountOre();
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getTemperature(stack);
		ToIntFunction<FluidStack> speedFunction = stack->100;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String liquidName = miscHelper.getFluidName("foundry_liquid", material.getName());
			helper.registerMeltingRecipe(
					miscHelper.getRecipeKey("foundry.ore_to_liquid", material.getName()),
					oreOredict, 1, liquidName, oreAmount, tempFunction, speedFunction);
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("liquid", "foundry_liquid");
		return builder.build();
	}
}
