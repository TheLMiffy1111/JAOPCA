package thelm.jaopca.compat.gregapi;

import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "gregapi")
public class GregApiOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "gregapi";
	}

	@Override
	public void register() {
		// Blacklisting *most* alternative names
		ApiImpl.INSTANCE.registerBlacklistedMaterialNames(
				"Adamant", "Adluorite", "Aluminum", "AluminumBrass", "Am241", "Am242", "Ancient", "ArcaneAsh", "Ash", "AshDark",
				"AshesDark", "AshesVolcanic", "AshVolcanic", "Astatine209", "At209", "Au198", "Beeswax", "BeesWax", "Beewax",
				"BeeWax", "Bibibium", "Bibiunium", "Bitriennium", "BoricAcid", "Brick", "BrickNether", "CalciumSulphate", "Chrome",
				"ClayCompound", "Co60", "CoffeeDust", "Columbium", "DarkAsh", "Electrotine", "Elementium", "Flour", "FZDarkIron",
				"Gol198", "Gregorium", "HSLA", "Iritanium", "IronWood", "Kalium", "KnightMetal", "Lantanium", "Lantanum", "Lanthanum",
				"Meat", "NaquadahAlloy", "Natrium", "NaturalAluminum", "Neptunium237", "Nitre", "Np237", "Oats", "ObsidianSteel",
				"Oilshale", "ParaffinWax", "Paraffinwax", "Peanutwood", "Pepper", "PhasedGold", "PhasedIron", "Phosphorous",
				"Plutonium244", "PotassiumBisulphate", "PotassiumPersulphate", "PotassiumPyrosulphate", "PotassiumSulphate",
				"PotassiumSulphide", "PotassiumSulphite", "Pu238", "Pu239", "Pu240", "Pu241", "QuartzSmoky", "QuickSilver",
				"Quicksilver", "Radium226", "Refractorywax", "RefractoryWax", "Salpeter", "Saphire", "SmokyQuartz", "SodiumBisulphate",
				"SodiumHydrogenSulfate", "SodiumHydrogenSulphate", "SodiumPersulphate", "SodiumPyrosulphate", "SodiumSulphate",
				"SodiumSulphide", "SodiumSulphite", "Sulphur", "Tantalium", "TeslatineAlloy", "Teslatite", "Th232", "Thorium232",
				"Triseptbium", "TungstenOxide", "TungstenSteel", "U233", "U235", "U238", "Unbihexium", "Unbipentium", "Unocthexium",
				"Unoctoctium", "Unoctpentium", "Unoctseptium", "Unpentbium", "Unquadpentium", "Unseptquadium", "Unstableingot", "Uran",
				"Uranium238", "UraniumEnriched", "Vibrant", "VolcanicAsh", "Wolfram", "WolframCarbide", "Wolframium", "Wolframsteel",
				"WolframSteel", "WoodSealed", "WrougtIron");
		ApiImpl.INSTANCE.registerUsedPlainPrefixes("ingotAnti", "gemAnti", "crystalAnti", "dustAnti", "dustSmallAnti");
	}
}
