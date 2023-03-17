package thelm.jaopca.materials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.common.MinecraftForge;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialColorEvent;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.client.colors.ColorHandler;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.MiscHelper;

public class Material implements IMaterial {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Set<String> DEFAULT_SMALL_BLOCKS = new TreeSet<>(Arrays.asList(
			"Quartz", "QuartzBlack"));

	private final String name;
	private final MaterialType type;
	private String modelType;
	private final TreeSet<String> alternativeNames = new TreeSet<>();
	private OptionalInt color = OptionalInt.empty();
	private boolean hasEffect = false;
	private EnumRarity displayRarity = EnumRarity.common;
	private final List<String> extras = new ArrayList<>();
	private final TreeSet<String> configModuleBlacklist = new TreeSet<>();
	private boolean isSmallStorageBlock;
	private IDynamicSpecConfig config;
	private String oredict;
	private boolean shouldFireColorEvent = true;

	public Material(String name, MaterialType type) {
		this.name = name;
		this.type = type;

		isSmallStorageBlock = DEFAULT_SMALL_BLOCKS.contains(name);

		switch(type) {
		case INGOT: case INGOT_PLAIN:
			modelType = "metallic";
			break;
		case GEM: case GEM_PLAIN: case CRYSTAL: case CRYSTAL_PLAIN:
			modelType = "crystal";
			break;
		case DUST: case DUST_PLAIN:
			modelType = "dust";
			break;
		}
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
		if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
			if(!color.isPresent() && config != null) {
				MiscHelper.INSTANCE.conditionalRunnable(FMLCommonHandler.instance().getSide()::isClient, ()->()->{
					shouldFireColorEvent = false;
					String oredict = getOredict();
					color = OptionalInt.of(0xFFFFFF);
					MiscHelper.INSTANCE.submitAsyncTask(()->{
						try {
							color = OptionalInt.of(config.getDefinedInt("general.color", ColorHandler.getAverageColor(oredict), "The color of this material."));
							MinecraftForge.EVENT_BUS.post(new MaterialColorEvent(this, color.getAsInt()));
						}
						catch(Exception e) {
							LOGGER.warn("Unable to get color for material {}", name, e);
						}
					});
				}, ()->()->{}).run();
			}
			if(color.isPresent() && shouldFireColorEvent) {
				shouldFireColorEvent = false;
				MinecraftForge.EVENT_BUS.post(new MaterialColorEvent(this, color.getAsInt()));
			}
		}
		else {
			LOGGER.warn("Tried to get color for material {} before post-init", name);
		}
		return 0xFF000000 | color.orElse(0xFFFFFF);
	}

	@Override
	public boolean hasEffect() {
		return hasEffect;
	}

	@Override
	public EnumRarity getDisplayRarity() {
		return displayRarity;
	}

	public void setAlternativeNames(Collection<String> names) {
		alternativeNames.addAll(names);
	}

	public void setConfig(IDynamicSpecConfig config) {
		this.config = config;

		config.setComment("general", "Configurations for material "+name+".");

		List<String> cfgList = config.getDefinedStringList("general.alternativeNames", new ArrayList<>(alternativeNames), "The alternative names of this material.");
		alternativeNames.clear();
		alternativeNames.addAll(cfgList);

		cfgList = config.getDefinedStringList("general.extras", extras, MaterialHandler::containsMaterial, "The byproducts of this material.");
		extras.clear();
		extras.addAll(cfgList);

		isSmallStorageBlock = config.getDefinedBoolean("general.isSmallStorageBlock", isSmallStorageBlock, "Is the storage block of this material small (2x2).");

		MiscHelper helper = MiscHelper.INSTANCE;
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

	private String getOredict() {
		if(oredict == null) {
			oredict = type.getFormName()+name;
		}
		return oredict;
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
