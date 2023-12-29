package thelm.jaopca.compat.nuclearcraft;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IFluidFormType;
import thelm.jaopca.api.fluids.IFluidInfo;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "nuclearcraft")
public class NuclearCraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "boron", "cobalt", "copper", "gold", "iron", "lead", "lithium", "magnesium",
			"platinum", "silver", "thorium", "tin", "uranium", "zinc"));

	private final IFluidFormSettings slurrySettings = FluidFormType.INSTANCE.getNewSettings().
			setDensityFunction(material->400).setTemperatureFunction(material->400);
	private final IForm slurryForm = ApiImpl.INSTANCE.newForm(this, "nuclearcraft_slurries", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("nuclearcraft:slurries").
			setDefaultMaterialBlacklist(BLACKLIST).setSettings(slurrySettings);
	private final IForm cleanSlurryForm = ApiImpl.INSTANCE.newForm(this, "nuclearcraft_clean_slurries", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT, MaterialType.INGOT_LEGACY).setSecondaryName("nuclearcraft:clean_slurries").
			setDefaultMaterialBlacklist(BLACKLIST).setSettings(slurrySettings);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, slurryForm, cleanSlurryForm).setGrouped(true);

	@Override
	public String getName() {
		return "nuclearcraft";
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
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		NuclearCraftHelper helper = NuclearCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IFluidFormType fluidFormType = FluidFormType.INSTANCE;
		ResourceLocation aquaRegiaLocation = new ResourceLocation("forge:aqua_regia_acid");
		Fluid hydrochloricAcid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("nuclearcraft:hydrochloric_acid"));
		Fluid nitricAcid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("nuclearcraft:nitric_acid"));
		Fluid calciumSulfateSolution = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("nuclearcraft:calcium_sulfate_solution"));
		for(IMaterial material : formRequest.getMaterials()) {
			IFluidInfo slurryInfo = fluidFormType.getMaterialFormInfo(slurryForm, material);
			ResourceLocation slurryLocation = miscHelper.getTagLocation("nuclearcraft:slurries", material.getName());
			IFluidInfo cleanSlurryInfo = fluidFormType.getMaterialFormInfo(cleanSlurryForm, material);
			ResourceLocation cleanSlurryLocation = miscHelper.getTagLocation("nuclearcraft:clean_slurries", material.getName());
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());

			helper.registerLeacherRecipe(
					new ResourceLocation("jaopca", "nuclearcraft.ore_to_slurry."+material.getName()),
					oreLocation, 1, aquaRegiaLocation, 250, slurryInfo, 1000, 1, 1, 1);

			helper.registerCentrifugeRecipe(
					new ResourceLocation("jaopca", "nuclearcraft.slurry_to_clean_slurry."+material.getName()),
					slurryLocation, 1000, new Object[] {
							cleanSlurryInfo, 800,
							hydrochloricAcid, 50,
							nitricAcid, 50,
							calciumSulfateSolution, 10,
					}, 1, 1, 1);

			helper.registerCrystallizerRecipe(
					new ResourceLocation("jaopca", "nuclearcraft.clean_slurry_to_dust."+material.getName()),
					cleanSlurryLocation, 400, dustLocation, 2, 1, 1, 1);
		}
	}

}
