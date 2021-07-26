package thelm.jaopca.custom.utils;

import java.util.Locale;

import com.google.common.collect.HashBiMap;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class BlockDeserializationHelper {

	public static final BlockDeserializationHelper INSTANCE = new BlockDeserializationHelper();

	private BlockDeserializationHelper() {}

	private static final HashBiMap<String, Material> BLOCK_MATERIALS = HashBiMap.create();
	private static final HashBiMap<String, SoundType> SOUND_TYPES = HashBiMap.create();

	public Material getBlockMaterial(String name) {
		return BLOCK_MATERIALS.get(name.toLowerCase(Locale.US));
	}

	public String getBlockMaterialName(Material material) {
		return BLOCK_MATERIALS.inverse().get(material);
	}

	public void putBlockMaterial(String name, Material material) {
		BLOCK_MATERIALS.put(name.toLowerCase(Locale.US), material);
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
		INSTANCE.putBlockMaterial("air", Material.AIR);
		INSTANCE.putBlockMaterial("structural_air", Material.STRUCTURAL_AIR);
		INSTANCE.putBlockMaterial("portal", Material.PORTAL);
		INSTANCE.putBlockMaterial("cloth_decoration", Material.CLOTH_DECORATION);
		INSTANCE.putBlockMaterial("plant", Material.PLANT);
		INSTANCE.putBlockMaterial("water_plant", Material.WATER_PLANT);
		INSTANCE.putBlockMaterial("replaceable_fireproof_plant", Material.REPLACEABLE_FIREPROOF_PLANT);
		INSTANCE.putBlockMaterial("replaceable_water_plant", Material.REPLACEABLE_WATER_PLANT);
		INSTANCE.putBlockMaterial("water", Material.WATER);
		INSTANCE.putBlockMaterial("bubble_column", Material.BUBBLE_COLUMN);
		INSTANCE.putBlockMaterial("lava", Material.LAVA);
		INSTANCE.putBlockMaterial("top_snow", Material.TOP_SNOW);
		INSTANCE.putBlockMaterial("fire", Material.FIRE);
		INSTANCE.putBlockMaterial("decoration", Material.DECORATION);
		INSTANCE.putBlockMaterial("web", Material.WEB);
		INSTANCE.putBlockMaterial("sculk", Material.SCULK);
		INSTANCE.putBlockMaterial("buildable_glass", Material.BUILDABLE_GLASS);
		INSTANCE.putBlockMaterial("clay", Material.CLAY);
		INSTANCE.putBlockMaterial("dirt", Material.DIRT);
		INSTANCE.putBlockMaterial("grass", Material.GRASS);
		INSTANCE.putBlockMaterial("ice_solid", Material.ICE_SOLID);
		INSTANCE.putBlockMaterial("sand", Material.SAND);
		INSTANCE.putBlockMaterial("sponge", Material.SPONGE);
		INSTANCE.putBlockMaterial("shulker_shell", Material.SHULKER_SHELL);
		INSTANCE.putBlockMaterial("wood", Material.WOOD);
		INSTANCE.putBlockMaterial("nether_wood", Material.NETHER_WOOD);
		INSTANCE.putBlockMaterial("bamboo_sapling", Material.BAMBOO_SAPLING);
		INSTANCE.putBlockMaterial("bamboo", Material.BAMBOO);
		INSTANCE.putBlockMaterial("wool", Material.WOOL);
		INSTANCE.putBlockMaterial("explosive", Material.EXPLOSIVE);
		INSTANCE.putBlockMaterial("leaves", Material.LEAVES);
		INSTANCE.putBlockMaterial("glass", Material.GLASS);
		INSTANCE.putBlockMaterial("ice", Material.ICE);
		INSTANCE.putBlockMaterial("cactus", Material.CACTUS);
		INSTANCE.putBlockMaterial("stone", Material.STONE);
		INSTANCE.putBlockMaterial("metal", Material.METAL);
		INSTANCE.putBlockMaterial("snow", Material.SNOW);
		INSTANCE.putBlockMaterial("heavy_metal", Material.HEAVY_METAL);
		INSTANCE.putBlockMaterial("barrier", Material.BARRIER);
		INSTANCE.putBlockMaterial("piston", Material.PISTON);
		INSTANCE.putBlockMaterial("moss", Material.MOSS);
		INSTANCE.putBlockMaterial("vegetable", Material.VEGETABLE);
		INSTANCE.putBlockMaterial("egg", Material.EGG);
		INSTANCE.putBlockMaterial("cake", Material.CAKE);
		INSTANCE.putBlockMaterial("amethyst", Material.AMETHYST);
		INSTANCE.putBlockMaterial("powder_snow", Material.POWDER_SNOW);

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
		INSTANCE.putSoundType("moss", SoundType.MOSS);
		INSTANCE.putSoundType("big_dripleaf", SoundType.BIG_DRIPLEAF);
		INSTANCE.putSoundType("small_dripleaf", SoundType.SMALL_DRIPLEAF);
		INSTANCE.putSoundType("rooted_dirt", SoundType.ROOTED_DIRT);
		INSTANCE.putSoundType("hanging_roots", SoundType.HANGING_ROOTS);
		INSTANCE.putSoundType("azalea_leaves", SoundType.AZALEA_LEAVES);
		INSTANCE.putSoundType("sculk_sensor", SoundType.SCULK_SENSOR);
		INSTANCE.putSoundType("glow_lichen", SoundType.GLOW_LICHEN);
		INSTANCE.putSoundType("deepslate", SoundType.DEEPSLATE);
		INSTANCE.putSoundType("deepslate_bricks", SoundType.DEEPSLATE_BRICKS);
		INSTANCE.putSoundType("deepslate_tiles", SoundType.DEEPSLATE_TILES);
		INSTANCE.putSoundType("polished_deepslate", SoundType.POLISHED_DEEPSLATE);
	}
}
