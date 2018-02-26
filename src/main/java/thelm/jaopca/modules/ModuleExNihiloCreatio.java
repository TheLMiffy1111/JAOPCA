package thelm.jaopca.modules;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
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

import exnihilocreatio.ExNihiloCreatio;
import exnihilocreatio.blocks.BlockSieve.MeshType;
import exnihilocreatio.config.ModConfig;
import exnihilocreatio.items.ore.Ore;
import exnihilocreatio.json.CustomBlockInfoJson;
import exnihilocreatio.json.CustomItemInfoJson;
import exnihilocreatio.json.CustomOreJson;
import exnihilocreatio.recipes.defaults.IRecipeDefaults;
import exnihilocreatio.registries.manager.CompatDefaultRecipes;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.registries.manager.ISieveDefaultRegistryProvider;
import exnihilocreatio.registries.registries.SieveRegistry;
import exnihilocreatio.util.BlockInfo;
import exnihilocreatio.util.ItemInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleExNihiloCreatio extends ModuleBase {

	public static final ItemEntry PIECE_ENTRY = new ItemEntry(EnumEntryType.ITEM, "piece", new ModelResourceLocation("jaopca:ore_crushed#inventory"));
	public static final ItemEntry CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "hunk", new ModelResourceLocation("jaopca:ore_broken#inventory"));

	public static final ArrayList<String> GEM_DUST_BLACKLIST = Lists.<String>newArrayList(
			"Coal", "Lapis", "Diamond", "Emerald", "Quartz", "QuartzBlack", "CertusQuartz", "Redstone", "Glowstone"
			);

	public static final HashMap<IOreEntry, IBlockState> ORE_SOURCES = Maps.<IOreEntry, IBlockState>newHashMap(); 
	public static final HashMap<IOreEntry, float[]> SIEVE_CHANCES = Maps.<IOreEntry, float[]>newHashMap();

	@Override
	public String getName() {
		return "exnihilocreatio";
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
		ArrayList<String> existingOres = Lists.<String>newArrayList();
		ArrayList<String> defaults = Lists.<String>newArrayList(
				"Iron", "Gold", "Copper", "Tin", "Aluminium", "Lead", "Silver", "Nickel", "Ardite", "Cobalt", "Yellorium", "Osmium"
				);
		if(ModConfig.misc.enableJSONLoading) {
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
			try {
				File file = new File(ExNihiloCreatio.configDirectory, "OreRegistry.json");
				if(file.exists()) {
					FileReader e = new FileReader(file);
					for(Ore ore : gson.<List<Ore>>fromJson(e, TYPE)) {
						existingOres.add(StringUtils.capitalize(ore.getName()));
					};
				}
				else {
					existingOres.addAll(defaults);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				existingOres.addAll(defaults);
			}
		}
		else {
			existingOres.addAll(defaults);
		}

		ArrayList<ItemEntry> ret = Lists.newArrayList(PIECE_ENTRY, CHUNK_ENTRY);
		for(ItemEntry entry : ret) {
			entry.blacklist.addAll(existingOres);
		}

		return ret;
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("piece")) {
			IBlockState state = Utils.parseBlockState(config.get(Utils.to_under_score(entry.getOreName()), "eNCSource", "minecraft:gravel").setRequiresMcRestart(true).getString());
			float[] data = {
					(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCFlintChance", state.getBlock() == Blocks.GRAVEL ? getDropChance(entry, 0.2D) : getDropChance(entry, 0.1D)).setRequiresMcRestart(true).getDouble(),
					(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCIronChance", getDropChance(entry, 0.2D)).setRequiresMcRestart(true).getDouble(),
					(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCDiamondChance", state.getBlock() == Blocks.GRAVEL ? getDropChance(entry, 0.1D) : getDropChance(entry, 0.3D)).setRequiresMcRestart(true).getDouble(),
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
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCIronChance", getDropChance(entry, 0.0625D)).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCDiamondChance", getDropChance(entry, 0.125D)).setRequiresMcRestart(true).getDouble(),
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
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCIronChance", getDropChance(entry, 0.008D)).setRequiresMcRestart(true).getDouble(),
						(float)config.get(Utils.to_under_score(entry.getOreName()), "eNCDiamondChance", getDropChance(entry, 0.016D)).setRequiresMcRestart(true).getDouble(),
				};
				ORE_SOURCES.put(entry, state);
				SIEVE_CHANCES.put(entry, data);
				break;
			}
			}
		}
	}

	@Override
	public void preInit() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("hunk")) {
			OreDictionary.registerOre("ore"+entry.getOreName(), Utils.getOreStack("hunk", entry, 1));
		}
		JAOPCADefaultRegistryProvider provider = new JAOPCADefaultRegistryProvider();
		ExNihiloRegistryManager.registerSieveDefaultRecipeHandler(provider);
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

	@Override
	public List<Pair<String, String>> remaps() {
		return Lists.<Pair<String, String>>newArrayList(
				Pair.of("orepiece", "piece"),
				Pair.of("orechunk", "hunk")
				);
	}

	public static double getDropChance(IOreEntry entry, double chance) {
		if(ModConfig.world.isSkyWorld) {
			return chance / entry.getRarity();
		}
		else {
			return chance / entry.getRarity() / 100D * (double)ModConfig.world.normalDropPercent;
		}
	}

	public class JAOPCADefaultRegistryProvider implements ISieveDefaultRegistryProvider {
		@Override
		public void registerRecipeDefaults(SieveRegistry registry) {
			for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("piece")) {
				IBlockState state = ORE_SOURCES.get(entry);
				float[] data = SIEVE_CHANCES.get(entry);
				if(data[0] > 0) {
					registry.register(state, Utils.getOreStack("piece", entry, 1), data[0], MeshType.FLINT.getID());
				}
				if(data[1] > 0) {
					registry.register(state, Utils.getOreStack("piece", entry, 1), data[1], MeshType.IRON.getID());
				}
				if(data[2] > 0) {
					registry.register(state, Utils.getOreStack("piece", entry, 1), data[2], MeshType.DIAMOND.getID());
				}
			}

			for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(ModuleExNihiloCreatio.this)) {
				String s = "gem";
				if(entry.getOreType() == EnumOreType.DUST) {
					s = "dust";
				}
				IBlockState state = ORE_SOURCES.get(entry);
				float[] data = SIEVE_CHANCES.get(entry);
				if(data[0] > 0) {
					registry.register(state, Utils.getOreStack(s, entry, 1), data[0], MeshType.STRING.getID());
				}
				if(data[1] > 0) {
					registry.register(state, Utils.getOreStack(s, entry, 1), data[1], MeshType.FLINT.getID());
				}
				if(data[2] > 0) {
					registry.register(state, Utils.getOreStack(s, entry, 1), data[2], MeshType.IRON.getID());
				}
				if(data[3] > 0) {
					registry.register(state, Utils.getOreStack(s, entry, 1), data[3], MeshType.DIAMOND.getID());
				}
			}
		}
	}
}
