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

import java.util.HashSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.GeoStrata.Blocks.BlockVoidOpal;
import Reika.GeoStrata.Registry.GeoBlocks;

public class VoidOpalRenderer extends ISBRH {

	private final HashSet<Coordinate> flecksToRender = new HashSet();

	public VoidOpalRenderer(int id) {
		super(id);
	}

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

		v5.setColorOpaque_I(0xffffff);
		double s = 0.125/2;
		double ds = 0.03125;
		boolean flag2 = false;
		if (renderPass == 1) {
			//this.renderFlecksAt(world, x, y, z, b, v5);
			//this.queueFleckRender(x, y, z);

			int mix = b.getMixedBrightnessForBlock(world, x, y, z);
			v5.setBrightness(mix);
			v5.setColorOpaque_I(0xffffff);
			IIcon ico = BlockVoidOpal.getBaseTexture(true);
			int i = 0;
			for (double o = 0; o < s; o += ds) {
				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
					flag2 |= this.drawSide(world, x, y, z, o, b, ico, v5, dir);
			};
		}
		else {
			boolean flag = true;
			v5.setBrightness(240);
			if (flag) {
				IIcon ico = BlockVoidOpal.getBaseTexture(false);
				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
					flag2 |= this.drawSide(world, x, y, z, s, b, ico, v5, dir);

				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
					flag2 |= this.drawSide(world, x, y, z, 1, b, b.blockIcon, v5, dir);
			}
			/*
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
			tv5.render();*/
		}
		return flag2;
	}

	private void queueFleckRender(int x, int y, int z) {
		flecksToRender.add(new Coordinate(x, y, z));
		//ReikaJavaLibrary.pConsole(new Coordinate(x, y, z));
	}

	public void renderFlecks(RenderWorldEvent.Post evt) {
		if (!flecksToRender.isEmpty() && evt.pass == 1) {
			Tessellator.instance.startDrawingQuads();
			Tessellator.instance.setTranslation(-evt.renderer.posX, -evt.renderer.posY, -evt.renderer.posZ);
			for (Coordinate c : flecksToRender) {
				this.renderFlecksAt(evt.chunkCache, c.xCoord, c.yCoord, c.zCoord, GeoBlocks.VOIDOPAL.getBlockInstance(), Tessellator.instance);
			}
			//ReikaJavaLibrary.pConsole(flecksToRender);
			Tessellator.instance.draw();
			flecksToRender.clear();
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void renderFlecksAt(IBlockAccess world, int x, int y, int z, Block b, Tessellator v5) {
		v5.setBrightness(240);
		double fo = 0.025*0+ReikaRandomHelper.getRandomPlusMinus(0.025, 0.01, rand);
		int[] clrs = {0x004aff, 0x22aaff, 0x00ff00, 0x00ffff, 0xB200FF};
		for (int i = 0; i < 6; i++) {
			int n = ReikaRandomHelper.getRandomBetween(1, 3, rand);
			for (int k = 0; k < n; k++) {
				//int clr = ReikaColorAPI.getModifiedHue(0xff0000, ReikaRandomHelper.getRandomBetween(/*190*/128, 285, rand));
				int clr = clrs[rand.nextInt(clrs.length)];
				v5.setColorOpaque_I(clr);
				IIcon ico = BlockVoidOpal.getRandomFleckTexture(rand);
				double minsz = 0.375;
				double nx = 0;
				double ny = 0;
				double mx = 1;
				double my = 1;
				ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[i];
				double slide = 0.75;
				boolean saxis = rand.nextBoolean();
				double slidea = saxis ? slide : 0;
				double slideb = !saxis ? slide : 0;
				switch(side) {
					case UP:
					case DOWN:
						if (world.getBlock(x+1, y, z) == b) {
							mx += slidea;
						}
						if (world.getBlock(x-1, y, z) == b) {
							nx -= slidea;
						}
						if (world.getBlock(x, y, z+1) == b) {
							my += slideb;
						}
						if (world.getBlock(x, y, z-1) == b) {
							ny -= slideb;
						}
						break;
					case EAST:
					case WEST:
						if (world.getBlock(x, y+1, z) == b) {
							mx += slidea;
						}
						if (world.getBlock(x, y-1, z) == b) {
							nx -= slidea;
						}
						if (world.getBlock(x, y, z+1) == b) {
							my += slideb;
						}
						if (world.getBlock(x, y, z-1) == b) {
							ny -= slideb;
						}
						break;
					case NORTH:
					case SOUTH:
						if (world.getBlock(x, y+1, z) == b) {
							mx += slidea;
						}
						if (world.getBlock(x, y-1, z) == b) {
							nx -= slidea;
						}
						if (world.getBlock(x+1, y, z) == b) {
							my += slideb;
						}
						if (world.getBlock(x-1, y, z) == b) {
							ny -= slideb;
						}
						break;
				}
				double x0 = ReikaRandomHelper.getRandomBetween(nx, mx-minsz, rand);//rand.nextDouble()*(mx-minsz)+0.01;
				double y0 = ReikaRandomHelper.getRandomBetween(ny, my-minsz, rand);//rand.nextDouble()*(my-minsz)+0.01;
				//x0 = rand.nextInt(4)/4D+0.01;
				//y0 = rand.nextInt(4)/4D+0.01;

				double s = ReikaRandomHelper.getRandomBetween(minsz, 1, rand);
				double sx = ReikaRandomHelper.getRandomPlusMinus(s, 0.0625, rand);
				double sy = ReikaRandomHelper.getRandomPlusMinus(s, 0.0625, rand);

				/*
				double sx = ReikaRandomHelper.getRandomBetween(minsz, Math.min(mx-nx, mx-x0), rand);
				double sy = ReikaRandomHelper.getRandomBetween(minsz, Math.min(my-ny, my-y0), rand);
				double f = sx/sy;
				while (f > 2 || f < 0.5) {
					if (f > 1) {
						sx *= 0.9;
						sy /= 0.9;
					}
					else {
						sy *= 0.9;
						sx /= 0.9;
					}
					f = sx/sy;
				}*/
				this.drawSide(world, x, y, z, fo, b, ico, v5, side, x0, y0, sx, sy, nx, ny, mx, my);
			}
		}
	}

	private boolean drawSide(IBlockAccess world, int x, int y, int z, double o, Block b, IIcon ico, Tessellator v5, ForgeDirection side) {
		return this.drawSide(world, x, y, z, o, b, ico, v5, side, 0, 0, 1, 1, 0, 0, 1, 1);
	}

	@SuppressWarnings("incomplete-switch")
	private boolean drawSide(IBlockAccess world, int x, int y, int z, double o, Block b, IIcon ico, Tessellator v5, ForgeDirection side, double x0, double y0, double sx, double sy, double minX, double minY, double maxX, double maxY) {
		boolean draw = o < 1 ? b.shouldSideBeRendered(world, x, y, z, side.ordinal()) : world.getBlock(x-side.offsetX, y-side.offsetY, z-side.offsetZ) != b;
		if (!draw)
			return false;
		double rnx = minX+o;
		double rpx = maxX-o;
		double rnz = minY+o;
		double rpz = maxY-o;
		if (x0 != 0 || y0 != 0) {
			rnx = x0;
			rnz = y0;
			rpx = Math.min(maxX, x0+sx);
			rpz = Math.min(maxY, y0+sy);
		}
		if (o < 1 && x0 == 0 && y0 == 0) {
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
		if (x0 != 0 || y0 != 0) {
			u = ico.getMinU();
			du = ico.getMaxU();
			v = ico.getMinV();
			dv = ico.getMaxV();
		}
		if (o == 1)
			o = 0.99;
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
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

}
