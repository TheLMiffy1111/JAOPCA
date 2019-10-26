package thelm.jaopca.compat.mekanism;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mekanism.api.gas.Gas;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.compat.mekanism.gases.GasFormType;
import thelm.jaopca.utils.ApiImpl;

public class MekanismCommonEventHandler {

	public static final MekanismCommonEventHandler INSTANCE = new MekanismCommonEventHandler();

	public void onConstruct() {
		GasFormType.init();
		ApiImpl.INSTANCE.registerRegistryEventHandler(Gas.class, this::onGasRegister);
	}

	public void onGasRegister(IForgeRegistry<Gas> registry) {
		GasFormType.registerGases(registry);
	}
}
