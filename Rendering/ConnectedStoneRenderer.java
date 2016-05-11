/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Rendering;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.BaseBlockRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.GeoStrata.Blocks.BlockConnectedRock;
import Reika.GeoStrata.Registry.RockTypes;

public class ConnectedStoneRenderer extends BaseBlockRenderer {

	public static int renderPass;

	public ConnectedStoneRenderer(int ID) {
		super(ID);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator v5 = Tessellator.instance;

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glColor3f(1, 1, 1);
		v5.startDrawingQuads();

		BlockConnectedRock b = (BlockConnectedRock)block;

		IIcon ico = b.getIcon(0, metadata);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		IIcon ico2 = b.getIconForEdge(0, RockTypes.getTypeFromID(block));
		float u2 = ico2.getMinU();
		float du2 = ico2.getMaxU();
		float v2 = ico2.getMinV();
		float dv2 = ico2.getMaxV();

		float dx = -0.5F;
		float dy = -0.5F;
		float dz = -0.5F;
		v5.addTranslation(dx, dy, dz);

		int color = b.getRenderColor(metadata);
		Color c = new Color(color);

		this.faceBrightnessNoWorld(ForgeDirection.DOWN, v5, c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F);
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(0, 1, 0, du, v);
		v5.addVertexWithUV(0, 1, 1, du, dv);
		v5.addVertexWithUV(1, 1, 1, u, dv);

		v5.addVertexWithUV(1, 1, 0, u2, v2);
		v5.addVertexWithUV(0, 1, 0, du2, v2);
		v5.addVertexWithUV(0, 1, 1, du2, dv2);
		v5.addVertexWithUV(1, 1, 1, u2, dv2);

		this.faceBrightnessNoWorld(ForgeDirection.UP, v5, c.getRed()/512F, c.getGreen()/512F, c.getBlue()/512F);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(1, 0, 0, u, v);
		v5.addVertexWithUV(1, 0, 1, u, dv);
		v5.addVertexWithUV(0, 0, 1, du, dv);

		v5.addVertexWithUV(0, 0, 0, du2, v2);
		v5.addVertexWithUV(1, 0, 0, u2, v2);
		v5.addVertexWithUV(1, 0, 1, u2, dv2);
		v5.addVertexWithUV(0, 0, 1, du2, dv2);

		this.faceBrightnessNoWorld(ForgeDirection.EAST, v5, c.getRed()/425F, c.getGreen()/425F, c.getBlue()/425F);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 1, u, dv);
		v5.addVertexWithUV(1, 0, 1, du, dv);

		v5.addVertexWithUV(1, 0, 0, du2, v2);
		v5.addVertexWithUV(1, 1, 0, u2, v2);
		v5.addVertexWithUV(1, 1, 1, u2, dv2);
		v5.addVertexWithUV(1, 0, 1, du2, dv2);

		this.faceBrightnessNoWorld(ForgeDirection.WEST, v5, c.getRed()/425F, c.getGreen()/425F, c.getBlue()/425F);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(0, 0, 1, du, dv);
		v5.addVertexWithUV(0, 1, 1, u, dv);

		v5.addVertexWithUV(0, 1, 0, u2, v2);
		v5.addVertexWithUV(0, 0, 0, du2, v2);
		v5.addVertexWithUV(0, 0, 1, du2, dv2);
		v5.addVertexWithUV(0, 1, 1, u2, dv2);

		this.faceBrightnessNoWorld(ForgeDirection.SOUTH, v5, c.getRed()/364F, c.getGreen()/364F, c.getBlue()/364F);
		v5.addVertexWithUV(0, 1, 1, u, v);
		v5.addVertexWithUV(0, 0, 1, du, v);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(1, 1, 1, u, dv);

		v5.addVertexWithUV(0, 1, 1, u2, v2);
		v5.addVertexWithUV(0, 0, 1, du2, v2);
		v5.addVertexWithUV(1, 0, 1, du2, dv2);
		v5.addVertexWithUV(1, 1, 1, u2, dv2);

