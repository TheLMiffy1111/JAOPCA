package thelm.jaopca.client.colors;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
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
import thelm.jaopca.mixins.SpriteContentsAccessor;

public class ColorHandler {

	public static final BlockColor BLOCK_COLOR = (state, world, pos, tintIndex)->{
		if(tintIndex == 0) {
			Block block = state.getBlock();
			if(block instanceof IMaterialForm materialForm) {
				return materialForm.getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static final ItemColor ITEM_COLOR = (stack, tintIndex)->{
		if(tintIndex == 0 || tintIndex == 2) {
			Item item = stack.getItem();
			if(item instanceof IMaterialForm materialForm) {
				return materialForm.getMaterial().getColor();
			}
		}
		return 0xFFFFFFFF;
	};

	public static void setup(RegisterColorHandlersEvent.Item event) {
		BlockColors blockColors = event.getBlockColors();
		ItemColors itemColors = event.getItemColors();
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			blockColors.register(BLOCK_COLOR, block.toBlock());
		}
		for(IMaterialFormBlockItem blockItem : BlockFormType.getBlockItems()) {
			itemColors.register(ITEM_COLOR, blockItem.toBlockItem());
		}
		for(IMaterialFormItem item : ItemFormType.getItems()) {
			itemColors.register(ITEM_COLOR, item.toItem());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			blockColors.register(BLOCK_COLOR, fluidBlock.toBlock());
		}
		for(IMaterialFormBucketItem bucketItem : FluidFormType.getBucketItems()) {
			itemColors.register(ITEM_COLOR, bucketItem.toItem());
		}
	}

	public static int getAverageColor(HolderSet<Item> tag) {
		Vector4f color = weightedAverageColor(Iterables.transform(tag, Holder::value), ConfigHandler.gammaValue);
		return toColorInt(color);
	}

	public static Vector4f weightedAverageColor(Iterable<Item> items, double gammaValue) {
		List<Vector4f> colors = Streams.stream(items).map(ItemStack::new).
				map(stack->weightedAverageColor(stack, gammaValue)).toList();
		return weightedAverageColor(colors, gammaValue);
	}

	public static Vector4f weightedAverageColor(ItemStack stack, double gammaValue) {
		List<BakedQuad> quads = getBakedQuads(stack);
		List<Vector4f> colors = new ArrayList<>();
		for(BakedQuad quad : quads) {
			Vector4f color = weightedAverageColor(quad.getSprite(), gammaValue);
			color = tintColor(color, getTint(stack, quad));
			colors.add(color);
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Vector4f weightedAverageColor(TextureAtlasSprite texture, double gammaValue) {
		int width = texture.contents().width();
		int height = texture.contents().height();
		int frameCount = ((SpriteContentsAccessor)texture.contents()).frameCount();
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
		double totalWeight = 0, r = 0, g = 0, b = 0;
		for(Vector4f color : colors) {
			totalWeight += color.w();
		}
		if(totalWeight <= 0) {
			return new Vector4f(1, 1, 1, 0);
		}
		if(gammaValue == 0) {
			r = 1;
			g = 1;
			b = 1;
			for(Vector4f color : colors) {
				r *= color.x()*color.w();
				g *= color.y()*color.w();
				b *= color.z()*color.w();
			}
			r = Math.pow(r, 1/totalWeight);
			g = Math.pow(g, 1/totalWeight);
			b = Math.pow(b, 1/totalWeight);
		}
		else {
			for(Vector4f color : colors) {
				r += Math.pow(color.x(), gammaValue)*color.w();
				g += Math.pow(color.y(), gammaValue)*color.w();
				b += Math.pow(color.z(), gammaValue)*color.w();
			}
			r = Math.pow(r/totalWeight, 1/gammaValue);
			g = Math.pow(g/totalWeight, 1/gammaValue);
			b = Math.pow(b/totalWeight, 1/gammaValue);
		}
		return new Vector4f(
				(float)Mth.clamp(r, 0, 1),
				(float)Mth.clamp(g, 0, 1),
				(float)Mth.clamp(b, 0, 1),
				(float)Mth.clamp(totalWeight/colors.size(), 0, 1)
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
				color.x()*(tint>>16&0xFF)/255F,
				color.y()*(tint>> 8&0xFF)/255F,
				color.z()*(tint    &0xFF)/255F,
				color.w()
				);
	}

	public static int toColorInt(Vector4f color) {
		int ret = 0;
		ret |= (Math.round(Mth.clamp(color.x()*255, 0, 255))&0xFF)<<16;
		ret |= (Math.round(Mth.clamp(color.y()*255, 0, 255))&0xFF)<< 8;
		ret |= (Math.round(Mth.clamp(color.z()*255, 0, 255))&0xFF);
		return ret;
	}

	public static List<BakedQuad> getBakedQuads(ItemStack stack) {
		List<BakedQuad> quads = new ArrayList<>();
		BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
		model.getQuads(null, null, RandomSource.create(0)).stream().filter(quad->quad.getDirection() == Direction.SOUTH).forEach(quads::add);
		for(Direction facing : Direction.values()) {
			model.getQuads(null, facing, RandomSource.create(0)).stream().filter(quad->quad.getDirection() == Direction.SOUTH).forEach(quads::add);
		}
		return quads;
	}

	public static int getTint(ItemStack stack, BakedQuad quad) {
		return Minecraft.getInstance().getItemColors().getColor(stack, quad.getTintIndex());
	}
}
