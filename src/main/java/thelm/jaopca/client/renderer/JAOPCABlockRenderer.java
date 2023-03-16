package thelm.jaopca.client.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.blocks.JAOPCABlock;

public class JAOPCABlockRenderer implements ISimpleBlockRenderingHandler {

	public static final JAOPCABlockRenderer INSTANCE = new JAOPCABlockRenderer();
	public boolean renderingOverlay = false;

	@Override
	public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		int color = block.getRenderColor(meta);
		float r = (color>>16&0xFF)/255F;
		float g = (color>> 8&0xFF)/255F;
		float b = (color    &0xFF)/255F;
		GL11.glColor4f(r, g, b, 1);
		renderBlock(block, meta, renderer);
		if(((IMaterialFormBlock)block).hasOverlay()) {
			GL11.glColor4f(1, 1, 1, 1);
			renderingOverlay = true;
			renderBlock(block, meta, renderer);
		}
		renderingOverlay = false;
	}

	private void renderBlock(Block block, int meta, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, -1, 0);
		renderer.renderFaceYNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
		tessellator.setNormal(0, 1, 0);
		renderer.renderFaceYPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
		tessellator.setNormal(0, 0, -1);
		renderer.renderFaceZNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.setNormal(0, 0, 1);
		renderer.renderFaceZPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
		tessellator.setNormal(-1, 0, 0);
		renderer.renderFaceXNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.setNormal(1, 0, 0);
		renderer.renderFaceXPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
		tessellator.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		boolean flag = renderer.renderStandardBlock(block, x, y, z);
		if(((JAOPCABlock)block).hasOverlay()) {
			renderingOverlay = true;
			double offset = 0.000001;
			renderer.renderMinX -= offset;
			renderer.renderMinY -= offset;
			renderer.renderMinZ -= offset;
			renderer.renderMaxX += offset;
			renderer.renderMaxY += offset;
			renderer.renderMaxZ += offset;
			flag |= renderer.renderStandardBlock(block, x, y, z);
		}
		renderingOverlay = false;
		return flag;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return JAOPCABlock.RENDER_ID;
	}
}
