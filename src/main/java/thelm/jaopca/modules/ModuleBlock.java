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
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.utils.Utils;

public class ModuleBlock extends ModuleBase {

	public static final BlockProperties METAL_BLOCK_PROPERTIES = new BlockProperties().
			setMaterialMapColor(Material.IRON).
			setHardnessFunc((entry)->{return 5F;}).
			setSoundType(SoundType.METAL).
			setBeaconBase(true);
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
	public void init() {
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

			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("ingot", entry, 1), new Object[] {
					"block"+entry.getOreName()
			}));
		}
	}
}
