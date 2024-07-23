package thelm.jaopca.fluids;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.json.FluidFormSettingsDeserializer;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.MiscHelper;

public class FluidFormType implements IFluidFormType {

	private FluidFormType() {}

	public static final FluidFormType INSTANCE = new FluidFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormFluid> FLUIDS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormFluidBlock> FLUID_BLOCKS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormBucketItem> BUCKET_ITEMS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IFluidInfo> FLUID_INFOS = TreeBasedTable.create();
	private static boolean registered = false;

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
		String fluidName = MiscHelper.INSTANCE.getFluidName(form.getSecondaryName(), material.getName());
		return !FluidRegistry.isFluidRegistered(fluidName);
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
		return builder;
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
		IMiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IFluidFormSettings settings = (IFluidFormSettings)form.getSettings();
			for(IMaterial material : form.getMaterials()) {
				String registryName = form.getName()+'.'+helper.toLowercaseUnderscore(material.getName());

				IMaterialFormFluid materialFormFluid = settings.getFluidCreator().create(form, material, settings);
				Fluid fluid = materialFormFluid.toFluid();
				FLUIDS.put(form, material, materialFormFluid);
				FluidRegistry.registerFluid(fluid);

				IMaterialFormFluidBlock materialFormFluidBlock = settings.getFluidBlockCreator().create(materialFormFluid, settings);
				Block fluidBlock = materialFormFluidBlock.toBlock();
				FLUID_BLOCKS.put(form, material, materialFormFluidBlock);
				GameRegistry.registerBlock(fluidBlock, null, registryName);

				IMaterialFormBucketItem materialFormBucketItem = settings.getBucketItemCreator().create(materialFormFluid, settings);
				Item bucketItem = materialFormBucketItem.toItem();
				BUCKET_ITEMS.put(form, material, materialFormBucketItem);
				GameRegistry.registerItem(bucketItem, registryName);
				FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucketItem), new ItemStack(Items.bucket));
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
