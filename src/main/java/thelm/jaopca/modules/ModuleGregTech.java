package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Lists;

import gregtech.api.items.ToolDictNames;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IItemRequest;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.item.ItemBase;
import thelm.jaopca.api.item.ItemProperties;
import thelm.jaopca.api.utils.Utils;

public class ModuleGregTech extends ModuleBase {

	public static final ArrayList<String> ORE_BLACKLIST = Lists.newArrayList(
			"Aluminium", "Beryllium", "Bismuth", "Cobalt", "Copper", "Gold", "Iridium", "Iron", "Lead", "Lithium", "Molybdenum", "Neodymium", "Nickel",
			"Niobium", "Osmium", "Palladium", "Platinum", "Silver", "Sulfur", "Thorium", "Tin", "Uranium", "Uranium235", "Zinc", "Almandine", "BandedIron",
			"BlueTopaz", "BrownLimonite", "Calcite", "Cassiterite", "CassiteriteSand", "Chalcopyrite", "Chromite", "Cinnabar", "Coal", "Cobaltite",
			"Cooperite", "Diamond", "Emerald", "Galena", "Garnierite", "GreenSapphire", "Grossular", "Ilmenite", "Bauxite", "Lazurite", "Magnesite",
			"Magnetite", "Molybdenite", "Phosphate", "Powellite", "Pyrite", "Pyrolusite", "Pyrope", "RockSalt", "Ruby", "Salt", "Saltpeter", "Sapphire",
			"Scheelite", "Sodalite", "Spessartine", "Sphalerite", "Stibnite", "Tanzanite", "Tetrahedrite", "Topaz", "Tungstate", "Uraninite", "Wulfenite",
			"YellowLimonite", "NetherQuartz", "CertusQuartz", "Quartzite", "Graphite", "Jasper", "Lignite", "Olivine", "Opal", "Amethyst", "Redstone",
			"Lapis", "Tantalite", "Apatite", "Phosphor", "GarnetRed", "GarnetYellow", "Vinteum", "VanadiumMagnetite", "Bastnasite", "Pentlandite",
			"Spodumene", "Lepidolite", "Glauconite", "Bentonite", "Pitchblende", "Monazite", "Malachite", "Barite", "Talc", "Soapstone", "Naquadah",
			"NaquadahEnriched", "Oilsands", "Quartz"
			);
	public static final ArrayList<String> MATERIAL_BLACKLIST = Lists.newArrayList(
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
			"Cocoa", "Wheat", "Quartz"
			); 

	public static final ItemProperties WASHABLE_PROPERTIES = new ItemProperties().setItemClass(ItemWashableBase.class);

