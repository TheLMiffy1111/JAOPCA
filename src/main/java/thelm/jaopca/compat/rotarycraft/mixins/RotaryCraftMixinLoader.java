package thelm.jaopca.compat.rotarycraft.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class RotaryCraftMixinLoader implements ILateMixinLoader {

	@Override
	public String getMixinConfig() {
		return "jaopca.mixins.rotarycraft.json";
	}

	@Override
	public List<String> getMixins(Set<String> loadedMods) {
		List<String> mixins = new ArrayList<>();
		if(loadedMods.contains("RotaryCraft")) {
			mixins.add("ExtractorHandlerMixin");
			mixins.add("ExtractorHandlerRecipeMixin");
			mixins.add("RecipesExtractorMixin");
			mixins.add("SlotExtractor1Mixin");
			mixins.add("SlotExtractor2Mixin");
			mixins.add("SlotExtractor3Mixin");
			mixins.add("SlotExtractor4Mixin");
			mixins.add("TileEntityExtractorMixin");
		}
		return mixins;
	}
}
