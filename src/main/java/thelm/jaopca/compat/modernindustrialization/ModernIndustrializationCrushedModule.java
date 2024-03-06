package thelm.jaopca.compat.modernindustrialization;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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

@JAOPCAModule(modDependencies = "modern_industrialization")
public class ModernIndustrializationCrushedModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"bauxite", "coal", "diamond", "emerald", "lapis", "lignite_coal", "monazite", "quartz", "redstone", "salt"));

	private final IForm crushedDustForm = ApiImpl.INSTANCE.newForm(this, "modern_industrialization_crushed_dusts", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT_LEGACY, MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST).
			setSecondaryName("modern_industrialization:crushed_dusts").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "modern_industrialization_crushed";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(crushedDustForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT_LEGACY, MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		ModernIndustrializationHelper helper = ModernIndustrializationHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			IItemInfo crushedDustInfo = itemFormType.getMaterialFormInfo(crushedDustForm, material);
			ResourceLocation crushedDustLocation = miscHelper.getTagLocation("modern_industrialization:crushed_dusts", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerMaceratorRecipe(
					new ResourceLocation("jaopca", "modern_industrialization.ore_to_crushed_dust."+material.getName()),
					oreLocation, 1, 1F, new Object[] {
							crushedDustInfo, 3, 1F,
					}, 2, 200);
			helper.registerMaceratorRecipe(
					new ResourceLocation("jaopca", "modern_industrialization.crushed_dust_to_dust."+material.getName()),
					crushedDustLocation, 1, 1F, new Object[] {
							dustLocation, 1, 1F,
							dustLocation, 1, 0.5F,
					}, 2, 100);
		}
	}
}
