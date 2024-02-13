package thelm.jaopca.compat.ic2;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import ic2.api.item.IC2Items;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "IC2")
public class IC2Module implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium"));

	private final IForm crushedForm = ApiImpl.INSTANCE.newForm(this, "ic2_crushed", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crushed").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm purifiedCrushedForm = ApiImpl.INSTANCE.newForm(this, "ic2_purified_crushed", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crushedPurified").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, crushedForm, purifiedCrushedForm).setGrouped(true);

	@Override
	public String getName() {
		return "ic2";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		builder.put(0, "tiny_dust");
		builder.put(1, "tiny_dust");
		return builder.build();
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
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IC2Helper helper = IC2Helper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		ItemStack stoneDust = IC2Items.getItem("stoneDust");
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo crushedInfo = itemFormType.getMaterialFormInfo(crushedForm, material);
			String crushedOredict = miscHelper.getOredictName("crushed", material.getName());
			IItemInfo purifiedCrushedInfo = itemFormType.getMaterialFormInfo(purifiedCrushedForm, material);
			String purifiedCrushedOredict = miscHelper.getOredictName("crushedPurified", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String tinyDustOredict = miscHelper.getOredictName("dustTiny", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			String extraTinyDustOredict = miscHelper.getOredictName("dustTiny", material.getExtra(1).getName());

			helper.registerMaceratorRecipe(
					miscHelper.getRecipeKey("ic2.ore_to_crushed", material.getName()),
					oreOredict, 1, crushedInfo, 2);

			helper.registerOreWashingRecipe(
					miscHelper.getRecipeKey("ic2.crushed_to_purified_crushed", material.getName()),
					crushedOredict, 1, 1000, new Object[] {
							purifiedCrushedInfo, 1, tinyDustOredict, 2, stoneDust, 1,
					});

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("ic2.crushed_to_material", material.getName()),
					crushedOredict, materialOredict, 1, 0.5F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("ic2.purified_crushed_to_material", material.getName()),
					purifiedCrushedOredict, materialOredict, 1, 0.5F);
			helper.registerMaceratorRecipe(
					miscHelper.getRecipeKey("ic2.crushed_to_dust_macerator", material.getName()),
					crushedOredict, 1, dustOredict, 1);
			helper.registerMaceratorRecipe(
					miscHelper.getRecipeKey("ic2.purified_crushed_to_dust_macerator", material.getName()),
					purifiedCrushedOredict, 1, dustOredict, 1);
			helper.registerCentrifugeRecipe(
					miscHelper.getRecipeKey("ic2.crushed_to_dust_centrifuge", material.getName()),
					crushedOredict, 1, 1500, new Object[] {
							extraTinyDustOredict, 1, dustOredict, 1, stoneDust, 1,
					});
			helper.registerCentrifugeRecipe(
					miscHelper.getRecipeKey("ic2.purified_crushed_to_dust_centrifuge", material.getName()),
					purifiedCrushedOredict, 1, 1500, new Object[] {
							extraTinyDustOredict, 1, dustOredict, 1,
					});
		}
	}
}
