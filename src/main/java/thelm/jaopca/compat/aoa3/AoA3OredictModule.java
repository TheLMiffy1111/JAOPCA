package thelm.jaopca.compat.aoa3;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "aoa3")
public class AoA3OredictModule implements IOredictModule {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public String getName() {
		return "aoa3";
	}

	@Override
	public void register() {
		try {
			Class<?> registerClass = Class.forName("net.tslat.aoa3.common.registration.ItemRegister");
			Field listField = registerClass.getDeclaredField("itemRegistryList");
			listField.setAccessible(true);
			Class<?> wrapperClass = Class.forName("net.tslat.aoa3.common.registration.ItemRegister$ItemRegistryWrapper");
			Field itemField = wrapperClass.getDeclaredField("item");
			itemField.setAccessible(true);
			Field oredictField = wrapperClass.getDeclaredField("oreDictEntries");
			oredictField.setAccessible(true);
			List<?> list = (List<?>)listField.get(null);
			for(Object entry : list) {
				Item item = (Item)itemField.get(entry);
				String[] oredicts = (String[])oredictField.get(entry);
				if(oredicts != null) {
					for(String oredict : oredicts) {
						ApiImpl.INSTANCE.registerOredict(oredict, item);
					}
				}
			}
		}
		catch(Exception e) {
			LOGGER.error("Could not access AoA3 item list.", e);
		}
		try {
			Class<?> registerClass = Class.forName("net.tslat.aoa3.common.registration.BlockRegister");
			Field listField = registerClass.getDeclaredField("blockRegistryList");
			listField.setAccessible(true);
			Class<?> wrapperClass = Class.forName("net.tslat.aoa3.common.registration.BlockRegister$BlockRegistryWrapper");
			Field itemField = wrapperClass.getDeclaredField("itemBlock");
			itemField.setAccessible(true);
			Field oredictField = wrapperClass.getDeclaredField("oreDictEntries");
			oredictField.setAccessible(true);
			List<?> list = (List<?>)listField.get(null);
			for(Object entry : list) {
				Item item = (Item)itemField.get(entry);
				String[] oredicts = (String[])oredictField.get(entry);
				if(oredicts != null) {
					for(String oredict : oredicts) {
						ApiImpl.INSTANCE.registerOredict(oredict, item);
					}
				}
			}
		}
		catch(Exception e) {
			LOGGER.error("Could not access AoA3 block list.", e);
		}
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("gemBlueGemstone", "aoa3:blue_gemstones");
		api.registerOredict("gemGreenGemstone", "aoa3:green_gemstones");
		api.registerOredict("gemPurpleGemstone", "aoa3:purple_gemstones");
		api.registerOredict("gemRedGemstone", "aoa3:red_gemstones");
		api.registerOredict("gemWhiteGemstone", "aoa3:white_gemstones");
		api.registerOredict("gemYellowGemstone", "aoa3:yellow_gemstones");
		api.registerOredict("ingotRunium", "aoa3:runium_chunk");
		api.registerOredict("ingotChargedRunium", "aoa3:charged_runium_chunk");
		api.registerOredict("crystalChestboneFragments", "aoa3:chestbone_fragment");
		api.registerOredict("crystalFootboneFragments", "aoa3:footbone_fragment");
		api.registerOredict("crystalLegboneFragments", "aoa3:legbone_fragment");
		api.registerOredict("crystalSkullboneFragments", "aoa3:skullbone_fragment");
	}
}
