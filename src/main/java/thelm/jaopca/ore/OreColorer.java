package thelm.jaopca.ore;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.androidpit.colorthief.ColorThief;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.utils.JAOPCAConfig;

/**
 * Code partially taken from mezz's JEI
 */
public class OreColorer {

	public static final HashMap<String, Color> DEFAULT_COLORS = Maps.<String, Color>newHashMap();

	static {

	}

	public static Color getColor(String prefix, String oreName) {
		if(DEFAULT_COLORS.containsKey(oreName)) {
			return DEFAULT_COLORS.get(oreName);
		}

		List<ItemStack> ores = Utils.getOres(prefix+oreName);
		if(ores.isEmpty()) {
			return Color.WHITE;
		}

		List<int[]> colors = Lists.<int[]>newArrayList();
		for(ItemStack stack : ores) {
			List<BakedQuad> quads = getBakedQuads(stack).stream().sorted((quad0, quad1)->Integer.compare(quad1.getTintIndex(), quad0.getTintIndex())).collect(Collectors.toList());
			if(quads.isEmpty()) {
				continue;
			}
			for(BakedQuad quad : quads) {
				int colorMultiplier = getColorMultiplier(stack, quad);
				BufferedImage texture = getBufferedImage(quad.getSprite());
				if(texture == null) {
					continue;
				}
				int pixelCount = getPixelCount(texture);
				if(pixelCount == 0) {
					continue;
				}
				int[] texColor = getTextureColor(texture);
				if(texColor == null) {
					continue;
				}
				int[] colorAndCount = new int[4];
				colorAndCount[0] = (int)Math.round(MathHelper.clamp(texColor[0]*(colorMultiplier>>16&0xFF)/255D, 0, 255));
				colorAndCount[1] = (int)Math.round(MathHelper.clamp(texColor[1]*(colorMultiplier>> 8&0xFF)/255D, 0, 255));
				colorAndCount[2] = (int)Math.round(MathHelper.clamp(texColor[2]*(colorMultiplier    &0xFF)/255D, 0, 255));
				colorAndCount[3] = pixelCount;
				colors.add(colorAndCount);
			}
		}

		if(colors.size() == 0) {
			return Color.WHITE;
		}
		double count = 0;
		long red = 0, green = 0, blue = 0;
		for(int[] c : colors) {
			red += c[0]*c[0]*c[3];
			green += c[1]*c[1]*c[3];
			blue += c[2]*c[2]*c[3];
			count += c[3];
		}
		return new Color((int)Math.round(MathHelper.clamp(Math.sqrt(red/count), 0, 255)),
				(int)Math.round(MathHelper.clamp(Math.sqrt(green/count), 0, 255)),
				(int)Math.round(MathHelper.clamp(Math.sqrt(blue/count), 0, 255)));
	}

	private static int[] getTextureColor(BufferedImage image) {
		switch(JAOPCAConfig.colorMode) {
		case 0:
		default: {
			Color color = getAverageColorFromTexture(image);
			return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
		}
		case 1: {
			int[][] texColors = ColorThief.getPalette(image, 4);
			if(texColors == null) {
				return null;
			}
			return texColors[0];
		}
		}
	}

	private static int getPixelCount(BufferedImage image) {
		int count = 0;
		for(int i = 0; i < image.getWidth(); i++) {
			for(int j = 0; j < image.getHeight(); j++) {
				Color color = new Color(image.getRGB(i, j), true);
				if(color.getAlpha() < 50) {
					continue;
				}
				count++;
			}
		}
		return count;
	}

	private static Color getAverageColorFromTexture(BufferedImage image) {
		long red = 0, green = 0, blue = 0;
		double count = 0;
		for(int i = 0; i < image.getWidth(); i++) {
			for(int j = 0; j < image.getHeight(); j++) {
				Color color = new Color(image.getRGB(i, j), true);
				if(color.getAlpha() < 50) {
					continue;
				}
				red += color.getRed()*color.getRed();
				green += color.getGreen()*color.getGreen();
				blue += color.getBlue()*color.getBlue();
				count++;
			}
		}
		if(count == 0) {
			return Color.WHITE;
		}
		return new Color((int)MathHelper.clamp(Math.sqrt(red/count), 0, 255),
				(int)MathHelper.clamp(Math.sqrt(green/count), 0, 255),
				(int)MathHelper.clamp(Math.sqrt(blue/count), 0, 255)).brighter();
	}

	private static BufferedImage getBufferedImage(TextureAtlasSprite textureAtlasSprite) {
		if(textureAtlasSprite == null) {
			return null;
		}
		final int iconWidth = textureAtlasSprite.getIconWidth();
		final int iconHeight = textureAtlasSprite.getIconHeight();
		final int frameCount = textureAtlasSprite.getFrameCount();
		if(iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
			return null;
		}

		BufferedImage bufferedImage = new BufferedImage(iconWidth, iconHeight * frameCount, BufferedImage.TYPE_4BYTE_ABGR);
		for(int i = 0; i < frameCount; i++) {
			int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(i);
			int[] largestMipMapTextureData = frameTextureData[0];
			bufferedImage.setRGB(0, i * iconHeight, iconWidth, iconHeight, largestMipMapTextureData, 0, iconWidth);
		}

		return bufferedImage;
	}

	private static List<BakedQuad> getBakedQuads(ItemStack itemStack) {
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(itemStack, null, null);
		ArrayList<BakedQuad> list = Lists.<BakedQuad>newArrayList();
		for(EnumFacing facing : EnumFacing.values()) {
			list.addAll(model.getQuads(null, facing, 0).stream().filter(quad->quad.getFace() == EnumFacing.SOUTH).collect(Collectors.toList()));
		}
		list.addAll(model.getQuads(null, null, 0).stream().filter(quad->quad.getFace() == EnumFacing.SOUTH).collect(Collectors.toList()));
		return list;
	}

	private static int getColorMultiplier(ItemStack itemStack, BakedQuad quad) {
		return Minecraft.getMinecraft().getItemColors().colorMultiplier(itemStack, quad.getTintIndex());
	}

	public static boolean getHasEffect(String prefix, String oreName) {
		List<ItemStack> ores = OreDictionary.getOres(prefix+oreName, false);
		if(ores.isEmpty()) {
			return false;
		}

		for(ItemStack ore : ores) {
			if(ore.hasEffect()) {
				return true;
			}
		}
		return false;
	}
}
