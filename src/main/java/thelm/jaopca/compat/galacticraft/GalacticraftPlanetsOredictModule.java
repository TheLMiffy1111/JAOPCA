package thelm.jaopca.compat.galacticraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "galacticraftplanets")
public class GalacticraftPlanetsOredictModule implements IOredictModule {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public String getName() {
		return "galacticraftplanets";
	}

	@Override
	public void register() {
		try {
			Class.forName("micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks").getMethod("oreDictRegistration").invoke(null);
		}
		catch(Exception e) {
			LOGGER.error("Could not invoke Galacticraft Mars oredict register method", e);
		}
		try {
			Class.forName("micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks").getMethod("oreDictRegistration").invoke(null);
		}
		catch(Exception e) {
			LOGGER.error("Could not invoke Galacticraft Asteroids oredict register method", e);
		}
		try {
			Class.forName("micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems").getMethod("oreDictRegistrations").invoke(null);
		}
		catch(Exception e) {
			LOGGER.error("Could not invoke Galacticraft Asteroids oredict register method", e);
		}
		try {
			Class.forName("micdoodle8.mods.galacticraft.planets.venus.VenusBlocks").getMethod("oreDictRegistration").invoke(null);
		}
		catch(Exception e) {
			LOGGER.error("Could not invoke Galacticraft Venus oredict register method", e);
		}
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("oreTitanium", "galacticraftplanets:asteroids_block@4");
		api.registerOredict("dustSolar", "galacticraftplanets:basic_item_venus@4");
	}
}
