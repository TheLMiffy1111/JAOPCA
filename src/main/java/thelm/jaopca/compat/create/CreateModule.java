package thelm.jaopca.compat.create;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
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
			"Copper", "Gold", "Iron", "Zinc"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

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
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
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

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("create.byproduct", "minecraft:cobblestone",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in Create Legacy's crusher.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			helper.registerCrushingRecipe(
					miscHelper.getRecipeKey("create.ore_to_crushed", material.getName()),
					oreOredict, 1280, new Object[] {
							crushedInfo, 1,
							crushedInfo, 1, 0.5F,
							byproduct, 1, 0.25F,
					});

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("create.crushed_to_material", material.getName()),
					crushedOredict, materialOredict, 1, 0.1F);
			helper.registerWashingRecipe(
					miscHelper.getRecipeKey("create.crushed_to_nugget", material.getName()),
					crushedOredict, new Object[] {
							nuggetOredict, 9,
					});
		}
	}
}
