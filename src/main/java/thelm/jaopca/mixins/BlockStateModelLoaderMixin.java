package thelm.jaopca.mixins;

import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import thelm.jaopca.forms.FormTypeHandler;

@Mixin(BlockStateModelLoader.class)
public class BlockStateModelLoaderMixin {

	@Shadow
	private static Logger LOGGER;
	@Shadow
	private static FileToIdConverter BLOCKSTATE_LISTER;

	@WrapOperation(method = "lambda$loadBlockStates$1", at = @At(value = "NEW", target = "Ljava/util/ArrayList;"))
	private static ArrayList<CompletableFuture<BlockStateModelLoader.LoadedModels>> injectBlockModelReplacements(
			int size,
			Operation<ArrayList<CompletableFuture<BlockStateModelLoader.LoadedModels>>> original,
			@Local(argsOnly = true) Function<Identifier, StateDefinition<Block, BlockState>> definitionToBlockState,
			@Local(argsOnly = true) Executor executor,
			@Local(argsOnly = true) Map<Identifier, List<Resource>> resources,
			@Share("remapTargets") LocalRef<Set<Identifier>> remapTargetsRef) {
		Set<Identifier> availableLocations = resources.keySet().stream().map(BLOCKSTATE_LISTER::fileToId).collect(Collectors.toUnmodifiableSet());
		Map<Identifier, Identifier> blockModelRemaps = new LinkedHashMap<>();
		FormTypeHandler.addBlockModelRemaps(availableLocations, blockModelRemaps::putIfAbsent);
		remapTargetsRef.set(Set.copyOf(blockModelRemaps.values()));

		ArrayList<CompletableFuture<BlockStateModelLoader.LoadedModels>> result = original.call(size + blockModelRemaps.size());

		blockModelRemaps.forEach((from, to) -> {
			if(availableLocations.contains(to)) {
				result.add(CompletableFuture.supplyAsync(() -> {
					StateDefinition<Block, BlockState> stateDefinition = definitionToBlockState.apply(from);
					if(stateDefinition == null) {
						LOGGER.debug("Discovered unknown block state definition {}, ignoring", from);
						return null;
					}
					List<Resource> stack = resources.get(BLOCKSTATE_LISTER.idToFile(to));
					List<Pair<String, BlockStateModelDispatcher>> loadedStack = new ArrayList<>(stack.size());
					for(Resource resource : stack) {
						try(Reader reader = resource.openAsReader()) {
							JsonElement element = StrictJsonParser.parse(reader);
							BlockStateModelDispatcher definition = BlockStateModelDispatcher.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(JsonParseException::new);
							loadedStack.add(Pair.of(resource.sourcePackId(), definition));
						}
						catch(Exception e) {
							LOGGER.error("Failed to load blockstate definition {} -> {} from pack {}", from, to, resource.sourcePackId(), e);
						}
					}
					try {
						return loadBlockStateDefinitionStack(to, stateDefinition, loadedStack);
					}
					catch(Exception e) {
						LOGGER.error("Failed to load blockstate definition {} -> {}", from, to, e);
						return null;
					}
				}, executor));
			}
		});

		return result;
	}

	@WrapOperation(method = "lambda$loadBlockStates$2", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Object;)V"))
	private static void suppressReplacedLogs(
			Logger logger, String format, Object arg,
			Operation<Void> original,
			@Local Identifier stateDefinitionId,
			@Share("remapTargets") LocalRef<Set<Identifier>> remapTargetsRef) {
		if(remapTargetsRef.get() == null || !remapTargetsRef.get().contains(stateDefinitionId)) {
			original.call(logger, format, arg);
		}
	}

	@Unique
	private static BlockStateModelLoader.LoadedModels loadBlockStateDefinitionStack(Identifier stateDefinitionId, StateDefinition<Block, BlockState> stateDefinition, List<Pair<String, BlockStateModelDispatcher>> definitionStack) {
		Map<BlockState, BlockStateModel.UnbakedRoot> result = new IdentityHashMap<>();
		for(Pair<String, BlockStateModelDispatcher> definition : definitionStack) {
			result.putAll(definition.getRight().instantiate(stateDefinition, () -> stateDefinitionId + "/" + definition.getLeft()));
		}
		return new BlockStateModelLoader.LoadedModels(result);
	}
}
