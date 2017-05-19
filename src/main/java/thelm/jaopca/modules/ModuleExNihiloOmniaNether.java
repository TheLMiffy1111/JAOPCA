package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import exnihiloomnia.blocks.ENOBlocks;
import exnihiloomnia.compatibility.ENOCompatibility;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleExNihiloOmniaNether extends ModuleBase {

	public static final ItemEntry ORE_NETHER_BROKEN_ENTRY = new ItemEntry(EnumEntryType.ITEM, "oreNetherBroken", new ModelResourceLocation("jaopca:oreNetherBroken#inventory"));
	public static final ItemEntry ORE_NETHER_GRAVEL_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "oreNetherGravel", new ModelResourceLocation("jaopca:oreNetherGravel#normal")).setBlockProperties(ModuleExNihiloOmnia.GRAVEL_PROPERTIES);

	@Override
	public String getName() {
		return "exnihiloomnianether";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("exnihiloomnia");
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		List<ItemEntry> ret = Lists.<ItemEntry>newArrayList(ORE_NETHER_BROKEN_ENTRY, ORE_NETHER_GRAVEL_ENTRY);
		for(ItemEntry entry : ret) {
			entry.blacklist.addAll(ModuleExNihiloOmnia.EXISTING_ORES);
		}
		return ret;
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreNetherBroken")) {
			ModuleExNihiloOmnia.addOreSieveRecipe(ENOBlocks.GRAVEL_NETHER, Utils.getOreStack("oreNetherBroken", entry, 1), (int)(15D/entry.getEnergyModifier())+2);

			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct") && FluidRegistry.isFluidRegistered(entry.getOreName().toLowerCase())) {
				ModuleTinkersConstruct.addMeltingRecipe("oreNetherBroken"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 36);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreNetherGravel")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("oreNetherGravel", entry, 1), new Object[] {
					"oreNetherBroken"+entry.getOreName(),
					"oreNetherBroken"+entry.getOreName(),
					"oreNetherBroken"+entry.getOreName(),
					"oreNetherBroken"+entry.getOreName(),
			}));

			Utils.addSmelting(Utils.getOreStack("oreNetherGravel", entry, 1), Utils.getOreStack("ingot", entry, 1), 0);

			ModuleExNihiloOmnia.addOreHammerRecipe(JAOPCAApi.BLOCKS_TABLE.get("oreNetherGravel", entry.getOreName()), Utils.getOreStack("oreCrushed", entry, 1));

			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct") && FluidRegistry.isFluidRegistered(entry.getOreName().toLowerCase())) {
				ModuleTinkersConstruct.addMeltingRecipe("oreNetherGravel"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 144);
			}

			if(ENOCompatibility.aa_crusher && Loader.isModLoaded("actuallyadditions")) {
				ModuleExNihiloOmnia.addActuallyAdditionsCrusherRecipe(Utils.getOreStack("oreNetherGravel", entry, 1), Utils.getOreStack("oreCrushed", entry, 5), Utils.getOreStack("oreCrushed", entry, 2), 30);
			}

			if(ENOCompatibility.mekanism_crusher && Loader.isModLoaded("Mekanism")) {
				ModuleMekanism.addCrusherRecipe(Utils.getOreStack("oreNetherGravel", entry, 1), Utils.getOreStack("oreCrushed", entry, 6));
			}

			if(ENOCompatibility.sag_mill && Loader.isModLoaded("EnderIO")) {
				ModuleExNihiloOmnia.addOreSAGMillRecipe("oreNetherGravel"+entry.getOreName(), "oreCrushed"+entry.getOreName());
			}
		}
	}
}
