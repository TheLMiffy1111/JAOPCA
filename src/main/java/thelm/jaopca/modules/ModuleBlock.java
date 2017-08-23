package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.utils.Utils;

public class ModuleBlock extends ModuleBase {

	public static final BlockProperties METAL_BLOCK_PROPERTIES = new BlockProperties().
			setMaterialMapColor(Material.IRON).
			setHardnessFunc(entry->5F).
			setResistanceFunc(entry->10F).
			setSoundType(SoundType.METAL).
			setBeaconBase(true);
	public static final ItemEntry BLOCK_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "block", new ModelResourceLocation("jaopca:block#normal"), ImmutableList.<String>of(
			"Iron", "Gold", "Coal", "Lapis", "Diamond", "Emerald", "Prismarine", "Redstone"
			)).setBlockProperties(METAL_BLOCK_PROPERTIES).
			setOreTypes(EnumOreType.values());

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
			String s = "ingot";
			switch(entry.getOreType()) {
			case GEM:
			case GEM_ORELESS:
				s = "gem";
				break;
			case DUST:
				s = "dust";
				break;
			default:
				break;
			}
			Utils.addShapelessOreRecipe(Utils.getOreStack("block", entry, 1), new Object[] {
					s+entry.getOreName(),
					s+entry.getOreName(),
					s+entry.getOreName(),
					s+entry.getOreName(),
					s+entry.getOreName(),
					s+entry.getOreName(),
					s+entry.getOreName(),
					s+entry.getOreName(),
					s+entry.getOreName(),
			});

			Utils.addShapelessOreRecipe(Utils.getOreStack(s, entry, 9), new Object[] {
					"block"+entry.getOreName()
			});
		}
	}
}
