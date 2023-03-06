package thelm.jaopca.compat.factorization;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import factorization.shared.Core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "factorization")
public class FactorizationModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Ardite", "Cobalt", "Copper", "FzDarkIron", "Galena", "Gold", "Iron", "Lead", "Silver", "Tin"));

	public FactorizationModule() {
		FMLCommonHandler.instance().bus().register(this);
	}

	private final IForm dirtyGravelForm = ApiImpl.INSTANCE.newForm(this, "factorization_dirty_gravel", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("dirtyGravel").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm cleanGravelForm = ApiImpl.INSTANCE.newForm(this, "factorization_clean_gravel", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("cleanGravel").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm reducedForm = ApiImpl.INSTANCE.newForm(this, "factorization_reduced", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("reduced").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm crystallineForm = ApiImpl.INSTANCE.newForm(this, "factorization_crystalline", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystalline").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this,
			dirtyGravelForm, cleanGravelForm, reducedForm, crystallineForm).setGrouped(true);

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "factorization";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(formRequest);
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		FactorizationHelper helper = FactorizationHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		ItemStack aquaRegia = new ItemStack(Core.registry.acid, 1, 1);
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo dirtyGravelInfo = itemFormType.getMaterialFormInfo(dirtyGravelForm, material);
			String dirtyGravelOredict = miscHelper.getOredictName("dirtyGravel", material.getName());
			IItemInfo cleanGravelInfo = itemFormType.getMaterialFormInfo(cleanGravelForm, material);
			String cleanGravelOredict = miscHelper.getOredictName("cleanGravel", material.getName());
			IItemInfo reducedInfo = itemFormType.getMaterialFormInfo(reducedForm, material);
			String reducedOredict = miscHelper.getOredictName("reduced", material.getName());
			IItemInfo crystallineInfo = itemFormType.getMaterialFormInfo(crystallineForm, material);
			String crystallineOredict = miscHelper.getOredictName("crystalline", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			helper.registerGrinderRecipe(
					miscHelper.getRecipeKey("factorization.ore_to_dirty_gravel", material.getName()),
					oreOredict, dirtyGravelInfo, 2F);

			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("factorization.dirty_gravel_to_clean_gravel_1", material.getName()),
					cleanGravelInfo, 1, new Object[] {
							"fz.waterBucketLike", dirtyGravelOredict,
					});
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("factorization.dirty_gravel_to_clean_gravel_8", material.getName()),
					cleanGravelInfo, 8, new Object[] {
							"fz.waterBucketLike", dirtyGravelOredict, dirtyGravelOredict,
							dirtyGravelOredict, dirtyGravelOredict, dirtyGravelOredict,
							dirtyGravelOredict, dirtyGravelOredict, dirtyGravelOredict,
					});

			helper.registerSlagFurnaceRecipe(
					miscHelper.getRecipeKey("factorization.clean_gravel_to_reduced", material.getName()),
					cleanGravelOredict, reducedInfo, 1F, reducedInfo, 0.25F);

			helper.registerCrystallizerRecipe(
					miscHelper.getRecipeKey("factorization.reduced_to_crystalline", material.getName()),
					reducedOredict, 1, aquaRegia, 1, crystallineInfo, 1.2F);

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("factorization.dirty_gravel_to_material_smelting", material.getName()),
					dirtyGravelOredict, materialOredict, 1, 0.7F);
			helper.registerSlagFurnaceRecipe(
					miscHelper.getRecipeKey("factorization.dirty_gravel_to_material_slag_furnace", material.getName()),
					dirtyGravelOredict, materialOredict, 1.1F, Blocks.dirt, 0.2F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("factorization.clean_gravel_to_material", material.getName()),
					cleanGravelOredict, materialOredict, 1, 0.7F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("factorization.reduced_to_material", material.getName()),
					reducedOredict, materialOredict, 1, 0.7F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("factorization.crystalline_to_material", material.getName()),
					crystallineOredict, materialOredict, 1, 0.7F);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("factorization.byproduct", "minecraft:stone",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in Factorization's slag furnace.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			helper.registerSlagFurnaceRecipe(
					miscHelper.getRecipeKey("factorization.ore_to_material", material.getName()),
					oreOredict, materialOredict, 1.2F, byproduct, 0.4F);
		}
	}

	@SubscribeEvent
	public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		EntityPlayer player = event.player;
		if(player.worldObj == null) {
			if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				return;
			}
		}
		else if(player.worldObj.isRemote) {
			return;
		}
		ItemStack output = event.crafting;
		IMaterial material;
		if(output.getItem() instanceof IMaterialForm) {
			IMaterialForm materialForm = (IMaterialForm)output.getItem();
			if(materialForm.getForm() != cleanGravelForm) {
				return;
			}
			material = materialForm.getIMaterial();
		}
		else {
			return;
		}
		IInventory inv = event.craftMatrix;
		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack input = inv.getStackInSlot(i);
			if(input == null) {
				continue;
			}
			if(input.getItem() instanceof IMaterialForm) {
				IMaterialForm materialForm = (IMaterialForm)input.getItem();
				if(materialForm.getForm() != dirtyGravelForm || materialForm.getIMaterial() != material || Math.random() > 0.25) {
					continue;
				}
				ItemStack toAdd = new ItemStack(Core.registry.sludge);
				if(!player.inventory.addItemStackToInventory(toAdd)) {
					player.dropPlayerItemWithRandomChoice(toAdd, false);
				}
			}
		}
	}
}
