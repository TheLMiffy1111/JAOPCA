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
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.fluids.IMaterialFormFluidType;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.functions.MaterialDoubleFunction;
import thelm.jaopca.api.functions.MaterialIntFunction;
import thelm.jaopca.api.functions.MaterialMappedFunction;
import thelm.jaopca.api.functions.MaterialPredicate;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
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
	private static boolean registered = false;

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
		return getNewSettings().
				setTickRateFunction(MaterialIntFunction.of(30)).setDensityFunction(MaterialIntFunction.of(2000)).
				setTemperatureFunction(MaterialIntFunction.of(1000)).setViscosityFunction(MaterialIntFunction.of(6000)).
				setFillSoundSupplier(()->SoundEvents.BUCKET_FILL_LAVA).setEmptySoundSupplier(()->SoundEvents.BUCKET_EMPTY_LAVA).
				setMotionScaleFunction(MaterialDoubleFunction.of(0.007/3)).setCanDrownFunction(MaterialPredicate.of(false)).
				setPathTypeFunction(MaterialMappedFunction.of(BlockPathTypes.class, BlockPathTypes.LAVA)).
				setAdjacentPathTypeFunction(MaterialMappedFunction.of(BlockPathTypes.class, null)).
				setFireTimeFunction(MaterialIntFunction.of(15));
	}

	@Override
	public Codec<IFormSettings> formSettingsCodec() {
		return FluidCustomCodecs.FLUID_FORM_SETTINGS;
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		IMiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IFluidFormSettings settings = (IFluidFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			String tagSeparator = form.getTagSeparator();
			for(IMaterial material : form.getMaterials()) {
				String name = form.getName()+'.'+material.getName();
				ResourceLocation registryName = new ResourceLocation("jaopca", name);

				Supplier<IMaterialFormFluid> materialFormFluid = Suppliers.memoize(()->settings.getFluidCreator().create(form, material, settings));
				FLUIDS.put(form, material, materialFormFluid);
				RegistryHandler.registerRegistryEntry(Registries.FLUID, name, ()->materialFormFluid.get().toFluid());

				Supplier<IMaterialFormFluidType> materialFormFluidType = Suppliers.memoize(()->settings.getFluidTypeCreator().create(materialFormFluid.get(), settings));
				FLUID_TYPES.put(form, material, materialFormFluidType);
				RegistryHandler.registerRegistryEntry(NeoForgeRegistries.Keys.FLUID_TYPES, name, ()->materialFormFluidType.get().toFluidType());

				Supplier<IMaterialFormFluidBlock> materialFormFluidBlock = Suppliers.memoize(()->settings.getFluidBlockCreator().create(materialFormFluid.get(), settings));
				FLUID_BLOCKS.put(form, material, materialFormFluidBlock);
				RegistryHandler.registerRegistryEntry(Registries.BLOCK, name, ()->materialFormFluidBlock.get().toBlock());

				Supplier<IMaterialFormBucketItem> materialFormBucketItem = Suppliers.memoize(()->settings.getBucketItemCreator().create(materialFormFluid.get(), settings));
				BUCKET_ITEMS.put(form, material, materialFormBucketItem);
				RegistryHandler.registerRegistryEntry(Registries.ITEM, name, ()->materialFormBucketItem.get().toItem());

				DataInjector.registerFluidTag(helper.createResourceLocation(secondaryName), registryName);
				DataInjector.registerFluidTag(helper.getTagLocation(secondaryName, material.getName(), tagSeparator), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerFluidTag(helper.getTagLocation(secondaryName, alternativeName, tagSeparator), registryName);
				}
			}
		}
	}

	@Override
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		getFluidBlocks().forEach(mf->mf.onRegisterCapabilities(event));
		getBucketItems().forEach(mf->mf.onRegisterCapabilities(event));
	}

	@Override
	public void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		getBucketItems().forEach(mf->mf.addToCreativeModeTab(parameters, output));
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
