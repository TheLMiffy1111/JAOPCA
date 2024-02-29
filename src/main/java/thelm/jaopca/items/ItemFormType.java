package thelm.jaopca.items;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Rarity;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.EnumDeserializer;
import thelm.jaopca.custom.json.ItemFormSettingsDeserializer;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ItemFormType implements IItemFormType {

	private ItemFormType() {}

	public static final ItemFormType INSTANCE = new ItemFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormItem>> ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IItemInfo> ITEM_INFOS = TreeBasedTable.create();
	private static boolean registered = false;

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
		ResourceLocation tagLocation = MiscHelper.INSTANCE.getTagLocation(form.getSecondaryName(), material.getName(), form.getTagSeparator());
		return !ApiImpl.INSTANCE.getItemTags().contains(tagLocation);
	}

	@Override
	public IItemInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IItemInfo info = ITEM_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new ItemInfo(ITEMS.get(form, material).get());
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
		return builder.registerTypeAdapter(Rarity.class, EnumDeserializer.INSTANCE);
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
		IMiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IItemFormSettings settings = (IItemFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			String tagSeparator = form.getTagSeparator();
			for(IMaterial material : form.getMaterials()) {
				String name = form.getName()+'.'+material.getName();
				ResourceLocation registryName = new ResourceLocation("jaopca", name);

				Supplier<IMaterialFormItem> materialFormItem = Suppliers.memoize(()->settings.getItemCreator().create(form, material, settings));
				ITEMS.put(form, material, materialFormItem);
				RegistryHandler.registerForgeRegistryEntry(Registries.ITEM, name, ()->materialFormItem.get().toItem());

				DataInjector.registerItemTag(helper.createResourceLocation(secondaryName), registryName);
				DataInjector.registerItemTag(helper.getTagLocation(secondaryName, material.getName(), tagSeparator), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerItemTag(helper.getTagLocation(secondaryName, alternativeName, tagSeparator), registryName);
				}
			}
		}
	}

	@Override
	public void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		getItems().forEach(mf->mf.addToCreativeModeTab(parameters, output));
	}

	public static Collection<IMaterialFormItem> getItems() {
		return Tables.transformValues(ITEMS, Supplier::get).values();
	}
}