	public static final ItemEntry CRUSHED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushedGreg", "crushed", new ModelResourceLocation("jaopca:crushed#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.ORE).
			setProperties(WASHABLE_PROPERTIES);
	public static final ItemEntry PURIFIED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushedPurifiedGreg", "crushedPurified", new ModelResourceLocation("jaopca:crushed_purified#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.ORE).
			setProperties(WASHABLE_PROPERTIES);
	public static final ItemEntry CENTRIFUGED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushedCentrifuged", new ModelResourceLocation("jaopca:crushed_centrifuged#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.ORE);
	public static final ItemEntry IMPURE_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustImpure", new ModelResourceLocation("jaopca:dust_impure#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.ORE).
			setProperties(WASHABLE_PROPERTIES);
	public static final ItemEntry PURE_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustPure", new ModelResourceLocation("jaopca:dust_pure#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.ORE).
			setProperties(WASHABLE_PROPERTIES);
	public static final ItemEntry CHIPPED_GEM_ENTRY = new ItemEntry(EnumEntryType.ITEM, "gemChipped", new ModelResourceLocation("jaopca:gem_chipped#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.GEM);
	public static final ItemEntry FLAWED_GEM_ENTRY = new ItemEntry(EnumEntryType.ITEM, "gemFlawed", new ModelResourceLocation("jaopca:gem_flawed#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.GEM);
	public static final ItemEntry FLAWLESS_GEM_ENTRY = new ItemEntry(EnumEntryType.ITEM, "gemFlawless", new ModelResourceLocation("jaopca:gem_flawless#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.GEM);
	public static final ItemEntry EXQUISITE_GEM_ENTRY = new ItemEntry(EnumEntryType.ITEM, "gemExquisite", new ModelResourceLocation("jaopca:gem_exquisite#inventory"), ORE_BLACKLIST).
			setOreTypes(EnumOreType.GEM);

	@Override
	public String getName() {
		return "gregtech";
	}

	@Override
	public List<String> addToPrefixBlacklist() {
		return Lists.newArrayList(
				"ingotHot", "gemChipped", "gemFlawed", "gemFlawless", "gemExquisite"
				);
	}

	@Override
	public List<String> getDependencies() {
		return Lists.newArrayList("nugget", "dust", "tinydust", "smalldust");
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return Utils.enumSetOf(EnumOreType.DUSTLESS);
	}

	@Override
	public List<String> getOreBlacklist() {
		return MATERIAL_BLACKLIST;
	}

	@Override
	public List<? extends IItemRequest> getItemRequests() {
		return Lists.newArrayList(ItemEntryGroup.of(CRUSHED_ENTRY, PURIFIED_ENTRY, CENTRIFUGED_ENTRY, IMPURE_DUST_ENTRY, PURE_DUST_ENTRY), ItemEntryGroup.of(CHIPPED_GEM_ENTRY, FLAWED_GEM_ENTRY, FLAWLESS_GEM_ENTRY, EXQUISITE_GEM_ENTRY));
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushedGreg")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().
				input("ore"+entry.getOreName(), 1).
				outputs(Utils.getJAOPCAOrOreStack("crushedGreg", "crushed", entry, 1)).
				duration(40).EUt(16).
				buildAndRegister();

				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("ore"+entry.getOreName(), 1).
				outputs(Utils.getJAOPCAOrOreStack("crushedGreg", "crushed", entry, 2)).
				chancedOutput(Utils.getOreStack("dust", entry, 1), 1000).
				duration(200).EUt(24).
				buildAndRegister();

				if(entry.getOreType() == EnumOreType.INGOT) {
					Utils.addSmelting(Utils.getJAOPCAOrOreStack("crushedGreg", "crushed", entry, 1), Utils.getOreStack("nugget", entry, 8), 0F);
				}
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushedPurifiedGreg")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.ORE_WASHER_RECIPES.recipeBuilder().
				input("crushed"+entry.getOreName(), 1).
				fluidInputs(ModHandler.getWater(1000)).
				outputs(Utils.getJAOPCAOrOreStack("crushedPurifiedGreg", "crushedPurified", entry, 1),
						Utils.getOreStackExtra("dustTiny", entry, 1),
						OreDictUnifier.get(OrePrefix.dust, Materials.Stone)).
				buildAndRegister();

				RecipeMaps.ORE_WASHER_RECIPES.recipeBuilder().
				input("crushed"+entry.getOreName(), 1).
				fluidInputs(ModHandler.getDistilledWater(1000)).
				outputs(Utils.getJAOPCAOrOreStack("crushedPurifiedGreg", "crushedPurified", entry, 1),
						Utils.getOreStackExtra("dustTiny", entry, 1),
						OreDictUnifier.get(OrePrefix.dust, Materials.Stone)).
				duration(300).
				buildAndRegister();

				if(entry.getOreType() == EnumOreType.INGOT) {
					Utils.addSmelting(Utils.getJAOPCAOrOreStack("crushedPurifiedGreg", "crushedPurified", entry, 1), Utils.getOreStack("nugget", entry, 7), 0F);
				}
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushedCentrifuged")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.THERMAL_CENTRIFUGE_RECIPES.recipeBuilder().
				input("crushed"+entry.getOreName(), 1).
				duration(Utils.energyI(entry, 98) * 20).
				outputs(Utils.getOreStack("crushedCentrifuged", entry, 1),
						Utils.getOreStackExtra("dustTiny", entry, 1),
						OreDictUnifier.get(OrePrefix.dust, Materials.Stone)).
				buildAndRegister();

				RecipeMaps.THERMAL_CENTRIFUGE_RECIPES.recipeBuilder().
				input("crushedPurified"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("crushedCentrifuged", entry, 1),
						Utils.getOreStackSecondExtra("dustTiny", entry, 1)).
				duration(Utils.energyI(entry, 98) * 20).
				EUt(60).
				buildAndRegister();

				RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().
				input("crushedCentrifuged"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 1)).
				duration(20).EUt(16).
				buildAndRegister();

				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("crushedCentrifuged"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 1)).
				chancedOutput(Utils.getOreStackThirdExtra("dustSmall", entry, 1), 1000).
				duration(40).EUt(16).
				buildAndRegister();

				Utils.addShapelessOreRecipe(Utils.getOreStack("dust", entry, 1), new Object[] {
						getToolNameByCharacter('h'),
						"crushedCentrifuged"+entry.getOreName(),
				});

				if(entry.getOreType() == EnumOreType.INGOT) {
					Utils.addSmelting(Utils.getOreStack("crushedCentrifuged", entry, 1), Utils.getOreStack("nugget", entry, 7), 0F);
				}
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustImpure")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().
				input("crushed"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dustImpure", entry, 1)).
				duration(10).EUt(16).
				buildAndRegister();

				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("crushed"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dustImpure", entry, 1)).
				chancedOutput(Utils.getOreStackExtra("dust", entry, 1), 1000).
				duration(100).EUt(24).
				buildAndRegister();

				Utils.addShapelessOreRecipe(Utils.getOreStack("dustImpure", entry, 1), new Object[] {
						getToolNameByCharacter('h'),
						"crushed"+entry.getOreName(),
				});

				RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder().
				input("dustImpure"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 1),
						Utils.getOreStackExtra("dustTiny", entry, 1)).
				duration(Utils.energyI(entry, 98) * 4).
				EUt(24).
				buildAndRegister();

				if(entry.getOreType() == EnumOreType.INGOT) {
					Utils.addSmelting(Utils.getOreStack("dustImpure", entry, 1), Utils.getOreStack("ingot", entry, 1), 0F);
				}
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustPure")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName()) && Utils.doesOreNameExist("crushedPurified"+entry.getOreName())) {
				RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().
				input("crushedPurified"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dustPure", entry, 1)).
				duration(20).EUt(16).
				buildAndRegister();

				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("crushedPurified"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dustPure", entry, 1)).
				chancedOutput(Utils.getOreStackSecondExtra("dustTiny", entry, 1), 1000).
				duration(40).EUt(16).
				buildAndRegister();

				Utils.addShapelessOreRecipe(Utils.getOreStack("dustPure", entry, 1), new Object[] {
						getToolNameByCharacter('h'),
						"crushedPurified"+entry.getOreName(),
				});

				RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder().
				input("dustPure"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 1),
						Utils.getOreStackSecondExtra("dustTiny", entry, 1)).
				duration(Utils.energyI(entry, 98) * 4).
				EUt(5).
				buildAndRegister();

				if(entry.getOreType() == EnumOreType.INGOT) {
					Utils.addSmelting(Utils.getOreStack("dustPure", entry, 1), Utils.getOreStack("ingot", entry, 1), 0F);
				}
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("gemChipped")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.SIFTER_RECIPES.recipeBuilder().
				input("crushedPurified"+entry.getOreName(), 1).
				chancedOutput(Utils.getOreStack("gemExquisite", entry, 1), 100).
				chancedOutput(Utils.getOreStack("gemFlawless", entry, 1), 400).
				chancedOutput(Utils.getOreStack("gem", entry, 1), 1500).
				chancedOutput(Utils.getOreStack("gemFlawed", entry, 1), 2000).
				chancedOutput(Utils.getOreStack("gemChipped", entry, 1), 4000).
				chancedOutput(Utils.getOreStack("dust", entry, 1), 5000).
				duration(800).EUt(16).
				buildAndRegister();

				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("gemChipped"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dustTiny", entry, 1)).
				duration(5).EUt(2).
				buildAndRegister();
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("gemFlawed")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("gemFlawed"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dustTiny", entry, 2)).
				duration(10).EUt(2).
				buildAndRegister();
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("gemFlawless")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("gemFlawless"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 2)).
				duration(40).EUt(2).
				buildAndRegister();
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("gemExquisite")) {
			if(!MATERIAL_BLACKLIST.contains(entry.getOreName())) {
				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("gemExquisite"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 4)).
				duration(80).EUt(2).
				buildAndRegister();
			}
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			switch(entry.getOreType()) {
			case INGOT:
			case INGOT_ORELESS:
				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("ingot"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 1)).
				duration(20).EUt(2).
				buildAndRegister();
				break;
			case GEM:
			case GEM_ORELESS:
				RecipeMaps.MACERATOR_RECIPES.recipeBuilder().
				input("gem"+entry.getOreName(), 1).
				outputs(Utils.getOreStack("dust", entry, 1)).
				duration(20).EUt(2).
				buildAndRegister();

				RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().
				input("dust"+entry.getOreName(), 1).
				fluidInputs(ModHandler.getWater(200)).
				chancedOutput(Utils.getOreStack("gem", entry, 1), 7000).
				duration(2000).EUt(24).
				buildAndRegister();

				RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().
				input("dust"+entry.getOreName(), 1).
				fluidInputs(ModHandler.getDistilledWater(200)).
				chancedOutput(Utils.getOreStack("gem", entry, 1), 9000).
				duration(1500).EUt(24).
				buildAndRegister();
				break;
			default:
				break;
			}
		}
	}

	public static String getToolNameByCharacter(char character) {
		switch(character) {
		case 'b': return ToolDictNames.craftingToolBlade.name();
		case 'c': return ToolDictNames.craftingToolCrowbar.name();
		case 'd': return ToolDictNames.craftingToolScrewdriver.name();
		case 'f': return ToolDictNames.craftingToolFile.name();
		case 'h': return ToolDictNames.craftingToolHardHammer.name();
		case 'i': return ToolDictNames.craftingToolSolderingIron.name();
		case 'j': return ToolDictNames.craftingToolSolderingMetal.name();
		case 'k': return ToolDictNames.craftingToolKnife.name();
		case 'm': return ToolDictNames.craftingToolMortar.name();
		case 'p': return ToolDictNames.craftingToolDrawplate.name();
		case 'r': return ToolDictNames.craftingToolSoftHammer.name();
		case 's': return ToolDictNames.craftingToolSaw.name();
		case 'w': return ToolDictNames.craftingToolWrench.name();
		case 'x': return ToolDictNames.craftingToolWireCutter.name();
		default: return null;
		}
	}

	public static class ItemWashableBase extends ItemBase {

		public ItemWashableBase(ItemEntry itemEntry, IOreEntry oreEntry) {
			super(itemEntry, oreEntry);
		}

		@Override
		public boolean onEntityItemUpdate(EntityItem entityItem) {
			if(entityItem.getEntityWorld().isRemote) {
				return false;
			}
			ItemStack replacement = ItemStack.EMPTY;
			if(this.getItemEntry() == CRUSHED_ENTRY || this.getItemEntry() == PURIFIED_ENTRY) {
				replacement = Utils.getOreStack("crushedCentrifuged", this.getOreEntry(), entityItem.getItem().getCount());
			}
			else if(this.getItemEntry() == IMPURE_DUST_ENTRY || this.getItemEntry() == PURE_DUST_ENTRY) {
				replacement = Utils.getOreStack("dust", this.getOreEntry(), entityItem.getItem().getCount());
			}
			else {
				return false;
			}
			BlockPos pos = new BlockPos(entityItem);
			IBlockState state = entityItem.getEntityWorld().getBlockState(pos);
			int waterLevel = state.getBlock() instanceof BlockCauldron ? state.getValue(BlockCauldron.LEVEL) : 0;
			if(waterLevel == 0) {
				return false;
			}
			entityItem.getEntityWorld().setBlockState(pos, state.withProperty(BlockCauldron.LEVEL, waterLevel - 1));
			entityItem.setItem(replacement);
			return false;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			super.addInformation(stack, worldIn, tooltip, flagIn);
			if(this.getItemEntry() == IMPURE_DUST_ENTRY || this.getItemEntry() == PURE_DUST_ENTRY) {
				tooltip.add(I18n.translateToLocal("metaitem.dust.tooltip.purify"));
			}
		}
	}
}
