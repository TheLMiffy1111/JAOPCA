package thelm.jaopca.custom.utils;

import java.util.Locale;

import com.google.common.collect.HashBiMap;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;

public class BlockDeserializationHelper {

	public static final BlockDeserializationHelper INSTANCE = new BlockDeserializationHelper();

	private BlockDeserializationHelper() {}

	private static final HashBiMap<String, Material> BLOCK_MATERIALS = HashBiMap.create();
	private static final HashBiMap<String, Block.SoundType> SOUND_TYPES = HashBiMap.create();

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
		INSTANCE.putBlockMaterial("air", Material.air);
		INSTANCE.putBlockMaterial("grass", Material.grass);
		INSTANCE.putBlockMaterial("ground", Material.ground);
		INSTANCE.putBlockMaterial("wood", Material.wood);
		INSTANCE.putBlockMaterial("rock", Material.rock);
		INSTANCE.putBlockMaterial("iron", Material.iron);
		INSTANCE.putBlockMaterial("anvil", Material.anvil);
		INSTANCE.putBlockMaterial("water", Material.water);
		INSTANCE.putBlockMaterial("lava", Material.lava);
		INSTANCE.putBlockMaterial("leaves", Material.leaves);
		INSTANCE.putBlockMaterial("plants", Material.plants);
		INSTANCE.putBlockMaterial("vine", Material.vine);
		INSTANCE.putBlockMaterial("sponge", Material.sponge);
		INSTANCE.putBlockMaterial("cloth", Material.cloth);
		INSTANCE.putBlockMaterial("fire", Material.fire);
		INSTANCE.putBlockMaterial("sand", Material.sand);
		INSTANCE.putBlockMaterial("circuits", Material.circuits);
		INSTANCE.putBlockMaterial("carpet", Material.carpet);
		INSTANCE.putBlockMaterial("glass", Material.glass);
		INSTANCE.putBlockMaterial("redstone_light", Material.redstoneLight);
		INSTANCE.putBlockMaterial("tnt", Material.tnt);
		INSTANCE.putBlockMaterial("coral", Material.coral);
		INSTANCE.putBlockMaterial("ice", Material.ice);
		INSTANCE.putBlockMaterial("packed_ice", Material.packedIce);
		INSTANCE.putBlockMaterial("snow", Material.snow);
		INSTANCE.putBlockMaterial("crafted_snow", Material.craftedSnow);
		INSTANCE.putBlockMaterial("cactus", Material.cactus);
		INSTANCE.putBlockMaterial("clay", Material.clay);
		INSTANCE.putBlockMaterial("gourd", Material.gourd);
		INSTANCE.putBlockMaterial("dragon_egg", Material.dragonEgg);
		INSTANCE.putBlockMaterial("portal", Material.portal);
		INSTANCE.putBlockMaterial("cake", Material.cake);
		INSTANCE.putBlockMaterial("web", Material.web);
		INSTANCE.putBlockMaterial("piston", Material.piston);

		INSTANCE.putSoundType("stone", Block.soundTypeStone);
		INSTANCE.putSoundType("wood", Block.soundTypeWood);
		INSTANCE.putSoundType("gravel", Block.soundTypeGravel);
		INSTANCE.putSoundType("grass", Block.soundTypeGrass);
		INSTANCE.putSoundType("piston", Block.soundTypePiston);
		INSTANCE.putSoundType("metal", Block.soundTypeMetal);
		INSTANCE.putSoundType("glass", Block.soundTypeGlass);
		INSTANCE.putSoundType("cloth", Block.soundTypeCloth);
		INSTANCE.putSoundType("sand", Block.soundTypeSand);
		INSTANCE.putSoundType("snow", Block.soundTypeSnow);
		INSTANCE.putSoundType("ladder", Block.soundTypeLadder);
		INSTANCE.putSoundType("anvil", Block.soundTypeAnvil);
	}
}
