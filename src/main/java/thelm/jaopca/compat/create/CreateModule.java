package thelm.jaopca.compat.create;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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

@JAOPCAModule(modDependencies = "create")
public class CreateModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Copper", "Gold", "Iron", "Lead", "Nickel", "Osmium", "Platinum", "Quicksilver", "Silver",
			"Tin", "Uranium", "Zinc"));

	private final IForm crushedForm = ApiImpl.INSTANCE.newForm(this, "create_crushed", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crushed").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "nugget");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(crushedForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		CreateHelper helper = CreateHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : crushedForm.getMaterials()) {
			IItemInfo crushedInfo = itemFormType.getMaterialFormInfo(crushedForm, material);
			String crushedOredict = miscHelper.getOredictName("crushed", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String nuggetOredict = miscHelper.getOredictName("nugget", material.getName());

			helper.registerMillingRecipe(
					miscHelper.getRecipeKey("create.ore_to_crushed", material.getName()),
					oreOredict, crushedInfo, 1, crushedInfo, 1, 50, 100);

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("create.crushed_to_material", material.getName()),
					crushedOredict, materialOredict, 1, 0.1F);
			helper.registerWashingRecipe(
					miscHelper.getRecipeKey("create.crushed_to_nugget", material.getName()),
					crushedOredict, nuggetOredict, 9, ItemStack.EMPTY, 0);
		}
	}
}
