package thelm.jaopca.blocks;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockFormType;
import thelm.jaopca.api.blocks.IBlockInfo;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.AABBDeserializer;
import thelm.jaopca.custom.json.BlockFormSettingsDeserializer;
import thelm.jaopca.custom.json.MaterialMappedFunctionDeserializer;
import thelm.jaopca.custom.utils.BlockDeserializationHelper;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.oredict.OredictHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class BlockFormType implements IBlockFormType {

	private BlockFormType() {}

	public static final BlockFormType INSTANCE = new BlockFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormBlock> BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormBlockItem> BLOCK_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IBlockInfo> BLOCK_INFOS = TreeBasedTable.create();
	private static boolean registered = false;

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
		String oredictName = MiscHelper.INSTANCE.getOredictName(form.getSecondaryName(), material.getName());
		return !OredictHandler.getOredict().contains(oredictName);
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
				registerTypeAdapter(AxisAlignedBB.class, AABBDeserializer.INSTANCE);
	}

	@Override
	public IBlockFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return BlockFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		ApiImpl api = ApiImpl.INSTANCE;
		MiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IBlockFormSettings settings = (IBlockFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				ResourceLocation registryName = new ResourceLocation("jaopca", form.getName()+'.'+helper.toLowercaseUnderscore(material.getName()));

				IMaterialFormBlock materialFormBlock = settings.getBlockCreator().create(form, material, settings);
				Block block = materialFormBlock.toBlock();
				block.setRegistryName(registryName);
				block.setCreativeTab(api.creativeTab());
				BLOCKS.put(form, material, materialFormBlock);
				ForgeRegistries.BLOCKS.register(block);

				IMaterialFormBlockItem materialFormBlockItem = settings.getBlockItemCreator().create(materialFormBlock, settings);
				ItemBlock blockItem = materialFormBlockItem.toBlockItem();
				blockItem.setRegistryName(registryName);
				BLOCK_ITEMS.put(form, material, materialFormBlockItem);
				ForgeRegistries.ITEMS.register(blockItem);

				api.registerOredict(helper.getOredictName(secondaryName, material.getName()), blockItem);
				for(String alternativeName : material.getAlternativeNames()) {
					api.registerOredict(helper.getOredictName(secondaryName, alternativeName), blockItem);
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
