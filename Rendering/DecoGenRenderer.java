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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Instantiable.Rendering.RotatedQuad;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Blocks.BlockDecoGen.Types;


public class DecoGenRenderer extends ISBRH {

	private static final RotatedQuad[][][] crystalShapes = new RotatedQuad[4][4][4];

	private final Random renderRand = new Random();
	private final Random renderRandNoY = new Random();

	static {
		for (int i = 0; i < crystalShapes.length; i++) {
			for (int j = 0; j < crystalShapes[i].length; j++) {
				for (int k = 0; k < crystalShapes[i][j].length; k++) {
					double r1 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double r2 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double r3 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double r4 = ReikaRandomHelper.getRandomBetween(0.125, 0.375);
					double rot = ReikaRandomHelper.getRandomPlusMinus(0D, 30D);
					crystalShapes[i][j][k] = new RotatedQuad(r1, r2, r3, r4, rot);
				}
			}
		}
	}

	public static RotatedQuad getCrystalShape(int x, int y, int z) {
		int i = ((x%crystalShapes.length)+crystalShapes.length)%crystalShapes.length;
		int j = ((y%crystalShapes[i].length)+crystalShapes[i].length)%crystalShapes[i].length;
		int k = ((z%crystalShapes[i][j].length)+crystalShapes[i][j].length)%crystalShapes[i][j].length;
		return crystalShapes[i][j][k];
	}

	public DecoGenRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderRand.setSeed(ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y);
		renderRand.nextBoolean();
		renderRand.nextBoolean();
		renderRandNoY.setSeed(ChunkCoordIntPair.chunkXZ2Int(x, z));
		renderRandNoY.nextBoolean();
		renderRandNoY.nextBoolean();

		int meta = world.getBlockMetadata(x, y, z);
		Tessellator v5 = Tessellator.instance;

