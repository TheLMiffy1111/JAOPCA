package thelm.jaopca.client.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import thelm.jaopca.forms.FormTypeHandler;

public class ModelHandler {

	private static final FileToIdConverter BLOCK_MODEL_FORMAT = FileToIdConverter.json("blockstates");
	private static final FileToIdConverter ITEM_MODEL_FORMAT = FileToIdConverter.json("models/item");
	private static final Map<ResourceLocation, ResourceLocation> BLOCK_MODEL_REMAPS = new LinkedHashMap<>();
	private static final Map<ResourceLocation, ResourceLocation> ITEM_MODEL_REMAPS = new LinkedHashMap<>();
	private static final Multimap<ResourceLocation, ResourceLocation> BLOCK_MODEL_REMAPS_REVERSE = LinkedHashMultimap.create();
	private static final Multimap<ResourceLocation, ResourceLocation> ITEM_MODEL_REMAPS_REVERSE = LinkedHashMultimap.create();

	public static void gatherBlockStateRemaps(Set<ResourceLocation> availableLocations) {
		BLOCK_MODEL_REMAPS.clear();
		BLOCK_MODEL_REMAPS_REVERSE.clear();
		FormTypeHandler.addBlockModelRemaps(
				availableLocations.stream().map(BLOCK_MODEL_FORMAT::fileToId).collect(Collectors.toSet()),
				BLOCK_MODEL_REMAPS::putIfAbsent);
		BLOCK_MODEL_REMAPS.forEach((k, v)->BLOCK_MODEL_REMAPS_REVERSE.put(v, k));
	}

	public static void gatherItemModelRemaps(Set<ResourceLocation> availableLocations) {
		ITEM_MODEL_REMAPS.clear();
		ITEM_MODEL_REMAPS_REVERSE.clear();
		FormTypeHandler.addItemModelRemaps(
				availableLocations.stream().map(ITEM_MODEL_FORMAT::fileToId).collect(Collectors.toSet()),
				ITEM_MODEL_REMAPS::putIfAbsent);
		ITEM_MODEL_REMAPS.forEach((k, v)->ITEM_MODEL_REMAPS_REVERSE.put(v, k));
	}

	public static ResourceLocation remapBlockModel(ResourceLocation location) {
		if(BLOCK_MODEL_REMAPS.containsKey(location)) {
			System.out.println(location + "->" + BLOCK_MODEL_REMAPS.get(location));
		}
		return BLOCK_MODEL_REMAPS.getOrDefault(location, location);
	}

	public static ResourceLocation remapItemModel(ResourceLocation location) {
		if(ITEM_MODEL_REMAPS.containsKey(location)) {
			System.out.println(location + "->" + ITEM_MODEL_REMAPS.get(location));
		}
		return ITEM_MODEL_REMAPS.getOrDefault(location, location);
	}

	public static void remapModels(ModelEvent.ModifyBakingResult event) {
		Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
		Set<ModelResourceLocation> originalKeys = Set.copyOf(modelRegistry.keySet());

		BLOCK_MODEL_REMAPS_REVERSE.asMap().forEach((to, froms)->{
			originalKeys.stream().filter(k->k.id().equals(to)).forEach(k->{
				froms.forEach(from->{
					modelRegistry.put(new ModelResourceLocation(from, k.getVariant()), modelRegistry.get(k));
				});
			});
		});
		ITEM_MODEL_REMAPS_REVERSE.asMap().forEach((to, froms)->{
			ModelResourceLocation mTo = ModelResourceLocation.inventory(to);
			if(originalKeys.contains(mTo)) {
				froms.forEach(from->{
					modelRegistry.put(ModelResourceLocation.inventory(from), modelRegistry.get(mTo));
				});
			}
		});

		BLOCK_MODEL_REMAPS.clear();
		ITEM_MODEL_REMAPS.clear();
		BLOCK_MODEL_REMAPS_REVERSE.clear();
		ITEM_MODEL_REMAPS_REVERSE.clear();
	}
}
