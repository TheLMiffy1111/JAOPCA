package thelm.jaopca.api;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.forms.IFormType;
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

	public abstract <I extends IMaterialFormInfo<?>> IFormType<I> getFormType(String name);

	public abstract IForm newForm(IModule module, String name, IFormType<?> type);

	public abstract IFormRequest newFormRequest(IModule module, IForm... forms);

	public abstract IMiscHelper miscHelper();

	public abstract IForm getForm(String name);

	public abstract IMaterial getMaterial(String name);

	public abstract ItemGroup itemGroup();

	public abstract Set<ResourceLocation> getBlockTags();

	public abstract Set<ResourceLocation> getItemTags();

	public abstract Set<ResourceLocation> getFluidTags();

	public abstract Set<ResourceLocation> getRecipes();

	public abstract Set<ResourceLocation> getAdvancements();

	public abstract ILocalizer currentLocalizer();

	public abstract <T extends IFormType<?>> T registerFormType(T type);

	public abstract boolean registerBlockTag(ResourceLocation key, Supplier<Block> blockSupplier);

	public abstract boolean registerBlockTag(ResourceLocation key, Block block);

	public abstract boolean registerBlockTag(ResourceLocation key, ResourceLocation blockKey);

	public abstract boolean registerItemTag(ResourceLocation key, Supplier<Item> itemSupplier);

	public abstract boolean registerItemTag(ResourceLocation key, Item item);

	public abstract boolean registerItemTag(ResourceLocation key, ResourceLocation itemKey);

	public abstract boolean registerFluidTag(ResourceLocation key, Supplier<Fluid> fluidSupplier);

	public abstract boolean registerFluidTag(ResourceLocation key, Fluid fluid);

	public abstract boolean registerFluidTag(ResourceLocation key, ResourceLocation fluidKey);

	public abstract boolean registerRecipe(ResourceLocation key, Supplier<IRecipe> recipeSupplier);

	public abstract boolean registerRecipe(ResourceLocation key, IRecipe recipe);

	public abstract boolean registerShapedRecipe(ResourceLocation key, String group, Object output, int count, Object... input);

	public abstract boolean registerShapedRecipe(ResourceLocation key, Object output, int count, Object... input);

	public abstract boolean registerShapelessRecipe(ResourceLocation key, String group, Object output, int count, Object... input);

	public abstract boolean registerShapelessRecipe(ResourceLocation key, Object output, int count, Object... input);

	public abstract boolean registerFurnaceRecipe(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerFurnaceRecipe(ResourceLocation key, Object input, Object output, int count, float experience, int time);

	public abstract boolean registerAdvancement(ResourceLocation key, Advancement.Builder advancementBuilder);

	public abstract void registerLocalizer(ILocalizer localizer, String... languages);
}
