package thelm.jaopca.custom.utils;

import java.util.Locale;

import com.google.common.collect.HashBiMap;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

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
		INSTANCE.putBlockMaterial("structure_void", Material.STRUCTURAL_AIR);
		INSTANCE.putBlockMaterial("portal", Material.PORTAL);
		INSTANCE.putBlockMaterial("carpet", Material.CLOTH_DECORATION);
		INSTANCE.putBlockMaterial("plants", Material.PLANT);
		INSTANCE.putBlockMaterial("ocean_plant", Material.WATER_PLANT);
		INSTANCE.putBlockMaterial("tall_plants", Material.REPLACEABLE_PLANT);
		INSTANCE.putBlockMaterial("nether_plants", Material.REPLACEABLE_FIREPROOF_PLANT);
		INSTANCE.putBlockMaterial("sea_grass", Material.REPLACEABLE_WATER_PLANT);
		INSTANCE.putBlockMaterial("water", Material.WATER);
		INSTANCE.putBlockMaterial("bubble_column", Material.BUBBLE_COLUMN);
		INSTANCE.putBlockMaterial("lava", Material.LAVA);
		INSTANCE.putBlockMaterial("snow", Material.TOP_SNOW);
		INSTANCE.putBlockMaterial("fire", Material.FIRE);
		INSTANCE.putBlockMaterial("miscellaneous", Material.DECORATION);
		INSTANCE.putBlockMaterial("web", Material.WEB);
		INSTANCE.putBlockMaterial("redstone_light", Material.BUILDABLE_GLASS);
		INSTANCE.putBlockMaterial("clay", Material.CLAY);
		INSTANCE.putBlockMaterial("earth", Material.DIRT);
		INSTANCE.putBlockMaterial("organic", Material.GRASS);
		INSTANCE.putBlockMaterial("packed_ice", Material.ICE_SOLID);
		INSTANCE.putBlockMaterial("sand", Material.SAND);
		INSTANCE.putBlockMaterial("sponge", Material.SPONGE);
		INSTANCE.putBlockMaterial("shulker", Material.SHULKER_SHELL);
		INSTANCE.putBlockMaterial("wood", Material.WOOD);
		INSTANCE.putBlockMaterial("nether_wood", Material.NETHER_WOOD);
		INSTANCE.putBlockMaterial("bamboo_sapling", Material.BAMBOO_SAPLING);
		INSTANCE.putBlockMaterial("bamboo", Material.BAMBOO);
		INSTANCE.putBlockMaterial("wool", Material.WOOL);
		INSTANCE.putBlockMaterial("tnt", Material.EXPLOSIVE);
		INSTANCE.putBlockMaterial("leaves", Material.LEAVES);
		INSTANCE.putBlockMaterial("glass", Material.GLASS);
		INSTANCE.putBlockMaterial("ice", Material.ICE);
		INSTANCE.putBlockMaterial("cactus", Material.CACTUS);
		INSTANCE.putBlockMaterial("rock", Material.STONE);
		INSTANCE.putBlockMaterial("iron", Material.METAL);
		INSTANCE.putBlockMaterial("snow_block", Material.SNOW);
		INSTANCE.putBlockMaterial("anvil", Material.HEAVY_METAL);
		INSTANCE.putBlockMaterial("barrier", Material.BARRIER);
		INSTANCE.putBlockMaterial("piston", Material.PISTON);
		INSTANCE.putBlockMaterial("coral", Material.CORAL);
		INSTANCE.putBlockMaterial("gourd", Material.VEGETABLE);
		INSTANCE.putBlockMaterial("dragon_egg", Material.EGG);
		INSTANCE.putBlockMaterial("cake", Material.CAKE);

		INSTANCE.putSoundType("wood", SoundType.WOOD);
		INSTANCE.putSoundType("ground", SoundType.GRAVEL);
		INSTANCE.putSoundType("plant", SoundType.GRASS);
		INSTANCE.putSoundType("lily_pad", SoundType.LILY_PAD);
		INSTANCE.putSoundType("stone", SoundType.STONE);
		INSTANCE.putSoundType("metal", SoundType.METAL);
		INSTANCE.putSoundType("glass", SoundType.GLASS);
		INSTANCE.putSoundType("cloth", SoundType.WOOL);
		INSTANCE.putSoundType("sand", SoundType.SAND);
		INSTANCE.putSoundType("snow", SoundType.SNOW);
		INSTANCE.putSoundType("ladder", SoundType.LADDER);
		INSTANCE.putSoundType("anvil", SoundType.ANVIL);
		INSTANCE.putSoundType("slime", SoundType.SLIME_BLOCK);
		INSTANCE.putSoundType("honey", SoundType.HONEY_BLOCK);
		INSTANCE.putSoundType("wet_grass", SoundType.WET_GRASS);
		INSTANCE.putSoundType("coral", SoundType.CORAL_BLOCK);
		INSTANCE.putSoundType("bamboo", SoundType.BAMBOO);
		INSTANCE.putSoundType("bamboo_sapling", SoundType.BAMBOO_SAPLING);
		INSTANCE.putSoundType("scaffolding", SoundType.SCAFFOLDING);
		INSTANCE.putSoundType("sweet_berry_bush", SoundType.SWEET_BERRY_BUSH);
		INSTANCE.putSoundType("crop", SoundType.CROP);
		INSTANCE.putSoundType("stem", SoundType.HARD_CROP);
		INSTANCE.putSoundType("vine", SoundType.VINE);
		INSTANCE.putSoundType("nether_wart", SoundType.NETHER_WART);
		INSTANCE.putSoundType("lantern", SoundType.LANTERN);
		INSTANCE.putSoundType("hyphae", SoundType.STEM);
		INSTANCE.putSoundType("nylium", SoundType.NYLIUM);
		INSTANCE.putSoundType("fungus", SoundType.FUNGUS);
		INSTANCE.putSoundType("root", SoundType.ROOTS);
		INSTANCE.putSoundType("shroomlight", SoundType.SHROOMLIGHT);
		INSTANCE.putSoundType("nether_vine", SoundType.WEEPING_VINES);
		INSTANCE.putSoundType("nether_vine_lower_pitch", SoundType.TWISTING_VINES);
		INSTANCE.putSoundType("soul_sand", SoundType.SOUL_SAND);
		INSTANCE.putSoundType("soul_soil", SoundType.SOUL_SOIL);
		INSTANCE.putSoundType("basalt", SoundType.BASALT);
		INSTANCE.putSoundType("wart", SoundType.WART_BLOCK);
		INSTANCE.putSoundType("netherrack", SoundType.NETHERRACK);
		INSTANCE.putSoundType("nether_brick", SoundType.NETHER_BRICKS);
		INSTANCE.putSoundType("nether_sprout", SoundType.NETHER_SPROUTS);
		INSTANCE.putSoundType("nether_ore", SoundType.NETHER_ORE);
		INSTANCE.putSoundType("bone", SoundType.BONE_BLOCK);
		INSTANCE.putSoundType("netherite", SoundType.NETHERITE_BLOCK);
		INSTANCE.putSoundType("ancient_debris", SoundType.ANCIENT_DEBRIS);
		INSTANCE.putSoundType("lodestone", SoundType.LODESTONE);
		INSTANCE.putSoundType("chain", SoundType.CHAIN);
		INSTANCE.putSoundType("nether_gold", SoundType.NETHER_GOLD_ORE);
		INSTANCE.putSoundType("gilded_blackstone", SoundType.GILDED_BLACKSTONE);
	}
}
