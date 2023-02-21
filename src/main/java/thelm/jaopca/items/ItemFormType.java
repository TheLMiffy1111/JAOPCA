package thelm.jaopca.items;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.ItemFormSettingsDeserializer;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.oredict.OredictHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ItemFormType implements IItemFormType {

	private ItemFormType() {}

	public static final ItemFormType INSTANCE = new ItemFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormItem> ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IItemInfo> ITEM_INFOS = TreeBasedTable.create();
	private static boolean registered = false;
	private static CreativeTabs creativeTab;

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "item";
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
	public IItemInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IItemInfo info = ITEM_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new ItemInfo(ITEMS.get(form, material));
			ITEM_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public IItemFormSettings getNewSettings() {
		return new ItemFormSettings();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder.registerTypeAdapter(EnumRarity.class, EnumDeserializer.INSTANCE);
	}

	@Override
	public IItemFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return ItemFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
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
			IItemFormSettings settings = (IItemFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				String registryName = form.getName()+'.'+helper.toLowercaseUnderscore(material.getName());

				IMaterialFormItem materialFormItem = settings.getItemCreator().create(form, material, settings);
				Item item = materialFormItem.asItem();
				item.setCreativeTab(creativeTab);
				ITEMS.put(form, material, materialFormItem);
				GameRegistry.registerItem(item, registryName);

				ItemStack stack = new ItemStack(item, 1);
				api.registerOredict(helper.getOredictName(secondaryName, material.getName()), stack);
				for(String alternativeName : material.getAlternativeNames()) {
					api.registerOredict(helper.getOredictName(secondaryName, alternativeName), stack);
				}
			}
		}
	}

	public static CreativeTabs getCreativeTab() {
		if(creativeTab == null) {
			creativeTab = new CreativeTabs("jaopca") {
				@SideOnly(Side.CLIENT)
				@Override
				public Item getTabIconItem() {
					return Items.glowstone_dust;
				}
			};
		}
		return creativeTab;
	}

	public static Collection<IMaterialFormItem> getItems() {
		return ITEMS.values();
	}
}
