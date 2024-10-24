package thelm.jaopca.compat.createmetallurgy;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
import thelm.jaopca.compat.create.CreateHelper;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "createmetallurgy")
public class CreateMetallurgyModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "wolframite", "zinc"));

	private final IForm dirtyDustForm = ApiImpl.INSTANCE.newForm(this, "createmetallurgy_dirty_dusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("createmetallurgy:dirty_dusts").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "createmetallurgy";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		builder.put(0, "create");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(dirtyDustForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		CreateHelper helper = CreateHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : dirtyDustForm.getMaterials()) {
			IItemInfo dirtyDustInfo = itemFormType.getMaterialFormInfo(dirtyDustForm, material);
			ResourceLocation dirtyDustLocation = miscHelper.getTagLocation("createmetallurgy:dirty_dusts", material.getName());
			ResourceLocation crushedLocation = miscHelper.getTagLocation("create:crushed_raw_materials", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			helper.registerMillingRecipe(
					new ResourceLocation("jaopca", "createmetallurgy.crushed_to_dirty_dust."+material.getName()),
					crushedLocation, 150, new Object[] {
							dirtyDustInfo, 1, 1F,
							dirtyDustInfo, 1, 0.5F,
					});

			helper.registerSplashingRecipe(
					new ResourceLocation("jaopca", "createmetallurgy.dirty_dust_to_dust."+material.getName()),
					dirtyDustLocation, new Object[] {
							dustLocation, 1,
					});

			api.registerItemTag(miscHelper.getTagLocation("dirty_dusts", material.getName()), dirtyDustInfo.asItem());
		}
	}
}
