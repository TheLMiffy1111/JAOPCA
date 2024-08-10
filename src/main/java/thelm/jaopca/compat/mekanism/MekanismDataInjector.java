package thelm.jaopca.compat.mekanism;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import mekanism.api.MekanismAPI;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.utils.ApiImpl;

public class MekanismDataInjector {

	private MekanismDataInjector() {}

	public static final Set<ResourceLocation> CHEMICAL_TAG_BLACKLIST = new TreeSet<>();

	public static boolean registerChemicalTag(ResourceLocation location, ResourceLocation chemicalLocation) {
		if(CHEMICAL_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.CHEMICAL_REGISTRY_NAME, location, chemicalLocation);
	}

	static void setupConfig(IDynamicSpecConfig config) {
		config.setComment("chemicalTags", "Configurations related to chemical tags.");
		CHEMICAL_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("chemicalTags.blacklist", new ArrayList<>(),
				"List of infuse type tags that should not be added."), ResourceLocation::parse));
		DataCollector.getDefinedTags(MekanismAPI.CHEMICAL_REGISTRY_NAME).addAll(Lists.transform(config.getDefinedStringList("chemicalTags.customDefined", new ArrayList<>(),
				"List of infuse type tags that should be considered as defined."), ResourceLocation::parse));
	}
}
