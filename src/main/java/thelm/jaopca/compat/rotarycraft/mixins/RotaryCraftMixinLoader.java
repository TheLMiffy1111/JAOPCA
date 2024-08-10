package thelm.jaopca.compat.rotarycraft.mixins;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Loader;
import io.github.tox1cozz.mixinbooterlegacy.ILateMixinLoader;
import io.github.tox1cozz.mixinbooterlegacy.LateMixin;

@LateMixin
public class RotaryCraftMixinLoader implements ILateMixinLoader {

	@Override
	public List<String> getMixinConfigs() {
		List<String> configs = new ArrayList<>();
		if(Loader.isModLoaded("RotaryCraft")) {
			configs.add("jaopca.rotarycraft.mixins.json");
		}
		return configs;
	}
}
