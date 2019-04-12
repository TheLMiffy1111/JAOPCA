package thelm.jaopca.items;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.JsonObject;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.items.ItemMaterialForm;
import thelm.jaopca.api.materials.EnumMaterialType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeRegistry;
import thelm.jaopca.utils.ApiImpl;

public class ItemFormType implements IItemFormType {

	private ItemFormType() {};

	public static final ItemFormType INSTANCE = new ItemFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, ItemMaterialForm> ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IItemInfo> ITEM_INFOS = TreeBasedTable.create();
	private static ItemGroup itemGroup;

	public static void init() {
		FormTypeRegistry.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "item";
	}

	@Override
	public String getTranslationKeyFormat() {
		return "item.jaopca.%s";
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
		if(material.getType().isNone()) {
			return true;
		}
		ResourceLocation tagLocation = new ResourceLocation("forge", form.getSecondaryName()+'/'+material.getName());
		return !ApiImpl.INSTANCE.getItemTags().contains(tagLocation);
	}

	@Override
	public IItemInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IItemInfo info = ITEM_INFOS.get(form, material);
		if(info == null && ITEMS.contains(form, material)) {
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
	public IItemFormSettings deserializeSettings(JsonObject jsonObject) {
		// TODO Auto-generated method stub
		IItemFormSettings ret = new ItemFormSettings();
		return ret;
	}

	public static void registerItems(IForgeRegistry<Item> registry) {
		for(IForm form : FORMS) {
			IItemFormSettings settings = (IItemFormSettings)form.getSettings();
			for(IMaterial material : form.getMaterials()) {
				boolean isMaterialNone = material.getType().isNone();
				ItemMaterialForm item = settings.getItemCreator().create(form, material, ()->(IItemFormSettings)form.getSettings());
				item.setRegistryName(new ResourceLocation(JAOPCA.MOD_ID, form.getName()+(isMaterialNone ? "" : '.'+material.getName())));
				registry.register(item);
				ITEMS.put(form, material, item);
				DataInjector.registerItemTag(new ResourceLocation("forge", form.getSecondaryName()), ()->item);
				if(!isMaterialNone) {
					DataInjector.registerItemTag(new ResourceLocation("forge", form.getSecondaryName()+'/'+material.getName()), ()->item);
				}
			}
		}
	}

	public static ItemGroup getItemGroup() {
		if(itemGroup == null) {
			itemGroup = new ItemGroup("jaopca") {
				@Override
				public ItemStack createIcon() {
					return new ItemStack(Items.GLOWSTONE_DUST);
				}
			};
		}
		return itemGroup;
	}

	public static Collection<ItemMaterialForm> getItems() {
		return ITEMS.values();
	}
}
