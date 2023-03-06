package thelm.jaopca.compat.hbm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.hbm.ntmmaterials.NTMMaterialFormType;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAModule(modDependencies = "hbm@[1.0.27_X4515,)")
public class HBMMaterialModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Actinium227", "AdvancedAlloy", "Aluminium", "Aluminum", "Americium241", "Americium242", "AmericiumRG",
			"Arsenic", "Beryllium", "Bismuth", "Boron", "Carbon", "CMBSteel", "Coal", "CoalCoke", "Cobalt",
			"Cobalt60", "Copper", "DuraSteel", "Ferrouranium", "Flux", "Ghiorsium336", "Gold", "Gold198",
			"Graphite", "Hematite", "Iron", "Lead", "Lead209", "Lignite", "LigniteCoke", "MagnetizedTungsten",
			"Malachite", "Mingrade", "NaturalAluminum", "Neptunium", "Neptunium237", "Niobium", "Obsidian",
			"PetCoke", "Plutonium", "Plutonium238", "Plutonium239", "Plutonium240", "Plutonium241", "PlutoniumRG",
			"Polonium", "Polonium210", "Radium226", "Redstone", "Saturnite", "Schrabidate", "Schrabidium",
			"Schraranium", "Slag", "Solinium", "Starmetal", "Steel", "Stone", "Tantalum", "TcAlloy", "Technetium99",
			"Thorium", "Thorium232", "Titanium", "Tungsten", "Uranium", "Uranium233", "Uranium235", "Uranium238"));

	public HBMMaterialModule() {
		NTMMaterialFormType.init();
	}
	
	private final IForm ntmMaterialForm = ApiImpl.INSTANCE.newForm(this, "hbm_material", NTMMaterialFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOTS).setSecondaryName("").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "hbm_material";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ntmMaterialForm.toRequest());
	}
}
