/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Rendering;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Blocks.BlockVoidOpal;

public class VoidOpalRenderer implements ISBRH {

	public static int renderPass;

	private final Random rand = new Random();

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(0, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(1, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(2, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(3, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(4, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(5, metadata));
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		rand.setSeed(this.calcSeed(x, y, z));
		rand.nextBoolean();

		v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
		v5.setColorOpaque_I(0xffffff);
		if (renderPass == 1) {
			IIcon ico = BlockVoidOpal.getBaseTexture(true);
			for (double o = 0; o <= 0.125; o += 0.03125) {
				this.drawSide(world, x, y, z, o, b, ico, v5, ForgeDirection.UP);
				this.drawSide(world, x, y, z, o, b, ico, v5, ForgeDirection.DOWN);
				this.drawSide(world, x, y, z, o, b, ico, v5, ForgeDirection.EAST);
				this.drawSide(world, x, y, z, o, b, ico, v5, ForgeDirection.WEST);
				this.drawSide(world, x, y, z, o, b, ico, v5, ForgeDirection.NORTH);
				this.drawSide(world, x, y, z, o, b, ico, v5, ForgeDirection.SOUTH);
			};
			return true;
		}
		else {
			boolean flag = false;
			if (flag) {
				IIcon ico = BlockVoidOpal.getBaseTexture(false);
				this.drawSide(world, x, y, z, 1, b, ico, v5, ForgeDirection.UP);
				this.drawSide(world, x, y, z, 1, b, ico, v5, ForgeDirection.DOWN);
				this.drawSide(world, x, y, z, 1, b, ico, v5, ForgeDirection.EAST);
				this.drawSide(world, x, y, z, 1, b, ico, v5, ForgeDirection.WEST);
				this.drawSide(world, x, y, z, 1, b, ico, v5, ForgeDirection.NORTH);
				this.drawSide(world, x, y, z, 1, b, ico, v5, ForgeDirection.SOUTH);
			}
			flag = true;
			double co = 0.025;
			int clr = 0x22aaff;
			IIcon ico = ChromaIcons.BLANK.getIcon();
			TessellatorVertexList tv5 = new TessellatorVertexList();
			double x0 = rand.nextDouble();
			double z0 = rand.nextDouble();
			double[] dx = new double[4];
			double[] dz = new double[4];
			double dw = 0.375;//0.25;
			double dl = 0.5;//0.375;0.25;
			double ol = 0.125;
			dx[0] = x0-rand.nextDouble()*dl-ol;
			dx[1] = x0-dw/2+rand.nextDouble()*dw;
			dx[2] = x0+rand.nextDouble()*dl+ol;
			dx[3] = x0-dw/2+rand.nextDouble()*dw;
			dz[0] = z0-dw/2+rand.nextDouble()*dw;
			dz[1] = z0+rand.nextDouble()*dl+ol;
			dz[2] = z0-dw/2+rand.nextDouble()*dw;
			dz[3] = z0-rand.nextDouble()*dl-ol;
			for (int i = 0; i < 4; i++) {
				dx[i] = MathHelper.clamp_double(dx[i], 0, 1);
				dz[i] = MathHelper.clamp_double(dz[i], 0, 1);
			}
			tv5.addVertexWithUVColor(x+dx[0], y+1-co, z+dz[0], ico.getMinU(), ico.getMaxV(), clr);
			tv5.addVertexWithUVColor(x+dx[1], y+1-co, z+dz[1], ico.getMaxU(), ico.getMaxV(), clr);
			tv5.addVertexWithUVColor(x+dx[2], y+1-co, z+dz[2], ico.getMaxU(), ico.getMinV(), clr);
			tv5.addVertexWithUVColor(x+dx[3], y+1-co, z+dz[3], ico.getMinU(), ico.getMinV(), clr);
			tv5.render();
			return flag;
		}
	}

	private void drawSide(IBlockAccess world, int x, int y, int z, double o, Block b, IIcon ico, Tessellator v5, ForgeDirection side) {
		if (o < 1 && !b.shouldSideBeRendered(world, x, y, z, side.ordinal()))
			return;
		double rnx = 0+o;
		double rpx = 1-o;
		double rny = 0+o;
		double rpy = 1-o;
		double rnz = 0+o;
		double rpz = 1-o;
		if (o < 1) {
			switch(side) {
				case UP:
				case DOWN:
					if (world.getBlock(x+1, y, z) == b) {
						rpx = 1;
					}
					if (world.getBlock(x-1, y, z) == b) {
						rnx = 0;
					}
					if (world.getBlock(x, y, z+1) == b) {
						rpz = 1;
					}
					if (world.getBlock(x, y, z-1) == b) {
						rnz = 0;
					}
					break;
				case EAST:
				case WEST:
					if (world.getBlock(x, y+1, z) == b) {
						rpx = 1;
					}
					if (world.getBlock(x, y-1, z) == b) {
						rnx = 0;
					}
					if (world.getBlock(x, y, z+1) == b) {
						rpz = 1;
					}
					if (world.getBlock(x, y, z-1) == b) {
						rnz = 0;
					}
					break;
				case NORTH:
				case SOUTH:
					if (world.getBlock(x, y+1, z) == b) {
						rpx = 1;
					}
					if (world.getBlock(x, y-1, z) == b) {
						rnx = 0;
					}
					if (world.getBlock(x+1, y, z) == b) {
						rpz = 1;
					}
					if (world.getBlock(x-1, y, z) == b) {
						rnz = 0;
					}
					break;
			}
		}
		int dx = BlockVoidOpal.getXIndex(x, y, z, side.ordinal());
		int dy = BlockVoidOpal.getYIndex(x, y, z, side.ordinal());
		double u = ico.getInterpolatedU(rnx * 4 + dx*4);
		double du = ico.getInterpolatedU(rpx * 4 + dx*4);
		double v = ico.getInterpolatedV(rnz * 4 + dy*4);
		double dv = ico.getInterpolatedV(rpz * 4 + dy*4);
		switch(side) {
			case UP:
				v5.addVertexWithUV(x+rpx, y+1-o, z+rpz, du, dv);
				v5.addVertexWithUV(x+rpx, y+1-o, z+rnz, du, v);
				v5.addVertexWithUV(x+rnx, y+1-o, z+rnz, u, v);
				v5.addVertexWithUV(x+rnx, y+1-o, z+rpz, u, dv);
				break;
			case DOWN:
				v5.addVertexWithUV(x+rnx, y+o, z+rpz, u, dv);
				v5.addVertexWithUV(x+rnx, y+o, z+rnz, u, v);
				v5.addVertexWithUV(x+rpx, y+o, z+rnz, du, v);
				v5.addVertexWithUV(x+rpx, y+o, z+rpz, du, dv);
				break;
			case EAST:
				v5.addVertexWithUV(x+1-o, y+rnx, z+rpz, u, dv);
				v5.addVertexWithUV(x+1-o, y+rnx, z+rnz, du, dv);
				v5.addVertexWithUV(x+1-o, y+rpx, z+rnz, du, v);
				v5.addVertexWithUV(x+1-o, y+rpx, z+rpz, u, v);
				break;
			case WEST:
				v5.addVertexWithUV(x+o, y+rpx, z+rpz, du, v);
				v5.addVertexWithUV(x+o, y+rpx, z+rnz, u, v);
				v5.addVertexWithUV(x+o, y+rnx, z+rnz, u, dv);
				v5.addVertexWithUV(x+o, y+rnx, z+rpz, du, dv);
				break;
			case NORTH:
				v5.addVertexWithUV(x+rpz, y+rnx, z+o, u, dv);
				v5.addVertexWithUV(x+rnz, y+rnx, z+o, du, dv);
				v5.addVertexWithUV(x+rnz, y+rpx, z+o, du, v);
				v5.addVertexWithUV(x+rpz, y+rpx, z+o, u, v);
				break;
			case SOUTH:
				v5.addVertexWithUV(x+rpz, y+rpx, z+1-o, du, v);
				v5.addVertexWithUV(x+rnz, y+rpx, z+1-o, u, v);
				v5.addVertexWithUV(x+rnz, y+rnx, z+1-o, u, dv);
				v5.addVertexWithUV(x+rpz, y+rnx, z+1-o, du, dv);
				break;
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return GeoStrata.proxy.voidopalRender;
	}

	private long calcSeed(int x, int y, int z) {
		return ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y;
	}



}
