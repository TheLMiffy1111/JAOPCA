package thelm.jaopca.compat.create;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.IDataModule;
import thelm.jaopca.api.data.JAOPCADataModule;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCADataModule(modDependencies = "create")
public class CreateDataModule implements IDataModule {

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper helper = MiscHelper.INSTANCE;
		String[] toRegister = {
				"aluminum", "copper", "gold", "iron", "lead", "nickel", "osmium", "platinum", "quicksilver",
				"silver", "tin", "uranium", "zinc"};
		for(String material : toRegister) {
			api.registerItemTag(
					helper.getTagLocation("create:crushed_raw_materials", material),
					ResourceLocation.parse("create:crushed_raw_"+material));
		}
	}
}
