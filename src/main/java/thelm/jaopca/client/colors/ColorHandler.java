package thelm.jaopca.client.colors;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.vecmath.TexCoord4f;
import javax.vecmath.Tuple4f;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.config.ConfigHandler;

public class ColorHandler {

	public static int getAverageColor(String oredictName) {
		Tuple4f color = weightedAverageColor(OreDictionary.getOres(oredictName, false), ConfigHandler.gammaValue);
		return toColorInt(color);
	}

	public static Tuple4f weightedAverageColor(Iterable<ItemStack> items, double gammaValue) {
		List<Tuple4f> colors = Lists.newArrayList(items).stream().
				map(stack->weightedAverageColor(stack, gammaValue)).
				collect(Collectors.toList());
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4f weightedAverageColor(ItemStack stack, double gammaValue) {
		Item item = stack.getItem();
		int mapId = item.getSpriteNumber();
		List<Tuple4f> colors = new ArrayList<>();
		for(int i = 0; i < item.getRenderPasses(stack.getItemDamage()); ++i) {
			IIcon icon = item.getIcon(stack, i);
			if(icon != null) {
				Tuple4f color = weightedAverageColor(toSprite(icon, mapId), mapId, gammaValue);
				color = tintColor(color, item.getColorFromItemStack(stack, i));
				colors.add(color);
			}
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4f weightedAverageColor(TextureAtlasSprite texture, int mapId, double gammaValue) {
		int frameCount = texture.getFrameCount();
		if(frameCount <= 0) {
			return weightedAverageColor(toImage(texture, mapId), gammaValue);
		}
		int width = texture.getIconWidth();
		int height = texture.getIconHeight();
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

	public static Tuple4f weightedAverageColor(BufferedImage image, double gammaValue) {
		if(image == null) {
			return new TexCoord4f(1, 1, 1, 0);
		}
		int width = image.getWidth();
		int height = image.getHeight();
		List<Tuple4f> colors = new ArrayList<>();
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int color = image.getRGB(x, y);
				colors.add(toColorTuple(color));
			}
		}
		return weightedAverageColor(colors, gammaValue);
	}

	public static Tuple4f weightedAverageColor(List<Tuple4f> colors, double gammaValue) {
		double totalWeight = 0, r = 0, g = 0, b = 0;
		for(Tuple4f color : colors) {
			totalWeight += color.w;
		}
		if(totalWeight <= 0) {
			return new TexCoord4f(1, 1, 1, 0);
		}
		if(gammaValue == 0) {
			r = 1;
			g = 1;
			b = 1;
			for(Tuple4f color : colors) {
				r *= color.x*color.w;
				g *= color.y*color.w;
				b *= color.z*color.w;
			}
			r = Math.pow(r, 1/totalWeight);
			g = Math.pow(g, 1/totalWeight);
			b = Math.pow(b, 1/totalWeight);
		}
		else {
			for(Tuple4f color : colors) {
				r += Math.pow(color.x, gammaValue)*color.w;
				g += Math.pow(color.y, gammaValue)*color.w;
				b += Math.pow(color.z, gammaValue)*color.w;
			}
			r = Math.pow(r/totalWeight, 1/gammaValue);
			g = Math.pow(g/totalWeight, 1/gammaValue);
			b = Math.pow(b/totalWeight, 1/gammaValue);
		}
		return new TexCoord4f(
				(float)MathHelper.clamp_double(r, 0, 1),
				(float)MathHelper.clamp_double(g, 0, 1),
				(float)MathHelper.clamp_double(b, 0, 1),
				(float)MathHelper.clamp_double(totalWeight/colors.size(), 0, 1)
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
				color.x*(tint>>16&0xFF)/255F,
				color.y*(tint>> 8&0xFF)/255F,
				color.z*(tint    &0xFF)/255F,
				color.w
				);
	}

	public static int toColorInt(Tuple4f color) {
		int ret = 0;
		ret |= (Math.round(MathHelper.clamp_float(color.x*255, 0, 255))&0xFF)<<16;
		ret |= (Math.round(MathHelper.clamp_float(color.y*255, 0, 255))&0xFF)<< 8;
		ret |= (Math.round(MathHelper.clamp_float(color.z*255, 0, 255))&0xFF);
		return ret;
	}

	public static TextureAtlasSprite toSprite(IIcon icon, int mapId) {
		if(icon instanceof TextureAtlasSprite) {
			return (TextureAtlasSprite)icon;
		}
		TextureMap textureMap = (TextureMap)Minecraft.getMinecraft().getTextureManager().
				getTexture(mapId == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
		return textureMap.getAtlasSprite(icon.getIconName());
	}

	public static BufferedImage toImage(TextureAtlasSprite texture, int mapId) {
		String namespace = "minecraft";
		String path = texture.getIconName();
		int colonIndex = path.indexOf(':');
		if(colonIndex >= 0) {
			if(colonIndex > 1) {
				namespace = path.substring(0, colonIndex);
			}
			path = path.substring(colonIndex+1);
		}
		ResourceLocation location = new ResourceLocation(namespace, "textures/"+(mapId == 0 ? "blocks/" : "items/")+path+".png");
		try {
			return ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream());
		}
		catch(Exception e) {
			return null;
		}
	}
}
