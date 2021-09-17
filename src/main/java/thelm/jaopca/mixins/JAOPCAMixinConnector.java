package thelm.jaopca.mixins;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import thelm.jaopca.JAOPCA;

public class JAOPCAMixinConnector implements IMixinConnector {

	@Override
	public void connect() {
		JAOPCA.mixinLoaded = true;
		Mixins.addConfiguration("jaopca.mixins.json");
	}
}
