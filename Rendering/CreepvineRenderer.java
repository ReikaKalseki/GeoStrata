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
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;

import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.GeoStrata.Blocks.BlockCreepvine;


public class CreepvineRenderer extends ISBRH {

	private final Random renderRand = new Random();
	private final Random renderRandNoY = new Random();

	public CreepvineRenderer(int id) {
		super(id);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		renderRand.setSeed(ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y);
		renderRand.nextBoolean();
		renderRand.nextBoolean();
		renderRandNoY.setSeed(ChunkCoordIntPair.chunkXZ2Int(x, z));
		renderRandNoY.nextBoolean();
		renderRandNoY.nextBoolean();
		float dx0 = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.25, renderRandNoY);
		float dz0 = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.25, renderRandNoY);
		float dy0 = (float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625, renderRandNoY);
		Tessellator.instance.addTranslation(dx0, dy0, dz0);

		int meta = world.getBlockMetadata(x, y, z);
		BlockCreepvine b = (BlockCreepvine)block;

		IIcon ico = Blocks.bedrock.blockIcon;
		switch(meta) {
			case 0:
				ico = b.getRandomBaseIcon(renderRand);
				break;
			case 1:
				ico = b.blockIcon;
				break;
			case 2:
				ico = b.getRandomTopIcon(renderRand);
				break;
		}
		Tessellator.instance.setColorOpaque_I(0xffffff);
		Tessellator.instance.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
		float dx = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125, renderRand);
		float dz = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125, renderRand);
		double w = ReikaRandomHelper.getRandomBetween(0, 0.125, rand);
		Tessellator.instance.addTranslation(dx, 0, dz);
		ReikaRenderHelper.renderCropTypeTex(world, x, y, z, ico, Tessellator.instance, rb, w, 1);

		if (meta >= 3) {
			Tessellator.instance.setBrightness(240);
			for (int i = 3; i <= meta; i++) {
				ReikaRenderHelper.renderCropTypeTex(world, x, y, z, b.getSeedIcon(i-3), Tessellator.instance, rb, w, 1);
			}
		}

		Tessellator.instance.addTranslation(-dx, 0, -dz);

		Tessellator.instance.addTranslation(-dx0, -dy0, -dz0);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

}
