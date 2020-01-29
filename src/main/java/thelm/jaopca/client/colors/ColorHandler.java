package thelm.jaopca.client.colors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector4f;
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
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
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
				IMaterialForm materialForm = (IMaterialForm)block;
				return materialForm.getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static final IItemColor ITEM_COLOR = (stack, tintIndex)->{
		if(tintIndex == 0 || tintIndex == 2) {
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
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			blockColors.register(BLOCK_COLOR, block.asBlock());
		}
		for(IMaterialFormBlockItem blockItem : BlockFormType.getBlockItems()) {
			itemColors.register(ITEM_COLOR, blockItem.asBlockItem());
		}
		for(IMaterialFormItem item : ItemFormType.getItems()) {
			itemColors.register(ITEM_COLOR, item.asItem());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			blockColors.register(BLOCK_COLOR, fluidBlock.asBlock());
		}
		for(IMaterialFormBucketItem bucketItem : FluidFormType.getBucketItems()) {
			itemColors.register(ITEM_COLOR, bucketItem.asItem());
		}
	}

	public static int getAverageColor(Tag<Item> tag) {
		Vector4f color = weightedAverageColor(tag.getAllElements(), ConfigHandler.gammaValue);
		return toColorInt(color);
	}

	public static Vector4f weightedAverageColor(Collection<Item> items, double gammaValue) {
		if(items.isEmpty()) {
			return new Vector4f(1, 1, 1, 0);
		}
		List<Vector4f> colors = items.stream().map(ItemStack::new).
				map(stack->weightedAverageColor(stack, gammaValue)).
				collect(Collectors.toList());
		return weightedAverageColor(colors, gammaValue);
	}

	public static Vector4f weightedAverageColor(ItemStack stack, double gammaValue) {
		List<BakedQuad> quads = getBakedQuads(stack);
		if(quads.isEmpty()) {
			return new Vector4f(1, 1, 1, 0);
		}
		List<Vector4f> colors = new ArrayList<>();
		for(BakedQuad quad : quads) {
			Vector4f color = weightedAverageColor(quad.func_187508_a(), gammaValue);
			color = tintColor(color, getTint(stack, quad));
			colors.add(color);
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Vector4f weightedAverageColor(TextureAtlasSprite texture, double gammaValue) {
		int width = texture.getWidth();
		int height = texture.getHeight();
		int frameCount = texture.getFrameCount();
		if(width <= 0 || height <= 0 || frameCount <= 0) {
			return new Vector4f(1, 1, 1, 0);
		}
		List<Vector4f> colors = new ArrayList<>();
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

	public static Vector4f weightedAverageColor(List<Vector4f> colors, double gammaValue) {
		if(colors.isEmpty()) {
			return new Vector4f(1, 1, 1, 0);
		}
		double weight, r = 0, g = 0, b = 0, totalWeight = 0;
		if(gammaValue == 0) {
			r = 1;
			g = 1;
			b = 1;
			for(Vector4f color : colors) {
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
			for(Vector4f color : colors) {
				totalWeight += weight = color.getW();
				r += Math.pow(color.getX(), gammaValue)*weight;
				g += Math.pow(color.getY(), gammaValue)*weight;
				b += Math.pow(color.getZ(), gammaValue)*weight;
			}
			r = Math.pow(r/totalWeight, 1/gammaValue);
			g = Math.pow(g/totalWeight, 1/gammaValue);
			b = Math.pow(b/totalWeight, 1/gammaValue);
		}
		return new Vector4f(
				(float)MathHelper.clamp(r, 0, 1),
				(float)MathHelper.clamp(g, 0, 1),
				(float)MathHelper.clamp(b, 0, 1),
				(float)MathHelper.clamp(totalWeight/colors.size(), 0, 1)
				);
	}

	public static Vector4f toColorTuple(int color) {
		return new Vector4f(
				(color    &0xFF)/255F,
				(color>> 8&0xFF)/255F,
				(color>>16&0xFF)/255F,
				(color>>24&0xFF)/255F
				);
	}

	public static Vector4f tintColor(Vector4f color, int tint) {
		return new Vector4f(
				color.getX()*(tint>>16&0xFF)/255F,
				color.getY()*(tint>> 8&0xFF)/255F,
				color.getZ()*(tint    &0xFF)/255F,
				color.getW()
				);
	}

	public static int toColorInt(Vector4f color) {
		int ret = 0;
		ret |= (Math.round(MathHelper.clamp(color.getX()*255, 0, 255))&0xFF)<<16;
		ret |= (Math.round(MathHelper.clamp(color.getY()*255, 0, 255))&0xFF)<< 8;
		ret |= (Math.round(MathHelper.clamp(color.getZ()*255, 0, 255))&0xFF);
		return ret;
	}

	public static List<BakedQuad> getBakedQuads(ItemStack stack) {
		List<BakedQuad> quads = new ArrayList<>();
		IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
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
