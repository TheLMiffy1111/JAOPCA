package thelm.jaopca.compat.foundry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import exter.foundry.fluid.LiquidMetalRegistry;
import net.minecraft.block.material.Material;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.foundry.fluids.JAOPCALiquidMetalFluidBlock;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAModule(modDependencies = "foundry@[3,)")
public class FoundryLiquidModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();

	private final IForm liquidForm = ApiImpl.INSTANCE.newForm(this, "foundry_liquid", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOTS).setSettings(FluidFormType.INSTANCE.getNewSettings().
					setFluidBlockCreator(JAOPCALiquidMetalFluidBlock::new).
					setLuminosityFunction(material->15).setDensityFunction(material->2000).
					setTemperatureFunction(this::getTemperature).setMaterialFunction(material->Material.LAVA));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "foundry_liquid";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "block");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		if(BLACKLIST.isEmpty()) {
			BLACKLIST.addAll(LiquidMetalRegistry.INSTANCE.getFluidNames());
			Collections.addAll(BLACKLIST, "Aluminum", "Constantan");
			liquidForm.setDefaultMaterialBlacklist(BLACKLIST);
		}
		return Collections.singletonList(liquidForm.toRequest());
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	public int getTemperature(IMaterial material) {
		return configs.get(material).getDefinedInt("foundry.temperature", 1000, "The temperature of this Foundry liquid metal.");
	}
}
