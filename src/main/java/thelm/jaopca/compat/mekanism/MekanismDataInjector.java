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

	public static final Set<ResourceLocation> GAS_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> INFUSE_TYPE_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> PIGMENT_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> SLURRY_TAG_BLACKLIST = new TreeSet<>();

	public static boolean registerGasTag(ResourceLocation location, ResourceLocation gasLocation) {
		if(GAS_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.GAS_REGISTRY_NAME, location, gasLocation);
	}

	public static boolean registerInfuseTypeTag(ResourceLocation location, ResourceLocation infuseTypeLocation) {
		if(INFUSE_TYPE_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME, location, infuseTypeLocation);
	}

	public static boolean registerPigmentTag(ResourceLocation location, ResourceLocation pigmentLocation) {
		if(PIGMENT_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.PIGMENT_REGISTRY_NAME, location, pigmentLocation);
	}

	public static boolean registerSlurryTag(ResourceLocation location, ResourceLocation slurryLocation) {
		if(SLURRY_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.SLURRY_REGISTRY_NAME, location, slurryLocation);
	}

	static void setupConfig(IDynamicSpecConfig config) {
		config.setComment("gasTags", "Configurations related to gas tags.");
		GAS_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("gasTags.blacklist", new ArrayList<>(),
				"List of gas tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("gases").addAll(Lists.transform(config.getDefinedStringList("gasTags.customDefined", new ArrayList<>(),
				"List of gas tags that should be considered as defined."), ResourceLocation::new));

		config.setComment("infuseTypeTags", "Configurations related to infuse type tags.");
		INFUSE_TYPE_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("infuseTypeTags.blacklist", new ArrayList<>(),
				"List of infuse type tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("infuseTypes").addAll(Lists.transform(config.getDefinedStringList("infuseTypeTags.customDefined", new ArrayList<>(),
				"List of infuse type tags that should be considered as defined."), ResourceLocation::new));

		config.setComment("pigmentTags", "Configurations related to pigment tags.");
		PIGMENT_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("pigmentTags.blacklist", new ArrayList<>(),
				"List of infuse type tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("pigments").addAll(Lists.transform(config.getDefinedStringList("pigmentTags.customDefined", new ArrayList<>(),
				"List of infuse type tags that should be considered as defined."), ResourceLocation::new));

		config.setComment("slurryTags", "Configurations related to slurry tags.");
		SLURRY_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("slurryTags.blacklist", new ArrayList<>(),
				"List of infuse type tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("slurries").addAll(Lists.transform(config.getDefinedStringList("slurryTags.customDefined", new ArrayList<>(),
				"List of infuse type tags that should be considered as defined."), ResourceLocation::new));
	}
}
