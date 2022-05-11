package thelm.oredictinit.lib;

import net.minecraftforge.fml.common.Loader;
import thelm.oredictinit.api.OreDictInitApi;
import thelm.oredictinit.compat.*;

public class Compat {

	public static void init() {
		OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatMinecraft());
		OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatAlternativeNaming());
		if(Loader.isModLoaded("mekanism")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatMekanism());
		}
		if(Loader.isModLoaded("draconicevolution")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatDraconicEvolution());
		}
		if(Loader.isModLoaded("galacticraftcore")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatGalacticraftCore());
		}
		if(Loader.isModLoaded("galacticraftplanets")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatGalacticraftPlanets());
		}
		if(Loader.isModLoaded("nuclearcraft")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatNuclearCraft());
		}
		if(Loader.isModLoaded("thebetweenlands")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatTheBetweenlands());
		}
		if(Loader.isModLoaded("calculator")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatCalculator());
		}
		if(Loader.isModLoaded("quark")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatQuark());
		}
		if(Loader.isModLoaded("rftools")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatRFTools());
		}
		if(Loader.isModLoaded("evilcraft")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatEvilCraft());
		}
		if(Loader.isModLoaded("astralsorcery")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatAstralSorcery());
		}
		if(Loader.isModLoaded("appliedenergistics2")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatAppliedEnergistics());
		}
		if(Loader.isModLoaded("gregtech")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatGregTech());
		}
		if(Loader.isModLoaded("frogcraftrebirth")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatFrogCraftRebirth());
		}
		if(Loader.isModLoaded("aoa3")) {
			OreDictInitApi.ORE_DICT_COMPAT_LIST.add(new CompatAOA3());
		}
	}
}
