package thelm.jaopca.compat.engtoolbox;

import emasher.items.Items;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "eng_toolbox")
public class EngToolboxOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "eng_toolbox";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("dustCobalt", new ItemStack(Items.dusts(), 1, 30));
		api.registerOredict("dustArdite", new ItemStack(Items.dusts(), 1, 31));
	}
}