		this.faceBrightnessNoWorld(ForgeDirection.NORTH, v5, c.getRed()/364F, c.getGreen()/364F, c.getBlue()/364F);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 0, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);

		v5.addVertexWithUV(0, 0, 0, du2, v2);
		v5.addVertexWithUV(0, 1, 0, u2, v2);
		v5.addVertexWithUV(1, 1, 0, u2, dv2);
		v5.addVertexWithUV(1, 0, 0, du2, dv2);

		v5.addTranslation(-dx, -dy, -dz);

		v5.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		BlockConnectedRock b = (BlockConnectedRock)block;
		RockTypes type = RockTypes.getTypeFromID(block);
		Tessellator v5 = Tessellator.instance;
		int color = b.colorMultiplier(world, x, y, z);
		int[] rgb = ReikaColorAPI.HexToRGB(color);

		if (renderPass == 0) {
			rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, rgb[0]/255F, rgb[1]/255F, rgb[2]/255F);
			/*
			for (int i = 0; i < 6; i++) {
				IIcon ico = b.getIcon(world, x, y, z, i);

				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();

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
			 */
		}
		else {

			//v5.addTranslation(-x, -y, -z);
			//v5.draw();
			//GL11.glEnable(GL11.GL_BLEND);
			//BlendMode.DEFAULT.apply();
			//v5.startDrawingQuads();
			//	v5.addTranslation(x, y, z);
			v5.addTranslation(x, y, z);
			this.renderOverlay(world, x, y, z, block, modelId, rb);
			v5.addTranslation(-x, -y, -z);
			//v5.draw();
			//GL11.glDisable(GL11.GL_BLEND);
			//v5.startDrawingQuads();
		}

		return true;
	}

	private void renderOverlay(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		BlockConnectedRock b = (BlockConnectedRock)block;
		RockTypes type = RockTypes.getTypeFromID(block);
		Tessellator v5 = Tessellator.instance;
		v5.setColorOpaque(255, 255, 255);

		double d = ModList.optifineInstalled() ? 0.005 : 0.001;
		if (b.shouldSideBeRendered(world, x, y+1, z, ForgeDirection.UP.ordinal())) {
			ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.UP, type);
			this.faceBrightness(ForgeDirection.DOWN, v5);
			for (int i = 0; i < li.size(); i++) {
				int edge = li.get(i);
				IIcon ico = b.getIconForEdge(edge, type);
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				float uu = du-u;
				float vv = dv-v;
				float dx = uu/16F;
				float dz = vv/16F;

				v5.addVertexWithUV(1+d, 1+d, 0-d, u, v);
				v5.addVertexWithUV(0-d, 1+d, 0-d, du, v);
				v5.addVertexWithUV(0-d, 1+d, 1+d, du, dv);
				v5.addVertexWithUV(1+d, 1+d, 1+d, u, dv);
			}

			if (b.hasCentralTexture(type)) {
				li = b.getSectionsForTexture(world, x, y, z, ForgeDirection.UP, type);
				for (int i = 0; i < li.size(); i++) {
					int edge = li.get(i);
					IIcon ico = b.getSectionForTexture(edge, type);
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					float uu = du-u;
					float vv = dv-v;
					float dx = uu/16F;
					float dz = vv/16F;

					v5.addVertexWithUV(1+d, 1+d, 0-d, u, v);
					v5.addVertexWithUV(0-d, 1+d, 0-d, du, v);
					v5.addVertexWithUV(0-d, 1+d, 1+d, du, dv);
					v5.addVertexWithUV(1+d, 1+d, 1+d, u, dv);
				}
			}
		}

		if (b.shouldSideBeRendered(world, x, y-1, z, ForgeDirection.DOWN.ordinal())) {
			ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.DOWN, type);
			this.faceBrightness(ForgeDirection.UP, v5);
			for (int i = 0; i < li.size(); i++) {
				int edge = li.get(i);
				IIcon ico = b.getIconForEdge(edge, type);
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				float uu = du-u;
				float vv = dv-v;
				float dx = uu/16F;
				float dz = vv/16F;

				v5.addVertexWithUV(0-d, 0-d, 0-d, du, v);
				v5.addVertexWithUV(1+d, 0-d, 0-d, u, v);
				v5.addVertexWithUV(1+d, 0-d, 1+d, u, dv);
				v5.addVertexWithUV(0-d, 0-d, 1+d, du, dv);
			}

			if (b.hasCentralTexture(type)) {
				li = b.getSectionsForTexture(world, x, y, z, ForgeDirection.DOWN, type);
				for (int i = 0; i < li.size(); i++) {
					int edge = li.get(i);
					IIcon ico = b.getSectionForTexture(edge, type);
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					float uu = du-u;
					float vv = dv-v;
					float dx = uu/16F;
					float dz = vv/16F;

					v5.addVertexWithUV(0-d, 0-d, 0-d, du, v);
					v5.addVertexWithUV(1+d, 0-d, 0-d, u, v);
					v5.addVertexWithUV(1+d, 0-d, 1+d, u, dv);
					v5.addVertexWithUV(0-d, 0-d, 1+d, du, dv);
				}
			}
		}

		if (b.shouldSideBeRendered(world, x+1, y, z, ForgeDirection.EAST.ordinal())) {
			ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.EAST, type);
			this.faceBrightness(ForgeDirection.WEST, v5);
			for (int i = 0; i < li.size(); i++) {
				int edge = li.get(i);
				IIcon ico = b.getIconForEdge(edge, type);
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				float uu = du-u;
				float vv = dv-v;
				float dx = uu/16F;
				float dz = vv/16F;

				v5.addVertexWithUV(1+d, 0-d, 0-d, du, v);
				v5.addVertexWithUV(1+d, 1+d, 0-d, u, v);
				v5.addVertexWithUV(1+d, 1+d, 1+d, u, dv);
				v5.addVertexWithUV(1+d, 0-d, 1+d, du, dv);
			}

			if (b.hasCentralTexture(type)) {
				li = b.getSectionsForTexture(world, x, y, z, ForgeDirection.EAST, type);
				for (int i = 0; i < li.size(); i++) {
					int edge = li.get(i);
					IIcon ico = b.getSectionForTexture(edge, type);
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					float uu = du-u;
					float vv = dv-v;
					float dx = uu/16F;
					float dz = vv/16F;

					v5.addVertexWithUV(1+d, 0-d, 0-d, du, v);
					v5.addVertexWithUV(1+d, 1+d, 0-d, u, v);
					v5.addVertexWithUV(1+d, 1+d, 1+d, u, dv);
					v5.addVertexWithUV(1+d, 0-d, 1+d, du, dv);
				}
			}
		}

		if (b.shouldSideBeRendered(world, x-1, y, z, ForgeDirection.WEST.ordinal())) {
			ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.WEST, type);
			this.faceBrightness(ForgeDirection.EAST, v5);
			for (int i = 0; i < li.size(); i++) {
				int edge = li.get(i);
				IIcon ico = b.getIconForEdge(edge, type);
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				float uu = du-u;
				float vv = dv-v;
				float dx = uu/16F;
				float dz = vv/16F;

				v5.addVertexWithUV(0-d, 1+d, 0-d, u, v);
				v5.addVertexWithUV(0-d, 0-d, 0-d, du, v);
				v5.addVertexWithUV(0-d, 0-d, 1+d, du, dv);
				v5.addVertexWithUV(0-d, 1+d, 1+d, u, dv);
			}

			if (b.hasCentralTexture(type)) {
				li = b.getSectionsForTexture(world, x, y, z, ForgeDirection.WEST, type);
				for (int i = 0; i < li.size(); i++) {
					int edge = li.get(i);
					IIcon ico = b.getSectionForTexture(edge, type);
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					float uu = du-u;
					float vv = dv-v;
					float dx = uu/16F;
					float dz = vv/16F;

					v5.addVertexWithUV(0-d, 1+d, 0-d, u, v);
					v5.addVertexWithUV(0-d, 0-d, 0-d, du, v);
					v5.addVertexWithUV(0-d, 0-d, 1+d, du, dv);
					v5.addVertexWithUV(0-d, 1+d, 1+d, u, dv);
				}
			}
		}

		if (b.shouldSideBeRendered(world, x, y, z+1, ForgeDirection.SOUTH.ordinal())) {
			ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.SOUTH, type);
			this.faceBrightness(ForgeDirection.NORTH, v5);
			for (int i = 0; i < li.size(); i++) {
				int edge = li.get(i);
				IIcon ico = b.getIconForEdge(edge, type);
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				float uu = du-u;
				float vv = dv-v;
				float dx = uu/16F;
				float dz = vv/16F;

				v5.addVertexWithUV(0-d, 1+d, 1+d, u, v);
				v5.addVertexWithUV(0-d, 0-d, 1+d, du, v);
				v5.addVertexWithUV(1+d, 0-d, 1+d, du, dv);
				v5.addVertexWithUV(1+d, 1+d, 1+d, u, dv);
			}

			if (b.hasCentralTexture(type)) {
				li = b.getSectionsForTexture(world, x, y, z, ForgeDirection.SOUTH, type);
				for (int i = 0; i < li.size(); i++) {
					int edge = li.get(i);
					IIcon ico = b.getSectionForTexture(edge, type);
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					float uu = du-u;
					float vv = dv-v;
					float dx = uu/16F;
					float dz = vv/16F;

					v5.addVertexWithUV(0-d, 1+d, 1+d, u, v);
					v5.addVertexWithUV(0-d, 0-d, 1+d, du, v);
					v5.addVertexWithUV(1+d, 0-d, 1+d, du, dv);
					v5.addVertexWithUV(1+d, 1+d, 1+d, u, dv);
				}
			}
		}

		if (b.shouldSideBeRendered(world, x, y, z-1, ForgeDirection.NORTH.ordinal())) {
			ArrayList<Integer> li = b.getEdgesForFace(world, x, y, z, ForgeDirection.NORTH, type);
			this.faceBrightness(ForgeDirection.SOUTH, v5);
			for (int i = 0; i < li.size(); i++) {
				int edge = li.get(i);
				IIcon ico = b.getIconForEdge(edge, type);
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				float uu = du-u;
				float vv = dv-v;
				float dx = uu/16F;
				float dz = vv/16F;

				v5.addVertexWithUV(0-d, 0-d, 0-d, du, v);
				v5.addVertexWithUV(0-d, 1+d, 0-d, u, v);
				v5.addVertexWithUV(1+d, 1+d, 0-d, u, dv);
				v5.addVertexWithUV(1+d, 0-d, 0-d, du, dv);
			}

			if (b.hasCentralTexture(type)) {
				li = b.getSectionsForTexture(world, x, y, z, ForgeDirection.NORTH, type);
				for (int i = 0; i < li.size(); i++) {
					int edge = li.get(i);
					IIcon ico = b.getSectionForTexture(edge, type);
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();
					float uu = du-u;
					float vv = dv-v;
					float dx = uu/16F;
					float dz = vv/16F;

					v5.addVertexWithUV(0-d, 0-d, 0-d, du, v);
					v5.addVertexWithUV(0-d, 1+d, 0-d, u, v);
					v5.addVertexWithUV(1+d, 1+d, 0-d, u, dv);
					v5.addVertexWithUV(1+d, 0-d, 0-d, du, dv);
				}
			}
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

}
