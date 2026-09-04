package thelm.jaopca.compat.neovitae;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
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

@JAOPCAModule(modDependencies = "neovitae")
public class NeoVitaeModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"amethyst", "coal", "copper", "diamond", "emerald", "gold", "hellforged", "iron", "lapis", "netherite_scrap", "quartz"));

	private final IForm fragmentForm = ApiImpl.INSTANCE.newForm(this, "neovitae_fragments", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY, MaterialType.GEM, MaterialType.CRYSTAL).
			setSecondaryName("neovitae:fragments").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm gravelForm = ApiImpl.INSTANCE.newForm(this, "neovitae_gravels", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY, MaterialType.GEM, MaterialType.CRYSTAL).
			setSecondaryName("neovitae:gravels").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, fragmentForm, gravelForm).setGrouped(true);

	@Override
	public String getName() {
		return "neovitae";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(formRequest);
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY, MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		NeoVitaeHelper helper = NeoVitaeHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Identifier cuttingFluidLocation = Identifier.parse("neovitae:athanor_tool/cutting_fluids");
		Identifier resonatorLocation = Identifier.parse("neovitae:athanor_tool/resonator");
		Item corruptedTinyDust = BuiltInRegistries.ITEM.getValue(Identifier.parse("neovitae:corrupted_tiny_dust"));
		Item corruptedDust = BuiltInRegistries.ITEM.getValue(Identifier.parse("neovitae:corrupted_dust"));
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo fragmentInfo = itemFormType.getMaterialFormInfo(fragmentForm, material);
			Identifier fragmentLocation = miscHelper.getTagLocation("neovitae:fragments", material.getName());
			IItemInfo gravelInfo = itemFormType.getMaterialFormInfo(gravelForm, material);
			Identifier gravelLocation = miscHelper.getTagLocation("neovitae:gravels", material.getName());
			Identifier oreLocation = miscHelper.getTagLocation("ores", material.getName());
			Identifier dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			if(material.getType() == MaterialType.INGOT) {
				Identifier rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				helper.registerAthanorRecipe(
						miscHelper.getRecipeKey("neovitae.ore_to_fragment", material.getName()),
						oreLocation, 1, cuttingFluidLocation, new Object[] {
								fragmentInfo, 5,
						}, Map.of(), true);
				helper.registerAthanorRecipe(
						miscHelper.getRecipeKey("neovitae.raw_material_to_fragment", material.getName()),
						rawMaterialLocation, 1, cuttingFluidLocation, new Object[] {
								fragmentInfo, 3,
						}, Map.of(), true);
			}
			else {
				helper.registerAthanorRecipe(
						miscHelper.getRecipeKey("neovitae.ore_to_fragment", material.getName()),
						oreLocation, 1, cuttingFluidLocation, new Object[] {
								fragmentInfo, material.getType().isIngot() ? 3 : 5,
						}, Map.of(), true);
			}

			helper.registerAthanorRecipe(
					miscHelper.getRecipeKey("neovitae.fragment_to_gravel_athanor", material.getName()),
					fragmentLocation, 1, resonatorLocation, new Object[] {
							gravelInfo, 1,
							corruptedTinyDust, 1, 0.5D,
					}, Map.of(), false);

			helper.registerAlchemyTableRecipe(
					miscHelper.getRecipeKey("neovitae.fragment_to_gravel_corrupted", material.getName()),
					new Object[] {
							fragmentLocation, corruptedDust,
					}, gravelInfo, 2, 200, 50, 3);

			helper.registerAthanorRecipe(
					miscHelper.getRecipeKey("neovitae.gravel_to_dust", material.getName()),
					gravelLocation, 1, cuttingFluidLocation, new Object[] {
							dustLocation, 1,
					}, Map.of(), false);
		}
		for(IMaterial material : moduleData.getMaterials()) {
			Identifier oreLocation = miscHelper.getTagLocation("ores", material.getName());
			Identifier dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerAlchemyTableRecipe(
					miscHelper.getRecipeKey("neovitae.ore_to_dust", material.getName()),
					new Object[] {
							oreLocation, cuttingFluidLocation,
					}, dustLocation, 2, 400, 200, 1);
		}
	}
}
