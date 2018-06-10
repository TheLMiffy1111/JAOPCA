package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleTinyDust extends ModuleBase {

	public static final ItemEntry TINY_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustTiny", new ModelResourceLocation("jaopca:dust_tiny#inventory")).
			skipWhenGrouped(Loader.isModLoaded("techreborn")).
			setOreTypes(EnumOreType.values());

	public static final ArrayList<String> IC2_BLACKLIST = Lists.newArrayList("Copper", "Gold", "Iron", "Lead", "Lithium", "Silver", "Tin", "Lapis", "Bronze");

	public static final ArrayList<String> GREGTECH_BLACKLIST = Lists.newArrayList(
			"Aluminium", "Americium", "Antimony", "Arsenic", "Barium", "Beryllium", "Bismuth", "Boron", "Caesium", "Calcium", "Carbon", "Cadmium", "Cerium",
			"Chrome", "Cobalt", "Copper", "Dysprosium", "Erbium", "Europium", "Gadolinium", "Gallium", "Gold", "Holmium", "Indium", "Iridium", "Iron",
			"Lanthanum", "Lead", "Lithium", "Lutetium", "Magnesium", "Manganese", "Molybdenum", "Neodymium", "Darmstadtium", "Nickel", "Niobium", "Osmium",
			"Palladium", "Phosphorus", "Platinum", "Plutonium", "Plutonium241", "Potassium", "Praseodymium", "Promethium", "Rubidium", "Samarium",
			"Scandium", "Silicon", "Silver", "Sodium", "Strontium", "Sulfur", "Tantalum", "Tellurium", "Terbium", "Thorium", "Thulium", "Tin", "Titanium",
			"Tungsten", "Uranium", "Uranium235", "Vanadium", "Yttrium", "Zinc", "Almandine", "Andradite", "AnnealedCopper", "Asbestos", "Ash", "BandedIron",
			"BatteryAlloy", "BlueTopaz", "Bone", "Brass", "Bronze", "BrownLimonite", "Calcite", "Cassiterite", "CassiteriteSand", "Chalcopyrite", "Charcoal",
			"Chromite", "Cinnabar", "Clay", "Coal", "Cobaltite", "Cooperite", "Cupronickel", "DarkAsh", "Diamond", "Electrum", "Emerald", "Galena",
			"Garnierite", "GreenSapphire", "Grossular", "Ice", "Ilmenite", "Rutile", "Bauxite", "Magnesiumchloride", "Invar", "Kanthal", "Lazurite",
			"Magnalium", "Magnesite", "Magnetite", "Molybdenite", "Nichrome", "NiobiumNitride", "NiobiumTitanium", "Obsidian", "Phosphate", "PigIron",
			"Plastic", "Epoxid", "Silicone", "Polycaprolactam", "Polytetrafluoroethylene", "Powellite", "Pumice", "Pyrite", "Pyrolusite", "Pyrope",
			"RockSalt", "Rubber", "RawRubber", "Ruby", "Salt", "Saltpeter", "Sapphire", "Scheelite", "SiliconDioxide", "Sodalite", "SolderingAlloy",
			"Spessartine", "Sphalerite", "StainlessSteel", "Steel", "Stibnite", "Tanzanite", "Tetrahedrite", "TinAlloy", "Topaz", "Tungstate", "Ultimet",
			"Uraninite", "Uvarovite", "VanadiumGallium", "Wood", "WroughtIron", "Wulfenite", "YellowLimonite", "YttriumBariumCuprate", "NetherQuartz",
			"CertusQuartz", "Quartzite", "Graphite", "Graphene", "Jasper", "Osmiridium", "WoodSealed", "Glass", "Borax", "Lignite", "Olivine", "Opal",
			"Amethyst", "Redstone", "Lapis", "Blaze", "EnderPearl", "EnderEye", "Flint", "Diatomite", "VolcanicAsh", "Niter", "Tantalite", "HydratedCoal",
			"Apatite", "SterlingSilver", "RoseGold", "BlackBronze", "BismuthBronze", "BlackSteel", "RedSteel", "BlueSteel", "DamascusSteel", "TungstenSteel",
			"RedAlloy", "CobaltBrass", "Phosphor", "Basalt", "Andesite", "Diorite", "GarnetRed", "GarnetYellow", "Marble", "Sugar", "Vinteum", "Redrock",
			"PotassiumFeldspar", "Biotite", "GraniteBlack", "GraniteRed", "Chrysotile", "Realgar", "VanadiumMagnetite", "BasalticMineralSand",
			"GraniticMineralSand", "GarnetSand", "QuartzSand", "Bastnasite", "Pentlandite", "Spodumene", "Pollucite", "Lepidolite", "Glauconite",
			"GlauconiteSand", "Vermiculite", "Bentonite", "FullersEarth", "Pitchblende", "Monazite", "Malachite", "Mirabilite", "Mica", "Trona", "Barite",
			"Gypsum", "Alunite", "Dolomite", "Wollastonite", "Zeolite", "Kyanite", "Kaolinite", "Talc", "Soapstone", "Concrete", "IronMagnetic",
			"SteelMagnetic", "NeodymiumMagnetic", "TungstenCarbide", "VanadiumSteel", "Hssg", "Hsse", "Hsss", "Naquadah", "NaquadahAlloy", "NaquadahEnriched",
			"Naquadria", "Tritanium", "Duranium", "Gunpowder", "Oilsands", "Paper", "RareEarth", "Stone", "Glowstone", "NetherStar", "Endstone", "Netherrack",
			"Cocoa", "Wheat"
			);

	@Override
	public String getName() {
		return "tinydust";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.newArrayList("dust");
	}

	@Override
	public List<ItemEntryGroup> getItemRequests() {
		if(Loader.isModLoaded("ic2")) {
			TINY_DUST_ENTRY.blacklist.addAll(IC2_BLACKLIST);
		}
		if(Loader.isModLoaded("gregtech")) {
			TINY_DUST_ENTRY.blacklist.addAll(IC2_BLACKLIST);
		}
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(TINY_DUST_ENTRY));
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustTiny")) {
			Utils.addShapelessOreRecipe(Utils.getOreStack("dust", entry, 1), new Object[] {
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
			});

			if(Utils.doesOreNameExist("dustSmall"+entry.getOreName())) {
				Utils.addShapedOreRecipe(Utils.getOreStack("dustTiny", entry, 9), new Object[] {
						"D ",
						"  ",
						'D', "dust"+entry.getOreName(),
				});
			}
			else {
				Utils.addShapelessOreRecipe(Utils.getOreStack("dustTiny", entry, 9), new Object[] {
						"dust"+entry.getOreName(),
				});
			}
		}
	}
}
