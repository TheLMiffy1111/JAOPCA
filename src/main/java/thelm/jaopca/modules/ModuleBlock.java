package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;
import thelm.jaopca.api.block.BlockBase;
import thelm.jaopca.api.block.BlockProperties;

public class ModuleBlock extends ModuleAbstract {

	public static final BlockProperties METAL_BLOCK_PROPERTIES = new BlockProperties().setMaterialMapColor(Material.IRON);
	public static final ItemEntry BLOCK_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "block", new ModelResourceLocation("jaopca:block#normal"), ImmutableList.<String>of(
			"Iron", "Gold"
			)).setBlockProperties(METAL_BLOCK_PROPERTIES);
	
	@Override
	public String getName() {
		return "block";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(BLOCK_ENTRY);
	}

	@Override
	public void setCustomProperties() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("block")) {
			BlockBase block = JAOPCAApi.BLOCKS_TABLE.get("block", entry.getOreName());
			block.setFallable().setSoundType(SoundType.METAL).setHardness(5F);
		}
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("block")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(JAOPCAApi.BLOCKS_TABLE.get("block", entry.getOreName())), new Object[] {
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
					"ingot"+entry.getOreName(),
			}));
			
			GameRegistry.addRecipe(new ShapelessOreRecipe(entry.getIngotStack(), new Object[] {
					"block"+entry.getOreName()
			}));
		}
	}
}
