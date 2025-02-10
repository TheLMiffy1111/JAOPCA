package thelm.jaopca.modules.active;

import net.minecraft.world.item.Items;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.data.IDataModule;
import thelm.jaopca.api.data.JAOPCADataModule;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCADataModule
public class MinecraftDataModule implements IDataModule {

	@Override
	public String getName() {
		return "minecraft";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper helper = MiscHelper.INSTANCE;
		api.registerItemTag(helper.getTagLocation("crystals", "coal"), Items.COAL);
		api.registerItemTag(helper.getTagLocation("ingots", "netherite_scrap"), Items.NETHERITE_SCRAP);
	}
}
