package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
/*import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.FuelType;*/
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.FuelType;

import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.IFuelHandler;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.item.ItemBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleAbyssalCraft extends ModuleBase {

	public static final BlockProperties CRYSTAL_CLUSTER_PROPERTIES = new BlockProperties().
			setHardnessFunc(entry->0.4F).
			setResistanceFunc(entry->0.8F).
			setLightOpacityFunc(entry->0).
			setSoundType(SoundType.GLASS).
			setHarvestTool("pickaxe").
			setHarvestLevel(3).
			setFull(false).
			setOpaque(false).
			setBoundingBox(new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.7D, 0.8D));
	public static final ItemEntry CRYSTAL_FRAGMENT_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crystalFragment", new ModelResourceLocation("jaopca:crystal_fragment#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Potassium", "Abyssalnite", "LiquifiedCoralium", "Dreadium", "Tin", "Copper", "Silicon", "Magnesium", "Aluminium", "Zinc"
			));
	public static final ItemEntry CRYSTAL_SHARD_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crystalShard", new ModelResourceLocation("jaopca:crystal_shard#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Potassium", "Abyssalnite", "LiquifiedCoralium", "Dreadium", "Tin", "Copper", "Silicon", "Magnesium", "Aluminium", "Zinc"
			));
	public static final ItemEntry CRYSTAL_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crystalAbyss", "crystal", new ModelResourceLocation("jaopca:crystal_abyss#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Potassium", "Abyssalnite", "LiquifiedCoralium", "Dreadium", "Tin", "Copper", "Silicon", "Magnesium", "Aluminium", "Zinc"
			));
	public static final ItemEntry CRYSTAL_CLUSTER_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "crystalCluster", new ModelResourceLocation("jaopca:crystal_cluster#normal"), ImmutableList.<String>of(
			"Iron", "Gold", "Potassium", "Abyssalnite", "LiquifiedCoralium", "Dreadium", "Tin", "Copper", "Silicon", "Magnesium", "Aluminium", "Zinc"
			)).setProperties(CRYSTAL_CLUSTER_PROPERTIES).
			skipWhenGrouped(true);

	@Override
	public String getName() {
		return "abyssalcraft";
	}

	@Override
	public List<ItemEntryGroup> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(CRYSTAL_FRAGMENT_ENTRY, CRYSTAL_SHARD_ENTRY, CRYSTAL_ENTRY, CRYSTAL_CLUSTER_ENTRY));
	}

	@Override
	public void init() {
		AbyssalCraftAPI.registerFuelHandler(new JAOPCAAbyssFuelHandler(), FuelType.CRYSTALLIZER);
		AbyssalCraftAPI.registerFuelHandler(new JAOPCAAbyssFuelHandler(), FuelType.TRANSMUTATOR);

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalFragment")) {
			Utils.addShapelessOreRecipe(Utils.getOreStack("crystalFragment", entry, 9), new Object[] {
					"crystalShard"+entry.getOreName(),
			});
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalShard")) {
			Utils.addShapelessOreRecipe(Utils.getOreStack("crystalShard", entry, 1), new Object[] {
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
			});

			Utils.addShapelessOreRecipe(Utils.getOreStack("crystalShard", entry, 9), new Object[] {
					"crystal"+entry.getOreName(),
			});

			addSingleCrystallization("ingot"+entry.getOreName(), "crystalShard"+entry.getOreName(), 4, 0.1F);
			addSingleCrystallization("ore"+entry.getOreName(), "crystalShard"+entry.getOreName(), 4, 0.1F);
			if(Utils.doesOreNameExist("nugget"+entry.getOreName())) {
				addSingleCrystallization("nugget"+entry.getOreName(), "crystalShard"+entry.getOreName(), 1, 0.1F);
				addTransmutation("crystalShard"+entry.getOreName(), "nugget"+entry.getOreName(), 1, 0.2F);
			}
			if(Utils.doesOreNameExist("dust"+entry.getOreName())) {
				addSingleCrystallization("dust"+entry.getOreName(), "crystalShard"+entry.getOreName(), 4, 0.1F);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalAbyss")) {
			Utils.addShapelessOreRecipe(Utils.getJAOPCAOrOreStack("crystalAbyss", "crystal", entry, 1), new Object[] {
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
			});

			Utils.addShapelessOreRecipe(Utils.getJAOPCAOrOreStack("crystalAbyss", "crystal", entry, 9), new Object[] {
					"crystalCluster"+entry.getOreName(),
			});

			if(Utils.doesOreNameExist("block"+entry.getOreName())) {
				addSingleCrystallization("block"+entry.getOreName(), "crystal"+entry.getOreName(), 4, 0.9F);
			}
			addTransmutation("crystal"+entry.getOreName(), "ingot"+entry.getOreName(), 1, 0.2F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalCluster")) {
			Utils.addShapelessOreRecipe(Utils.getOreStack("crystalCluster", entry, 9), new Object[] {
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
			});
		}
	}

	public static void addSingleCrystallization(String input, String output, int amount, float xp) {
		AbyssalCraftAPI.addSingleCrystallization(input, output, amount, xp);
	}

	public static void addTransmutation(String input, String output, int amount, float xp) {
		AbyssalCraftAPI.addTransmutation(input, output, amount, xp);
	}

	public static class JAOPCAAbyssFuelHandler implements IFuelHandler {
		@Override
		public int getBurnTime(ItemStack fuel) {
			if(fuel.getItem() instanceof ItemBase) {
				ItemBase item = (ItemBase)fuel.getItem();
				String prefix = item.itemEntry.prefix;
				return prefix.equals("crystalCluster") ? 12150 :
					prefix.equals("crystal") ? 1350 :
						prefix.equals("crystalShard") ? 150 :
							prefix.equals("crystalFragment") ? 17 : 0;
			}
			return 0;
		}
	}
}
