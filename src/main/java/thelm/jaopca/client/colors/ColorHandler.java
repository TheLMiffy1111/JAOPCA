package thelm.jaopca.client.colors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.TexCoord4f;
import javax.vecmath.Tuple4f;

import com.google.common.collect.Streams;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.items.ItemFormType;

public class ColorHandler {

	public static final IBlockColor BLOCK_COLOR = (state, world, pos, tintIndex)->{
		if(tintIndex == 0) {
			Block block = state.getBlock();
			if(block instanceof IMaterialForm) {
				return ((IMaterialForm)block).getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static final IItemColor ITEM_COLOR = (stack, tintIndex)->{
		if(tintIndex == 0 || tintIndex == 2) {
			Item item = stack.getItem();
			if(item instanceof IMaterialForm) {
				return ((IMaterialForm)item).getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static void setup(ColorHandlerEvent.Item event) {
		BlockColors blockColors = event.getBlockColors();
		ItemColors itemColors = event.getItemColors();
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			blockColors.registerBlockColorHandler(BLOCK_COLOR, block.asBlock());
		}
		for(IMaterialFormBlockItem blockItem : BlockFormType.getBlockItems()) {
			itemColors.registerItemColorHandler(ITEM_COLOR, blockItem.asBlockItem());
		}
		for(IMaterialFormItem item : ItemFormType.getItems()) {
			itemColors.registerItemColorHandler(ITEM_COLOR, item.asItem());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			blockColors.registerBlockColorHandler(BLOCK_COLOR, fluidBlock.asBlock());
		}
	}

	public static int getAverageColor(String oredictName) {
		Tuple4f color = weightedAverageColor(OreDictionary.getOres(oredictName), ConfigHandler.gammaValue);
		return toColorInt(color);
	}

	public static Tuple4f weightedAverageColor(Iterable<ItemStack> items, double gammaValue) {
		if(!items.iterator().hasNext()) {
			return new TexCoord4f(1, 1, 1, 0);
		}
		List<Tuple4f> colors = Streams.stream(items).
				map(stack->weightedAverageColor(stack, gammaValue)).
				collect(Collectors.toList());
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4f weightedAverageColor(ItemStack stack, double gammaValue) {
		List<BakedQuad> quads = getBakedQuads(stack);
		if(quads.isEmpty()) {
			return new TexCoord4f(1, 1, 1, 0);
		}
		List<Tuple4f> colors = new ArrayList<>();
		for(BakedQuad quad : quads) {
			Tuple4f color = weightedAverageColor(quad.getSprite(), gammaValue);
			color = tintColor(color, getTint(stack, quad));
			colors.add(color);
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4f weightedAverageColor(TextureAtlasSprite texture, double gammaValue) {
		int width = texture.getIconWidth();
		int height = texture.getIconHeight();
		int frameCount = texture.getFrameCount();
		if(width <= 0 || height <= 0 || frameCount <= 0) {
			return new TexCoord4f(1, 1, 1, 0);
		}
		List<Tuple4f> colors = new ArrayList<>();
		for(int frameIndex = 0; frameIndex < frameCount; ++frameIndex) {
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					int color = texture.getFrameTextureData(frameIndex)[0][y*width+x];
					colors.add(toColorTuple(color));
				}
			}
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4f weightedAverageColor(List<Tuple4f> colors, double gammaValue) {
		if(colors.isEmpty()) {
			return new TexCoord4f(1, 1, 1, 0);
		}
		double weight, r = 0, g = 0, b = 0, totalWeight = 0;
		if(gammaValue == 0) {
			r = 1;
			g = 1;
			b = 1;
			for(Tuple4f color : colors) {
				totalWeight += weight = color.getW();
				r *= color.getX()*weight;
				g *= color.getY()*weight;
				b *= color.getZ()*weight;
			}
			r = Math.pow(r, 1/totalWeight);
			g = Math.pow(g, 1/totalWeight);
			b = Math.pow(b, 1/totalWeight);
		}
		else {
			for(Tuple4f color : colors) {
				totalWeight += weight = color.getW();
				r += Math.pow(color.getX(), gammaValue)*weight;
				g += Math.pow(color.getY(), gammaValue)*weight;
				b += Math.pow(color.getZ(), gammaValue)*weight;
			}
			r = Math.pow(r/totalWeight, 1/gammaValue);
			g = Math.pow(g/totalWeight, 1/gammaValue);
			b = Math.pow(b/totalWeight, 1/gammaValue);
		}
		return new TexCoord4f(
				(float)MathHelper.clamp(r, 0, 1),
				(float)MathHelper.clamp(g, 0, 1),
				(float)MathHelper.clamp(b, 0, 1),
				(float)MathHelper.clamp(totalWeight/colors.size(), 0, 1)
				);
	}

	public static Tuple4f toColorTuple(int color) {
		return new TexCoord4f(
				(color>>16&0xFF)/255F,
				(color>> 8&0xFF)/255F,
				(color    &0xFF)/255F,
				(color>>24&0xFF)/255F
				);
	}

	public static Tuple4f tintColor(Tuple4f color, int tint) {
		return new TexCoord4f(
				color.getX()*(tint>>16&0xFF)/255F,
				color.getY()*(tint>> 8&0xFF)/255F,
				color.getZ()*(tint    &0xFF)/255F,
				color.getW()
				);
	}

	public static int toColorInt(Tuple4f color) {
		int ret = 0;
		ret |= (Math.round(MathHelper.clamp(color.getX()*255, 0, 255))&0xFF)<<16;
		ret |= (Math.round(MathHelper.clamp(color.getY()*255, 0, 255))&0xFF)<< 8;
		ret |= (Math.round(MathHelper.clamp(color.getZ()*255, 0, 255))&0xFF);
		return ret;
	}

	public static List<BakedQuad> getBakedQuads(ItemStack stack) {
		List<BakedQuad> quads = new ArrayList<>();
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
		model.getQuads(null, null, 0).stream().filter(quad->quad.getFace() == EnumFacing.SOUTH).forEach(quads::add);
		for(EnumFacing facing : EnumFacing.values()) {
			model.getQuads(null, facing, 0).stream().filter(quad->quad.getFace() == EnumFacing.SOUTH).forEach(quads::add);
		}
		return quads;
	}

	public static int getTint(ItemStack stack, BakedQuad quad) {
		return Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, quad.getTintIndex());
	}
}
