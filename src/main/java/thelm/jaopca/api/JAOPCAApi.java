package thelm.jaopca.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializer;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
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

/**
 * The main API class of JAOPCA, which consists of the main methods that modules use to add content to
 * JAOPCA. Most methods here should only be called in modules/
 */
public abstract class JAOPCAApi {

	private static JAOPCAApi instance;

	protected static void setInstance(JAOPCAApi api) {
		if(instance == null) {
			instance = api;
		}
	}

	/**
	 * Returns if the API instance was set or not. Use this if your mod needs to use the API methods that
	 * work before JAOPCA collects data.
	 * @return true if the API instance was set
	 */
	public static boolean initialized() {
		return instance != null;
	}

	/**
	 * Returns the implementation instance of the API.
	 * @return The API instance
	 * @throws IllegalStateException if the API instance was not set
	 */
	public static JAOPCAApi instance() {
		if(instance == null) {
			throw new IllegalStateException("Got API instance before it is set");
		}
		return instance;
	}

	/**
	 * Returns the implementation instance of the {@link IBlockFormType}.
	 * @return the block form type instance
	 */
	public abstract IBlockFormType blockFormType();

	/**
	 * Returns the implementation instance of {@link IItemFormType}.
	 * @return The item form type instance
	 */
	public abstract IItemFormType itemFormType();

	/**
	 * Returns the implementation instance of {@link IFluidFormType}.
	 * @return The fluid form type instance
	 */
	public abstract IFluidFormType fluidFormType();

	/**
	 * Gets an {@link IFormType} by name.
	 * @param name The name of the form type
	 * @return The form type with the name provided, null if no form type registered has this name
	 */
	public abstract IFormType getFormType(String name);

	/**
	 * Creates a new {@link IForm} with the specified module, name, and form type. The form type does not need
	 * to be registered.
	 * @param module The module that the form should be under
	 * @param name The name of the form
	 * @param type The type of the form
	 * @return The form with the provided name and type
	 */
	public abstract IForm newForm(IModule module, String name, IFormType type);

	/**
	 * Creates a form request from a list of forms. Forms that do not have the same module as provided
	 * will not be included.
	 * @param module The module that the form request should be under
	 * @param forms The forms to be put into the form request
	 * @return The form request consisting of the provided valid forms
	 */
	public abstract IFormRequest newFormRequest(IModule module, IForm... forms);

	/**
	 * Returns the implementation instance of {@link IMiscHelper}.
	 * @return The misc helper instance
	 */
	public abstract IMiscHelper miscHelper();

	/**
	 * Returns the implementation instance of {@link IJsonHelper}.
	 * @return The JSON helper instance
	 */
	public abstract IJsonHelper jsonHelper();

	/**
	 * Returns the enum JSON deserializer instance used by JAOPCA. The deserializer deserializes enums with
	 * case-insensitive names and enum ordinals.
	 * @return The enum deserializer instance
	 */
	public abstract JsonDeserializer<Enum<?>> enumDeserializer();

	/**
	 * Returns the material to enum function JSON deserializer instance used by JAOPCA. The deserializer
	 * creates a {@linkplain it.unimi.dsi.fastutil.objects.Object2ObjectMap Object2ObjectMap} tha may
	 * have its values based on the configuration files, and deserializes default enum values with
	 * case-insensitive names and enum ordinals.
	 * @return The material to enum function deserializer instance
	 */
	public abstract JsonDeserializer<Function<IMaterial, Enum<?>>> materialEnumFunctionDeserializer();

	/**
	 * Creates an instance of a mapped material function JSON deserializer using the provided functions.
	 * The deserializer creates a {@linkplain it.unimi.dsi.fastutil.objects.Object2ObjectMap Object2ObjectMap}
	 * that may have its values based on the configuration files. The deserializer will use the stringToValue
	 * function to read default values and configuration values, and use the valueToString function to
	 * put the default values into configuration files.
	 * @param <T> The type of the value in the function
	 * @param stringToValue The function used to convert a string into a value.
	 * @param valueToString The function used to convert a value into a string.
	 * @return The instance of a mapped material function deserializer using the provided functions
	 */
	public abstract <T> JsonDeserializer<Function<IMaterial, T>> materialMappedFunctionDeserializer(Function<String, T> stringToValue, Function<T, String> valueToString);

