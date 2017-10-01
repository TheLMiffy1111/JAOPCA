package thelm.jaopca.modules;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import exnihilocreatio.ExNihiloCreatio;
import exnihilocreatio.ModBlocks;
import exnihilocreatio.blocks.BlockSieve.MeshType;
import exnihilocreatio.config.ModConfig;
import exnihilocreatio.items.ore.Ore;
import exnihilocreatio.json.CustomBlockInfoJson;
import exnihilocreatio.json.CustomItemInfoJson;
import exnihilocreatio.json.CustomOreJson;
import exnihilocreatio.registries.RegistryReloadedEvent;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.util.BlockInfo;
import exnihilocreatio.util.ItemInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleExNihiloCreatio extends ModuleBase {

	public static final ItemEntry PIECE_ENTRY = new ItemEntry(EnumEntryType.ITEM, "piece", new ModelResourceLocation("jaopca:ore_crushed#inventory"));
	public static final ItemEntry CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "hunk", new ModelResourceLocation("jaopca:ore_broken#inventory"));

	public static final ArrayList<String> EXISTING_ORES = Lists.<String>newArrayList();

	public static final HashSet<IOreEntry> NETHER_ORES = Sets.<IOreEntry>newHashSet(); 
	public static final HashMap<IOreEntry, double[]> RARITY_MUTIPLIERS = Maps.<IOreEntry, double[]>newHashMap();

	@Override
	public String getName() {
		return "exnihilocreatio";
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
			boolean isNether = config.get(Utils.to_under_score(entry.getOreName()), "eNCIsNether", false).setRequiresMcRestart(true).getBoolean();
			double[] data = {
					config.get(Utils.to_under_score(entry.getOreName()), "eNCFlintMultiplier", isNether ? 0.1D : 0.2D).setRequiresMcRestart(true).getDouble(),
					config.get(Utils.to_under_score(entry.getOreName()), "eNCIronMultiplier", 0.2D).setRequiresMcRestart(true).getDouble(),
					config.get(Utils.to_under_score(entry.getOreName()), "eNCDiamondMultiplier", isNether ? 0.3D : 0.1D).setRequiresMcRestart(true).getDouble(),
			};
			if(isNether) {
				NETHER_ORES.add(entry);
			}
			RARITY_MUTIPLIERS.put(entry, data);
		}
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("hunk")) {
			OreDictionary.registerOre("ore"+entry.getOreName(), Utils.getOreStack("hunk", entry, 1));
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("hunk")) {
			Utils.addShapelessOreRecipe(Utils.getOreStack("hunk", entry, 1), new Object[] {
					"piece"+entry.getOreName(),
					"piece"+entry.getOreName(),
					"piece"+entry.getOreName(),
					"piece"+entry.getOreName(),
			});
			Utils.addSmelting(Utils.getOreStack("hunk", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.7F);
		}
	}

	@SubscribeEvent
	public void onRegistryReload(RegistryReloadedEvent event) {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("piece")) {
			boolean isNether = NETHER_ORES.contains(entry);
			double[] data = RARITY_MUTIPLIERS.get(entry);
			ExNihiloRegistryManager.SIEVE_REGISTRY.register(isNether ? ModBlocks.netherrackCrushed.getDefaultState() : Blocks.GRAVEL.getDefaultState(),
					Utils.getOreStack("piece", entry, 1), Utils.rarityReciprocalF(entry, data[0]), MeshType.FLINT.getID());
			ExNihiloRegistryManager.SIEVE_REGISTRY.register(isNether ? ModBlocks.netherrackCrushed.getDefaultState() : Blocks.GRAVEL.getDefaultState(),
					Utils.getOreStack("piece", entry, 1), Utils.rarityReciprocalF(entry, data[1]), MeshType.IRON.getID());
			ExNihiloRegistryManager.SIEVE_REGISTRY.register(isNether ? ModBlocks.netherrackCrushed.getDefaultState() : Blocks.GRAVEL.getDefaultState(),
					Utils.getOreStack("piece", entry, 1), Utils.rarityReciprocalF(entry, data[2]), MeshType.DIAMOND.getID());
		}
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
			File file = new File(ExNihiloCreatio.configDirectory, "OreRegistry.json");
			boolean doRead = ModConfig.misc.enableJSONLoading;
			if(file.exists() && doRead) {
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
