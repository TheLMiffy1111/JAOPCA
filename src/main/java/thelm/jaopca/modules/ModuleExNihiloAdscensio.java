package thelm.jaopca.modules;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import exnihiloadscensio.ExNihiloAdscensio;
import exnihiloadscensio.blocks.BlockSieve.MeshType;
import exnihiloadscensio.items.ore.Ore;
import exnihiloadscensio.json.CustomBlockInfoJson;
import exnihiloadscensio.json.CustomItemInfoJson;
import exnihiloadscensio.json.CustomOreJson;
import exnihiloadscensio.registries.SieveRegistry;
import exnihiloadscensio.util.BlockInfo;
import exnihiloadscensio.util.ItemInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleExNihiloAdscensio extends ModuleBase {

	public static final ItemEntry PIECE_ENTRY = new ItemEntry(EnumEntryType.ITEM, "piece", new ModelResourceLocation("jaopca:ore_crushed#inventory"));
	public static final ItemEntry CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "hunk", new ModelResourceLocation("jaopca:ore_broken#inventory"));

	public static final ArrayList<String> EXISTING_ORES = Lists.<String>newArrayList();
	public static final ArrayList<String> GEM_DUST_BLACKLIST = Lists.<String>newArrayList(
			"Coal", "Lapis", "Diamond", "Emerald", "Quartz", "Redstone", "Glowstone"
			);

	public static final HashMap<IOreEntry, IBlockState> ORE_SOURCES = Maps.<IOreEntry, IBlockState>newHashMap(); 
	public static final HashMap<IOreEntry, float[]> SIEVE_CHANCES = Maps.<IOreEntry, float[]>newHashMap();

	@Override
	public String getName() {
		return "exnihiloadscensio";
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return EnumSet.<EnumOreType>of(EnumOreType.GEM, EnumOreType.DUST);
	}

	@Override
	public List<String> getOreBlacklist() {
		return GEM_DUST_BLACKLIST;
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		ArrayList<ItemEntry> ret = Lists.newArrayList(PIECE_ENTRY, CHUNK_ENTRY);
		for(ItemEntry entry : ret) {
			entry.blacklist.addAll(EXISTING_ORES);
		}

		return ret;
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("piece")) {
			IBlockState state = Utils.parseBlockState(config.get(Utils.to_under_score(entry.getOreName()), "eNCSource", "minecraft:gravel").setRequiresMcRestart(true).getString());
			float[] data = {
					(float)config.get(Utils.to_under_score(entry.getOreName()), "eNAFlintChance", 0.2D/entry.getRarity()).setRequiresMcRestart(true).getDouble(),
					(float)config.get(Utils.to_under_score(entry.getOreName()), "eNAIronChance", 0.2D/entry.getRarity()).setRequiresMcRestart(true).getDouble(),
					(float)config.get(Utils.to_under_score(entry.getOreName()), "eNADiamondChance", 0.1D/entry.getRarity()).setRequiresMcRestart(true).getDouble(),
			};
			ORE_SOURCES.put(entry, state);
			SIEVE_CHANCES.put(entry, data);
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			switch(entry.getOreType()) {
			case DUST: {
				IBlockState state = Utils.parseBlockState(config.get(Utils.to_under_score(entry.getOreName()), "eNCSource", "exnihilocreatio:block_dust").setRequiresMcRestart(true).getString());
				float[] data = {
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCStringChance", 0D).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCFlintChance", 0D).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCIronChance", 0.0625D/entry.getRarity()).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCDiamondChance", 0.125D/entry.getRarity()).setRequiresMcRestart(true).getDouble(),
				};
				ORE_SOURCES.put(entry, state);
				SIEVE_CHANCES.put(entry, data);
				break;
			}
			case GEM:
			default: {
				IBlockState state = Utils.parseBlockState(config.get(Utils.to_under_score(entry.getOreName()), "eNCSource", "minecraft:gravel").setRequiresMcRestart(true).getString());
				float[] data = {
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCStringChance", 0D).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCFlintChance", 0D).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCIronChance", 0.008D/entry.getRarity()).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCDiamondChance", 0.016D/entry.getRarity()).setRequiresMcRestart(true).getDouble(),
				};
				ORE_SOURCES.put(entry, state);
				SIEVE_CHANCES.put(entry, data);
				break;
			}
			}
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("piece")) {
			IBlockState state = ORE_SOURCES.get(entry);
			float[] data = SIEVE_CHANCES.get(entry);
			if(data[0] > 0) {
				SieveRegistry.register(state, Utils.getOreStack("piece", entry, 1), data[0], MeshType.FLINT.getID());
			}
			if(data[1] > 0) {
				SieveRegistry.register(state, Utils.getOreStack("piece", entry, 1), data[1], MeshType.IRON.getID());
			}
			if(data[2] > 0) {
				SieveRegistry.register(state, Utils.getOreStack("piece", entry, 1), data[2], MeshType.DIAMOND.getID());
			}
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			String s = "gem";
			if(entry.getOreType() == EnumOreType.DUST) {
				s = "dust";
			}
			IBlockState state = ORE_SOURCES.get(entry);
			float[] data = SIEVE_CHANCES.get(entry);
			if(data[0] > 0) {
				SieveRegistry.register(state, Utils.getOreStack(s, entry, 1), data[0], MeshType.STRING.getID());
			}
			if(data[1] > 0) {
				SieveRegistry.register(state, Utils.getOreStack(s, entry, 1), data[1], MeshType.FLINT.getID());
			}
			if(data[2] > 0) {
				SieveRegistry.register(state, Utils.getOreStack(s, entry, 1), data[2], MeshType.IRON.getID());
			}
			if(data[3] > 0) {
				SieveRegistry.register(state, Utils.getOreStack(s, entry, 1), data[3], MeshType.DIAMOND.getID());
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("hunk")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("hunk", entry, 1), new Object[] {
					"piece"+entry.getOreName(),
					"piece"+entry.getOreName(),
					"piece"+entry.getOreName(),
					"piece"+entry.getOreName(),
			}));
			Utils.addSmelting(Utils.getOreStack("hunk", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.7F);
		}
		ExNihiloAdscensio.configsLoaded = false;
	}

	@Override
	public List<Pair<String, String>> remaps() {
		return Lists.<Pair<String, String>>newArrayList(
				Pair.of("orepiece", "piece"),
				Pair.of("orechunk", "hunk")
				);
	}

	static {
		//yep, only way i could think of adding the blacklist
		//should work
		Gson gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(ItemInfo.class, new CustomItemInfoJson())
				.registerTypeAdapter(BlockInfo.class, new CustomBlockInfoJson())
				.registerTypeAdapter(Ore.class, new CustomOreJson()).create();
		ParameterizedType TYPE = new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] {Ore.class};
			}

			@Override
			public Type getRawType() {
				return List.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}
		};
		ArrayList<String> defaults = Lists.<String>newArrayList(
				"Iron", "Gold", "Copper", "Tin", "Aluminium", "Lead", "Silver", "Nickel", "Ardite", "Cobalt"
				);
		try {
			File file = new File(ExNihiloAdscensio.configDirectory, "OreRegistry.json");
			if(file.exists()) {
				FileReader e = new FileReader(file);
				for(Ore ore : gson.<List<Ore>>fromJson(e, TYPE)) {
					EXISTING_ORES.add(StringUtils.capitalize(ore.getName()));
				};
			}
			else {
				EXISTING_ORES.addAll(defaults);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			EXISTING_ORES.addAll(defaults);
		}
	}
}
