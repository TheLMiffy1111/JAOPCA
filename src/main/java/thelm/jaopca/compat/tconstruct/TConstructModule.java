package thelm.jaopca.compat.tconstruct;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "tconstruct")
public class TConstructModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminum", "aluminium", "amethyst", "cobalt", "copper", "debris", "diamond", "emerald", "gold", "iron",
			"lead", "netherite", "netherite_scrap", "nickel", "osmium", "platinum", "quartz", "silver", "tin",
			"tungsten", "uranium", "zinc"));

	private static boolean jaopcaOnly = true;

	static {
		if(ModList.get().isLoaded("allthemodium")) {
			Collections.addAll(BLACKLIST, "allthemodium", "unobtainium", "vibranium");
		}
		if(ModList.get().isLoaded("materialis")) {
			Collections.addAll(BLACKLIST, "cloggrum", "froststeel", "iesnium", "quicksilver", "regalium", "starmetal", "utherium");
		}
		if(ModList.get().isLoaded("bettercompat")) {
			Collections.addAll(BLACKLIST, "black_opal", "certus_quartz", "moonstone", "thallasium");
		}
		if(ModList.get().isLoaded("natureminerals")) {
			Collections.addAll(BLACKLIST, "astrite", "kunzite", "stibnite", "uvarovite");
		}
		if(ModList.get().isLoaded("taiga")) {
			Collections.addAll(BLACKLIST, "abyssum", "aurorium", "dilithium", "duranite", "eezo", "jauxum", "karmesine",
					"osram", "ovium", "palladium", "prometheum", "tiberium", "uru", "valyrium", "vibranium");
		}
	}

	@Override
	public String getName() {
		return "tconstruct";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "molten");
		builder.put(1, "molten");
		return builder.build();
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
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IFluidFormType fluidFormType = FluidFormType.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		IForm moltenForm = api.getForm("molten");
		Set<IMaterial> moltenMaterials = moltenForm.getMaterials();
		ResourceLocation metalTooltipLocation = new ResourceLocation("tconstruct:tooltips/metal");
		ResourceLocation brickTooltipLocation = new ResourceLocation("tconstruct:tooltips/clay");
		ResourceLocation gemTooltipLocation = new ResourceLocation("tconstruct:tooltips/gem_large");
		ResourceLocation smallGemTooltipLocation = new ResourceLocation("tconstruct:tooltips/gem_small");
		ResourceLocation sparseOreLocation = new ResourceLocation("forge:ore_rates/sparse");
		ResourceLocation denseOreLocation = new ResourceLocation("forge:ore_rates/dense");
		ToIntFunction<FluidStack> tempFunction = stack->stack.getFluid().getAttributes().getTemperature(stack)-300;
		for(IMaterial material : moltenMaterials) {
			ResourceLocation tooltipLocation = material.getType().isIngot() ?
					(material.isSmallStorageBlock() ? brickTooltipLocation : metalTooltipLocation) :
						(material.isSmallStorageBlock() ? smallGemTooltipLocation : gemTooltipLocation);
			api.registerFluidTag(tooltipLocation, fluidFormType.getMaterialFormInfo(moltenForm, material).asFluid());
		}
		for(IMaterial material : moduleData.getMaterials()) {
			if(!jaopcaOnly || moltenMaterials.contains(material)) {
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				ResourceLocation extraMoltenLocation = miscHelper.getTagLocation("molten", material.getExtra(1).getName(), "_");
				boolean isIngot = material.getType().isIngot();
				boolean isByIngot = material.getExtra(1).getType().isIngot();
				int baseAmount = isIngot ? 90 : 100;
				int baseByAmount = isByIngot ? 90 : 100;
				int rate = isIngot ? 0 : 1;
				if(material.hasExtra(1)) {
					helper.registerOreMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten_singular."+material.getName()),
							CompoundIngredientObject.difference(oreLocation, sparseOreLocation, denseOreLocation),
							moltenLocation, (isIngot ? baseAmount*2 : baseAmount),
							rate, tempFunction, getMeltTimeFunction(isIngot ? 2.5F : 1.5F),
							extraMoltenLocation, (isByIngot ? baseByAmount*2 : baseByAmount));
					helper.registerOreMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten_sparse."+material.getName()),
							CompoundIngredientObject.intersection(oreLocation, sparseOreLocation),
							moltenLocation, (isIngot ? baseAmount : baseAmount/2),
							rate, tempFunction, getMeltTimeFunction(isIngot ? 1.5F : 1F),
							extraMoltenLocation, (isByIngot ? baseByAmount : baseByAmount/2));
					helper.registerOreMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten_dense."+material.getName()),
							CompoundIngredientObject.intersection(oreLocation, denseOreLocation),
							moltenLocation, (isIngot ? baseAmount*6 : baseAmount*3),
							rate, tempFunction, getMeltTimeFunction(4.5F),
							extraMoltenLocation, (isByIngot ? baseByAmount*6 : baseByAmount*3));
				}
				else {
					helper.registerOreMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten_singular."+material.getName()),
							CompoundIngredientObject.difference(oreLocation, sparseOreLocation, denseOreLocation),
							moltenLocation, (isIngot ? baseAmount*2 : baseAmount),
							rate, tempFunction, getMeltTimeFunction(isIngot ? 2.5F : 1.5F));
					helper.registerOreMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten_sparse."+material.getName()),
							CompoundIngredientObject.intersection(oreLocation, sparseOreLocation),
							moltenLocation, (isIngot ? baseAmount : baseAmount/2),
							rate, tempFunction, getMeltTimeFunction(isIngot ? 1.5F : 1F));
					helper.registerOreMeltingRecipe(
							new ResourceLocation("jaopca", "tconstruct.ore_to_molten_dense."+material.getName()),
							CompoundIngredientObject.intersection(oreLocation, denseOreLocation),
							moltenLocation, (isIngot ? baseAmount*6 : baseAmount*3),
							rate, tempFunction, getMeltTimeFunction(4.5F));
				}
				if(material.getType() == MaterialType.INGOT) {
					ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
					ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
					if(material.hasExtra(1)) {
						helper.registerOreMeltingRecipe(
								new ResourceLocation("jaopca", "tconstruct.raw_material_to_molten."+material.getName()),
								rawMaterialLocation,
								moltenLocation, baseAmount,
								0, tempFunction, getMeltTimeFunction(1.5F),
								extraMoltenLocation, (isByIngot ? baseByAmount : baseByAmount/2));
					}
					else {
						helper.registerOreMeltingRecipe(
								new ResourceLocation("jaopca", "tconstruct.raw_material_to_molten."+material.getName()),
								rawMaterialLocation,
								moltenLocation, baseAmount,
								0, tempFunction, getMeltTimeFunction(1.5F));
					}
					if(itemTags.contains(rawStorageBlockLocation)) {
						if(material.hasExtra(1)) {
							helper.registerOreMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.raw_storage_block_to_molten."+material.getName()),
									rawStorageBlockLocation,
									moltenLocation, baseAmount*9,
									0, tempFunction, getMeltTimeFunction(6F),
									extraMoltenLocation, (isByIngot ? baseAmount*9 : baseAmount*9/2));
						}
						else {
							helper.registerOreMeltingRecipe(
									new ResourceLocation("jaopca", "tconstruct.raw_storage_block_to_molten."+material.getName()),
									rawStorageBlockLocation,
									moltenLocation, baseAmount*9,
									0, tempFunction, getMeltTimeFunction(6F));
						}
					}
				}
			}
		}
	}

	public ToIntFunction<FluidStack> getMeltTimeFunction(float timeFactor) {
		return stack->IMeltingRecipe.calcTime(stack.getFluid().getAttributes().getTemperature(stack)-300, timeFactor);
	}
}
