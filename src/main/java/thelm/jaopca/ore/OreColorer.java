package thelm.jaopca.ore;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Code partially taken from mezz's JEI
 */
public class OreColorer {

    public static final HashMap<String, Color> DEFAULT_COLORS = Maps.<String, Color>newHashMap();

    static {

    }

    public static Color getAverageColorFromTexture(BufferedImage image) {

        long r = 0, g = 0, b = 0;
        long amt = 0;

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j), true);
                if (color.getAlpha() < 50)
                    continue;

                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
                amt++;
            }
        }
        if (amt == 0) return Color.WHITE;
        return new Color((int) MathHelper.clamp(r / amt, 0, 255),
                (int) MathHelper.clamp(g / amt, 0, 255), (int) MathHelper.clamp(b / amt, 0, 255)).brighter();
    }

    public static Color getColor(String prefix, String oreName) {
        if (DEFAULT_COLORS.containsKey(oreName)) {
            return DEFAULT_COLORS.get(oreName);
        }

        List<ItemStack> ores = Utils.getOres(prefix + oreName);
        if (ores.isEmpty()) {
            return Color.WHITE;
        }

        List<int[]> colors = Lists.<int[]>newArrayList();
        for (ItemStack stack : ores) {
            List<BakedQuad> quads = getBakedQuads(stack).stream().sorted((quad0, quad1) -> Integer.compare(quad1.getTintIndex(), quad0.getTintIndex())).collect(Collectors.toList());
            if (quads.isEmpty()) {
                continue;
            }
            int colorMultiplier = getColorMultiplier(stack, quads.get(0));
            for (BakedQuad quad : quads) {
                BufferedImage texture = getBufferedImage(quad.getSprite());
                if (texture == null) {
                    continue;
                }

                Color color = getAverageColorFromTexture(texture);
                Color colorMult = new Color(colorMultiplier);
                colors.add(new int[]{(int) MathHelper.clamp(color.getRed() * colorMult.getRed() / 255D, 0, 255),
                        (int) MathHelper.clamp(color.getGreen() * colorMult.getGreen() / 255D, 0, 255),
                        (int) MathHelper.clamp(color.getBlue() * colorMult.getBlue() / 255D, 0, 255)});
            }
        }

        double count = colors.size();
        if (count == 0) {
            return Color.WHITE;
        }
        long red = 0;
        long green = 0;
        long blue = 0;
        for (int[] c : colors) {
            red += c[0] * c[0];
            green += c[1] * c[1];
            blue += c[2] * c[2];
        }

        return new Color((int) Math.sqrt(red / count), (int) Math.sqrt(green / count), (int) Math.sqrt(blue / count));
    }

    private static BufferedImage getBufferedImage(TextureAtlasSprite textureAtlasSprite) {
        if (textureAtlasSprite == null) {
            return null;
        }
        final int iconWidth = textureAtlasSprite.getIconWidth();
        final int iconHeight = textureAtlasSprite.getIconHeight();
        final int frameCount = textureAtlasSprite.getFrameCount();
        if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
            return null;
        }

        BufferedImage bufferedImage = new BufferedImage(iconWidth, iconHeight * frameCount, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < frameCount; i++) {
            int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(i);
            int[] largestMipMapTextureData = frameTextureData[0];
            bufferedImage.setRGB(0, i * iconHeight, iconWidth, iconHeight, largestMipMapTextureData, 0, iconWidth);
        }

        return bufferedImage;
    }

    private static List<BakedQuad> getBakedQuads(ItemStack itemStack) {
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(itemStack, null, null);
        ArrayList<BakedQuad> list = Lists.<BakedQuad>newArrayList();
        for (EnumFacing facing : EnumFacing.values()) {
            list.addAll(model.getQuads(null, facing, 0).stream().filter(quad -> quad.getFace() == EnumFacing.SOUTH).collect(Collectors.toList()));
        }
        list.addAll(model.getQuads(null, null, 0).stream().filter(quad -> quad.getFace() == EnumFacing.SOUTH).collect(Collectors.toList()));
        return list;
    }

    private static int getColorMultiplier(ItemStack itemStack, BakedQuad quad) {
        return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(itemStack, quad.getTintIndex());
    }

    public static boolean getHasEffect(String prefix, String oreName) {
        List<ItemStack> ores = OreDictionary.getOres(prefix + oreName, false);
        if (ores.isEmpty()) {
            return false;
        }

        for (ItemStack ore : ores) {
            if (ore.hasEffect()) {
                return true;
            }
        }
        return false;
    }
}
