package thelm.jaopca.compat.tconstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.smeltery.TinkerSmeltery;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "TConstruct")
public class TConstructCompatModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configBlockToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configCrystallineToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();
	private static Set<String> configToNuggetBlacklist = new TreeSet<>();
	private static Set<String> configMaterialCastBlacklist = new TreeSet<>();
	private static Set<String> configNuggetCastBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = true;

	@Override
	public String getName() {
		return "tconstruct_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		BLACKLIST.addAll(FluidType.fluidTypes.keySet());
		if(Loader.isModLoaded("Mariculture")) {
			Collections.addAll(BLACKLIST, "Magnesium", "Rutile", "Titanium");
		}
		IMiscHelper helper = MiscHelper.INSTANCE;
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material melting recipes added."),
				configMaterialToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block melting recipes added."),
				configBlockToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget melting recipes added."),
				configNuggetToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have dust melting recipes added."),
				configDustToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.crystallineToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have crystalline melting recipes added."),
				configCrystallineToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material casting recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have block casting recipes added."),
				configToBlockBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toNuggetMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget casting recipes added."),
				configToNuggetBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialCastMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material cast recipes added."),
				configMaterialCastBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetCastMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget cast recipes added."),
				configNuggetCastBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		ItemStack ingotCast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
		ItemStack gemCast = new ItemStack(TinkerSmeltery.metalPattern, 1, 26);
		ItemStack nuggetCast = new ItemStack(TinkerSmeltery.metalPattern, 1, 27);
		Fluid alubrass = TinkerSmeltery.moltenAlubrassFluid;
		Fluid gold = TinkerSmeltery.moltenGoldFluid;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !BLACKLIST.contains(name) && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName(".molten", name);
				int baseAmount = 144;
				int baseTemp = TConstructModule.tempFunction.applyAsInt(material);
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(!configMaterialToMoltenBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						String blockOredict = miscHelper.getOredictName("block", name);
						helper.registerMeltingRecipe(
								miscHelper.getRecipeKey("tconstruct.material_to_molten", name),
								materialOredict, blockOredict, moltenName, baseAmount, baseTemp-50);
					}
					if(!configBlockToMoltenBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.block_to_molten", name),
									blockOredict, blockOredict, moltenName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9), baseTemp+100);
						}
					}
					if(!configNuggetToMoltenBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.nugget_to_molten", name),
									nuggetOredict, blockOredict, moltenName, baseAmount/9, baseTemp-100);
						}
					}
					if(!configDustToMoltenBlacklist.contains(name)) {
						String dustOredict = miscHelper.getOredictName("dust", name);
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(dustOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.dust_to_molten", name),
									dustOredict, blockOredict, moltenName, baseAmount, baseTemp-50);
						}
					}
					if(!configCrystallineToMoltenBlacklist.contains(name)) {
						String crystallineOredict = miscHelper.getOredictName("crystalline", name);
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(crystallineOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("tconstruct.crystalline_to_molten", name),
									crystallineOredict, blockOredict, moltenName, baseAmount, baseTemp-50);
						}
					}
					if(!configToMaterialBlacklist.contains(name)) {
						String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
						helper.registerTableCastingRecipe(
								miscHelper.getRecipeKey("tconstruct.molten_to_material", name),
								(type.isIngot() ? ingotCast : gemCast), moltenName, baseAmount, materialOredict,
								80, false);
					}
					if(!configToBlockBlacklist.contains(name)) {
						String blockOredict = miscHelper.getOredictName("block", name);
						if(oredict.contains(blockOredict)) {
							helper.registerBasinCastingRecipe(
									miscHelper.getRecipeKey("tconstruct.molten_to_block", name),
									null, moltenName, baseAmount*(material.isSmallStorageBlock() ? 4 : 9), blockOredict,
									400, false);
						}
					}
					if(!configToNuggetBlacklist.contains(name)) {
						String nuggetOredict = miscHelper.getOredictName("nugget", name);
						if(oredict.contains(nuggetOredict)) {
							helper.registerTableCastingRecipe(
									miscHelper.getRecipeKey("tconstruct.molten_to_nugget", name),
									nuggetCast, moltenName, baseAmount/9, nuggetOredict,
									40, false);
						}
					}
				}
			}
			if(!type.isDust() && !BLACKLIST.contains(name) && !configMaterialCastBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(type.isIngot()) {
					helper.registerTableCastingRecipe(
							miscHelper.getRecipeKey("tconstruct.material_cast_alubrass", name),
							materialOredict, alubrass, 144, ingotCast,
							50, false);
					helper.registerTableCastingRecipe(
							miscHelper.getRecipeKey("tconstruct.material_cast_gold", name),
							materialOredict, gold, 288, ingotCast,
							50, false);
				}
				else {
					helper.registerTableCastingRecipe(
							miscHelper.getRecipeKey("tconstruct.material_cast_alubrass", name),
							materialOredict, alubrass, 144, gemCast,
							50, false);
					helper.registerTableCastingRecipe(
							miscHelper.getRecipeKey("tconstruct.material_cast_gold", name),
							materialOredict, gold, 288, gemCast,
							50, false);
				}
			}
			if(!type.isDust() && !BLACKLIST.contains(name) && !configNuggetCastBlacklist.contains(name)) {
				String nuggetOredict = miscHelper.getOredictName("nugget", name);
				if(oredict.contains(nuggetOredict)) {
					helper.registerTableCastingRecipe(
							miscHelper.getRecipeKey("tconstruct.nugget_cast_alubrass", name),
							nuggetOredict, alubrass, 144, nuggetCast,
							50, false);
					helper.registerTableCastingRecipe(
							miscHelper.getRecipeKey("tconstruct.nugget_cast_gold", name),
							nuggetOredict, gold, 288, nuggetCast,
							50, false);
				}
			}
		}
	}
}
