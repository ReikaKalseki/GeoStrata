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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Interfaces.ISBRH;

public class LavaRockRenderer implements ISBRH {

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		IIcon ico = Blocks.lava.getIcon(0, 0);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(240);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);

		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);

		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);

		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);

		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);

		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		ico = b.getIcon(0, metadata);

		GL11.glTranslatef(0, 0, 1);

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, -0.01D, 0.0D, ico);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.01D, 0.0D, ico);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, -0.01D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.01D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, -0.01D, 0.0D, 0.0D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.01D, 0.0D, 0.0D, ico);
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);

		IIcon ico = Blocks.lava.getIcon(0, 0);
		v5.setBrightness(240);
		v5.setColorOpaque(255, 255, 255);
		if (b.shouldSideBeRendered(world, x, y-1, z, ForgeDirection.DOWN.ordinal()))
			rb.renderFaceYNeg(b, x, y, z, ico);
		if (b.shouldSideBeRendered(world, x, y+1, z, ForgeDirection.UP.ordinal()))
			rb.renderFaceYPos(b, x, y, z, ico);
		if (b.shouldSideBeRendered(world, x, y, z-1, ForgeDirection.NORTH.ordinal()))
			rb.renderFaceZNeg(b, x, y, z, ico);
		if (b.shouldSideBeRendered(world, x, y, z+1, ForgeDirection.SOUTH.ordinal()))
			rb.renderFaceZPos(b, x, y, z, ico);
		if (b.shouldSideBeRendered(world, x-1, y, z, ForgeDirection.WEST.ordinal()))
			rb.renderFaceXNeg(b, x, y, z, ico);
		if (b.shouldSideBeRendered(world, x+1, y, z, ForgeDirection.EAST.ordinal()))
			rb.renderFaceXPos(b, x, y, z, ico);

		rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.runeRender;
	}



}
