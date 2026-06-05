package thelm.jaopca.client.colors;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.mixins.ItemStackRenderStateAccessor;
import thelm.jaopca.mixins.SpriteContentsAccessor;

public class ColorHandler {

	public static final BlockTintSource BLOCK_TINT = state->{
		Block block = state.getBlock();
		if(block instanceof IMaterialForm materialForm) {
			return materialForm.getMaterial().getColor();
		}
		return 0xFFFFFFFF;
	};

	public static final ItemTintSource ITEM_TINT = new ItemTintSource() {
		@Override
		public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
			Item item = stack.getItem();
			if(item instanceof IMaterialForm materialForm) {
				return materialForm.getMaterial().getColor();
			}
			return 0xFFFFFFFF;
		}

		@Override
		public MapCodec<? extends ItemTintSource> type() {
			return MapCodec.unit(this);
		}
	};

	public static final FluidTintSource FLUID_TINT = state->{
		Fluid fluid = state.getType();
		if(fluid instanceof IMaterialForm materialForm) {
			return materialForm.getMaterial().getColor();
		}
		return 0xFFFFFFFF;
	};

	public static void setupBlockTint(RegisterColorHandlersEvent.BlockTintSources event) {
		List<BlockTintSource> blockTints = List.of(BLOCK_TINT);
		for(IMaterialFormBlock block : BlockFormType.getBlocks()) {
			event.register(blockTints, block.toBlock());
		}
		for(IMaterialFormFluidBlock fluidBlock : FluidFormType.getFluidBlocks()) {
			event.register(blockTints, fluidBlock.toBlock());
		}
	}

	public static void setupItemTint(RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Identifier.parse("jaopca:material_form"), ITEM_TINT.type());
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
			Vector4f color = weightedAverageColor(quad.materialInfo().sprite(), gammaValue);
			for(int i = 0; i < 4; ++i) {
				color = tintColor(color, quad.bakedColors().color(i));
				colors.add(color);
			}
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
				(color>>16&0xFF)/255F,
				(color>> 8&0xFF)/255F,
				(color    &0xFF)/255F,
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
		ItemModel model = Minecraft.getInstance().getModelManager().getItemModel(stack.get(DataComponents.ITEM_MODEL));
		ItemStackRenderState quadExtractor = new ItemStackRenderState();
		model.update(quadExtractor, stack, Minecraft.getInstance().getItemModelResolver(), ItemDisplayContext.GUI, null, null, 0);
		LayerRenderState[] layers = ((ItemStackRenderStateAccessor)quadExtractor).layers();
		for(LayerRenderState layer : layers) {
			layer.prepareQuadList().stream().filter(q -> q.direction() == Direction.SOUTH).forEach(quads::add);
		}
		return quads;
	}
}
