package thelm.jaopca.client.colors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.vecmath.Point4d;
import javax.vecmath.Tuple4d;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;
import thelm.jaopca.api.items.MaterialFormItem;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.items.ItemFormType;

public class ColorHandler {

	public static final IBlockColor BLOCK_COLOR = (state, world, pos, tintIndex)->{
		if(tintIndex == 0) {
			Block block = state.getBlock();
			if(block instanceof IMaterialForm) {
				IMaterialForm materialForm = (IMaterialForm)block;
				return materialForm.getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static final IItemColor ITEM_COLOR = (stack, tintIndex)->{
		if(tintIndex == 0) {
			Item item = stack.getItem();
			if(item instanceof IMaterialForm) {
				IMaterialForm materialForm = (IMaterialForm)item;
				return materialForm.getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static void setup(ColorHandlerEvent.Item event) {
		BlockColors blockColors = event.getBlockColors();
		ItemColors itemColors = event.getItemColors();
		for(BlockMaterialForm block : BlockFormType.getBlocks()) {
			blockColors.register(BLOCK_COLOR, block);
		}
		for(MaterialFormBlockItem itemBlock : BlockFormType.getBlockItems()) {
			itemColors.register(ITEM_COLOR, itemBlock);
		}
		for(MaterialFormItem item : ItemFormType.getItems()) {
			itemColors.register(ITEM_COLOR, item);
		}
	}

	public static int getAverageColor(Tag<Item> tag) {
		Tuple4d color = weightedAverageColor(tag.getAllElements(), ConfigHandler.gammaValue);
		return toColorInt(color);
	}

	public static Tuple4d weightedAverageColor(Collection<Item> items, double gammaValue) {
		if(items.isEmpty()) {
			return new Point4d(1, 1, 1, 0);
		}
		List<Tuple4d> colors = items.stream().map(ItemStack::new).
				map(stack->weightedAverageColor(stack, gammaValue)).
				collect(Collectors.toList());
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4d weightedAverageColor(ItemStack stack, double gammaValue) {
		List<BakedQuad> quads = getBakedQuads(stack);
		if(quads.isEmpty()) {
			return new Point4d(1, 1, 1, 0);
		}
		List<Tuple4d> colors = new ArrayList<>();
		for(BakedQuad quad : quads) {
			Tuple4d color = weightedAverageColor(quad.getSprite(), gammaValue);
			color = tintColor(color, getTint(stack, quad));
			colors.add(color);
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4d weightedAverageColor(TextureAtlasSprite texture, double gammaValue) {
		int width = texture.getWidth();
		int height = texture.getHeight();
		int frameCount = texture.getFrameCount();
		if(width <= 0 || height <= 0 || frameCount <= 0) {
			return new Point4d(1, 1, 1, 0);
		}
		List<Tuple4d> colors = new ArrayList<>();
		for(int frameIndex = 0; frameIndex < frameCount; ++frameIndex) {
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					int color = texture.getPixelRGBA(frameIndex, x, y);
					colors.add(toColorTuple(color));
				}
			}
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4d weightedAverageColor(List<Tuple4d> colors, double gammaValue) {
		if(colors.isEmpty()) {
			return new Point4d(1, 1, 1, 0);
		}
		double weight, r = 0, g = 0, b = 0, totalWeight = 0;
		if(gammaValue == 0) {
			r = 1;
			g = 1;
			b = 1;
			for(Tuple4d color : colors) {
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
			for(Tuple4d color : colors) {
				totalWeight += weight = color.getW();
				r += Math.pow(color.getX(), gammaValue)*weight;
				g += Math.pow(color.getY(), gammaValue)*weight;
				b += Math.pow(color.getZ(), gammaValue)*weight;
			}
			r = Math.pow(r/totalWeight, 1/gammaValue);
			g = Math.pow(g/totalWeight, 1/gammaValue);
			b = Math.pow(b/totalWeight, 1/gammaValue);
		}
		return new Point4d(
				MathHelper.clamp(r, 0, 1),
				MathHelper.clamp(g, 0, 1),
				MathHelper.clamp(b, 0, 1),
				MathHelper.clamp(totalWeight/colors.size(), 0, 1)
				);
	}

	public static Tuple4d toColorTuple(int color) {
		return new Point4d(
				(color    &0xFF)/255D,
				(color>> 8&0xFF)/255D,
				(color>>16&0xFF)/255D,
				(color>>24&0xFF)/255D
				);
	}

	public static Tuple4d tintColor(Tuple4d color, int tint) {
		return new Point4d(
				color.getX()*(tint>>16&0xFF)/255D,
				color.getY()*(tint>> 8&0xFF)/255D,
				color.getZ()*(tint    &0xFF)/255D,
				color.getW()
				);
	}

	public static int toColorInt(Tuple4d color) {
		int ret = 0;
		ret |= (Math.round(MathHelper.clamp(color.getX()*255, 0, 255))&0xFF)<<16;
		ret |= (Math.round(MathHelper.clamp(color.getY()*255, 0, 255))&0xFF)<< 8;
		ret |= (Math.round(MathHelper.clamp(color.getZ()*255, 0, 255))&0xFF);
		return ret;
	}

	public static List<BakedQuad> getBakedQuads(ItemStack stack) {
		List<BakedQuad> quads = new ArrayList<>();
		IBakedModel model = Minecraft.getInstance().getItemRenderer().getModelWithOverrides(stack);
		model.getQuads(null, null, new Random(0)).stream().filter(quad->quad.getFace() == Direction.SOUTH).forEach(quads::add);
		for(Direction facing : Direction.values()) {
			model.getQuads(null, facing, new Random(0)).stream().filter(quad->quad.getFace() == Direction.SOUTH).forEach(quads::add);
		}
		return quads;
	}

	public static int getTint(ItemStack stack, BakedQuad quad) {
		return Minecraft.getInstance().getItemColors().getColor(stack, quad.getTintIndex());
	}
}
