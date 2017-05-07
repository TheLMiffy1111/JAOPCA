package thelm.jaopca.modules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;
import thelm.jaopca.api.block.BlockBase;
import thelm.jaopca.api.block.BlockProperties;

public class ModuleExNihiloOmnia extends ModuleAbstract {

	/*
	 * Ex Nihilo Omnia has ores for each dimension, so I split this module into four.
	 * Also Ex Nihilo Omnia has the problem of filtering out ores at the wrong time, so we help them.
	 * However we will not help ENO do dimension-only ores, we instead let the users decide which they want.
	 * 
	 * EnderIO compat will come later
	 */

	public static final BlockProperties SAND_PROPERTIES = new BlockProperties().setMaterialMapColor(Material.SAND);
	public static final ItemEntry ORE_CRUSHED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "oreCrushed", new ModelResourceLocation("jaopca:oreCrushed#inventory"));
	public static final ItemEntry ORE_POWDERED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "orePowdered", new ModelResourceLocation("jaopca:orePowdered#inventory"));
	public static final ItemEntry ORE_SAND_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "oreSand", new ModelResourceLocation("jaopca:oreSand#normal")).setBlockProperties(SAND_PROPERTIES);
	//oreFine because oreFine may be used
	public static final ItemEntry ORE_DUST_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "oreFine", new ModelResourceLocation("jaopca:oreFine#normal")).setBlockProperties(SAND_PROPERTIES);

	public static final ArrayList<String> EXISTING_ORES = Lists.<String>newArrayList();

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
	public void setCustomProperties() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreSand")) {
			BlockBase block = JAOPCAApi.BLOCKS_TABLE.get("oreSand", entry.getOreName());
			block.setFallable().setSoundType(SoundType.SAND).setHardness(0.6F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreFine")) {
			BlockBase block = JAOPCAApi.BLOCKS_TABLE.get("oreFine", entry.getOreName());
			block.setFallable().setSoundType(SoundType.SNOW).setHardness(0.6F);
		}
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreCrushed")) {
			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct")) {
				ModuleTinkersConstruct.addMeltingRecipe("oreCrushed"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 36);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("orePowdered")) {
			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct")) {
				ModuleTinkersConstruct.addMeltingRecipe("orePowdered"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 36);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreSand")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(getOreStack("oreSand", entry, 1), new Object[] {
					"oreCrushed"+entry.getOreName(),
					"oreCrushed"+entry.getOreName(),
					"oreCrushed"+entry.getOreName(),
					"oreCrushed"+entry.getOreName(),
			}));

			addSmelting(getOreStack("oreSand", entry, 1), entry.getIngotStack().copy(), 0);

			addOreHammerRecipe(JAOPCAApi.BLOCKS_TABLE.get("oreSand", entry.getOreName()), getOreStack("orePowdered", entry, 1));

			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct")) {
				ModuleTinkersConstruct.addMeltingRecipe("oreSand"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 144);
			}

			if(ENOCompatibility.aa_crusher && Loader.isModLoaded("actuallyadditions")) {
				addActuallyAdditionsCrusherRecipe(getOreStack("oreSand", entry, 1), getOreStack("orePowdered", entry, 5), getOreStack("orePowdered", entry, 2), 30);
			}
			
			if(ENOCompatibility.mekanism_crusher & Loader.isModLoaded("Mekanism")) {
				ModuleMekanism.addCrusherRecipe(getOreStack("oreSand", entry, 1), getOreStack("orePowdered", entry, 6));
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreFine")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(getOreStack("oreFine", entry, 1), new Object[] {
					"orePowdered"+entry.getOreName(),
					"orePowdered"+entry.getOreName(),
					"orePowdered"+entry.getOreName(),
					"orePowdered"+entry.getOreName(),
			}));

			addSmelting(getOreStack("oreFine", entry, 1), entry.getIngotStack().copy(), 0);

			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct")) {
				ModuleTinkersConstruct.addMeltingRecipe("oreFine"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 144);
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

	static {
		//This should work
		OreRegistry.registry.keySet().forEach((name) -> {
			EXISTING_ORES.add(name.substring(0, 1).toUpperCase()+name.substring(1));
		});
	}
}
