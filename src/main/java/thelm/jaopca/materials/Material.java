package thelm.jaopca.materials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.TreeMultiset;

import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.utils.MiscHelper;

public class Material implements IMaterial {

	private final String name;
	private final MaterialType type;
	private String modelType = "metallic";
	private final TreeSet<String> alternativeNames = new TreeSet<>();
	private OptionalInt color = OptionalInt.empty();
	private boolean hasEffect = false;
	private Rarity displayRarity = Rarity.COMMON;
	private final List<String> extras = new ArrayList<>();
	private final TreeSet<String> configModuleBlacklist = new TreeSet<>();
	private IDynamicSpecConfig config;

	public Material(String name, MaterialType type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public MaterialType getType() {
		return type;
	}

	@Override
	public Set<String> getAlternativeNames() {
		return Collections.unmodifiableNavigableSet(alternativeNames);
	}

	@Override
	public IMaterial getExtra(int index) {
		return index == 0 || !hasExtra(index) ? this : Optional.ofNullable(MaterialHandler.getMaterial(extras.get(index-1))).orElse(this);
	}

	@Override
	public boolean hasExtra(int index) {
		return index == 0 || index-1 < extras.size() && !StringUtils.isEmpty(extras.get(index-1));
	}

	@Override
	public Set<String> getConfigModuleBlacklist() {
		return Collections.unmodifiableNavigableSet(configModuleBlacklist);
	}

	@Override
	public String getModelType() {
		return modelType;
	}

	@Override
	public int getColor() {
		if(!color.isPresent() && config != null) {
			DistExecutor.runWhenOn(Dist.CLIENT, ()->()->{
				color = OptionalInt.of(0xFFFFFF);
				MiscHelper.INSTANCE.submitAsyncTask(()->{
					color = OptionalInt.of(config.getDefinedInt("general.color", ColorHandler.getAverageColor(getTag()), "The color of this material."));
				});
			});
		}
		return 0xFF000000 | color.orElse(0xFFFFFF);
	}

	@Override
	public boolean hasEffect() {
		return hasEffect;
	}

	@Override
	public Rarity getDisplayRarity() {
		return displayRarity;
	}

	public void setConfig(IDynamicSpecConfig config) {
		this.config = config;

		List<String> cfgList = config.getDefinedStringList("general.alternativeNames", new ArrayList<>(alternativeNames), "The alternative names of this material.");
		alternativeNames.clear();
		alternativeNames.addAll(cfgList);

		cfgList = config.getDefinedStringList("general.extras", extras, MaterialHandler::containsMaterial, "The byproducts of this material.");
		extras.clear();
		extras.addAll(cfgList);

		TreeMultiset<String> blacklist = TreeMultiset.create(config.getDefinedStringList("general.moduleBlacklist", new ArrayList<>(configModuleBlacklist),
				s->ModuleHandler.getModuleMap().containsKey(s) || "*".equals(s), "The module blacklist of this material."));
		int count = blacklist.count("*");
		ModuleHandler.getModuleMap().keySet().forEach(s->blacklist.add(s, count));
		blacklist.remove("*", count);
		configModuleBlacklist.clear();
		configModuleBlacklist.addAll(blacklist.entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(e->e.getElement()).collect(Collectors.toList()));

		hasEffect = config.getDefinedBoolean("general.hasEffect", hasEffect, "Should items of this material have the enchanted glow.");
		modelType = config.getDefinedString("general.modelType", modelType, s->isModelTypeValid(s), "The model type of the material.");

		color = config.getOptionalInt("general.color");
	}

	private Tag<Item> getTag() {
		String path = "";
		switch(type) {
		case INGOT:
		case INGOT_PLAIN:
			path = "ingots/"+name;
			break;
		case GEM:
		case GEM_PLAIN:
			path = "gems/"+name;
			break;
		case CRYSTAL:
		case CRYSTAL_PLAIN:
			path = "crystals/"+name;
			break;
		case DUST:
		case DUST_PLAIN:
			path = "dusts/"+name;
			break;
		default:
			break;
		}
		return new ItemTags.Wrapper(new ResourceLocation("forge", path));
	}

	@Override
	public String toString() {
		return "Material:"+name;
	}

	private static boolean isModelTypeValid(String modelType) {
		return modelType.chars().allMatch(c -> c == 95 || c == 45
				|| (c >= 97 && c <= 122) || (c >= 48 && c <= 57)
				|| c == 47 || c == 46);
	}
}
