package thelm.jaopca.blocks;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.common.base.Strings;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeBasedTable;
import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class BlockFormType implements IBlockFormType {

	private BlockFormType() {}

	public static final BlockFormType INSTANCE = new BlockFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormBlock>> BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormBlockItem>> BLOCK_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IBlockInfo> BLOCK_INFOS = TreeBasedTable.create();
	private static boolean registered = false;

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "block";
	}

	@Override
	public void addForm(IForm form) {
		FORMS.add(form);
	}

	@Override
	public Set<IForm> getForms() {
		return Collections.unmodifiableNavigableSet(FORMS);
	}

	@Override
	public boolean shouldRegister(IForm form, IMaterial material) {
		ResourceLocation tagLocation = MiscHelper.INSTANCE.getTagLocation(form.getSecondaryName(), material.getName(), form.getTagSeparator());
		return !ApiImpl.INSTANCE.getItemTags().contains(tagLocation);
	}

	@Override
	public IBlockInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IBlockInfo info = BLOCK_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new BlockInfo(BLOCKS.get(form, material).get(), BLOCK_ITEMS.get(form, material).get());
			BLOCK_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public IBlockFormSettings getNewSettings() {
		return new BlockFormSettings();
	}

	@Override
	public Codec<IFormSettings> formSettingsCodec() {
		return BlockCustomCodecs.BLOCK_FORM_SETTINGS;
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IBlockFormSettings settings = (IBlockFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			String tagSeparator = form.getTagSeparator();
			for(IMaterial material : form.getMaterials()) {
				String name = form.getName()+'.'+material.getName();
				ResourceLocation registryName = ResourceLocation.fromNamespaceAndPath("jaopca", name);
				ResourceLocation lootLocation = ResourceLocation.fromNamespaceAndPath("jaopca", "blocks/"+name);
				String toolTag = settings.getHarvestToolTagFunction().apply(material);
				String tierTag = settings.getHarvestTierTagFunction().apply(material);

				Supplier<IMaterialFormBlock> materialFormBlock = MemoizingSuppliers.of(()->settings.getBlockCreator().create(form, material, settings));
				BLOCKS.put(form, material, materialFormBlock);
				api.registerRegistryEntry(Registries.BLOCK, name, ()->materialFormBlock.get().toBlock());

				Supplier<IMaterialFormBlockItem> materialFormBlockItem = MemoizingSuppliers.of(()->settings.getBlockItemCreator().create(materialFormBlock.get(), settings));
				BLOCK_ITEMS.put(form, material, materialFormBlockItem);
				api.registerRegistryEntry(Registries.ITEM, name, ()->materialFormBlockItem.get().toBlockItem());

				api.registerLootTable(lootLocation, ()->settings.getBlockLootTableCreator().create(materialFormBlock.get(), settings));

				api.registerBlockTag(helper.createResourceLocation(secondaryName), registryName);
				api.registerBlockTag(helper.getTagLocation(secondaryName, material.getName(), tagSeparator), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					api.registerBlockTag(helper.getTagLocation(secondaryName, alternativeName, tagSeparator), registryName);
				}

				api.registerBlockTag(ResourceLocation.parse(toolTag), registryName);
				if(!Strings.isNullOrEmpty(tierTag)) {
					api.registerBlockTag(ResourceLocation.parse(tierTag), registryName);
				}

				api.registerItemTag(helper.createResourceLocation(secondaryName), registryName);
				api.registerItemTag(helper.getTagLocation(secondaryName, material.getName(), tagSeparator), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					api.registerItemTag(helper.getTagLocation(secondaryName, alternativeName, tagSeparator), registryName);
				}
			}
		}
	}

	@Override
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		getBlocks().forEach(mf->mf.onRegisterCapabilities(event));
		getBlockItems().forEach(mf->mf.onRegisterCapabilities(event));
	}

	@Override
	public void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		getBlockItems().forEach(mf->mf.addToCreativeModeTab(parameters, output));
	}

	@Override
	public void addBlockModelRemaps(Set<ResourceLocation> allLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {
		getBlocks().forEach(mf->mf.addBlockModelRemaps(allLocations, output));
	}

	@Override
	public void addItemModelRemaps(Set<ResourceLocation> allLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {
		getBlockItems().forEach(mf->mf.addItemModelRemaps(allLocations, output));
	}

	public static Collection<IMaterialFormBlock> getBlocks() {
		return Tables.transformValues(BLOCKS, Supplier::get).values();
	}

	public static Collection<IMaterialFormBlockItem> getBlockItems() {
		return Tables.transformValues(BLOCK_ITEMS, Supplier::get).values();
	}
}
