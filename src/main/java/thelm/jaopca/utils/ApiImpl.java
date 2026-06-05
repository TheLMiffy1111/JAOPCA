package thelm.jaopca.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.blocks.IBlockLike;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.entities.IEntityTypeFormType;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.localization.ILocalizer;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.Form;
import thelm.jaopca.forms.FormHandler;
import thelm.jaopca.forms.FormRequest;
import thelm.jaopca.forms.FormTypeHandler;
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
	public IDynamicSpecConfig getMaterialConfig(IMaterial material) {
		return ConfigHandler.MATERIAL_CONFIGS.get(material);
	}

	@Override
	public Set<IMaterial> getMaterials() {
		return ImmutableSortedSet.copyOf(MaterialHandler.getMaterials());
	}

	@Override
	public Set<Identifier> getTags(ResourceKey<? extends Registry<?>> registry) {
		return Sets.union(DataCollector.getDefinedTags(registry), DataInjector.getInjectTags(registry));
	}

	@Override
	public Set<Identifier> getBlockTags() {
		return getTags(Registries.BLOCK);
	}

	@Override
	public Set<Identifier> getItemTags() {
		return getTags(Registries.ITEM);
	}

	@Override
	public Set<Identifier> getFluidTags() {
		return getTags(Registries.FLUID);
	}

	@Override
	public Set<Identifier> getEntityTypeTags() {
		return getTags(Registries.ENTITY_TYPE);
	}

	@Override
	public Set<Identifier> getTags(String type) {
		return Collections.unmodifiableSet(DataCollector.getDefinedTags(type));
	}

	@Override
	public Set<Identifier> getRecipes() {
		return Sets.union(DataCollector.getDefinedRecipes(), DataInjector.getInjectRecipes());
	}

	@Override
	public Set<Identifier> getLootTables() {
		return Sets.union(DataCollector.getDefinedLootTables(), DataInjector.getInjectLootTables());
	}

	@Override
	public Set<Identifier> getAdvancements() {
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
		return null;
	}

	@Override
	public boolean registerFormType(IFormType type) {
		return FormTypeHandler.registerFormType(type);
	}

	@Override
	public <T, I extends T> DeferredHolder<T, I> registerRegistryEntry(ResourceKey<? extends Registry<T>> registry, String name, Supplier<I> entry) {
		return RegistryHandler.registerRegistryEntry(registry, name, entry);
	}

	@Override
	public <T, I extends T> DeferredHolder<T, I> registerRegistryEntry(Identifier registry, String name, Supplier<I> entry) {
		return RegistryHandler.registerRegistryEntry(registry, name, entry);
	}

	@Override
	public boolean registerDefinedTag(ResourceKey<? extends Registry<?>> registry, Identifier key) {
		return DataCollector.getDefinedTags(registry).add(key);
	}

	@Override
	public boolean registerDefinedBlockTag(Identifier key) {
		return registerDefinedTag(Registries.BLOCK, key);
	}

	@Override
	public boolean registerDefinedItemTag(Identifier key) {
		return registerDefinedTag(Registries.ITEM, key);
	}

	@Override
	public boolean registerDefinedFluidTag(Identifier key) {
		return registerDefinedTag(Registries.FLUID, key);
	}

	@Override
	public boolean registerDefinedEntityTypeTag(Identifier key) {
		return registerDefinedTag(Registries.ENTITY_TYPE, key);
	}

	@Override
	public boolean registerReloadInjector(Class<?> clazz, Consumer<Object> injector) {
		return DataInjector.registerReloadInjector(clazz, injector);
	}

	@Override
	public boolean registerTag(ResourceKey<? extends Registry<?>> registry, Identifier key, Supplier<Identifier> objKey) {
		return DataInjector.registerTag(registry, key, objKey);
	}

	@Override
	public boolean registerTag(ResourceKey<? extends Registry<?>> registry, Identifier key, Identifier objKey) {
		return registerTag(registry, key, ()->objKey);
	}

	@Override
	public <T> boolean registerTag(ResourceKey<? extends Registry<T>> registry, Identifier key, T obj) {
		return registerTag(registry, key, (Supplier<Identifier>)()->((Registry<T>)BuiltInRegistries.REGISTRY.getValue(registry.identifier())).getKey(obj));
	}

	@Override
	public boolean registerBlockTag(Identifier key, Supplier<Identifier> blockKey) {
		if(ConfigHandler.BLOCK_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registries.BLOCK, key, blockKey);
	}

	@Override
	public boolean registerBlockTag(Identifier key, Identifier blockKey) {
		return registerBlockTag(key, ()->blockKey);
	}

	@Override
	public boolean registerBlockTag(Identifier key, Block block) {
		return registerBlockTag(key, ()->BuiltInRegistries.BLOCK.getKey(block));
	}

	@Override
	public boolean registerBlockTag(Identifier key, IBlockLike block) {
		return registerBlockTag(key, ()->BuiltInRegistries.BLOCK.getKey(block.asBlock()));
	}

	@Override
	public boolean registerItemTag(Identifier key, Supplier<Identifier> itemKey) {
		if(ConfigHandler.ITEM_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registries.ITEM, key, itemKey);
	}

	@Override
	public boolean registerItemTag(Identifier key, Identifier itemKey) {
		return registerItemTag(key, ()->itemKey);
	}

	@Override
	public boolean registerItemTag(Identifier key, Item item) {
		return registerItemTag(key, ()->BuiltInRegistries.ITEM.getKey(item));
	}

	@Override
	public boolean registerItemTag(Identifier key, ItemLike item) {
		return registerItemTag(key, ()->BuiltInRegistries.ITEM.getKey(item.asItem()));
	}

	@Override
	public boolean registerFluidTag(Identifier key, Supplier<Identifier> fluidKey) {
		if(ConfigHandler.FLUID_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registries.FLUID, key, fluidKey);
	}

	@Override
	public boolean registerFluidTag(Identifier key, Identifier fluidKey) {
		return registerFluidTag(key, ()->fluidKey);
	}

	@Override
	public boolean registerFluidTag(Identifier key, Fluid fluid) {
		return registerFluidTag(key, ()->BuiltInRegistries.FLUID.getKey(fluid));
	}

	@Override
	public boolean registerFluidTag(Identifier key, IFluidLike fluid) {
		return registerFluidTag(key, ()->BuiltInRegistries.FLUID.getKey(fluid.asFluid()));
	}

	@Override
	public boolean registerEntityTypeTag(Identifier key, Supplier<Identifier> entityTypeKey) {
		if(ConfigHandler.ENTITY_TYPE_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag(Registries.ENTITY_TYPE, key, entityTypeKey);
	}

	@Override
	public boolean registerEntityTypeTag(Identifier key, Identifier entityTypeKey) {
		return registerEntityTypeTag(key, ()->entityTypeKey);
	}

	@Override
	public boolean registerEntityTypeTag(Identifier key, EntityType<?> entityType) {
		return registerEntityTypeTag(key, ()->BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
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
	public boolean registerRecipe(Identifier key, IRecipeSerializer recipeSerializer) {
		if(DataCollector.getDefinedRecipes().contains(key) || ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		return DataInjector.registerRecipe(key, recipeSerializer);
	}

	@Override
	public boolean registerShapedRecipe(Identifier key, String group, CraftingBookCategory category, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSerializer(key, group, category, output, count, input));
	}

	@Override
	public boolean registerShapedRecipe(Identifier key, CraftingBookCategory category, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSerializer(key, category, output, count, input));
	}

	@Override
	public boolean registerShapedRecipe(Identifier key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSerializer(key, group, output, count, input));
	}

	@Override
	public boolean registerShapedRecipe(Identifier key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSerializer(key, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(Identifier key, String group, CraftingBookCategory category, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSerializer(key, group, category, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(Identifier key, CraftingBookCategory category, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSerializer(key, category, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(Identifier key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSerializer(key, group, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(Identifier key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSerializer(key, output, count, input));
	}

	@Override
	public boolean registerSmeltingRecipe(Identifier key, String group, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSerializer(key, group, category, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmeltingRecipe(Identifier key, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSerializer(key, category, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmeltingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSerializer(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmeltingRecipe(Identifier key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSerializer(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(Identifier key, String group, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSerializer(key, group, category, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(Identifier key, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSerializer(key, category, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSerializer(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(Identifier key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSerializer(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(Identifier key, String group, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSerializer(key, group, category, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(Identifier key, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSerializer(key, category, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(Identifier key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSerializer(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(Identifier key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSerializer(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(Identifier key, String group, CookingBookCategory category, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSerializer(key, group, category, input, output, count, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(Identifier key, CookingBookCategory category, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSerializer(key, category, input, output, count, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(Identifier key, String group, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSerializer(key, group, input, output, count, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(Identifier key, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSerializer(key, input, output, count, time));
	}

	@Override
	public boolean registerStonecuttingRecipe(Identifier key, String group, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSerializer(key, group, input, output, count));
	}

	@Override
	public boolean registerStonecuttingRecipe(Identifier key, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSerializer(key, input, output, count));
	}

	@Override
	public boolean registerSmithingRecipe(Identifier key, Object template, Object base, Object addition, Object output, int count) {
		return registerRecipe(key, new SmithingRecipeSerializer(key, template, base, addition, output, count));
	}

	@Override
	public boolean registerLootTable(Identifier key, Supplier<LootTable> lootTableSupplier) {
		if(DataCollector.getDefinedLootTables().contains(key) || ConfigHandler.LOOT_TABLE_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerLootTable(key, lootTableSupplier);
	}

	@Override
	public boolean registerLootTable(Identifier key, LootTable lootTable) {
		return registerLootTable(key, ()->lootTable);
	}

	@Override
	public boolean registerAdvancement(Identifier key, Supplier<Builder> advancementBuilderSupplier) {
		if(DataCollector.getDefinedAdvancements().contains(key) || ConfigHandler.ADVANCEMENT_BLACKLIST.contains(key)) {
			return false;
		}
		return DataInjector.registerAdvancement(key, advancementBuilderSupplier);
	}

	@Override
	public boolean registerAdvancement(Identifier key, Advancement.Builder advancementBuilder) {
		return registerAdvancement(key, ()->advancementBuilder);
	}

	@Override
	public <T> boolean registerDataMapEntry(DataMapType<?, T> type, ExtraCodecs.TagOrElementLocation location, Supplier<T> valueSupplier) {
		return DataInjector.registerDataMapEntry(type, location, valueSupplier);
	}

	@Override
	public <T> boolean registerDataMapEntry(DataMapType<?, T> type, Identifier tagLocation, Supplier<T> valueSupplier) {
		return registerDataMapEntry(type, new ExtraCodecs.TagOrElementLocation(tagLocation, true), valueSupplier);
	}

	@Override
	public void registerLocalizer(ILocalizer translator, String... languages) {
		LocalizationHandler.registerLocalizer(translator, languages);
	}
}
