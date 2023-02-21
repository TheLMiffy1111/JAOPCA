package thelm.jaopca.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class JAOPCAItemRenderer implements IItemRenderer {

	public static final JAOPCAItemRenderer INSTANCE = new JAOPCAItemRenderer();
	public boolean rendering = false;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type != ItemRenderType.INVENTORY && type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return type == ItemRenderType.ENTITY;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(type == ItemRenderType.ENTITY) {
			GL11.glScalef(2, 2, 2);
			if(RenderItem.renderInFrame) {
				GL11.glScalef(6.3F/16F, 6.3F/16F, 6.3F/16F);
				GL11.glRotatef(-90, 0, 1, 0);
				GL11.glTranslatef(-8/16F, -7/16F, 0.5F/16F);
			}
			else {
				GL11.glScalef(7/16F, 7/16F, 7/16F);
				GL11.glTranslatef(-8/16F, -3/16F, 0);
			}
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		for(int i = 0; i < item.getItem().getRenderPasses(item.getItemDamage()); ++i) {
			IIcon icon = item.getItem().getIcon(item, i);
			int color = item.getItem().getColorFromItemStack(item, i);
			float r = (color>>16&0xFF)/255F;
			float g = (color>> 8&0xFF)/255F;
			float b = (color    &0xFF)/255F;
			GL11.glColor4f(r, g, b, 1);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
}
