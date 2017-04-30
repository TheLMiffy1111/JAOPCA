package thelm.jaopca.ore;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.utils.JAOPCAConfig;
import thelm.jaopca.api.IOreEntry;

public class OreColorer {

	public static final HashMap<String, Color> DEFAULT_COLORS = Maps.<String, Color>newHashMap();
	
	static {
		DEFAULT_COLORS.put("Cobalt", new Color(0x2376DD));
		DEFAULT_COLORS.put("Ardite", new Color(0xF48A00));
		DEFAULT_COLORS.put("Aluminum", new Color(0xEDEDED));
	}
	
	public static Color getColor(String prefix, String oreName) {
		if(DEFAULT_COLORS.containsKey(oreName)) {
			return DEFAULT_COLORS.get(oreName);
		}
		
		List<ItemStack> ores = OreDictionary.getOres(prefix + oreName);
		if(ores.isEmpty())
			return Color.WHITE;

		Set<Color> colors = new LinkedHashSet<Color>();
		for(ItemStack stack : ores) {
			try {
				BufferedImage texture = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(getIconResource(stack)).getInputStream());
				Color texColor = getAverageColor(texture);
				colors.add(texColor);
				for(int pass = 0; pass < 2; pass++) {
					int c = getStackColor(stack, pass);
					if((c & 0xFFFFFF) != 0xFFFFFF) {
						colors.add(new Color(c));
						colors.remove(texColor);
					}
				}
			}
			catch(Exception e) {
				continue;
			}
		}

		float red = 0;
		float green = 0;
		float blue = 0;
		for(Color c : colors) {
			red += c.getRed();
			green += c.getGreen();
			blue += c.getBlue();
		}
		float count = colors.size();
		return new Color((int)(red / count), (int)(green / count), (int)(blue / count));
	}

	private static int getStackColor(ItemStack stack, int tintIndex) {
		return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, tintIndex);
	}

	public static Color getAverageColor(BufferedImage image) {
		float red = 0F;
		float green = 0F;
		float blue = 0F;
		float count = 0F;
		for(int i = 0; i < image.getWidth(); i++) {
			for(int j = 0; j < image.getHeight(); j++) {
				Color c = new Color(image.getRGB(i, j));
				if((c.getAlpha() == 255) && ((c.getRed() > 10) || (c.getBlue() > 10) || (c.getGreen() > 10))) {
					red += c.getRed();
					green += c.getGreen();
					blue += c.getBlue();
					count += 1F;
				}
			}
		}
		return new Color((int)(red / count), (int)(green / count), (int)(blue / count));
	}

	public static String getIconName(ItemStack stack) {
		TextureAtlasSprite icon = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null).getParticleTexture();
		if(icon != null) {
			return icon.getIconName();
		}
		return null;
	}

	public static ResourceLocation getIconResource(ItemStack stack) {
		String iconName = getIconName(stack);
		if(iconName == null) {
			return null;
		}

		String string = "minecraft";

		int colonIndex = iconName.indexOf(':');
		if(colonIndex >= 0) {
			if(colonIndex > 1)
				string = iconName.substring(0, colonIndex);

			iconName = iconName.substring(colonIndex + 1);
		}

		string = string.toLowerCase();
		iconName = "textures/" + iconName + ".png";
		return new ResourceLocation(string, iconName);
	}
}
