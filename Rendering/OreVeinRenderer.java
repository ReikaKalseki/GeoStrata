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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Base.BaseBlockRenderer;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.GeoStrata.Blocks.BlockOreVein;
import Reika.GeoStrata.Blocks.BlockOreVein.TileOreVein;
import Reika.GeoStrata.Blocks.BlockOreVein.VeinType;


public class OreVeinRenderer extends BaseBlockRenderer {

	private final Random renderRand = new Random();

	public OreVeinRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		this.setParams(world, x, y, z, block, rb);
		boolean flag = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			IIcon ico = block.getIcon(world, x, y, z, dir.ordinal());
			if (this.isSideExposed(world, x, y, z, dir, block)) {
				this.renderSide(world, x, y, z, block, ico, rb, dir);
				flag = true;
			}/*
			else {
				switch(dir) {
					case DOWN:
						rb.renderFaceYNeg(block, x, y, z, ico);
						break;
					case UP:
						rb.renderFaceYPos(block, x, y, z, ico);
						break;
					case WEST:
						rb.renderFaceXNeg(block, x, y, z, ico);
						break;
					case EAST:
						rb.renderFaceXPos(block, x, y, z, ico);
						break;
					case NORTH:
						rb.renderFaceZNeg(block, x, y, z, ico);
						break;
					case SOUTH:
						rb.renderFaceZPos(block, x, y, z, ico);
						break;
				}
			}*/
		}
		return flag;
	}

	private boolean isSideExposed(IBlockAccess world, int x, int y, int z, ForgeDirection dir, Block b) {
		return b.shouldSideBeRendered(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, dir.ordinal());//!world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ).isOpaqueCube();
	}

	@SuppressWarnings("incomplete-switch")
	private void renderSide(IBlockAccess world, int x, int y, int z, Block block, IIcon ico, RenderBlocks rb, ForgeDirection dir) {
		renderRand.setSeed((ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y) + dir.ordinal()*19831);
		renderRand.nextBoolean();
		renderRand.nextBoolean();
		TileOreVein te = (TileOreVein)world.getTileEntity(x, y, z);
		VeinType v = te.getType();
		int base = 4-v.maximumHarvestCycles/3; //min size when < 6, full size when >= 9
		double edge = ReikaRandomHelper.getRandomBetween(MathHelper.clamp_int(base-1, 1, 3), MathHelper.clamp_int(base+1, 1, 3), renderRand)/8D;
		double inset = 0.245;//0.125;
		double maxOut = 0;//0.125;
		double u1 = edge*16;
		double u2 = (1-edge)*16;
		double u3 = (1-inset)*16;
		TessellatorVertexList v5 = new TessellatorVertexList(0.5, 0.5, 0.5);
		v5.addVertexWithUV(0, 1, edge, ico.getInterpolatedU(0), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(1, 1, edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(1, 1, 0, ico.getInterpolatedU(16), ico.getInterpolatedV(0));
		v5.addVertexWithUV(0, 1, 0, ico.getInterpolatedU(0), ico.getInterpolatedV(0));

		v5.addVertexWithUV(0, 1, 1, ico.getInterpolatedU(0), ico.getInterpolatedV(16));
		v5.addVertexWithUV(1, 1, 1, ico.getInterpolatedU(16), ico.getInterpolatedV(16));
		v5.addVertexWithUV(1, 1, 1-edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(0, 1, 1-edge, ico.getInterpolatedU(0), ico.getInterpolatedV(u2));

		v5.addVertexWithUV(0, 1, 1-edge, ico.getInterpolatedU(0), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(edge, 1, 1-edge, ico.getInterpolatedU(u1), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(edge, 1, edge, ico.getInterpolatedU(u1), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(0, 1, edge, ico.getInterpolatedU(0), ico.getInterpolatedV(u1));

		v5.addVertexWithUV(1-edge, 1, 1-edge, ico.getInterpolatedU(u2), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1, 1, 1-edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1, 1, edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(1-edge, 1, edge, ico.getInterpolatedU(u2), ico.getInterpolatedV(u1));

		v5.addVertexWithUV(edge, 1-inset, 1-edge, ico.getInterpolatedU(u1), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1-edge, 1-inset, 1-edge, ico.getInterpolatedU(u2), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1-edge, 1-inset, edge, ico.getInterpolatedU(u2), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(edge, 1-inset, edge, ico.getInterpolatedU(u1), ico.getInterpolatedV(u1));

		v5.addVertexWithUV(edge, 1-inset, edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(edge, 1, edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(edge, 1, 1-edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(edge, 1-inset, 1-edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u2));

		v5.addVertexWithUV(1-edge, 1-inset, 1-edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1-edge, 1, 1-edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1-edge, 1, edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(1-edge, 1-inset, edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u1));

		v5.addVertexWithUV(1-edge, 1-inset, edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1-edge, 1, edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(edge, 1, edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(edge, 1-inset, edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u1));

		v5.addVertexWithUV(edge, 1-inset, 1-edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(edge, 1, 1-edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u1));
		v5.addVertexWithUV(1-edge, 1, 1-edge, ico.getInterpolatedU(16), ico.getInterpolatedV(u2));
		v5.addVertexWithUV(1-edge, 1-inset, 1-edge, ico.getInterpolatedU(u3), ico.getInterpolatedV(u2));

		Block b2 = v.containedBlockIcon;
		switch(dir) {
			case DOWN:
				v5.invertY();
				break;
			case WEST:
				v5.rotateYtoX();
				v5.rotateYtoZ();
				v5.invertX();
				break;
			case EAST:
				v5.rotateYtoX();
				v5.rotateYtoZ();
				break;
			case NORTH:
				v5.rotateYtoZ();
				v5.invertZ();
				break;
			case SOUTH:
				v5.rotateYtoZ();
				break;
		}
		BlockOreVein.isRenderCenter = true;
		for (int i = (int)u1; i < u2; i += 2) {
			for (int k = (int)u1; k < u2; k += 2) {
				double h = ReikaRandomHelper.getRandomBetween(0.5, (inset+maxOut)*16, renderRand); //calculate this before the return to prevent reseed as it drops
				if (renderRand.nextFloat() > te.getRichness())
					continue;
				switch(dir) {
					case DOWN:
						ReikaRenderHelper.renderBlockSubCube(x, y, z, i, inset*16-h, k, 2, h, 2, Tessellator.instance, rb, b2, 0);
						break;
					case UP:
						ReikaRenderHelper.renderBlockSubCube(x, y, z, i, 16-inset*16, k, 2, h, 2, Tessellator.instance, rb, b2, 0);
						break;
					case WEST:
						ReikaRenderHelper.renderBlockSubCube(x, y, z, inset*16-h, i, k, h, 2, 2, Tessellator.instance, rb, b2, 0);
						break;
					case EAST:
						ReikaRenderHelper.renderBlockSubCube(x, y, z, 16-inset*16, i, k, h, 2, 2, Tessellator.instance, rb, b2, 0);
						break;
					case NORTH:
						ReikaRenderHelper.renderBlockSubCube(x, y, z, k, i, inset*16-h, 2, 2, h, Tessellator.instance, rb, b2, 0);
						break;
					case SOUTH:
						ReikaRenderHelper.renderBlockSubCube(x, y, z, k, i, 16-inset*16, 2, 2, h, Tessellator.instance, rb, b2, 0);
						break;
				}
			}
		}
		BlockOreVein.isRenderCenter = false;
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ));
		this.faceBrightness(dir, Tessellator.instance);
		v5.offset(x, y, z);
		v5.render();
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

}
