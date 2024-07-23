package thelm.jaopca.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializer;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;
import thelm.jaopca.api.JAOPCAApi;
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
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.ForgeRegistryEntrySupplierDeserializer;
import thelm.jaopca.custom.json.MaterialEnumFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialFunctionDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.Form;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormRequest;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.localization.LocalizationHandler;
import thelm.jaopca.localization.LocalizationRepoHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.recipes.BlastingRecipeSerializer;
import thelm.jaopca.recipes.CampfireCookingRecipeSerializer;
import thelm.jaopca.recipes.ShapedRecipeSerializer;
import thelm.jaopca.recipes.ShapelessRecipeSerializer;
import thelm.jaopca.recipes.SmeltingRecipeSerializer;
import thelm.jaopca.recipes.SmithingRecipeSerializer;
import thelm.jaopca.recipes.SmokingRecipeSerializer;
import thelm.jaopca.recipes.StonecuttingRecipeSerializer;
import thelm.jaopca.registries.RegistryHandler;

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
	public IEntityTypeFormType entityTypeFormType() {
		// TODO Auto-generated method stub
		return null;
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
	public JsonDeserializer<Supplier<?>> forgeRegistryEntrySupplierDeserializer() {
		return ForgeRegistryEntrySupplierDeserializer.INSTANCE;
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
	public CreativeModeTab creativeTab() {
		return ItemFormType.getCreativeTab();
	}

	@Override
	public Set<ResourceLocation> getTags(ResourceKey<? extends Registry<?>> registry) {
		return Sets.union(DataCollector.getDefinedTags(registry), DataInjector.getInjectTags(registry));
	}

	@Override
	public Set<ResourceLocation> getBlockTags() {
		return getTags(Registry.BLOCK_REGISTRY);
	}

	@Override
	public Set<ResourceLocation> getItemTags() {
		return getTags(Registry.ITEM_REGISTRY);
	}

	@Override
	public Set<ResourceLocation> getFluidTags() {
		return getTags(Registry.FLUID_REGISTRY);
	}

	@Override
	public Set<ResourceLocation> getEntityTypeTags() {
		return getTags(Registry.ENTITY_TYPE_REGISTRY);
	}

	@Override
	public Set<ResourceLocation> getTags(String type) {
		return Collections.unmodifiableSet(DataCollector.getDefinedTags(type));
	}

	@Override
	public Set<ResourceLocation> getRecipes() {
		return Sets.union(DataCollector.getDefinedRecipes(), DataInjector.getInjectRecipes());
	}

	@Override
	public Set<ResourceLocation> getLootTables() {
		return Sets.union(DataCollector.getDefinedLootTables(), DataInjector.getInjectLootTables());
	}

	@Override
	public Set<ResourceLocation> getAdvancements() {
		return Sets.union(DataCollector.getDefinedAdvancements(), DataInjector.getInjectAdvancements());
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
	public Ingredient emptyIngredient() {
		return EmptyIngredient.INSTANCE;
	}

	@Override
	public boolean registerFormType(IFormType type) {
		return FormTypeHandler.registerFormType(type);
	}

	@Override
	public <T, I extends T> RegistryObject<I> registerForgeRegistryEntry(ResourceKey<? extends Registry<T>> registry, String name, Supplier<I> entry) {
		return RegistryHandler.registerForgeRegistryEntry(registry, name, entry);
	}

	@Override
	public <T, I extends T> RegistryObject<I> registerForgeRegistryEntry(ResourceLocation registry, String name, Supplier<I> entry) {
		return RegistryHandler.registerForgeRegistryEntry(registry, name, entry);
	}

	@Override
	public boolean registerDefinedTag(ResourceKey<? extends Registry<?>> registry, ResourceLocation key) {
		return DataCollector.getDefinedTags(registry).add(key);
	}

	@Override
	public boolean registerDefinedBlockTag(ResourceLocation key) {
		return registerDefinedTag(Registry.BLOCK_REGISTRY, key);
	}

	@Override
	public boolean registerDefinedItemTag(ResourceLocation key) {
		return registerDefinedTag(Registry.ITEM_REGISTRY, key);
	}

	@Override
	public boolean registerDefinedFluidTag(ResourceLocation key) {
		return registerDefinedTag(Registry.FLUID_REGISTRY, key);
	}

	@Override
	public boolean registerDefinedEntityTypeTag(ResourceLocation key) {
		return registerDefinedTag(Registry.ENTITY_TYPE_REGISTRY, key);
	}

	@Override
	public boolean registerReloadInjector(Class<?> clazz, Consumer<Object> injector) {
		return DataInjector.registerReloadInjector(clazz, injector);
	}

	@Override
	public boolean registerTag(ResourceKey<? extends Registry<?>> registry, ResourceLocation key, ResourceLocation objKey) {
		return DataInjector.registerTag(registry, key, objKey);
	}

	@Override
	public <T> boolean registerTag(ResourceKey<? extends Registry<T>> registry, ResourceLocation key, T obj) {
		return registerTag(registry, key, RegistryManager.ACTIVE.getRegistry(registry).getKey(obj));
	}

	@Override
	public boolean registerBlockTag(ResourceLocation key, ResourceLocation blockKey) {
		if(ConfigHandler.BLOCK_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registry.BLOCK_REGISTRY, key, blockKey);
	}

	@Override
	public boolean registerBlockTag(ResourceLocation key, Block block) {
		return registerBlockTag(key, ForgeRegistries.BLOCKS.getKey(block));
	}

	@Override
	public boolean registerItemTag(ResourceLocation key, ResourceLocation itemKey) {
		if(ConfigHandler.ITEM_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registry.ITEM_REGISTRY, key, itemKey);
	}

	@Override
	public boolean registerItemTag(ResourceLocation key, Item item) {
		return registerItemTag(key, ForgeRegistries.ITEMS.getKey(item));
	}

	@Override
	public boolean registerFluidTag(ResourceLocation key, ResourceLocation fluidKey) {
		if(ConfigHandler.FLUID_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registry.FLUID_REGISTRY, key, fluidKey);
	}

	@Override
	public boolean registerFluidTag(ResourceLocation key, Fluid fluid) {
		return registerFluidTag(key, ForgeRegistries.FLUIDS.getKey(fluid));
	}

	@Override
	public boolean registerEntityTypeTag(ResourceLocation key, ResourceLocation entityTypeKey) {
		if(ConfigHandler.ENTITY_TYPE_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registry.ENTITY_TYPE_REGISTRY, key, entityTypeKey);
	}

	@Override
	public boolean registerEntityTypeTag(ResourceLocation key, EntityType<?> entityType) {
		return registerEntityTypeTag(key, ForgeRegistries.ENTITY_TYPES.getKey(entityType));
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
	public boolean registerRecipe(ResourceLocation key, IRecipeSerializer recipeSerializer) {
		if(DataCollector.getDefinedRecipes().contains(key) || ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		return DataInjector.registerRecipe(key, recipeSerializer);
	}

	@Override
	public boolean registerShapedRecipe(ResourceLocation key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSerializer(key, group, output, count, input));
	}

	@Override
	public boolean registerShapedRecipe(ResourceLocation key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSerializer(key, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(ResourceLocation key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSerializer(key, group, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(ResourceLocation key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSerializer(key, output, count, input));
	}

	@Override
	public boolean registerSmeltingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSerializer(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmeltingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSerializer(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSerializer(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSerializer(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSerializer(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSerializer(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(ResourceLocation key, String group, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSerializer(key, group, input, output, count, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(ResourceLocation key, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSerializer(key, input, output, count, time));
	}

	@Override
	public boolean registerStonecuttingRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSerializer(key, group, input, output, count));
	}

	@Override
	public boolean registerStonecuttingRecipe(ResourceLocation key, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSerializer(key, input, output, count));
	}

	@Override
	public boolean registerSmithingRecipe(ResourceLocation key, Object base, Object addition, Object output, int count) {
		return registerRecipe(key, new SmithingRecipeSerializer(key, base, addition, output, count));
	}

	@Override
	public boolean registerLootTable(ResourceLocation key, Supplier<LootTable> lootTableSupplier) {
		if(DataCollector.getDefinedLootTables().contains(key) || ConfigHandler.LOOT_TABLE_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerLootTable(key, lootTableSupplier);
	}

	@Override
	public boolean registerLootTable(ResourceLocation key, LootTable lootTable) {
		return registerLootTable(key, ()->lootTable);
	}

	@Override
	public boolean registerAdvancement(ResourceLocation key, Supplier<Builder> advancementBuilderSupplier) {
		if(DataCollector.getDefinedAdvancements().contains(key) || ConfigHandler.ADVANCEMENT_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerAdvancement(key, advancementBuilderSupplier);
	}

	@Override
	public boolean registerAdvancement(ResourceLocation key, Advancement.Builder advancementBuilder) {
		return registerAdvancement(key, ()->advancementBuilder);
	}

	@Override
	public void registerLocalizer(ILocalizer translator, String... languages) {
		LocalizationHandler.registerLocalizer(translator, languages);
	}
}
