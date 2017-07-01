package thelm.jaopca.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.block.BlockBase;
import thelm.jaopca.api.item.ItemBase;
import thelm.jaopca.api.item.ItemBlockBase;

public class ClientProxy extends CommonProxy {

	public static final CreativeTabs JAOPCA_TAB = new CreativeTabs("jaopca") {
		@Override
		public Item getTabIconItem() {
			return Items.GLOWSTONE_DUST;
		}

		@Override
		public boolean hasSearchBar() {
			return true;
		}
	}.setBackgroundImageName("item_search.png");

	public static final IItemColor JAOPCA_ITEM_COLOR = new IItemColor() {
		@Override
		public int getColorFromItemstack(ItemStack stack, int tintIndex) {
			if(tintIndex == 0) {
				if(stack.getItem() instanceof ItemBase) {
					return ((ItemBase)stack.getItem()).oreEntry.getColor();
				}

				if(stack.getItem() instanceof ItemBlockBase) {
					return ((ItemBlockBase)stack.getItem()).oreEntry.getColor();
				}
			}
			return 0xFFFFFF;
		}
	};

	public static final IBlockColor JAOPCA_BLOCK_COLOR = new IBlockColor() {
		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			if(tintIndex == 0) {
				if(state.getBlock() instanceof BlockBase) {
					return ((BlockBase)state.getBlock()).oreEntry.getColor();
				}
			}
			return 0xFFFFFF;
		}
	};

	@Override
	public void handleBlockRegister(ItemEntry itemEntry, IOreEntry oreEntry, Block block, ItemBlock itemblock) {
		super.handleBlockRegister(itemEntry, oreEntry, block, itemblock);
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			block.setCreativeTab(JAOPCA_TAB);
			ModelLoader.setCustomModelResourceLocation(itemblock, 0, itemEntry.itemModelLocation);
			ModelLoader.setCustomStateMapper(block, new JAOPCAStateMap(itemEntry.itemModelLocation));
		}
	}

	@Override
	public void handleItemRegister(ItemEntry itemEntry, IOreEntry oreEntry, Item item) {
		super.handleItemRegister(itemEntry, oreEntry, item);
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			item.setCreativeTab(JAOPCA_TAB);
			ModelLoader.setCustomModelResourceLocation(item, 0, itemEntry.itemModelLocation);
		}
	}

	@Override
	public void initItemColors() {
		super.initItemColors();
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			ItemColors itemcolors = Minecraft.getMinecraft().getItemColors();
			for(Item item : JAOPCAApi.ITEMS_TABLE.values()) {
				itemcolors.registerItemColorHandler(JAOPCA_ITEM_COLOR, item);
			}
		}
	}

	@Override
	public void initBlockColors() {
		super.initBlockColors();
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			BlockColors blockcolors = Minecraft.getMinecraft().getBlockColors();
			ItemColors itemcolors = Minecraft.getMinecraft().getItemColors();
			for(Block block : JAOPCAApi.BLOCKS_TABLE.values()) {
				blockcolors.registerBlockColorHandler(JAOPCA_BLOCK_COLOR, block);
				itemcolors.registerItemColorHandler(JAOPCA_ITEM_COLOR, block);
			}
		}
	}

	public static class JAOPCAStateMap extends StateMapperBase {

		private final ModelResourceLocation location;

		public JAOPCAStateMap(ModelResourceLocation location) {
			this.location = location;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return location;
		}
	}
}
