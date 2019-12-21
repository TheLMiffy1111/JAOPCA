package thelm.jaopca.fluids;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
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

	private FluidFormType() {};

	public static final FluidFormType INSTANCE = new FluidFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormFluid> FLUIDS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormFluidBlock> FLUID_BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormBucketItem> BUCKET_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IFluidInfo> FLUID_INFOS = TreeBasedTable.create();

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
		ResourceLocation tagLocation = MiscHelper.INSTANCE.getTagLocation(form.getSecondaryName(), material.getName());
		return !ApiImpl.INSTANCE.getFluidTags().contains(tagLocation);
	}

	@Override
	public IFluidInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IFluidInfo info = FLUID_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new FluidInfo(FLUIDS.get(form, material), FLUID_BLOCKS.get(form, material), BUCKET_ITEMS.get(form, material));
			FLUID_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public IFluidFormSettings getNewSettings() {
		return new FluidFormSettings();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder.registerTypeAdapter(SOUND_EVENT_SUPPLIER_TYPE, ForgeRegistryEntrySupplierDeserializer.INSTANCE);
	}

	@Override
	public IFluidFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return FluidFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	public static void registerEntries() {
		MiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IFluidFormSettings settings = (IFluidFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				ResourceLocation registryName = new ResourceLocation("jaopca", form.getName()+'.'+material.getName());

				IMaterialFormFluid materialFormFluid = settings.getFluidCreator().create(form, material, settings);
				Fluid fluid = materialFormFluid.asFluid();
				fluid.setRegistryName(registryName);
				FLUIDS.put(form, material, materialFormFluid);
				RegistryHandler.registerForgeRegistryEntry(fluid);

				IMaterialFormFluidBlock materialFormFluidBlock = settings.getFluidBlockCreator().create(materialFormFluid, settings);
				Block fluidBlock = materialFormFluidBlock.asBlock();
				fluidBlock.setRegistryName(registryName);
				FLUID_BLOCKS.put(form, material, materialFormFluidBlock);
				RegistryHandler.registerForgeRegistryEntry(fluidBlock);

				IMaterialFormBucketItem materialFormBucketItem = settings.getBucketItemCreator().create(materialFormFluid, settings);
				Item bucketItem = materialFormBucketItem.asItem();
				bucketItem.setRegistryName(registryName);
				BUCKET_ITEMS.put(form, material, materialFormBucketItem);
				RegistryHandler.registerForgeRegistryEntry(bucketItem);

				Supplier<Fluid> fluidSupplier = ()->fluid;
				DataInjector.registerFluidTag(helper.createResourceLocation(secondaryName), fluidSupplier);
				DataInjector.registerFluidTag(helper.getTagLocation(secondaryName, material.getName()), fluidSupplier);
				for(String alternativeName : material.getAlternativeNames()) {
					DataInjector.registerFluidTag(helper.getTagLocation(secondaryName, alternativeName), fluidSupplier);
				}
			}
		}
	}

	public static Collection<IMaterialFormFluid> getFluids() {
		return FLUIDS.values();
	}

	public static Collection<IMaterialFormFluidBlock> getFluidBlocks() {
		return FLUID_BLOCKS.values();
	}

	public static Collection<IMaterialFormBucketItem> getBucketItems() {
		return BUCKET_ITEMS.values();
	}
}
