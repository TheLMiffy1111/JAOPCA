package thelm.jaopca.blocks;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.BlockFormSettingsDeserializer;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.custom.json.VoxelShapeDeserializer;
import thelm.jaopca.custom.utils.BlockDeserializationHelper;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.ApiImpl;

public class BlockFormType implements IBlockFormType {

	private BlockFormType() {};

	public static final BlockFormType INSTANCE = new BlockFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, BlockMaterialForm> BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, MaterialFormBlockItem> BLOCK_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IBlockInfo> BLOCK_INFOS = TreeBasedTable.create();

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "block";
	}

	@Override
	public String getTranslationKeyFormat() {
		return "block.jaopca.%s";
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
		if(material.getType().isDummy()) {
			return true;
		}
		ResourceLocation tagLocation = new ResourceLocation("forge", form.getSecondaryName()+'/'+material.getName());
		return !ApiImpl.INSTANCE.getItemTags().contains(tagLocation);
	}

	@Override
	public IBlockInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IBlockInfo info = BLOCK_INFOS.get(form, material);
		if(info == null && BLOCKS.contains(form, material) && BLOCK_ITEMS.contains(form, material)) {
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
				registerTypeAdapter(new TypeToken<Function<IMaterial, Material>>(){}.getType(),
						new MaterialMappedFunctionDeserializer<>(BlockDeserializationHelper.INSTANCE::getBlockMaterial,
								BlockDeserializationHelper.INSTANCE::getBlockMaterialName)).
				registerTypeAdapter(new TypeToken<Function<IMaterial, SoundType>>(){}.getType(),
						new MaterialMappedFunctionDeserializer<>(BlockDeserializationHelper.INSTANCE::getSoundType,
								BlockDeserializationHelper.INSTANCE::getSoundTypeName)).
				registerTypeAdapter(new TypeToken<Function<IMaterial, ToolType>>(){}.getType(),
						new MaterialMappedFunctionDeserializer<>(ToolType::get, ToolType::getName)).
				registerTypeAdapter(BlockRenderLayer.class, EnumDeserializer.INSTANCE).
				registerTypeAdapter(VoxelShape.class, VoxelShapeDeserializer.INSTANCE);
	}

	@Override
	public IBlockFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return BlockFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	public static void registerBlocks(IForgeRegistry<Block> registry) {
		for(IForm form : FORMS) {
			IBlockFormSettings settings = (IBlockFormSettings)form.getSettings();
			for(IMaterial material : form.getMaterials()) {
				boolean isMaterialDummy = material.getType().isDummy();
				BlockMaterialForm block = settings.getBlockCreator().create(form, material, ()->(IBlockFormSettings)form.getSettings());
				String registryKey = isMaterialDummy ? material.getName()+form.getName() : form.getName()+'.'+material.getName();
				block.setRegistryName(new ResourceLocation(JAOPCA.MOD_ID, registryKey));
				registry.register(block);
				BLOCKS.put(form, material, block);
				if(!isMaterialDummy) {
					Supplier<Block> supplier = ()->block;
					DataInjector.registerBlockTag(new ResourceLocation("forge", form.getSecondaryName()), supplier);
					DataInjector.registerBlockTag(new ResourceLocation("forge", form.getSecondaryName()+'/'+material.getName()), supplier);
					for(String alternativeName : material.getAlternativeNames()) {
						DataInjector.registerBlockTag(new ResourceLocation("forge", form.getSecondaryName()+'/'+alternativeName), supplier);
					}
				}
			}
		}
	}

	public static void registerBlockItems(IForgeRegistry<Item> registry) {
		for(Table.Cell<IForm, IMaterial, BlockMaterialForm> cell : BLOCKS.cellSet()) {
			IForm form = cell.getRowKey();
			IMaterial material = cell.getColumnKey();
			BlockMaterialForm block = cell.getValue();
			IBlockFormSettings settings = (IBlockFormSettings)form.getSettings();
			boolean isMaterialDummy = material.getType().isDummy();
			MaterialFormBlockItem blockItem = settings.getBlockItemCreator().create(block, ()->(IBlockFormSettings)form.getSettings());
			blockItem.setRegistryName(block.getRegistryName());
			registry.register(blockItem);
			BLOCK_ITEMS.put(form, material, blockItem);
			if(!isMaterialDummy) {
				Supplier<Item> supplier = ()->blockItem;
				DataInjector.registerItemTag(new ResourceLocation("forge", form.getSecondaryName()), supplier);
				DataInjector.registerItemTag(new ResourceLocation("forge", form.getSecondaryName()+'/'+material.getName()), supplier);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerItemTag(new ResourceLocation("forge", form.getSecondaryName()+'/'+alternativeName), supplier);
				}
			}
		}
	}

	public static Collection<BlockMaterialForm> getBlocks() {
		return BLOCKS.values();
	}

	public static Collection<MaterialFormBlockItem> getBlockItems() {
		return BLOCK_ITEMS.values();
	}
}
