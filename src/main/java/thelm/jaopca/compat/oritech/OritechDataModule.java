package thelm.jaopca.compat.oritech;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.IDataModule;
import thelm.jaopca.api.data.JAOPCADataModule;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCADataModule(modDependencies = "oritech@[0.15.3,)")
public class OritechDataModule implements IDataModule {

	@Override
	public String getName() {
		return "oritech";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper helper = MiscHelper.INSTANCE;
		String[] toRegister = {
				"copper", "gold", "iron", "nickel", "platinum"};
		for(String material : toRegister) {
			api.registerItemTag(
					helper.getTagLocation("oritech:clumps", material),
					ResourceLocation.parse("oritech:"+material+"_clump"));
			api.registerItemTag(
					helper.getTagLocation("oritech:small_clumps", material),
					ResourceLocation.parse("oritech:small_"+material+"_clump"));
			api.registerItemTag(
					helper.getTagLocation("oritech:gems", material),
					ResourceLocation.parse("oritech:"+material+"_gem"));
		}
	}
}
