package thelm.jaopca.minetweaker;

import minetweaker.MineTweakerAPI;

public class RegistryMineTweaker {

	public static void preInit() {
		MineTweakerAPI.registerClass(JAOPCA.class);
		MineTweakerAPI.registerClass(OreEntry.class);
	}
}
