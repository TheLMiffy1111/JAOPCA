package thelm.jaopca.compat.mekanism;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

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

	public static boolean registerGasTag(ResourceLocation location, Supplier<ResourceLocation> gasLocation) {
		if(GAS_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.gasRegistryName(), location, gasLocation);
	}

	public static boolean registerGasTag(ResourceLocation location, ResourceLocation gasLocation) {
		return registerGasTag(location, ()->gasLocation);
	}

	public static boolean registerInfuseTypeTag(ResourceLocation location, Supplier<ResourceLocation> infuseTypeLocation) {
		if(INFUSE_TYPE_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.infuseTypeRegistryName(), location, infuseTypeLocation);
	}

	public static boolean registerInfuseTypeTag(ResourceLocation location, ResourceLocation infuseTypeLocation) {
		return registerInfuseTypeTag(location, ()->infuseTypeLocation);
	}

	public static boolean registerPigmentTag(ResourceLocation location, Supplier<ResourceLocation> pigmentLocation) {
		if(PIGMENT_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.pigmentRegistryName(), location, pigmentLocation);
	}

	public static boolean registerPigmentTag(ResourceLocation location, ResourceLocation pigmentLocation) {
		return registerPigmentTag(location, ()->pigmentLocation);
	}

	public static boolean registerSlurryTag(ResourceLocation location, Supplier<ResourceLocation> slurryLocation) {
		if(SLURRY_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		return ApiImpl.INSTANCE.registerTag(MekanismAPI.slurryRegistryName(), location, slurryLocation);
	}

	public static boolean registerSlurryTag(ResourceLocation location, ResourceLocation slurryLocation) {
		return registerSlurryTag(location, ()->slurryLocation);
	}

	static void setupConfig(IDynamicSpecConfig config) {
		config.setComment("gasTags", "Configurations related to gas tags.");
		GAS_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("gasTags.blacklist", new ArrayList<>(),
				"List of gas tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags(MekanismAPI.gasRegistryName()).addAll(Lists.transform(config.getDefinedStringList("gasTags.customDefined", new ArrayList<>(),
				"List of gas tags that should be considered as defined."), ResourceLocation::new));

		config.setComment("infuseTypeTags", "Configurations related to infuse type tags.");
		INFUSE_TYPE_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("infuseTypeTags.blacklist", new ArrayList<>(),
				"List of infuse type tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags(MekanismAPI.infuseTypeRegistryName()).addAll(Lists.transform(config.getDefinedStringList("infuseTypeTags.customDefined", new ArrayList<>(),
				"List of infuse type tags that should be considered as defined."), ResourceLocation::new));

		config.setComment("pigmentTags", "Configurations related to pigment tags.");
		PIGMENT_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("pigmentTags.blacklist", new ArrayList<>(),
				"List of infuse type tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags(MekanismAPI.pigmentRegistryName()).addAll(Lists.transform(config.getDefinedStringList("pigmentTags.customDefined", new ArrayList<>(),
				"List of infuse type tags that should be considered as defined."), ResourceLocation::new));

		config.setComment("slurryTags", "Configurations related to slurry tags.");
		SLURRY_TAG_BLACKLIST.addAll(Lists.transform(config.getDefinedStringList("slurryTags.blacklist", new ArrayList<>(),
				"List of infuse type tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags(MekanismAPI.slurryRegistryName()).addAll(Lists.transform(config.getDefinedStringList("slurryTags.customDefined", new ArrayList<>(),
				"List of infuse type tags that should be considered as defined."), ResourceLocation::new));
	}
}
