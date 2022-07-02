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
		INSTANCE.putBlockMaterial("grass", Material.GRASS);
		INSTANCE.putBlockMaterial("ground", Material.GROUND);
		INSTANCE.putBlockMaterial("wood", Material.WOOD);
		INSTANCE.putBlockMaterial("rock", Material.ROCK);
		INSTANCE.putBlockMaterial("iron", Material.IRON);
		INSTANCE.putBlockMaterial("anvil", Material.ANVIL);
		INSTANCE.putBlockMaterial("water", Material.WATER);
		INSTANCE.putBlockMaterial("lava", Material.LAVA);
		INSTANCE.putBlockMaterial("leaves", Material.LEAVES);
		INSTANCE.putBlockMaterial("plants", Material.PLANTS);
		INSTANCE.putBlockMaterial("vine", Material.VINE);
		INSTANCE.putBlockMaterial("sponge", Material.SPONGE);
		INSTANCE.putBlockMaterial("cloth", Material.CLOTH);
		INSTANCE.putBlockMaterial("fire", Material.FIRE);
		INSTANCE.putBlockMaterial("sand", Material.SAND);
		INSTANCE.putBlockMaterial("circuits", Material.CIRCUITS);
		INSTANCE.putBlockMaterial("carpet", Material.CARPET);
		INSTANCE.putBlockMaterial("glass", Material.GLASS);
		INSTANCE.putBlockMaterial("redstone_light", Material.REDSTONE_LIGHT);
		INSTANCE.putBlockMaterial("tnt", Material.TNT);
		INSTANCE.putBlockMaterial("coral", Material.CORAL);
		INSTANCE.putBlockMaterial("ice", Material.ICE);
		INSTANCE.putBlockMaterial("packed_ice", Material.PACKED_ICE);
		INSTANCE.putBlockMaterial("snow", Material.SNOW);
		INSTANCE.putBlockMaterial("crafted_snow", Material.CRAFTED_SNOW);
		INSTANCE.putBlockMaterial("cactus", Material.CACTUS);
		INSTANCE.putBlockMaterial("clay", Material.CLAY);
		INSTANCE.putBlockMaterial("gourd", Material.GOURD);
		INSTANCE.putBlockMaterial("dragon_egg", Material.DRAGON_EGG);
		INSTANCE.putBlockMaterial("portal", Material.PORTAL);
		INSTANCE.putBlockMaterial("cake", Material.CAKE);
		INSTANCE.putBlockMaterial("web", Material.WEB);
		INSTANCE.putBlockMaterial("piston", Material.PISTON);
		INSTANCE.putBlockMaterial("barrier", Material.BARRIER);
		INSTANCE.putBlockMaterial("structure_void", Material.STRUCTURE_VOID);

		INSTANCE.putSoundType("wood", SoundType.WOOD);
		INSTANCE.putSoundType("ground", SoundType.GROUND);
		INSTANCE.putSoundType("plant", SoundType.PLANT);
		INSTANCE.putSoundType("stone", SoundType.STONE);
		INSTANCE.putSoundType("metal", SoundType.METAL);
		INSTANCE.putSoundType("glass", SoundType.GLASS);
		INSTANCE.putSoundType("cloth", SoundType.CLOTH);
		INSTANCE.putSoundType("sand", SoundType.SAND);
		INSTANCE.putSoundType("snow", SoundType.SNOW);
		INSTANCE.putSoundType("ladder", SoundType.LADDER);
		INSTANCE.putSoundType("anvil", SoundType.ANVIL);
		INSTANCE.putSoundType("slime", SoundType.SLIME);
	}
}
