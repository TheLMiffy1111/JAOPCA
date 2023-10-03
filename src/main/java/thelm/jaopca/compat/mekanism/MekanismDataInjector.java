package thelm.jaopca.compat.mekanism;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.resources.IInMemoryResourcePack;
import thelm.jaopca.data.DataCollector;

public class MekanismDataInjector {

	private MekanismDataInjector() {}

	private static final Logger LOGGER = LogManager.getLogger();
	public static final Set<ResourceLocation> GAS_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> INFUSE_TYPE_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> PIGMENT_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> SLURRY_TAG_BLACKLIST = new TreeSet<>();
	private static final ListMultimap<ResourceLocation, ResourceLocation> GAS_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> INFUSE_TYPE_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> PIGMENT_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();
	private static final ListMultimap<ResourceLocation, ResourceLocation> SLURRY_TAGS_INJECT = MultimapBuilder.treeKeys().arrayListValues().build();

	public static boolean registerGasTag(ResourceLocation location, ResourceLocation gasLocation) {
		if(GAS_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		Objects.requireNonNull(location);
		Objects.requireNonNull(gasLocation);
		return GAS_TAGS_INJECT.put(location, gasLocation);
	}

	public static boolean registerInfuseTypeTag(ResourceLocation location, ResourceLocation infuseTypeLocation) {
		if(INFUSE_TYPE_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		Objects.requireNonNull(location);
		Objects.requireNonNull(infuseTypeLocation);
		return INFUSE_TYPE_TAGS_INJECT.put(location, infuseTypeLocation);
	}

	public static boolean registerPigmentTag(ResourceLocation location, ResourceLocation pigmentLocation) {
		if(PIGMENT_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		Objects.requireNonNull(location);
		Objects.requireNonNull(pigmentLocation);
		return PIGMENT_TAGS_INJECT.put(location, pigmentLocation);
	}

	public static boolean registerSlurryTag(ResourceLocation location, ResourceLocation slurryLocation) {
		if(SLURRY_TAG_BLACKLIST.contains(location)) {
			return false;
		}
		Objects.requireNonNull(location);
		Objects.requireNonNull(slurryLocation);
		return SLURRY_TAGS_INJECT.put(location, slurryLocation);
	}

	public static Set<ResourceLocation> getInjectGasTags() {
		return GAS_TAGS_INJECT.keySet();
	}

	public static Set<ResourceLocation> getInjectInfuseTypeTags() {
		return INFUSE_TYPE_TAGS_INJECT.keySet();
	}

	public static Set<ResourceLocation> getInjectPigmentTags() {
		return PIGMENT_TAGS_INJECT.keySet();
	}

	public static Set<ResourceLocation> getInjectSlurryTags() {
		return SLURRY_TAGS_INJECT.keySet();
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

	static void putJsons(IInMemoryResourcePack pack) {
		GAS_TAGS_INJECT.asMap().forEach((location, locations)->{
			TagBuilder builder = TagBuilder.create();
			locations.forEach(l->builder.addOptionalElement(l));
			pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/mekanism/gas/"+location.getPath()+".json"), serializeTag(builder));
		});
		INFUSE_TYPE_TAGS_INJECT.asMap().forEach((location, locations)->{
			TagBuilder builder = TagBuilder.create();
			locations.forEach(l->builder.addOptionalElement(l));
			pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/mekanism/infuse_type/"+location.getPath()+".json"), serializeTag(builder));
		});
		PIGMENT_TAGS_INJECT.asMap().forEach((location, locations)->{
			TagBuilder builder = TagBuilder.create();
			locations.forEach(l->builder.addOptionalElement(l));
			pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/mekanism/pigment/"+location.getPath()+".json"), serializeTag(builder));
		});
		SLURRY_TAGS_INJECT.asMap().forEach((location, locations)->{
			TagBuilder builder = TagBuilder.create();
			locations.forEach(l->builder.addOptionalElement(l));
			pack.putJson(PackType.SERVER_DATA, new ResourceLocation(location.getNamespace(), "tags/mekanism/slurry/"+location.getPath()+".json"), serializeTag(builder));
		});
	}

	public static JsonElement serializeTag(TagBuilder tagBuilder) {
		return TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(tagBuilder.build(), false)).getOrThrow(false, LOGGER::error);
	}
}
