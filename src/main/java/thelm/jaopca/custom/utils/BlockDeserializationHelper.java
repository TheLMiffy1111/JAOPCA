package thelm.jaopca.custom.utils;

import java.util.Locale;

import com.google.common.collect.HashBiMap;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class BlockDeserializationHelper {

	public static final BlockDeserializationHelper INSTANCE = new BlockDeserializationHelper();

	private BlockDeserializationHelper() {}

	private static final HashBiMap<String, MapColor> MAP_COLORS = HashBiMap.create();
	private static final HashBiMap<String, SoundType> SOUND_TYPES = HashBiMap.create();

	public MapColor getMapColor(String name) {
		return MAP_COLORS.get(name.toLowerCase(Locale.US));
	}

	public String getMapColorName(MapColor color) {
		return MAP_COLORS.inverse().get(color);
	}

	public void putMapColor(String name, MapColor color) {
		MAP_COLORS.put(name.toLowerCase(Locale.US), color);
	}

	public SoundType getSoundType(String name) {
		return SOUND_TYPES.get(name.toLowerCase(Locale.US));
	}

	public String getSoundTypeName(SoundType sound) {
		return SOUND_TYPES.inverse().get(sound);
	}

	public void putSoundType(String name, SoundType sound) {
		SOUND_TYPES.put(name.toLowerCase(Locale.US), sound);
	}

	static {
		INSTANCE.putMapColor("none", MapColor.NONE);
		INSTANCE.putMapColor("grass", MapColor.GRASS);
		INSTANCE.putMapColor("sand", MapColor.SAND);
		INSTANCE.putMapColor("wool", MapColor.WOOL);
		INSTANCE.putMapColor("fire", MapColor.FIRE);
		INSTANCE.putMapColor("ice", MapColor.ICE);
		INSTANCE.putMapColor("metal", MapColor.METAL);
		INSTANCE.putMapColor("plant", MapColor.PLANT);
		INSTANCE.putMapColor("snow", MapColor.SNOW);
		INSTANCE.putMapColor("clay", MapColor.CLAY);
		INSTANCE.putMapColor("dirt", MapColor.DIRT);
		INSTANCE.putMapColor("stone", MapColor.STONE);
		INSTANCE.putMapColor("water", MapColor.WATER);
		INSTANCE.putMapColor("wood", MapColor.WOOD);
		INSTANCE.putMapColor("quartz", MapColor.QUARTZ);
		INSTANCE.putMapColor("color_orange", MapColor.COLOR_ORANGE);
		INSTANCE.putMapColor("color_magenta", MapColor.COLOR_MAGENTA);
		INSTANCE.putMapColor("color_light_blue", MapColor.COLOR_LIGHT_BLUE);
		INSTANCE.putMapColor("color_yellow", MapColor.COLOR_YELLOW);
		INSTANCE.putMapColor("color_light_green", MapColor.COLOR_LIGHT_GREEN);
		INSTANCE.putMapColor("color_pink", MapColor.COLOR_PINK);
		INSTANCE.putMapColor("color_gray", MapColor.COLOR_GRAY);
		INSTANCE.putMapColor("color_light_gray", MapColor.COLOR_LIGHT_GRAY);
		INSTANCE.putMapColor("color_cyan", MapColor.COLOR_CYAN);
		INSTANCE.putMapColor("color_purple", MapColor.COLOR_PURPLE);
		INSTANCE.putMapColor("color_blue", MapColor.COLOR_BLUE);
		INSTANCE.putMapColor("color_brown", MapColor.COLOR_BROWN);
		INSTANCE.putMapColor("color_green", MapColor.COLOR_GREEN);
		INSTANCE.putMapColor("color_red", MapColor.COLOR_RED);
		INSTANCE.putMapColor("color_black", MapColor.COLOR_BLACK);
		INSTANCE.putMapColor("gold", MapColor.GOLD);
		INSTANCE.putMapColor("diamond", MapColor.DIAMOND);
		INSTANCE.putMapColor("lapis", MapColor.LAPIS);
		INSTANCE.putMapColor("emerald", MapColor.EMERALD);
		INSTANCE.putMapColor("podzol", MapColor.PODZOL);
		INSTANCE.putMapColor("nether", MapColor.NETHER);
		INSTANCE.putMapColor("terracotta_white", MapColor.TERRACOTTA_WHITE);
		INSTANCE.putMapColor("terracotta_orange", MapColor.TERRACOTTA_ORANGE);
		INSTANCE.putMapColor("terracotta_magenta", MapColor.TERRACOTTA_MAGENTA);
		INSTANCE.putMapColor("terracotta_light_blue", MapColor.TERRACOTTA_LIGHT_BLUE);
		INSTANCE.putMapColor("terracotta_yellow", MapColor.TERRACOTTA_YELLOW);
		INSTANCE.putMapColor("terracotta_light_green", MapColor.TERRACOTTA_LIGHT_GREEN);
		INSTANCE.putMapColor("terracotta_pink", MapColor.TERRACOTTA_PINK);
		INSTANCE.putMapColor("terracotta_gray", MapColor.TERRACOTTA_GRAY);
		INSTANCE.putMapColor("terracotta_light_gray", MapColor.TERRACOTTA_LIGHT_GRAY);
		INSTANCE.putMapColor("terracotta_cyan", MapColor.TERRACOTTA_CYAN);
		INSTANCE.putMapColor("terracotta_purple", MapColor.TERRACOTTA_PURPLE);
		INSTANCE.putMapColor("terracotta_blue", MapColor.TERRACOTTA_BLUE);
		INSTANCE.putMapColor("terracotta_brown", MapColor.TERRACOTTA_BROWN);
		INSTANCE.putMapColor("terracotta_green", MapColor.TERRACOTTA_GREEN);
		INSTANCE.putMapColor("terracotta_red", MapColor.TERRACOTTA_RED);
		INSTANCE.putMapColor("terracotta_black", MapColor.TERRACOTTA_BLACK);
		INSTANCE.putMapColor("crimson_nylium", MapColor.CRIMSON_NYLIUM);
		INSTANCE.putMapColor("crimson_stem", MapColor.CRIMSON_STEM);
		INSTANCE.putMapColor("crimson_hyphae", MapColor.CRIMSON_HYPHAE);
		INSTANCE.putMapColor("warped_nylium", MapColor.WARPED_NYLIUM);
		INSTANCE.putMapColor("warped_stem", MapColor.WARPED_STEM);
		INSTANCE.putMapColor("warped_hyphae", MapColor.WARPED_HYPHAE);
		INSTANCE.putMapColor("warped_wart_block", MapColor.WARPED_WART_BLOCK);
		INSTANCE.putMapColor("deepslate", MapColor.DEEPSLATE);
		INSTANCE.putMapColor("raw_iron", MapColor.RAW_IRON);
		INSTANCE.putMapColor("glow_lichen", MapColor.GLOW_LICHEN);

		INSTANCE.putSoundType("empty", SoundType.EMPTY);
		INSTANCE.putSoundType("wood", SoundType.WOOD);
		INSTANCE.putSoundType("gravel", SoundType.GRAVEL);
		INSTANCE.putSoundType("grass", SoundType.GRASS);
		INSTANCE.putSoundType("lily_pad", SoundType.LILY_PAD);
		INSTANCE.putSoundType("stone", SoundType.STONE);
		INSTANCE.putSoundType("metal", SoundType.METAL);
		INSTANCE.putSoundType("glass", SoundType.GLASS);
		INSTANCE.putSoundType("wool", SoundType.WOOL);
		INSTANCE.putSoundType("sand", SoundType.SAND);
		INSTANCE.putSoundType("snow", SoundType.SNOW);
		INSTANCE.putSoundType("powder_snow", SoundType.POWDER_SNOW);
		INSTANCE.putSoundType("ladder", SoundType.LADDER);
		INSTANCE.putSoundType("anvil", SoundType.ANVIL);
		INSTANCE.putSoundType("slime_block", SoundType.SLIME_BLOCK);
		INSTANCE.putSoundType("honey_block", SoundType.HONEY_BLOCK);
		INSTANCE.putSoundType("wet_grass", SoundType.WET_GRASS);
		INSTANCE.putSoundType("coral_block", SoundType.CORAL_BLOCK);
		INSTANCE.putSoundType("bamboo", SoundType.BAMBOO);
		INSTANCE.putSoundType("bamboo_sapling", SoundType.BAMBOO_SAPLING);
		INSTANCE.putSoundType("scaffolding", SoundType.SCAFFOLDING);
		INSTANCE.putSoundType("sweet_berry_bush", SoundType.SWEET_BERRY_BUSH);
		INSTANCE.putSoundType("crop", SoundType.CROP);
		INSTANCE.putSoundType("hard_crop", SoundType.HARD_CROP);
		INSTANCE.putSoundType("vine", SoundType.VINE);
		INSTANCE.putSoundType("nether_wart", SoundType.NETHER_WART);
		INSTANCE.putSoundType("lantern", SoundType.LANTERN);
		INSTANCE.putSoundType("stem", SoundType.STEM);
		INSTANCE.putSoundType("nylium", SoundType.NYLIUM);
		INSTANCE.putSoundType("fungus", SoundType.FUNGUS);
		INSTANCE.putSoundType("roots", SoundType.ROOTS);
		INSTANCE.putSoundType("shroomlight", SoundType.SHROOMLIGHT);
		INSTANCE.putSoundType("weeping_vines", SoundType.WEEPING_VINES);
		INSTANCE.putSoundType("twisting_vines", SoundType.TWISTING_VINES);
		INSTANCE.putSoundType("soul_sand", SoundType.SOUL_SAND);
		INSTANCE.putSoundType("soul_soil", SoundType.SOUL_SOIL);
		INSTANCE.putSoundType("basalt", SoundType.BASALT);
		INSTANCE.putSoundType("wart_block", SoundType.WART_BLOCK);
		INSTANCE.putSoundType("netherrack", SoundType.NETHERRACK);
		INSTANCE.putSoundType("nether_bricks", SoundType.NETHER_BRICKS);
		INSTANCE.putSoundType("nether_sprouts", SoundType.NETHER_SPROUTS);
		INSTANCE.putSoundType("nether_ore", SoundType.NETHER_ORE);
		INSTANCE.putSoundType("bone_block", SoundType.BONE_BLOCK);
		INSTANCE.putSoundType("netherite_block", SoundType.NETHERITE_BLOCK);
		INSTANCE.putSoundType("ancient_debris", SoundType.ANCIENT_DEBRIS);
		INSTANCE.putSoundType("lodestone", SoundType.LODESTONE);
		INSTANCE.putSoundType("chain", SoundType.CHAIN);
		INSTANCE.putSoundType("nether_gold_ore", SoundType.NETHER_GOLD_ORE);
		INSTANCE.putSoundType("gilded_blackstone", SoundType.GILDED_BLACKSTONE);
		INSTANCE.putSoundType("candle", SoundType.CANDLE);
		INSTANCE.putSoundType("amethyst", SoundType.AMETHYST);
		INSTANCE.putSoundType("amethyst_cluster", SoundType.AMETHYST_CLUSTER);
		INSTANCE.putSoundType("small_amethyst_bud", SoundType.SMALL_AMETHYST_BUD);
		INSTANCE.putSoundType("medium_amethyst_bud", SoundType.MEDIUM_AMETHYST_BUD);
		INSTANCE.putSoundType("large_amethyst_bud", SoundType.LARGE_AMETHYST_BUD);
		INSTANCE.putSoundType("tuff", SoundType.TUFF);
		INSTANCE.putSoundType("calcite", SoundType.CALCITE);
		INSTANCE.putSoundType("dripstone_block", SoundType.DRIPSTONE_BLOCK);
		INSTANCE.putSoundType("pointed_dripstone", SoundType.POINTED_DRIPSTONE);
		INSTANCE.putSoundType("copper", SoundType.COPPER);
		INSTANCE.putSoundType("cave_vines", SoundType.CAVE_VINES);
		INSTANCE.putSoundType("spore_blossom", SoundType.SPORE_BLOSSOM);
		INSTANCE.putSoundType("azalea", SoundType.AZALEA);
		INSTANCE.putSoundType("flowering_azalea", SoundType.FLOWERING_AZALEA);
		INSTANCE.putSoundType("moss_carpet", SoundType.MOSS_CARPET);
		INSTANCE.putSoundType("pink_petals", SoundType.PINK_PETALS);
		INSTANCE.putSoundType("moss", SoundType.MOSS);
		INSTANCE.putSoundType("big_dripleaf", SoundType.BIG_DRIPLEAF);
		INSTANCE.putSoundType("small_dripleaf", SoundType.SMALL_DRIPLEAF);
		INSTANCE.putSoundType("rooted_dirt", SoundType.ROOTED_DIRT);
		INSTANCE.putSoundType("hanging_roots", SoundType.HANGING_ROOTS);
		INSTANCE.putSoundType("azalea_leaves", SoundType.AZALEA_LEAVES);
		INSTANCE.putSoundType("sculk_sensor", SoundType.SCULK_SENSOR);
		INSTANCE.putSoundType("sculk_catalyst", SoundType.SCULK_CATALYST);
		INSTANCE.putSoundType("sculk", SoundType.SCULK);
		INSTANCE.putSoundType("sculk_vein", SoundType.SCULK_VEIN);
		INSTANCE.putSoundType("sculk_shrieker", SoundType.SCULK_SHRIEKER);
		INSTANCE.putSoundType("glow_lichen", SoundType.GLOW_LICHEN);
		INSTANCE.putSoundType("deepslate", SoundType.DEEPSLATE);
		INSTANCE.putSoundType("deepslate_bricks", SoundType.DEEPSLATE_BRICKS);
		INSTANCE.putSoundType("deepslate_tiles", SoundType.DEEPSLATE_TILES);
		INSTANCE.putSoundType("polished_deepslate", SoundType.POLISHED_DEEPSLATE);
		INSTANCE.putSoundType("froglight", SoundType.FROGLIGHT);
		INSTANCE.putSoundType("frogspawn", SoundType.FROGSPAWN);
		INSTANCE.putSoundType("mangrove_roots", SoundType.MANGROVE_ROOTS);
		INSTANCE.putSoundType("muddy_mangrove_roots", SoundType.MUDDY_MANGROVE_ROOTS);
		INSTANCE.putSoundType("mud", SoundType.MUD);
		INSTANCE.putSoundType("mud_bricks", SoundType.MUD_BRICKS);
		INSTANCE.putSoundType("packed_mud", SoundType.PACKED_MUD);
		INSTANCE.putSoundType("hanging_sign", SoundType.HANGING_SIGN);
		INSTANCE.putSoundType("nether_wood_hanging_sign", SoundType.NETHER_WOOD_HANGING_SIGN);
		INSTANCE.putSoundType("bamboo_wood_hanging_sign", SoundType.BAMBOO_WOOD_HANGING_SIGN);
		INSTANCE.putSoundType("bamboo_wood", SoundType.BAMBOO_WOOD);
		INSTANCE.putSoundType("nether_wood", SoundType.NETHER_WOOD);
		INSTANCE.putSoundType("cherry_wood", SoundType.CHERRY_WOOD);
		INSTANCE.putSoundType("cherry_sapling", SoundType.CHERRY_SAPLING);
		INSTANCE.putSoundType("cherry_leaves", SoundType.CHERRY_LEAVES);
		INSTANCE.putSoundType("cherry_wood_hanging_sign", SoundType.CHERRY_WOOD_HANGING_SIGN);
		INSTANCE.putSoundType("chiseled_bookshelf", SoundType.CHISELED_BOOKSHELF);
		INSTANCE.putSoundType("suspicious_sand", SoundType.SUSPICIOUS_SAND);
		INSTANCE.putSoundType("suspicious_gravel", SoundType.SUSPICIOUS_GRAVEL);
		INSTANCE.putSoundType("decorated_pot", SoundType.DECORATED_POT);
		INSTANCE.putSoundType("decorated_pot_cracked", SoundType.DECORATED_POT_CRACKED);
	}
}
