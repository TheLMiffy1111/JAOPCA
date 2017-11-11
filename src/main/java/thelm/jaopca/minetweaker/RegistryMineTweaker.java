package thelm.jaopca.minetweaker;

import minetweaker.MineTweakerAPI;

public class RegistryMineTweaker {

	public static void preInit() {
		MineTweakerAPI.registerClass(JAOPCAAccess.class);
		MineTweakerAPI.registerClass(OreEntryAccess.class);
	}
}
