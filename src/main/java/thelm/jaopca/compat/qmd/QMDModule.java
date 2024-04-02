package thelm.jaopca.compat.qmd;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "qmd", classDependencies = "lach_01298.qmd.QMD")
public class QMDModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Boron", "Copper", "Gold", "Iridium", "Iron", "Lead", "Lithium", "Magnesium",
			"Nickel", "Osmium", "Platinum", "Silver", "Thorium", "Tin", "Titanium", "Uranium"));

	@Override
	public String getName() {
		return "qmd";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		builder.put(1, "dust");
		builder.put(2, "dust");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		QMDHelper helper = QMDHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Object[] fluidInput = {
				"nitric_acid", 16,
				"hydrochloric_acid", 16,
				"sulfuric_acid", 16,
		};
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());

			Object[] output = {
					dustOredict, 3, 100, 0,
			};
			if(material.hasExtra(1)) {
				String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
				output = ArrayUtils.addAll(output, extraDustOredict, 1, 10, 0);
			}
			if(material.hasExtra(2)) {
				String secondExtraDustOredict = miscHelper.getOredictName("dust", material.getExtra(2).getName());
				output = ArrayUtils.addAll(output, secondExtraDustOredict, 1, 10, 0);
			}
			helper.registerOreLeacherRecipe(
					miscHelper.getRecipeKey("qmd.ore_to_dust", material.getName()),
					oreOredict, 1, fluidInput, output);
		}
	}
}
