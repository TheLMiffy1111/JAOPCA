package thelm.jaopca.modules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import exnihiloomnia.compatibility.ENOCompatibility;
import exnihiloomnia.registries.hammering.HammerRegistry;
import exnihiloomnia.registries.hammering.HammerRegistryEntry;
import exnihiloomnia.registries.ore.OreRegistry;
import exnihiloomnia.registries.sifting.SieveRegistry;
import exnihiloomnia.registries.sifting.SieveRegistryEntry;
import exnihiloomnia.util.enums.EnumMetadataBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.utils.Utils;

public class ModuleExNihiloOmnia extends ModuleBase {

	/*
	 * Ex Nihilo Omnia has ores for each dimension, so I split this module into four.
	 * Also Ex Nihilo Omnia has the problem of filtering out ores at the wrong time, so we help them.
	 * However we will not help ENO do dimension-only ores, we instead let the users decide which they want.
	 */

	public static final BlockProperties GRAVEL_PROPERTIES = new BlockProperties().
			setMaterialMapColor(Material.SAND).
			setHardnessFunc((entry)->{return 0.6F;}).
			setSoundType(SoundType.GROUND).
			setFallable(true);
	public static final BlockProperties SAND_PROPERTIES = new BlockProperties().
			setMaterialMapColor(Material.SAND).
			setHardnessFunc((entry)->{return 0.6F;}).
			setSoundType(SoundType.GROUND).
			setFallable(true);
	public static final BlockProperties DUST_PROPERTIES = new BlockProperties().
			setMaterialMapColor(Material.SAND).
			setHardnessFunc((entry)->{return 0.6F;}).
			setSoundType(SoundType.SNOW).
			setFallable(true);

