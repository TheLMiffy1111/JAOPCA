package thelm.jaopca.compat.hbm;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

@JAOPCAModule(modDependencies = "hbm@[1.0.27_X4515,)")
public class HBMModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminum", "Aluminium", "Beryllium", "CertusQuartz", "Cinnabar", "Coal", "Cobalt", "Copper", "Diamond",
			"Fluorite", "Gold", "Iron", "Lead", "Lithium", "NaturalAluminum", "Niter", "Plutonium", "RareEarth",
			"Redstone", "Saltpeter", "Schrabidium", "Starmetal", "Sulfur", "Thorium", "Titanium", "Tungsten",
			"Uranium"));
	private static final Set<String> MODULE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminum", "Aluminium", "Beryllium", "CertusQuartz", "Cinnabar", "Coal", "Cobalt", "Copper", "Diamond",
			"Emerald", "Fluorite", "Gold", "Iron", "Lapis", "Lead", "Lignite", "Lithium", "NaturalAluminum",
			"NetherQuartz", "Niter", "Plutonium", "Quartz", "RareEarth", "Redstone", "Saltpeter", "Schrabidium",
			"Starmetal", "Sulfur", "Thorium", "Titanium", "Tungsten", "Uranium"));

	public HBMModule() {
		ApiImpl.INSTANCE.registerBlacklistedMaterialNames(
				"Ac227", "Am241", "Am242", "At209", "Au198", "Co60", "Cs137", "Gh336", "I131", "Np237", "Pb209",
				"Po210", "Pu238", "Pu239", "Pu240", "Pu241", "Ra226", "Sr90", "Tc99", "Th232", "Thorium232", "U233",
				"U235", "U238", "Xe135");
	}

	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "hbm_crystal", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.ORE).setSecondaryName("hbm:crystal").setDefaultMaterialBlacklist(BLACKLIST);
	
	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "hbm";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		builder.put(1, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(crystalForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.copyOf(Arrays.asList(MaterialType.ORE));
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
		HBMHelper helper = HBMHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Item tinyLithium = ModItems.powder_lithium_tiny;
		for(IMaterial material : crystalForm.getMaterials()) {
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			String crystalOredict = miscHelper.getOredictName("hbm:crystal", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());

			IDynamicSpecConfig config = configs.get(material);
			boolean useSulfuric = config.getDefinedBoolean("hbm.byproduct", false, "Should this ore use sulfuric acid in HBMNTM's crystallizer.");
			FluidType acid = useSulfuric ? Fluids.SULFURIC_ACID : Fluids.ACID;

			helper.registerCrystallizerRecipe(
					miscHelper.getRecipeKey("hbm.ore_to_crystal", material.getName()),
					oreOredict, acid, 500, crystalInfo, 1, 600);

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("hbm.crystal_to_material", material.getName()),
					crystalOredict, materialOredict, material.getType().isDust() ? 6 : 2, 2F);
			int count = material.getType().isDust() ? 8 : 3;
			helper.registerShredderRecipe(
					miscHelper.getRecipeKey("hbm.crystal_to_dust_shredder", material.getName()),
					crystalOredict, dustOredict, count);
			count = material.getType().isDust() ? 4 : 2;
			if(material.hasExtra(1)) {
				helper.registerCentrifugeRecipe(
						miscHelper.getRecipeKey("hbm.crystal_to_dust_centrifuge", material.getName()),
						crystalOredict, new Object[] {
								dustOredict, count, dustOredict, count, extraDustOredict, 1, tinyLithium, 1,
						});
			}
			else {
				helper.registerCentrifugeRecipe(
						miscHelper.getRecipeKey("hbm.crystal_to_dust_centrifuge", material.getName()),
						crystalOredict, new Object[] {
								dustOredict, count, dustOredict, count, tinyLithium, 1, tinyLithium, 1,
						});
			}
		}
		for(IMaterial material : Sets.difference(moduleData.getMaterials(), MODULE_BLACKLIST)) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("hbm.byproduct", "minecraft:gravel",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in HBMNTM's centrifuge.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			int count = material.getType().isDust() ? 3 : 1;
			helper.registerCentrifugeRecipe(
					miscHelper.getRecipeKey("hbm.ore_to_dust_centrifuge", material.getName()),
					oreOredict, new Object[] {
							dustOredict, count, dustOredict, count, extraDustOredict, 1, byproduct, 1,
					});
		}
	}
}
