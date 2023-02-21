package thelm.jaopca.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.JsonDeserializer;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.localization.ILocalizer;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.client.resources.ResourceHandler;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.MaterialEnumFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.Form;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormRequest;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.localization.LocalizationHandler;
import thelm.jaopca.localization.LocalizationRepoHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.oredict.OredictHandler;
import thelm.jaopca.recipes.RecipeHandler;
import thelm.jaopca.recipes.ShapedRecipeAction;
import thelm.jaopca.recipes.ShapelessRecipeAction;
import thelm.jaopca.recipes.SmeltingRecipeAction;

public class ApiImpl extends JAOPCAApi {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final ApiImpl INSTANCE = new ApiImpl();

	private ApiImpl() {}

	public void init() {
		JAOPCAApi.setInstance(this);
	}

	@Override
	public IBlockFormType blockFormType() {
		return BlockFormType.INSTANCE;
	}

	@Override
	public IItemFormType itemFormType() {
		return ItemFormType.INSTANCE;
	}

	@Override
	public IFluidFormType fluidFormType() {
		return FluidFormType.INSTANCE;
	}

	@Override
	public IFormType getFormType(String name) {
		return FormTypeHandler.getFormType(name);
	}

	@Override
	public IForm newForm(IModule module, String name, IFormType type) {
		return new Form(module, name, type);
	}

	@Override
	public IFormRequest newFormRequest(IModule module, IForm... forms) {
		IFormRequest request = new FormRequest(module, forms);
		return request;
	}

	@Override
	public IMiscHelper miscHelper() {
		return MiscHelper.INSTANCE;
	}

	@Override
	public IJsonHelper jsonHelper() {
		return JsonHelper.INSTANCE;
	}

	@Override
	public JsonDeserializer<Enum<?>> enumDeserializer() {
		return EnumDeserializer.INSTANCE;
	}

	@Override
	public JsonDeserializer<Function<IMaterial, Enum<?>>> materialEnumFunctionDeserializer() {
		return MaterialEnumFunctionDeserializer.INSTANCE;
	}

	@Override
	public <T> JsonDeserializer<Function<IMaterial, T>> materialMappedFunctionDeserializer(Function<String, T> stringToValue, Function<T, String> valueToString) {
		return new MaterialMappedFunctionDeserializer<>(stringToValue, valueToString);
	}

	@Override
	public JsonDeserializer<Function<IMaterial, ?>> materialFunctionDeserializer() {
		return MaterialFunctionDeserializer.INSTANCE;
	}

	@Override
	public IForm getForm(String name) {
		return FormHandler.getForm(name);
	}

	@Override
	public Set<IForm> getForms() {
		return ImmutableSortedSet.copyOf(FormHandler.getForms());
	}

	@Override
	public IMaterial getMaterial(String name) {
		return MaterialHandler.getMaterial(name);
	}

	@Override
	public Set<IMaterial> getMaterials() {
		return ImmutableSortedSet.copyOf(MaterialHandler.getMaterials());
	}

	@Override
	public CreativeTabs creativeTab() {
		return ItemFormType.getCreativeTab();
	}

	@Override
	public Set<String> getOredict() {
		return Collections.unmodifiableSet(OredictHandler.getOredict());
	}

	@Override
	public Set<String> getRecipes() {
		return RecipeHandler.getRegisteredRecipes();
	}

	@Override
	public ILocalizer currentLocalizer() {
		return LocalizationHandler.getCurrentLocalizer();
	}

	@Override
	public Map<String, String> currentMaterialLocalizationMap() {
		return LocalizationRepoHandler.getCurrentLocalizationMap();
	}

	@Override
	public boolean registerBlacklistedMaterialNames(String... names) {
		return MaterialHandler.registerBlacklistedMaterialNames(names);
	}

	@Override
	public boolean registerMaterialAlternativeNames(String name, String... alternatives) {
		return MaterialHandler.registerMaterialAlternativeNames(name, alternatives);
	}

	@Override
	public boolean registerFormType(IFormType type) {
		return FormTypeHandler.registerFormType(type);
	}

	@Override
	public boolean registerOredict(String oredict, Item item) {
		if(ConfigHandler.OREDICT_BLACKLIST.contains(oredict) || item == null) {
			return false;
		}
		OreDictionary.registerOre(oredict, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
		return true;
	}

	@Override
	public boolean registerOredict(String oredict, Block block) {
		return registerOredict(oredict, Item.getItemFromBlock(block));
	}

	@Override
	public boolean registerOredict(String oredict, ItemStack stack) {
		if(ConfigHandler.OREDICT_BLACKLIST.contains(oredict) || stack == null || stack.getItem() == null) {
			return false;
		}
		OreDictionary.registerOre(oredict, stack);
		return true;
	}

	@Override
	public boolean registerOredict(String oredict, String metaItemString) {
		if(metaItemString.matches(".*?@\\d*$")) {
			return registerOredict(oredict, MiscHelper.INSTANCE.parseMetaItem(metaItemString));
		}
		else {
			return registerOredict(oredict, (Item)Item.itemRegistry.getObject(metaItemString));
		}
	}

	@Override
	public void registerDefaultGemOverride(String materialName) {
		ConfigHandler.DEFAULT_GEM_OVERRIDES.add(materialName);
	}

	@Override
	public void registerDefaultCrystalOverride(String materialName) {
		ConfigHandler.DEFAULT_CRYSTAL_OVERRIDES.add(materialName);
	}

	@Override
	public void registerDefaultDustOverride(String materialName) {
		ConfigHandler.DEFAULT_DUST_OVERRIDES.add(materialName);
	}

	@Override
	public boolean registerRecipe(String key, IRecipeAction recipeAction) {
		if(ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		if(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
			return RecipeHandler.registerRecipe(key, recipeAction);
		}
		else if(!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
			return RecipeHandler.registerLateRecipe(key, recipeAction);
		}
		else {
			return RecipeHandler.registerFinalRecipe(key, recipeAction);
		}
	}

	@Override
	public boolean registerLateRecipe(String key, IRecipeAction recipeAction) {
		if(ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		if(!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
			return RecipeHandler.registerLateRecipe(key, recipeAction);
		}
		else {
			return RecipeHandler.registerFinalRecipe(key, recipeAction);
		}
	}

	@Override
	public boolean registerFinalRecipe(String key, IRecipeAction recipeAction) {
		if(ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		return RecipeHandler.registerFinalRecipe(key, recipeAction);
	}

	@Override
	public boolean registerShapedRecipe(String key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeAction(key, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(String key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeAction(key, output, count, input));
	}

	@Override
	public boolean registerSmeltingRecipe(String key, Object input, Object output, int count, float experience) {
		return registerRecipe(key, new SmeltingRecipeAction(key, input, output, count, experience));
	}

	@Override
	public void registerTextures(int mapId, Supplier<List<String>> locations, Consumer<List<IIcon>> consumer) {
		ResourceHandler.registerTextures(mapId, locations, consumer);
	}

	@Override
	public void registerLocalizer(ILocalizer translator, String... languages) {
		LocalizationHandler.registerLocalizer(translator, languages);
	}
}
