package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import exnihiloomnia.blocks.ENOBlocks;
import exnihiloomnia.compatibility.ENOCompatibility;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
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

public class ModuleExNihiloOmniaOverworld extends ModuleAbstract {

	public static final ItemEntry ORE_BROKEN_ENTRY = new ItemEntry(EnumEntryType.ITEM, "oreBroken", new ModelResourceLocation("jaopca:oreBroken#inventory"));
	public static final ItemEntry ORE_GRAVEL_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "oreGravel", new ModelResourceLocation("jaopca:oreGravel#normal")).setBlockProperties(ModuleExNihiloOmnia.SAND_PROPERTIES);

	@Override
	public String getName() {
		return "exnihiloomniaoverworld";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("exnihiloomnia");
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		List<ItemEntry> ret = Lists.<ItemEntry>newArrayList(ORE_BROKEN_ENTRY, ORE_GRAVEL_ENTRY);
		for(ItemEntry entry : ret) {
			entry.blacklist.addAll(ModuleExNihiloOmnia.EXISTING_ORES);
		}
		return ret;
	}

	@Override
	public void setCustomProperties() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreGravel")) {
			BlockBase block = JAOPCAApi.BLOCKS_TABLE.get("oreGravel", entry.getOreName());
			block.setFallable().setSoundType(SoundType.GROUND).setHardness(0.6F);
		}
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreBroken")) {
			ModuleExNihiloOmnia.addOreSieveRecipe(Blocks.GRAVEL, getOreStack("oreBroken", entry, 1), (int)(15D/entry.getEnergyModifier())+2);
			//Should exist
			ModuleExNihiloOmnia.addOreSieveRecipe(Blocks.SAND, getOreStack("oreCrushed", entry, 1), (int)(15D/entry.getEnergyModifier())+2);
			ModuleExNihiloOmnia.addOreSieveRecipe(ENOBlocks.DUST, getOreStack("orePowdered", entry, 1), (int)(15D/entry.getEnergyModifier())+2);

			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct") && FluidRegistry.isFluidRegistered(entry.getOreName().toLowerCase())) {
				ModuleTinkersConstruct.addMeltingRecipe("oreBroken"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 36);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("oreGravel")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(getOreStack("oreGravel", entry, 1), new Object[] {
					"oreBroken"+entry.getOreName(),
					"oreBroken"+entry.getOreName(),
					"oreBroken"+entry.getOreName(),
					"oreBroken"+entry.getOreName(),
			}));

			addSmelting(getOreStack("oreGravel", entry, 1), entry.getIngotStack().copy(), 0);

			ModuleExNihiloOmnia.addOreHammerRecipe(JAOPCAApi.BLOCKS_TABLE.get("oreGravel", entry.getOreName()), getOreStack("oreCrushed", entry, 1));

			if(ENOCompatibility.add_smeltery_melting && Loader.isModLoaded("tconstruct") && FluidRegistry.isFluidRegistered(entry.getOreName().toLowerCase())) {
				ModuleTinkersConstruct.addMeltingRecipe("oreGravel"+entry.getOreName(), FluidRegistry.getFluid(entry.getOreName().toLowerCase()), 144);
			}

			if(ENOCompatibility.aa_crusher && Loader.isModLoaded("actuallyadditions")) {
				ModuleExNihiloOmnia.addActuallyAdditionsCrusherRecipe(getOreStack("oreGravel", entry, 1), getOreStack("oreCrushed", entry, 5), getOreStack("oreCrushed", entry, 2), 30);
			}
			
			if(ENOCompatibility.mekanism_crusher & Loader.isModLoaded("Mekanism")) {
				ModuleMekanism.addCrusherRecipe(getOreStack("oreGravel", entry, 1), getOreStack("oreCrushed", entry, 6));
			}
		}
	}
}
