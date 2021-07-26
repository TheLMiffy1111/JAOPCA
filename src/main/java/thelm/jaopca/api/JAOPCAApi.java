package thelm.jaopca.api;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializer;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.entities.IEntityTypeFormType;
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
import thelm.jaopca.api.recipes.IRecipeSerializer;

/**
 * The main API class of JAOPCA, which consists of the main methods that modules use to add content to
 * JAOPCA. Most methods here should only be called in modules since most of JAOPCA is initialized in the
 * deferred work queue.
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
	 * Returns the implementation instance of {@link IEntityTypeFormType}. <b>Not yet implemented!</b>
	 * @return The entity type form type instance
	 */
	public abstract IEntityTypeFormType entityTypeFormType();

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
	 * Returns the forge registry entry supplier JSON deserializer instance used by JAOPCA. The deserializer
	 * deserializes registry entries with locations.
	 * @return The forge registry entry deserializer instance
	 */
	public abstract JsonDeserializer<Supplier<IForgeRegistryEntry<?>>> forgeRegistryEntrySupplierDeserializer();

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
	public abstract CreativeModeTab creativeTab();

	/**
	 * Returns the set of known block tag locations, which is the union of defined block tag locations
	 * and registered block tag locations. Note that tags added by custom data packs may not be included.
	 * @return The set of block tag locations known by JAOPCA
	 */
	public abstract Set<ResourceLocation> getBlockTags();

	/**
	 * Returns the set of known item tag locations, which is the union of defined item tag locations
	 * and registered item tag locations. Note that tags added by custom data packs may not be included.
	 * @return The set of item tag locations known by JAOPCA
	 */
	public abstract Set<ResourceLocation> getItemTags();

	/**
	 * Returns the set of known fluid tag locations, which is the union of defined fluid tag locations
	 * and registered fluid tag locations. Note that tags added by custom data packs may not be included.
	 * @return The set of fluid tag locations known by JAOPCA
	 */
	public abstract Set<ResourceLocation> getFluidTags();

	/**
	 * Returns the set of known entity type tag locations, which is the union of defined entity type tag
	 * locations and registered entity type tag locations. Note that tags added by custom data packs may
	 * not be included.
	 * @return The set of entity type tag locations known by JAOPCA
	 */
	public abstract Set<ResourceLocation> getEntityTypeTags();

	/**
	 * Returns the set of known tag locations of the supplied type, which is the registered tag locations.
	 * Note that tags added by custom data packs may not be included.
	 * @param type The type of the tag
	 * @return The set of tag locations known by JAOPCA
	 */
	public abstract Set<ResourceLocation> getTags(String type);

	/**
	 * Returns the set of known recipe locations, which is the union of defined recipe locations and
	 * registered recipe locations. Note that recipes added by custom data packs may not be included.
	 * @return The set of recipe locations known by JAOPCA
	 */
	public abstract Set<ResourceLocation> getRecipes();

	/**
	 * Returns the set of known loot table locations, which is the union of defined loot table locations
	 * and registered loot table locations. Note that loot tables added by custom data packs may not be
	 * included.
	 * @return The set of loot table locations known by JAOPCA
	 */
	public abstract Set<ResourceLocation> getLootTables();

	/**
	 * Returns the current {@link ILocalizer} based on Minecraft's current language. Will always return the
	 * default localizer on dedicated servers.
	 * @return The current localizer based on Minecraft's current language
	 */
	public abstract ILocalizer currentLocalizer();

	public abstract Map<String, String> currentMaterialLocalizationMap();

	/**
	 * Registers an {@link IFormType} to be used in custom form deserialization.
	 * @param type The form type to be registered
	 * @return true if the form type was successfully registered
	 */
	public abstract boolean registerFormType(IFormType type);

	public abstract void registerForgeRegistryEntry(IForgeRegistryEntry<?> entry);

	/**
	 * Registers a block tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedBlockTag(ResourceLocation key);

	/**
	 * Registers an item tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedItemTag(ResourceLocation key);

	/**
	 * Registers a fluid tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedFluidTag(ResourceLocation key);

	/**
	 * Registers an entity type tag location that may be added externally and should be known to JAOPCA.
	 * @param key The tag location that should be known by JAOPCA
	 * @return true if the tag location was not already defined
	 */
	public abstract boolean registerDefinedEntityTypeTag(ResourceLocation key);

	/**
	 * Registers a block location to be added to a tag by JAOPCA's in memory data pack. Locations that do
	 * not correspond to a block will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param blockKey The block location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerBlockTag(ResourceLocation key, ResourceLocation blockKey);

	/**
	 * Registers a block to be added to a tag by JAOPCA's in memory data pack. Null blocks will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param block The block to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerBlockTag(ResourceLocation key, Block block);

	/**
	 * Registers an item location to be added to a tag by JAOPCA's in memory data pack. Locations that do
	 * not correspond to an item will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param itemKey The item location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerItemTag(ResourceLocation key, ResourceLocation itemKey);

	/**
	 * Registers an item to be added to a tag by JAOPCA's in memory data pack. Null items will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param item The item to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerItemTag(ResourceLocation key, Item item);

	/**
	 * Registers a fluid location to be added to a tag by JAOPCA's in memory data pack. Locations that do
	 * not correspond to a fluid will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param fluidKey The fluid location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerFluidTag(ResourceLocation key, ResourceLocation fluidKey);

	/**
	 * Registers a fluid to be added to a tag by JAOPCA's in memory data pack. Null fluids will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param fluid The fluid to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerFluidTag(ResourceLocation key, Fluid fluid);

	/**
	 * Registers an entity type location to be added to a tag by JAOPCA's in memory data pack. Locations that
	 * do not correspond to an entity type will effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param entityTypeKey The entity type location to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerEntityTypeTag(ResourceLocation key, ResourceLocation entityTypeKey);

	/**
	 * Registers an entity type to be added to a tag by JAOPCA's in memory data pack. Null entity types will
	 * effectively be ignored by the data pack.
	 * @param key The location of the tag
	 * @param entityType The entity type to be added
	 * @return true if the tag location was not blacklisted in the configuration file
	 */
	public abstract boolean registerEntityTypeTag(ResourceLocation key, EntityType<?> entityType);

	public abstract void registerDefaultGemOverride(String materialName);

	public abstract void registerDefaultCrystalOverride(String materialName);

	public abstract void registerDefaultDustOverride(String materialName);

	/**
	 * Registers a recipe serializer to be injected by JAOPCA.
	 * @param key The id of the recipe
	 * @param recipeSerializer The recipe serializer
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerRecipe(ResourceLocation key, IRecipeSerializer recipeSerializer);

	/**
	 * Creates a shaped recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input The input of the recipe in the form of how one would specify inputs in Minecraft versions
	 * prior to 1.12, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapedRecipe(ResourceLocation key, String group, Object output, int count, Object... input);

	/**
	 * Creates a shaped recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input The input of the recipe in the form of how one would specify inputs in Minecraft versions
	 * prior to 1.12, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapedRecipe(ResourceLocation key, Object output, int count, Object... input);

	/**
	 * Creates a shapeless recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input An array of inputs for the recipe, see {@link IMiscHelper#getIngredient(Object)} for valid
	 * input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapelessRecipe(ResourceLocation key, String group, Object output, int count, Object... input);

	/**
	 * Creates a shapeless recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param input An array of inputs for the recipe, see {@link IMiscHelper#getIngredient(Object)} for valid
	 * input forms
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerShapelessRecipe(ResourceLocation key, Object output, int count, Object... input);

	/**
	 * Creates a furnace recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmeltingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a furnace recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmeltingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a blasting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerBlastingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a blasting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerBlastingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a smoking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmokingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a smoking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param experience The amount of experience the recipe gives
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerSmokingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time);

	/**
	 * Creates a campfire cooking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerCampfireCookingRecipe(ResourceLocation key, String group, Object input, Object output, int count, int time);

	/**
	 * Creates a campfire cooking recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @param time The time required for the recipe in ticks
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerCampfireCookingRecipe(ResourceLocation key, Object input, Object output, int count, int time);

	/**
	 * Creates a stonecutting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param group The identifier used to group recipes in the recipe book
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerStonecuttingRecipe(ResourceLocation key, String group, Object input, Object output, int count);

	/**
	 * Creates a stonecutting recipe supplier that is then registered for injection.
	 * @param key The id of the recipe
	 * @param input The input of he recipe, see {@link IMiscHelper#getIngredient(Object)} for valid input forms
	 * @param output The output of the recipe, see {@link IMiscHelper#getItemStack(Object, int)} for valid output forms
	 * @param count The size of the output
	 * @return true if the id of the recipe was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerStonecuttingRecipe(ResourceLocation key, Object input, Object output, int count);

	/**
	 * Registers a loot table supplier to be added by JAOPCA's in memory data pack.
	 * @param key The id of the loot table
	 * @param lootTableSupplier The loot table supplier
	 * @return true if the id of the loot table was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerLootTable(ResourceLocation key, Supplier<LootTable> lootTableSupplier);

	/**
	 * Registers a loot table to be added by JAOPCA's in memory data pack.
	 * @param key The id of the loot table
	 * @param lootTable The loot table
	 * @return true if the id of the loot table was not blacklisted in the configuration file and was not taken
	 */
	public abstract boolean registerLootTable(ResourceLocation key, LootTable lootTable);

	/**
	 * Registers an {@link ILocalizer} to languages for use by JAOPCA.
	 * @param localizer The localizer
	 * @param languages An array of language identifiers that the localizer should be used for
	 */
	public abstract void registerLocalizer(ILocalizer localizer, String... languages);
}
