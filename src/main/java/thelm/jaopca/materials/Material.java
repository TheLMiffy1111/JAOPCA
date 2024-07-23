package thelm.jaopca.materials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialColorEvent;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.MiscHelper;

public class Material implements IMaterial {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Set<String> DEFAULT_SMALL_BLOCKS = new TreeSet<>(List.of(
			"amethyst", "certus_quartz", "quartz"));

	private final String name;
	private final MaterialType type;
	private final TreeSet<String> alternativeNames = new TreeSet<>();
	private final List<String> extras = new ArrayList<>();
	private boolean hasEffect = false;
	private final TreeSet<String> configModuleBlacklist = new TreeSet<>();
	private boolean isSmallStorageBlock;
	private String modelType;
	private Rarity displayRarity = Rarity.COMMON;
	private OptionalInt color = OptionalInt.empty();
	private IDynamicSpecConfig config;
	private ITag<Item> tag;
	private boolean shouldFireColorEvent = true;

	public Material(String name, MaterialType type) {
		this.name = name;
		this.type = type;

		isSmallStorageBlock = DEFAULT_SMALL_BLOCKS.contains(name);

		modelType = switch(type) {
		case INGOT, INGOT_LEGACY, INGOT_PLAIN -> "metallic";
		case GEM, GEM_PLAIN, CRYSTAL, CRYSTAL_PLAIN -> "crystal";
		case DUST, DUST_PLAIN -> "dust";
		};
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
	public boolean isSmallStorageBlock() {
		return isSmallStorageBlock;
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
		if(MaterialHandler.clientTagsBound) {
			if(!color.isPresent() && config != null) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
					shouldFireColorEvent = false;
					ITag<Item> tag = getTag();
					color = OptionalInt.of(0xFFFFFF);
					MiscHelper.INSTANCE.submitAsyncTask(()->{
						try {
							color = OptionalInt.of(config.getDefinedInt("general.color", ColorHandler.getAverageColor(tag), "The color of this material."));
							MinecraftForge.EVENT_BUS.post(new MaterialColorEvent(this, color.getAsInt()));
						}
						catch(Exception e) {
							LOGGER.warn("Unable to get color for material {}", name, e);
						}
					});
				});
			}
			if(color.isPresent() && shouldFireColorEvent) {
				shouldFireColorEvent = false;
				MinecraftForge.EVENT_BUS.post(new MaterialColorEvent(this, color.getAsInt()));
			}
		}
		else {
			LOGGER.warn("Tried to get color for material {} when tags are not bound", name);
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

		isSmallStorageBlock = config.getDefinedBoolean("general.isSmallStorageBlock", isSmallStorageBlock, "Is the storage block of this material small (2x2).");

		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateModuleSet(
				config.getDefinedStringList("general.moduleBlacklist", new ArrayList<>(configModuleBlacklist),
						helper.configModulePredicate(), "The module blacklist of this material."),
				configModuleBlacklist);

		hasEffect = config.getDefinedBoolean("general.hasEffect", hasEffect, "Should items of this material have the enchanted glow.");
		modelType = config.getDefinedString("general.modelType", modelType, s->isModelTypeValid(s), "The model type of the material.");

		if(ConfigHandler.resetColors) {
			config.remove("general.color");
		}
		color = config.getOptionalInt("general.color");
	}

	private ITag<Item> getTag() {
		if(tag == null) {
			tag = ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:"+type.getFormName()+'/'+name)));
		}
		return tag;
	}

	@Override
	public String toString() {
		return "Material:"+name;
	}

	private static boolean isModelTypeValid(String modelType) {
		return modelType.chars().allMatch(c -> c == '_' || c == '-'
				|| (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
				|| c == '/' || c == '.');
	}
}
