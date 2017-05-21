package thelm.jaopca.modules;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import exnihiloadscensio.ExNihiloAdscensio;
import exnihiloadscensio.blocks.BlockSieve.MeshType;
import exnihiloadscensio.config.Config;
import exnihiloadscensio.items.ore.Ore;
import exnihiloadscensio.json.CustomBlockInfoJson;
import exnihiloadscensio.json.CustomItemInfoJson;
import exnihiloadscensio.json.CustomOreJson;
import exnihiloadscensio.registries.SieveRegistry;
import exnihiloadscensio.util.BlockInfo;
import exnihiloadscensio.util.ItemInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleExNihiloAdscensio extends ModuleBase {

	public static final ItemEntry PIECE_ENTRY = new ItemEntry(EnumEntryType.ITEM, "orePiece", new ModelResourceLocation("jaopca:ore_crushed#inventory"));
	public static final ItemEntry CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "oreChunk", new ModelResourceLocation("jaopca:ore_broken#inventory"));

	public static final ArrayList<String> EXISTING_ORES = Lists.<String>newArrayList();

	public static final String ENDER_IO_MESSAGE = "" +
			"<recipeGroup name=\"JAOPCA_ENA\">" +
			"<recipe name=\"%s\" energyCost=\"400\">" +
			"<input>" +
			"<itemStack oreDictionary=\"%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"%s\" number=\"2\" />" +
			"</output>" +
			"</recipe>" + 
			"</recipeGroup>";

	@Override
	public String getName() {
		return "exnihiloadscensio";
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
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("orePiece")) {
			SieveRegistry.register(Blocks.GRAVEL.getDefaultState(), Utils.getOreStack("orePiece", entry, 1), 0.2F, MeshType.FLINT.getID());
			SieveRegistry.register(Blocks.GRAVEL.getDefaultState(), Utils.getOreStack("orePiece", entry, 1), 0.2F, MeshType.IRON.getID());
			SieveRegistry.register(Blocks.GRAVEL.getDefaultState(), Utils.getOreStack("orePiece", entry, 1), 0.1F, MeshType.DIAMOND.getID());
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreChunk")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("oreChunk", entry, 1), new Object[] {
					"orePiece"+entry.getOreName(),
					"orePiece"+entry.getOreName(),
					"orePiece"+entry.getOreName(),
					"orePiece"+entry.getOreName(),
			}));
			Utils.addSmelting(Utils.getOreStack("oreChunk", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.7F);

			if(Config.doTICCompat && Loader.isModLoaded("tconstruct")) {
				ModuleTinkersConstruct.addMeltingRecipe("oreChunk"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 144);
			}

			if(Config.doEnderIOCompat && Loader.isModLoaded("EnderIO")) {
				addOreSAGMillRecipe("oreChunk"+entry.getOreName(), "dust"+entry.getOreName());
			}
		}
	}

	public static void addOreSAGMillRecipe(String input, String output) {
		FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(ENDER_IO_MESSAGE, input, input, output));
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
				"Iron", "Gold", "Copper", "Tin", "Aluminum", "Lead", "Silver", "Nickel", "Ardite", "Cobalt"
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