	public static final ItemEntry ORE_CRUSHED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "oreCrushed", new ModelResourceLocation("jaopca:ore_crushed#inventory"));
	public static final ItemEntry ORE_POWDERED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "orePowdered", new ModelResourceLocation("jaopca:ore_powdered#inventory"));
	public static final ItemEntry ORE_SAND_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "oreSand", new ModelResourceLocation("jaopca:ore_sand#normal")).setBlockProperties(SAND_PROPERTIES);
	//oreFine because oreDust may be used
	public static final ItemEntry ORE_DUST_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "oreFine", new ModelResourceLocation("jaopca:ore_fine#normal")).setBlockProperties(DUST_PROPERTIES);

	public static final ArrayList<String> EXISTING_ORES = Lists.<String>newArrayList();

	public static final String ENDER_IO_MESSAGE = "" +
			"<recipeGroup name=\"JAOPCA_ENO\">" +
			"<recipe name=\"%s\" energyCost=\"2000\">" +
			"<input>" +
			"<itemStack oreDictionary=\"%s\" />" +
			"</input>" +
			"<output>" +
			"<itemStack oreDictionary=\"%s\" number=\"5\" />" +
			"<itemStack oreDictionary=\"%s\" number=\"2\" chance=\"0.3\" />" +
			"</output>" +
			"</recipe>" + 
			"</recipeGroup>";

	public static boolean add_smeltery_melting;
	public static boolean aa_crusher;
	public static boolean mekanism_crusher;
	public static boolean sag_mill;

	@Override
	public String getName() {
		return "exnihiloomnia";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		List<ItemEntry> ret = Lists.<ItemEntry>newArrayList(ORE_CRUSHED_ENTRY, ORE_POWDERED_ENTRY, ORE_SAND_ENTRY, ORE_DUST_ENTRY);
		for(ItemEntry entry : ret) {
			entry.blacklist.addAll(EXISTING_ORES);
		}
		return ret;
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreCrushed")) {
			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct")) {
				ModuleTinkersConstruct.addMeltingRecipe("oreCrushed"+entry.getOreName(), FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 36);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("orePowdered")) {
			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct")) {
				ModuleTinkersConstruct.addMeltingRecipe("orePowdered"+entry.getOreName(), FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 36);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreSand")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("oreSand", entry, 1), new Object[] {
					"oreCrushed"+entry.getOreName(),
					"oreCrushed"+entry.getOreName(),
					"oreCrushed"+entry.getOreName(),
					"oreCrushed"+entry.getOreName(),
			}));

			Utils.addSmelting(Utils.getOreStack("oreSand", entry, 1), Utils.getOreStack("ingot", entry, 1), 0);

			addOreHammerRecipe(JAOPCAApi.BLOCKS_TABLE.get("oreSand", entry.getOreName()), Utils.getOreStack("orePowdered", entry, 1));

			if(add_smeltery_melting && Loader.isModLoaded("tconstruct") && FluidRegistry.isFluidRegistered(Utils.to_under_score(entry.getOreName()))) {
				ModuleTinkersConstruct.addMeltingRecipe("oreSand"+entry.getOreName(), FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144);
			}

			if(aa_crusher && Loader.isModLoaded("actuallyadditions")) {
				addActuallyAdditionsCrusherRecipe(Utils.getOreStack("oreSand", entry, 1), Utils.getOreStack("orePowdered", entry, 5), Utils.getOreStack("orePowdered", entry, 2), 30);
			}

			if(mekanism_crusher && Loader.isModLoaded("Mekanism")) {
				ModuleMekanism.addCrusherRecipe(Utils.getOreStack("oreSand", entry, 1), Utils.getOreStack("orePowdered", entry, 6));
			}

			if(sag_mill && Loader.isModLoaded("EnderIO")) {
				addOreSAGMillRecipe("oreSand"+entry.getOreName(), "orePowdered"+entry.getOreName());
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreFine")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("oreFine", entry, 1), new Object[] {
					"orePowdered"+entry.getOreName(),
					"orePowdered"+entry.getOreName(),
					"orePowdered"+entry.getOreName(),
					"orePowdered"+entry.getOreName(),
			}));

			Utils.addSmelting(Utils.getOreStack("oreFine", entry, 1), Utils.getOreStack("ingot", entry, 1), 0);

			if(add_smeltery_melting && Loader.isModLoaded("tconstruct") && FluidRegistry.isFluidRegistered(Utils.to_under_score(entry.getOreName()))) {
				ModuleTinkersConstruct.addMeltingRecipe("oreFine"+entry.getOreName(), FluidRegistry.getFluid(Utils.to_under_score(entry.getOreName())), 144);
			}
		}
	}

	public static void addOreHammerRecipe(Block input, ItemStack output) {
		HammerRegistryEntry hammer = new HammerRegistryEntry(input.getDefaultState(), EnumMetadataBehavior.IGNORED);
		hammer.addReward(output, 100, 0);
		hammer.addReward(output, 100, 0);
		hammer.addReward(output, 100, 0);
		hammer.addReward(output, 100, 0);
		hammer.addReward(output, 50, 2);
		hammer.addReward(output, 5, 1);
		HammerRegistry.add(hammer);
	}

	public static void addOreSieveRecipe(Block input, ItemStack output, int chance) {
		SieveRegistryEntry sieve = new SieveRegistryEntry(input.getDefaultState(), EnumMetadataBehavior.IGNORED);
		sieve.addReward(output, chance);
		SieveRegistry.add(sieve);
	}

	public static void addActuallyAdditionsCrusherRecipe(ItemStack input, ItemStack output, ItemStack output2, int output2chance) {
		//Use reflection because ActuallyAdditions does not need a module and I don't want to create a class for this
		try {
			Class<?> apiClass = Class.forName("de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI");
			Method addMethod = apiClass.getMethod("addCrusherRecipe", ItemStack.class, ItemStack.class, ItemStack.class, Integer.TYPE);
			addMethod.invoke(null, input, output, output2, output2chance);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void addOreSAGMillRecipe(String input, String output) {
		FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", String.format(ENDER_IO_MESSAGE, input, input, output, output));
	}

	static {
		//This should work
		for(String name : OreRegistry.registry.keySet()) {
			EXISTING_ORES.add(StringUtils.capitalize(name));
		};

		try {
			add_smeltery_melting = ENOCompatibility.class.getField("add_smeltery_melting").getBoolean(null);
		}
		catch(Exception e) {
			add_smeltery_melting = false;
		}
		
		try {
			aa_crusher = ENOCompatibility.class.getField("aa_crusher").getBoolean(null);
			mekanism_crusher = ENOCompatibility.class.getField("mekanism_crusher").getBoolean(null);
			sag_mill = ENOCompatibility.class.getField("sag_mill").getBoolean(null);
		}
		catch(Exception e) {
			aa_crusher = mekanism_crusher = sag_mill = false;
		}
	}
}
