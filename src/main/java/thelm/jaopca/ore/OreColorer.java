package thelm.jaopca.ore;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.androidpit.colorthief.ColorThief;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;

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

		List<ItemStack> ores = OreDictionary.getOres(prefix+oreName, false);
		if(ores.isEmpty()) {
			return Color.WHITE;
		}

		List<int[]> colors = Lists.<int[]>newArrayList();
		for(ItemStack stack : ores) {
			try {
				BufferedImage texture = getBufferedImage(getTextureAtlasSprite(stack));
				int[] texColor = ColorThief.getColor(texture);
				int colorMultiplier = getColorMultiplier(stack);
				texColor[0] = MathHelper.clamp((int)((texColor[0]-1)*(float)(colorMultiplier>>16&0xFF)/255F), 0, 255);
				texColor[1] = MathHelper.clamp((int)((texColor[1]-1)*(float)(colorMultiplier>>8&0xFF)/255F), 0, 255);
				texColor[2] = MathHelper.clamp((int)((texColor[2]-1)*(float)(colorMultiplier&0xFF)/255F), 0, 255);
				colors.add(texColor);
			}
			catch(Exception e) {
				continue;
			}
		}

		float red = 0;
		float green = 0;
		float blue = 0;
		for(int[] c : colors) {
			red += c[0]*c[0];
			green += c[1]*c[1];
			blue += c[2]*c[2];
		}
		int count = colors.size();
		return new Color((int)Math.sqrt(red/count), (int)Math.sqrt(green/count), (int)Math.sqrt(blue/count));
	}

	private static BufferedImage getBufferedImage(TextureAtlasSprite textureAtlasSprite) {
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

	private static TextureAtlasSprite getTextureAtlasSprite(ItemStack itemStack) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemStack).getParticleTexture();
	}
	
	private static int getColorMultiplier(ItemStack itemStack) {
		return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(itemStack, 0);
	}
}
