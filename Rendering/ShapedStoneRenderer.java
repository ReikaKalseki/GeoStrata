/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Rendering;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Base.BaseBlockRenderer;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.GeoStrata.Blocks.BlockShapedRock;
import Reika.GeoStrata.Registry.RockTypes;

public class ShapedStoneRenderer extends BaseBlockRenderer {

	private int blend1;
	private int blend2;

	public ShapedStoneRenderer(int ID) {
		super(ID);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator v5 = Tessellator.instance;

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glColor3f(1, 1, 1);
		v5.startDrawingQuads();

		BlockShapedRock b = (BlockShapedRock)block;

		IIcon ico = RockTypes.getTypeFromID(block).getIcon();//b.getIcon(0, metadata);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		IIcon ico2 = Blocks.glass.getIcon(0, 0);//RockShapes.getShape(block).getIcon();//b.getIconForEdge(0, RockTypes.getTypeFromID(Blocks.blockID, metadata));
		float u2 = ico2.getMinU();
		float du2 = ico2.getMaxU();
		float v2 = ico2.getMinV();
		float dv2 = ico2.getMaxV();

		float dx = -0.5F;
		float dy = -0.5F;
		float dz = -0.5F;
		v5.addTranslation(dx, dy, dz);

		this.drawInventoryBlock(b, metadata, renderer, ico);

		v5.addTranslation(-dx, -dy, -dz);

		v5.draw();

		v5.startDrawingQuads();
		v5.addTranslation(dx, dy, dz);
		this.blendMode();
		this.drawInventoryBlock(b, metadata, renderer, ico2);
		v5.addTranslation(-dx, -dy, -dz);
		v5.draw();
		this.unblend();
	}

	private void blendMode() {
		blend1 = GL11.glGetInteger(GL11.GL_BLEND_SRC);
		blend2 = GL11.glGetInteger(GL11.GL_BLEND_DST);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.PREALPHA.apply();
	}

	private void unblend() {
		GL11.glBlendFunc(blend1, blend2);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void drawInventoryBlock(BlockShapedRock b, int metadata, RenderBlocks renderer, IIcon ico) {
		Tessellator v5 = Tessellator.instance;
		int color = b.getRenderColor(metadata);
		Color c = new Color(color);

		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		this.faceBrightnessNoWorld(ForgeDirection.DOWN, v5, c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F);
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(1, 1, 0, u, dv);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(0, 1, 1, du, v);
		v5.addVertexWithUV(1, 1, 1, du, dv);

		this.faceBrightnessNoWorld(ForgeDirection.UP, v5, c.getRed()/512F, c.getGreen()/512F, c.getBlue()/512F);
		v5.addVertexWithUV(0, 0, 0, du, dv);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(1, 0, 1, u, v);
		v5.addVertexWithUV(0, 0, 1, u, dv);

		this.faceBrightnessNoWorld(ForgeDirection.EAST, v5, c.getRed()/425F, c.getGreen()/425F, c.getBlue()/425F);
		v5.addVertexWithUV(1, 0, 0, du, dv);
		v5.addVertexWithUV(1, 1, 0, du, v);
		v5.addVertexWithUV(1, 1, 1, u, v);
		v5.addVertexWithUV(1, 0, 1, u, dv);

		this.faceBrightnessNoWorld(ForgeDirection.WEST, v5, c.getRed()/425F, c.getGreen()/425F, c.getBlue()/425F);
		v5.addVertexWithUV(0, 1, 0, u, dv);
		v5.addVertexWithUV(0, 0, 0, u, v);
		v5.addVertexWithUV(0, 0, 1, du, v);
		v5.addVertexWithUV(0, 1, 1, du, dv);

		this.faceBrightnessNoWorld(ForgeDirection.SOUTH, v5, c.getRed()/364F, c.getGreen()/364F, c.getBlue()/364F);
		v5.addVertexWithUV(0, 1, 1, u, dv);
		v5.addVertexWithUV(0, 0, 1, u, v);
		v5.addVertexWithUV(1, 0, 1, du, v);
		v5.addVertexWithUV(1, 1, 1, du, dv);

		this.faceBrightnessNoWorld(ForgeDirection.NORTH, v5, c.getRed()/364F, c.getGreen()/364F, c.getBlue()/364F);
		v5.addVertexWithUV(0, 0, 0, du, dv);
		v5.addVertexWithUV(0, 1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(1, 0, 0, u, dv);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		BlockShapedRock b = (BlockShapedRock)block;
		int meta = world.getBlockMetadata(x, y, z);
		RockTypes type = RockTypes.getTypeFromID(block);
		Tessellator v5 = Tessellator.instance;
		v5.addTranslation(x, y, z);
		IIcon ico = type.getIcon();
		this.renderBlock(world, x, y, z, b, rb, ico);
		v5.addTranslation(-x, -y, -z);
		v5.draw();
		this.blendMode();
		v5.startDrawingQuads();
		v5.addTranslation(x, y, z);
		IIcon ico2 = Blocks.glass.getIcon(0, 0);//RockShapes.getShape(block).getIcon();
		this.renderBlock(world, x, y, z, b, rb, ico2);
		v5.addTranslation(-x, -y, -z);
		v5.draw();
		this.unblend();
		v5.startDrawingQuads();

		return true;
	}

	private void renderBlock(IBlockAccess world, int x, int y, int z, BlockShapedRock b, RenderBlocks rb, IIcon ico) {
		Tessellator v5 = Tessellator.instance;
		int color = b.colorMultiplier(world, x, y, z);
		Color c = new Color(color);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		for (int i = 0; i < 6; i++) {
			this.faceBrightnessColor(dirs[i].getOpposite(), v5, c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F);
			switch(i) {
			case 0:
				v5.addVertexWithUV(0, 0, 0, u, v);
				v5.addVertexWithUV(1, 0, 0, du, v);
				v5.addVertexWithUV(1, 0, 1, du, dv);
				v5.addVertexWithUV(0, 0, 1, u, dv);
				break;
			case 1:
				v5.addVertexWithUV(0, 1, 1, u, dv);
				v5.addVertexWithUV(1, 1, 1, du, dv);
				v5.addVertexWithUV(1, 1, 0, du, v);
				v5.addVertexWithUV(0, 1, 0, u, v);
				break;
			case 2:
				v5.addVertexWithUV(0, 1, 0, u, v);
				v5.addVertexWithUV(1, 1, 0, du, v);
				v5.addVertexWithUV(1, 0, 0, du, dv);
				v5.addVertexWithUV(0, 0, 0, u, dv);
				break;
			case 3:
				v5.addVertexWithUV(0, 0, 1, u, dv);
				v5.addVertexWithUV(1, 0, 1, du, dv);
				v5.addVertexWithUV(1, 1, 1, du, v);
				v5.addVertexWithUV(0, 1, 1, u, v);
				break;
			case 4:
				v5.addVertexWithUV(0, 0, 0, u, dv);
				v5.addVertexWithUV(0, 0, 1, du, dv);
				v5.addVertexWithUV(0, 1, 1, du, v);
				v5.addVertexWithUV(0, 1, 0, u, v);
				break;
			case 5:
				v5.addVertexWithUV(1, 1, 0, u, v);
				v5.addVertexWithUV(1, 1, 1, du, v);
				v5.addVertexWithUV(1, 0, 1, du, dv);
				v5.addVertexWithUV(1, 0, 0, u, dv);
				break;
			}
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

}