	/**
	 * Returns the material function JSON deserializer instance used by JAOPCA. The deserializer creates a
	 * {@linkplain it.unimi.dsi.fastutil.objects.Object2ObjectMap Object2ObjectMap} that will not have
	 * its values based on the configuration files. The values will be deserialized using the type adapters
	 * present.
	 * @return The material function JSON deserializer instance
	 */
	public abstract JsonDeserializer<Function<IMaterial, ?>> materialFunctionDeserializer();

	/**
	 * Gets an {@link IForm} by name.
	 * @param name The name of the form
	 * @return The form with the name provided, null if no form registered has this name
	 */
	public abstract IForm getForm(String name);

	public abstract Set<IForm> getForms();

	/**
	 * Gets an {@link IMaterial} by name.
	 * @param name The name of the material
	 * @return The material with the name provided, null if no material has been found with this name
	 */
	public abstract IMaterial getMaterial(String name);

	public abstract Set<IMaterial> getMaterials();

	/**
	 * Returns the creative tab used by items added by JAOPCA.
	 * @return The creative tab used by JAOPCA
	 */
	public abstract CreativeTabs creativeTab();

	/**
	 * Returns the set of known oredict entries. Note that oredict entries added by customization mods
	 * may not be included.
	 * @return The set of oredict entries known by JAOPCA
	 */
	public abstract Set<String> getOredict();

	/**
	 * Returns the set of recipe action keys that has been registered with JAOPCA.
	 * @return The set of recipe keys known by JAOPCA
	 */
	public abstract Set<String> getRecipes();

	/**
	 * Returns the current {@link ILocalizer} based on Minecraft's current language. Will always return the
	 * default localizer on dedicated servers.
	 * @return The current localizer based on Minecraft's current language
	 */
	public abstract ILocalizer currentLocalizer();

	public abstract Map<String, String> currentMaterialLocalizationMap();

	public abstract boolean registerBlacklistedMaterialNames(String... names);

	public abstract boolean registerUsedPlainPrefixes(String... prefixes);

	public abstract boolean registerMaterialAlternativeNames(String name, String... alternatives);

	/**
	 * Registers an {@link IFormType} to be used in custom form deserialization.
	 * @param type The form type to be registered
	 * @return true if the form type was successfully registered
	 */
	public abstract boolean registerFormType(IFormType type);

	public abstract boolean registerOredict(String oredict, Item item);

	public abstract boolean registerOredict(String oredict, Block block);

	public abstract boolean registerOredict(String oredict, ItemStack stack);

	public abstract boolean registerOredict(String oredict, String metaItemString);

	public abstract void registerDefaultGemOverride(String materialName);

	public abstract void registerDefaultCrystalOverride(String materialName);

	public abstract void registerDefaultDustOverride(String materialName);

	/**
	 * Registers a recipe action to be ran by JAOPCA.
	 * @param key The id of the recipe
	 * @param recipeAction The recipe action
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerRecipe(String key, IRecipeAction recipeAction);

	public abstract boolean registerLateRecipe(String key, IRecipeAction recipeAction);

	public abstract boolean registerFinalRecipe(String key, IRecipeAction recipeAction);

	/**
	 * Creates a shaped recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input The input of the recipe in the form of how one would specify inputs in Minecraft versions
	 * prior to 1.12, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapedRecipe(String key, Object output, int count, Object... input);

	/**
	 * Creates a shapeless recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input An array of inputs for the recipe, see {@link IMiscHelper#getIngredient(Object)} for valid
	 * input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapelessRecipe(String key, Object output, int count, Object... input);

	/**
	 * Creates a furnace recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of the recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmeltingRecipe(String key, Object input, Object output, int count, float experience);

	public abstract void registerTextures(int mapId, Supplier<List<String>> locations, Consumer<List<IIcon>> consumer);

	/**
	 * Registers an {@link ILocalizer} to languages for use by JAOPCA.
	 * @param localizer The localizer
	 * @param languages An array of language identifiers that the localizer should be used for
	 */
	public abstract void registerLocalizer(ILocalizer localizer, String... languages);
}
