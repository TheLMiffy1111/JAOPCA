package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.FuelType;
import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DimensionCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.lib.ACLib;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.block.IBlockWithProperty;
import thelm.jaopca.api.item.ItemBase;
import thelm.jaopca.api.item.ItemBlockBase;
import thelm.jaopca.api.item.ItemProperties;
import thelm.jaopca.api.utils.Utils;

public class ModuleAbyssalCraft extends ModuleBase {

	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Carbon", "Potassium", "Abyssalnite", "LiquifiedCoralium", "Dreadium", "Tin", "Copper", "Silicon", "Magnesium", "Aluminium", "Zinc", "Calcium", "Beryllium"
			);
	
	public static final ItemProperties CRYSTAL_PROPERTIES = new ItemProperties().
			setItemClass(ItemUnlockableBase.class);
	public static final BlockProperties CRYSTAL_CLUSTER_PROPERTIES = new BlockProperties().
			setHardnessFunc(entry->0.4F).
			setResistanceFunc(entry->0.8F).
			setLightOpacityFunc(entry->0).
			setSoundType(SoundType.GLASS).
			setHarvestTool("pickaxe").
			setHarvestLevel(3).
			setFull(false).
			setOpaque(false).
			setBoundingBox(new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.7D, 0.8D)).
			setItemBlockClass(ItemBlockUnlockableBase.class);

	public static final ItemEntry CRYSTAL_FRAGMENT_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crystalFragment", new ModelResourceLocation("jaopca:crystal_fragment#inventory"), BLACKLIST).
			setProperties(CRYSTAL_PROPERTIES);
	public static final ItemEntry CRYSTAL_SHARD_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crystalShard", new ModelResourceLocation("jaopca:crystal_shard#inventory"), BLACKLIST).
			setProperties(CRYSTAL_PROPERTIES);
	public static final ItemEntry CRYSTAL_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crystalAbyss", "crystal", new ModelResourceLocation("jaopca:crystal_abyss#inventory"), BLACKLIST).
			setProperties(CRYSTAL_PROPERTIES);
	public static final ItemEntry CRYSTAL_CLUSTER_ENTRY = new ItemEntry(EnumEntryType.BLOCK, "crystalCluster", new ModelResourceLocation("jaopca:crystal_cluster#normal"), BLACKLIST).
			setProperties(CRYSTAL_CLUSTER_PROPERTIES).
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
	public void preInit() {
		for(Item item : JAOPCAApi.ITEMS_TABLE.row("crystalFragment").values()) {
			((IUnlockableItem)item).setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id));
		}
		for(Item item : JAOPCAApi.ITEMS_TABLE.row("crystalShard").values()) {
			((IUnlockableItem)item).setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id));
		}
		for(Item item : JAOPCAApi.ITEMS_TABLE.row("crystalAbyss").values()) {
			((IUnlockableItem)item).setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id));
		}
		for(Block block : JAOPCAApi.BLOCKS_TABLE.row("crystalCluster").values()) {
			((IUnlockableItem)Item.getItemFromBlock(block)).setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id));
		}
	}

	@Override
	public void init() {
		AbyssalCraftAPI.registerFuelHandler(new JAOPCAAbyssFuelHandler(), FuelType.CRYSTALLIZER);
		AbyssalCraftAPI.registerFuelHandler(new JAOPCAAbyssFuelHandler(), FuelType.TRANSMUTATOR);

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalFragment")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("crystalFragment", entry, 9), new Object[] {
					"crystalShard"+entry.getOreName(),
			}));
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalShard")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("crystalShard", entry, 1), new Object[] {
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
					"crystalFragment"+entry.getOreName(),
			}));

			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("crystalShard", entry, 9), new Object[] {
					"crystal"+entry.getOreName(),
			}));

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
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getJAOPCAOrOreStack("crystalAbyss", "crystal", entry, 1), new Object[] {
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
					"crystalShard"+entry.getOreName(),
			}));

			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getJAOPCAOrOreStack("crystalAbyss", "crystal", entry, 9), new Object[] {
					"crystalCluster"+entry.getOreName(),
			}));

			if(Utils.doesOreNameExist("block"+entry.getOreName())) {
				addSingleCrystallization("block"+entry.getOreName(), "crystal"+entry.getOreName(), 4, 0.9F);
			}
			addTransmutation("crystal"+entry.getOreName(), "ingot"+entry.getOreName(), 1, 0.2F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalCluster")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("crystalCluster", entry, 9), new Object[] {
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
					"crystal"+entry.getOreName(),
			}));
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
				return prefix.equals("crystal") ? 1350 :
					prefix.equals("crystalShard") ? 150 :
						prefix.equals("crystalFragment") ? 17 : 0;
			}
			else if(fuel.getItem() instanceof ItemBlockBase) {
				ItemBlockBase item = (ItemBlockBase)fuel.getItem();
				String prefix = item.itemEntry.prefix;
				return prefix.equals("crystalCluster") ? 12150 : 0;
			}
			return 0;
		}
	}

	public static class ItemUnlockableBase extends ItemBase implements IUnlockableItem {

		private IUnlockCondition condition = new DefaultCondition();

		public ItemUnlockableBase(ItemEntry itemEntry, IOreEntry oreEntry) {
			super(itemEntry, oreEntry);
		}

		@Override
		public Item setUnlockCondition(IUnlockCondition condition) {
			this.condition = condition;
			return this;
		}

		@Override
		public IUnlockCondition getUnlockCondition(ItemStack stack) {
			return this.condition;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public FontRenderer getFontRenderer(ItemStack stack) {
			return APIUtils.getFontRenderer(stack);
		}
	}

	public static class ItemBlockUnlockableBase extends ItemBlockBase implements IUnlockableItem {

		private IUnlockCondition condition = new DefaultCondition();

		public ItemBlockUnlockableBase(IBlockWithProperty block) {
			super(block);
		}

		@Override
		public Item setUnlockCondition(IUnlockCondition condition) {
			this.condition = condition;
			return this;
		}

		@Override
		public IUnlockCondition getUnlockCondition(ItemStack stack) {
			return this.condition;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public FontRenderer getFontRenderer(ItemStack stack) {
			return APIUtils.getFontRenderer(stack);
		}
	}
}
