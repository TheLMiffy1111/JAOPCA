package thelm.jaopca.fluids;

import java.lang.reflect.Type;
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
import com.google.gson.reflect.TypeToken;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.fluids.IMaterialFormFluidType;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.FluidFormSettingsDeserializer;
import thelm.jaopca.custom.json.ForgeRegistryEntrySupplierDeserializer;
import thelm.jaopca.data.DataInjector;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class FluidFormType implements IFluidFormType {

	private FluidFormType() {}

	public static final FluidFormType INSTANCE = new FluidFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormFluid>> FLUIDS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormFluidType>> FLUID_TYPES = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormFluidBlock>> FLUID_BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormBucketItem>> BUCKET_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IFluidInfo> FLUID_INFOS = TreeBasedTable.create();
	private static boolean registered;
	
	public static final Type SOUND_EVENT_SUPPLIER_TYPE = new TypeToken<Supplier<SoundEvent>>(){}.getType();

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "fluid";
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
		return !ApiImpl.INSTANCE.getFluidTags().contains(tagLocation);
	}

	@Override
	public IFluidInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IFluidInfo info = FLUID_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new FluidInfo(FLUIDS.get(form, material).get(), FLUID_TYPES.get(form, material).get(), FLUID_BLOCKS.get(form, material).get(), BUCKET_ITEMS.get(form, material).get());
			FLUID_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public IFluidFormSettings getNewSettings() {
		return new FluidFormSettings();
	}

	public IFluidFormSettings getNewSettingsLava() {
		return new FluidFormSettings().setTickRateFunction(material->30).
				setDensityFunction(material->2000).setTemperatureFunction(material->1000).
				setFillSoundSupplier(()->SoundEvents.BUCKET_FILL_LAVA).setEmptySoundSupplier(()->SoundEvents.BUCKET_EMPTY_LAVA).
				setMotionScaleFunction(material->0.007D/3).setCanDrownFunction(material->false).
				setPathTypeFunction(material->BlockPathTypes.LAVA).setAdjacentPathTypeFunction(material->null).
				setMaterialFunction(material->Material.LAVA).setFireTimeFunction(material->15);
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder.registerTypeAdapter(SOUND_EVENT_SUPPLIER_TYPE, ForgeRegistryEntrySupplierDeserializer.INSTANCE);
	}

	@Override
	public IFluidFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return FluidFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		MiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IFluidFormSettings settings = (IFluidFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			String tagSeparator = form.getTagSeparator();
			for(IMaterial material : form.getMaterials()) {
				String name = form.getName()+'.'+material.getName();
				ResourceLocation registryName = new ResourceLocation("jaopca", name);

				Supplier<IMaterialFormFluid> materialFormFluid = Suppliers.memoize(()->settings.getFluidCreator().create(form, material, settings));
				FLUIDS.put(form, material, materialFormFluid);
				RegistryHandler.registerForgeRegistryEntry(Registry.FLUID_REGISTRY, name, ()->materialFormFluid.get().asFluid());

				Supplier<IMaterialFormFluidType> materialFormFluidType = Suppliers.memoize(()->settings.getFluidTypeCreator().create(materialFormFluid.get(), settings));
				FLUID_TYPES.put(form, material, materialFormFluidType);
				RegistryHandler.registerForgeRegistryEntry(ForgeRegistries.Keys.FLUID_TYPES, name, ()->materialFormFluidType.get().asFluidType());

				Supplier<IMaterialFormFluidBlock> materialFormFluidBlock = Suppliers.memoize(()->settings.getFluidBlockCreator().create(materialFormFluid.get(), settings));
				FLUID_BLOCKS.put(form, material, materialFormFluidBlock);
				RegistryHandler.registerForgeRegistryEntry(Registry.BLOCK_REGISTRY, name, ()->materialFormFluidBlock.get().asBlock());

				Supplier<IMaterialFormBucketItem> materialFormBucketItem = Suppliers.memoize(()->settings.getBucketItemCreator().create(materialFormFluid.get(), settings));
				BUCKET_ITEMS.put(form, material, materialFormBucketItem);
				RegistryHandler.registerForgeRegistryEntry(Registry.ITEM_REGISTRY, name, ()->materialFormBucketItem.get().asItem());

				DataInjector.registerFluidTag(helper.createResourceLocation(secondaryName), registryName);
				DataInjector.registerFluidTag(helper.getTagLocation(secondaryName, material.getName(), tagSeparator), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerFluidTag(helper.getTagLocation(secondaryName, alternativeName, tagSeparator), registryName);
				}
			}
		}
	}

	public static Collection<IMaterialFormFluid> getFluids() {
		return Tables.transformValues(FLUIDS, Supplier::get).values();
	}

	public static Collection<IMaterialFormFluidType> getFluidTypes() {
		return Tables.transformValues(FLUID_TYPES, Supplier::get).values();
	}

	public static Collection<IMaterialFormFluidBlock> getFluidBlocks() {
		return Tables.transformValues(FLUID_BLOCKS, Supplier::get).values();
	}

	public static Collection<IMaterialFormBucketItem> getBucketItems() {
		return Tables.transformValues(BUCKET_ITEMS, Supplier::get).values();
	}
}