		this.render(world, x, y, z, block, meta, renderer, v5, renderPass);
		if (renderPass == 1) {
			//this.renderWater(world, x, y, z, block, meta, v5);
		}


		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		return true;
	}

	private void render(IBlockAccess world, int x, int y, int z, Block block, int meta, RenderBlocks rb, Tessellator v5, int renderPass) {
		switch(Types.list[meta]) {
			case CRYSTALSPIKE:
				v5.addTranslation(x, y, z);
				if (renderPass == 0) {
					RotatedQuad r1 = DecoGenRenderer.getCrystalShape(x, y, z);
					RotatedQuad r2 = DecoGenRenderer.getCrystalShape(x, y+1, z);
					IIcon ico = block.getIcon(0, meta);
					float u = ico.getMinU();
					float v = ico.getMinV();
					float du = ico.getMaxU();
					float dv = ico.getMaxV();
					v5.setBrightness(240);
					v5.setColorOpaque_I(0xffffff);
					int n = 0;
					while (world.getBlock(x, y+1+n, z) == block && world.getBlockMetadata(x, y+1+n, z) == meta)
						n++;
					v5.setColorOpaque_I(ReikaColorAPI.GStoHex(Math.max(32+(int)(16*Math.sin((x+y*8+z*2)/8D)), 255-6*ReikaMathLibrary.intpow2(n+1, 2))));

					v5.addTranslation(0.5F, 0, 0.5F);

					double r10x = r1.getPosX(0);
					double r11x = r1.getPosX(1);
					double r12x = r1.getPosX(2);
					double r13x = r1.getPosX(3);
					double r20x = r2.getPosX(0);
					double r21x = r2.getPosX(1);
					double r22x = r2.getPosX(2);
					double r23x = r2.getPosX(3);
					double r10z = r1.getPosZ(0);
					double r11z = r1.getPosZ(1);
					double r12z = r1.getPosZ(2);
					double r13z = r1.getPosZ(3);
					double r20z = r2.getPosZ(0);
					double r21z = r2.getPosZ(1);
					double r22z = r2.getPosZ(2);
					double r23z = r2.getPosZ(3);

					if (world.getBlock(x, y+1, z) != block || world.getBlockMetadata(x, y+1, z) != meta) {
						double d = 0.125;
						r20x *= d;
						r21x *= d;
						r22x *= d;
						r23x *= d;
						r20z *= d;
						r21z *= d;
						r22z *= d;
						r23z *= d;
					}

					if (world.getBlock(x, y-1, z) != block || world.getBlockMetadata(x, y-1, z) != meta) {
						double d = 0.75;
						r10x = Math.signum(r10x)*(1-(d*(1-Math.abs(r10x))));
						r11x = Math.signum(r11x)*(1-(d*(1-Math.abs(r11x))));
						r12x = Math.signum(r12x)*(1-(d*(1-Math.abs(r12x))));
						r13x = Math.signum(r13x)*(1-(d*(1-Math.abs(r13x))));
						r10z = Math.signum(r10z)*(1-(d*(1-Math.abs(r10z))));
						r11z = Math.signum(r11z)*(1-(d*(1-Math.abs(r11z))));
						r12z = Math.signum(r12z)*(1-(d*(1-Math.abs(r12z))));
						r13z = Math.signum(r13z)*(1-(d*(1-Math.abs(r13z))));
					}

					v5.addVertexWithUV(r10x, 0, r10z, u, v);
					v5.addVertexWithUV(r11x, 0, r11z, du, v);
					v5.addVertexWithUV(r12x, 0, r12z, du, dv);
					v5.addVertexWithUV(r13x, 0, r13z, u, dv);

					v5.addVertexWithUV(r23x, 1, r23z, u, dv);
					v5.addVertexWithUV(r22x, 1, r22z, du, dv);
					v5.addVertexWithUV(r21x, 1, r21z, du, v);
					v5.addVertexWithUV(r20x, 1, r20z, u, v);

					v5.addVertexWithUV(r20x, 1, r20z, u, dv);
					v5.addVertexWithUV(r21x, 1, r21z, du, dv);
					v5.addVertexWithUV(r11x, 0, r11z, du, v);
					v5.addVertexWithUV(r10x, 0, r10z, u, v);

					v5.addVertexWithUV(r13x, 0, r13z, u, v);
					v5.addVertexWithUV(r12x, 0, r12z, du, v);
					v5.addVertexWithUV(r22x, 1, r22z, du, dv);
					v5.addVertexWithUV(r23x, 1, r23z, u, dv);

					v5.addVertexWithUV(r21x, 1, r21z, u, dv);
					v5.addVertexWithUV(r22x, 1, r22z, du, dv);
					v5.addVertexWithUV(r12x, 0, r12z, du, v);
					v5.addVertexWithUV(r11x, 0, r11z, u, v);

					v5.addVertexWithUV(r10x, 0, r10z, u, v);
					v5.addVertexWithUV(r13x, 0, r13z, du, v);
					v5.addVertexWithUV(r23x, 1, r23z, du, dv);
					v5.addVertexWithUV(r20x, 1, r20z, u, dv);

					v5.addTranslation(-0.5F, 0, -0.5F);
				}
				v5.addTranslation(-x, -y, -z);
				break;
			case ICICLE:
				if (renderPass == 0) {
					for (int i = 0; i < 3; i++) {
						int y0 = y;
						double w = ReikaRandomHelper.getRandomBetween(2D, 4D, renderRand);
						double dx = ReikaRandomHelper.getRandomPlusMinus(0, 8-w, renderRand);
						double dz = ReikaRandomHelper.getRandomPlusMinus(0, 8-w, renderRand);
						double h = (float)ReikaRandomHelper.getRandomBetween(7D, 24D, renderRand);
						double dy = 16-h;
						while (w >= 1) {
							ReikaRenderHelper.renderBlockSubCube(x, y, z, 8-w+dx, dy, 8-w+dz, w*2, h, w*2, v5, rb, Blocks.packed_ice, 1);
							w -= ReikaRandomHelper.getRandomBetween(0.5D, 1.25D, renderRand);
							h *= ReikaRandomHelper.getRandomBetween(0.75, 1, renderRand);
							dy -= h;
							y0 = (int)(y-dy/16D);
						}
					}
				}
				break;
		}
	}

	private void renderWater(IBlockAccess world, int x, int y, int z, Block block, int meta, Tessellator v5) {
		Block above = world.getBlock(x, y+1, z);
		if (above != Blocks.water && above != Blocks.flowing_water && (above != block || world.getBlockMetadata(x, y+1, z) != meta)) {
			boolean flag = ReikaWorldHelper.hasAdjacentWater(world, x, y, z, false, true);
			if (!flag) {
				for (int i = 2; i < 6 && !flag; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					int dx = x+dir.offsetX;
					int dy = y+dir.offsetY;
					int dz = z+dir.offsetZ;
					flag = flag || ReikaWorldHelper.hasAdjacentWater(world, dx, dy, dz, false, true);
				}
			}
			if (flag) {
				IIcon ico = FluidRegistry.WATER.getIcon();
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				double h = 0.888;
				v5.addVertexWithUV(0, h, 1, u, dv);
				v5.addVertexWithUV(1, h, 1, du, dv);
				v5.addVertexWithUV(1, h, 0, du, v);
				v5.addVertexWithUV(0, h, 0, u, v);
			}
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

}
