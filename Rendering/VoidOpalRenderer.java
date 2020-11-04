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
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;

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

		Tessellator.instance.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
		Tessellator.instance.setColorOpaque_I(0xffffff);
		IIcon ico = BlockVoidOpal.getBaseTexture(/*renderPass == 1*/true);
		if (renderPass == 1) {
			rb.setRenderBounds(0, 0, 0, 1, 1, 1);
		}
		else {
			double o = 0.0625;
			rb.setRenderBounds(o, o, o, 1-o, 1-o, 1-o);
		}
		rb.renderFaceXNeg(b, x, y, z, ico);
		rb.renderFaceYNeg(b, x, y, z, ico);
		rb.renderFaceZNeg(b, x, y, z, ico);
		rb.renderFaceXPos(b, x, y, z, ico);
		rb.renderFaceYPos(b, x, y, z, ico);
		rb.renderFaceZPos(b, x, y, z, ico);
		return true;
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
