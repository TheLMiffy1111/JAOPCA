package thelm.jaopca.utils;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializer;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
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
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.localization.LocalizationHandler;
import thelm.jaopca.localization.LocalizationRepoHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.recipes.BlastingRecipeSupplier;
import thelm.jaopca.recipes.CampfireCookingRecipeSupplier;
import thelm.jaopca.recipes.ShapedRecipeSupplier;
import thelm.jaopca.recipes.ShapelessRecipeSupplier;
import thelm.jaopca.recipes.SmeltingRecipeSupplier;
import thelm.jaopca.recipes.SmithingRecipeSupplier;
import thelm.jaopca.recipes.SmokingRecipeSupplier;
import thelm.jaopca.recipes.StonecuttingRecipeSupplier;
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
	public JsonDeserializer<Supplier<IForgeRegistryEntry<?>>> forgeRegistryEntrySupplierDeserializer() {
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
	public ItemGroup itemGroup() {
		return ItemFormType.getItemGroup();
	}

	@Override
	public Set<ResourceLocation> getTags(String type) {
		return Sets.union(DataCollector.getDefinedTags(type), DataInjector.getInjectTags(type));
	}

	@Override
	public Set<ResourceLocation> getBlockTags() {
		return getTags("blocks");
	}

	@Override
	public Set<ResourceLocation> getItemTags() {
		return getTags("items");
	}

	@Override
	public Set<ResourceLocation> getFluidTags() {
		return getTags("fluids");
	}

	@Override
	public Set<ResourceLocation> getEntityTypeTags() {
		return getTags("entity_types");
	}

	@Override
	public Set<ResourceLocation> getRecipes() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedRecipes(), DataInjector.getInjectRecipes()));
	}

	@Override
	public Set<ResourceLocation> getLootTables() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedLootTables(), DataInjector.getInjectLootTables()));
	}

	@Override
	public Set<ResourceLocation> getAdvancements() {
		return ImmutableSortedSet.copyOf(Sets.union(DataCollector.getDefinedAdvancements(), DataInjector.getInjectAdvancements()));
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
	public boolean registerFormType(IFormType type) {
		return FormTypeHandler.registerFormType(type);
	}

	@Override
	public void registerForgeRegistryEntry(IForgeRegistryEntry<?> entry) {
		RegistryHandler.registerForgeRegistryEntry(entry);
	}

	@Override
	public boolean registerDefinedTag(String type, ResourceLocation key) {
		return DataCollector.getDefinedTags(type).add(key);
	}

	@Override
	public boolean registerDefinedBlockTag(ResourceLocation key) {
		return registerDefinedTag("blocks", key);
	}

	@Override
	public boolean registerDefinedItemTag(ResourceLocation key) {
		return registerDefinedTag("items", key);
	}

	@Override
	public boolean registerDefinedFluidTag(ResourceLocation key) {
		return registerDefinedTag("fluids", key);
	}

	@Override
	public boolean registerDefinedEntityTypeTag(ResourceLocation key) {
		return registerDefinedTag("entity_types", key);
	}

	@Override
	public boolean registerTag(String type, ResourceLocation key, ResourceLocation objKey) {
		return DataInjector.registerTag(type, key, objKey);
	}

	@Override
	public boolean registerTag(String type, ResourceLocation key, IForgeRegistryEntry<?> obj) {
		return registerTag(type, key, obj.getRegistryName());
	}

	@Override
	public boolean registerBlockTag(ResourceLocation key, ResourceLocation blockKey) {
		if(ConfigHandler.BLOCK_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag("blocks", key, blockKey);
	}

	@Override
	public boolean registerBlockTag(ResourceLocation key, Block block) {
		return registerBlockTag(key, block.getRegistryName());
	}

	@Override
	public boolean registerItemTag(ResourceLocation key, ResourceLocation itemKey) {
		if(ConfigHandler.ITEM_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag("items", key, itemKey);
	}

	@Override
	public boolean registerItemTag(ResourceLocation key, Item item) {
		return registerItemTag(key, item.getRegistryName());
	}

	@Override
	public boolean registerFluidTag(ResourceLocation key, ResourceLocation fluidKey) {
		if(ConfigHandler.FLUID_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag("fluids", key, fluidKey);
	}

	@Override
	public boolean registerFluidTag(ResourceLocation key, Fluid fluid) {
		return registerFluidTag(key, fluid.getRegistryName());
	}

	@Override
	public boolean registerEntityTypeTag(ResourceLocation key, ResourceLocation entityTypeKey) {
		if(ConfigHandler.ENTITY_TYPE_TAG_BLACKLIST.contains(key)) {
			return false;
		}
		return registerTag("entity_types", key, entityTypeKey);
	}

	@Override
	public boolean registerEntityTypeTag(ResourceLocation key, EntityType<?> entityType) {
		return registerEntityTypeTag(key, entityType.getRegistryName());
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
	public boolean registerRecipe(ResourceLocation key, Supplier<? extends IRecipe<?>> recipeSupplier) {
		if(DataCollector.getDefinedRecipes().contains(key) || ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		return DataInjector.registerRecipe(key, recipeSupplier);
	}

	@Override
	public boolean registerRecipe(ResourceLocation key, IRecipe<?> recipe) {
		return registerRecipe(key, ()->recipe);
	}

	@Override
	public boolean registerShapedRecipe(ResourceLocation key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSupplier(key, group, output, count, input));
	}

	@Override
	public boolean registerShapedRecipe(ResourceLocation key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapedRecipeSupplier(key, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(ResourceLocation key, String group, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSupplier(key, group, output, count, input));
	}

	@Override
	public boolean registerShapelessRecipe(ResourceLocation key, Object output, int count, Object... input) {
		return registerRecipe(key, new ShapelessRecipeSupplier(key, output, count, input));
	}

	@Override
	public boolean registerSmeltingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmeltingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmeltingRecipeSupplier(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerBlastingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new BlastingRecipeSupplier(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSupplier(key, group, input, output, count, experience, time));
	}

	@Override
	public boolean registerSmokingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		return registerRecipe(key, new SmokingRecipeSupplier(key, input, output, count, experience, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(ResourceLocation key, String group, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSupplier(key, group, input, output, count, time));
	}

	@Override
	public boolean registerCampfireCookingRecipe(ResourceLocation key, Object input, Object output, int count, int time) {
		return registerRecipe(key, new CampfireCookingRecipeSupplier(key, input, output, count, time));
	}

	@Override
	public boolean registerStonecuttingRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSupplier(key, group, input, output, count));
	}

	@Override
	public boolean registerStonecuttingRecipe(ResourceLocation key, Object input, Object output, int count) {
		return registerRecipe(key, new StonecuttingRecipeSupplier(key, input, output, count));
	}

	@Override
	public boolean registerSmithingRecipe(ResourceLocation key, Object base, Object addition, Object output, int count) {
		return registerRecipe(key, new SmithingRecipeSupplier(key, base, addition, output, count));
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
