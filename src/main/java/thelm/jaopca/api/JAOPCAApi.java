package thelm.jaopca.api;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializer;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
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
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;

public abstract class JAOPCAApi {

	private static JAOPCAApi instance;

	public static void setInstance(JAOPCAApi api) {
		if(instance == null) {
			instance = api;
		}
	}

	public static JAOPCAApi instance() {
		if(instance == null) {
			throw new IllegalStateException("Got API instance before it is initialized");
		}
		return instance;
	}

	public abstract IBlockFormType blockFormType();

	public abstract IItemFormType itemFormType();

	public abstract IFluidFormType fluidFormType();

	public abstract IEntityTypeFormType entityTypeFormType();

	public abstract <I extends IMaterialFormInfo<?>> IFormType<I> getFormType(String name);

	public abstract IForm newForm(IModule module, String name, IFormType<?> type);

	public abstract IFormRequest newFormRequest(IModule module, IForm... forms);

	public abstract IMiscHelper miscHelper();

	public abstract IJsonHelper jsonHelper();

	public abstract JsonDeserializer<Enum<?>> enumDeserializer();

	public abstract JsonDeserializer<Function<IMaterial, Enum<?>>> materialEnumFunctionDeserializer();

	public abstract <T> JsonDeserializer<Function<IMaterial, T>> materialMappedFunctionDeserializer(Function<String, T> stringToValue, Function<T, String> valueToString);

	public abstract JsonDeserializer<Function<IMaterial, ?>> materialFunctionDeserializer();

	public abstract IForm getForm(String name);

	public abstract IMaterial getMaterial(String name);

	public abstract ItemGroup itemGroup();

	public abstract Set<ResourceLocation> getBlockTags();

	public abstract Set<ResourceLocation> getItemTags();

	public abstract Set<ResourceLocation> getFluidTags();

	public abstract Set<ResourceLocation> getEntityTypeTags();

	public abstract Set<ResourceLocation> getRecipes();

	public abstract Set<ResourceLocation> getAdvancements();

	public abstract ILocalizer currentLocalizer();

	public abstract <T extends IFormType<?>> T registerFormType(T type);

	public abstract boolean registerDefinedBlockTag(ResourceLocation key);

	public abstract boolean registerDefinedItemTag(ResourceLocation key);

	public abstract boolean registerDefinedFluidTag(ResourceLocation key);

	public abstract boolean registerDefinedEntityTypeTag(ResourceLocation key);

	public abstract boolean registerBlockTag(ResourceLocation key, Supplier<? extends Block> blockSupplier);

	public abstract boolean registerBlockTag(ResourceLocation key, Block block);

	public abstract boolean registerBlockTag(ResourceLocation key, ResourceLocation blockKey);

	public abstract boolean registerItemTag(ResourceLocation key, Supplier<? extends Item> itemSupplier);

	public abstract boolean registerItemTag(ResourceLocation key, Item item);

	public abstract boolean registerItemTag(ResourceLocation key, ResourceLocation itemKey);

	public abstract boolean registerFluidTag(ResourceLocation key, Supplier<? extends Fluid> fluidSupplier);

	public abstract boolean registerFluidTag(ResourceLocation key, Fluid fluid);

	public abstract boolean registerFluidTag(ResourceLocation key, ResourceLocation fluidKey);

	public abstract boolean registerEntityTypeTag(ResourceLocation key, Supplier<? extends EntityType<?>> entityTypeSupplier);

	public abstract boolean registerEntityTypeTag(ResourceLocation key, EntityType<?> entityType);

	public abstract boolean registerEntityTypeTag(ResourceLocation key, ResourceLocation entityTypeKey);

	public abstract boolean registerRecipe(ResourceLocation key, Supplier<? extends IRecipe<?>> recipeSupplier);

	public abstract boolean registerRecipe(ResourceLocation key, IRecipe<?> recipe);

	public abstract boolean registerShapedRecipe(ResourceLocation key, String group, Object output, int count, Object... input);

	public abstract boolean registerShapedRecipe(ResourceLocation key, Object output, int count, Object... input);

	public abstract boolean registerShapelessRecipe(ResourceLocation key, String group, Object output, int count, Object... input);

	public abstract boolean registerShapelessRecipe(ResourceLocation key, Object output, int count, Object... input);

	public abstract boolean registerFurnaceRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerFurnaceRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerBlastingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerBlastingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerSmokingRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerSmokingRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerCampfireCookingRecipe(ResourceLocation key, String group, Object input, Object output, int count, int time);

	public abstract boolean registerCampfireCookingRecipe(ResourceLocation key, Object input, Object output, int count, int time);

	public abstract boolean registerStonecuttingRecipe(ResourceLocation key, String group, Object input, Object output, int count);

	public abstract boolean registerStonecuttingRecipe(ResourceLocation key, Object input, Object output, int count);

	public abstract boolean registerAdvancement(ResourceLocation key, Advancement.Builder advancementBuilder);

	public abstract void registerLocalizer(ILocalizer localizer, String... languages);
}
