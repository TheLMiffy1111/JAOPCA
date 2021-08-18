package thelm.jaopca.blocks;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import com.google.common.base.Strings;
import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShape;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.BlockFormSettingsDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.custom.json.VoxelShapeDeserializer;
import thelm.jaopca.custom.utils.BlockDeserializationHelper;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class BlockFormType implements IBlockFormType {

	private BlockFormType() {};

	public static final BlockFormType INSTANCE = new BlockFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormBlock> BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormBlockItem> BLOCK_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IBlockInfo> BLOCK_INFOS = TreeBasedTable.create();

	public static final Type MATERIAL_FUNCTION_TYPE = new TypeToken<Function<IMaterial, Material>>(){}.getType();
	public static final Type SOUND_TYPE_FUNCTION_TYPE = new TypeToken<Function<IMaterial, SoundType>>(){}.getType();

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
		ResourceLocation tagLocation = MiscHelper.INSTANCE.getTagLocation(form.getSecondaryName(), material.getName());
		return !ApiImpl.INSTANCE.getItemTags().contains(tagLocation);
	}

	@Override
	public IBlockInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IBlockInfo info = BLOCK_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new BlockInfo(BLOCKS.get(form, material), BLOCK_ITEMS.get(form, material));
			BLOCK_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public IBlockFormSettings getNewSettings() {
		return new BlockFormSettings();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder.
				registerTypeAdapter(MATERIAL_FUNCTION_TYPE,
						new MaterialMappedFunctionDeserializer<>(BlockDeserializationHelper.INSTANCE::getBlockMaterial,
								BlockDeserializationHelper.INSTANCE::getBlockMaterialName)).
				registerTypeAdapter(SOUND_TYPE_FUNCTION_TYPE,
						new MaterialMappedFunctionDeserializer<>(BlockDeserializationHelper.INSTANCE::getSoundType,
								BlockDeserializationHelper.INSTANCE::getSoundTypeName)).
				registerTypeAdapter(VoxelShape.class, VoxelShapeDeserializer.INSTANCE);
	}

	@Override
	public IBlockFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return BlockFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	public static void registerEntries() {
		MiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IBlockFormSettings settings = (IBlockFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				ResourceLocation registryName = new ResourceLocation("jaopca", form.getName()+'.'+material.getName());
				ResourceLocation lootLocation = new ResourceLocation("jaopca", "blocks/"+form.getName()+'.'+material.getName());
				String toolTag = settings.getHarvestToolTagFunction().apply(material);
				String tierTag = settings.getHarvestTierTagFunction().apply(material);

				IMaterialFormBlock materialFormBlock = settings.getBlockCreator().create(form, material, settings);
				Block block = materialFormBlock.asBlock();
				block.setRegistryName(registryName);
				BLOCKS.put(form, material, materialFormBlock);
				RegistryHandler.registerForgeRegistryEntry(block);

				IMaterialFormBlockItem materialFormBlockItem = settings.getBlockItemCreator().create(materialFormBlock, settings);
				BlockItem blockItem = materialFormBlockItem.asBlockItem();
				blockItem.setRegistryName(registryName);
				BLOCK_ITEMS.put(form, material, materialFormBlockItem);
				RegistryHandler.registerForgeRegistryEntry(blockItem);

				DataInjector.registerLootTable(lootLocation, ()->settings.getBlockLootTableCreator().create(materialFormBlock, settings));

				DataInjector.registerBlockTag(helper.createResourceLocation(secondaryName), registryName);
				DataInjector.registerBlockTag(helper.getTagLocation(secondaryName, material.getName()), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerBlockTag(helper.getTagLocation(secondaryName, alternativeName), registryName);
				}

				DataInjector.registerBlockTag(new ResourceLocation(toolTag), registryName);
				if(!Strings.isNullOrEmpty(tierTag)) {
					DataInjector.registerBlockTag(new ResourceLocation(tierTag), registryName);
				}

				DataInjector.registerItemTag(helper.createResourceLocation(secondaryName), registryName);
				DataInjector.registerItemTag(helper.getTagLocation(secondaryName, material.getName()), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerItemTag(helper.getTagLocation(secondaryName, alternativeName), registryName);
				}
			}
		}
	}

	public static Collection<IMaterialFormBlock> getBlocks() {
		return BLOCKS.values();
	}

	public static Collection<IMaterialFormBlockItem> getBlockItems() {
		return BLOCK_ITEMS.values();
	}
}
