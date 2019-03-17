/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Math.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.GeoStrata.Registry.GeoBlocks;

public class LavaRockGeneratorRedesign implements RetroactiveGenerator {

	public static final LavaRockGeneratorRedesign instance = new LavaRockGeneratorRedesign();

	private SimplexNoiseGenerator lavaRockThickness;

	private LavaRockGeneratorRedesign() {

	}

	private void seedNoise(World world) {
		if (lavaRockThickness == null || lavaRockThickness.seed != world.getSeed()) {
			lavaRockThickness = new SimplexNoiseGenerator(world.getSeed());
			lavaRockThickness.setFrequency(0.1);
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		this.seedNoise(world);

		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT && Math.abs(world.provider.dimensionId) != 1) {
			//double t = lavaRockThickness.getValue(dx, dz);
			Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 1; y <= 14; y++) {
						Block b = c.getBlock(x, y, z);
						if (b.isReplaceableOreGen(world, x+chunkX*16, y, z+chunkZ*16, Blocks.stone) || b == GeoBlocks.LAVAROCK.getBlockInstance()) {
							int d = this.getLavaDistance(c, x, y, z);
							if (d <= 4) {
								this.placeBlock(c, x, y, z, d-1, b);
							}
						}
					}
				}
			}
		}
	}

	private void placeBlock(Chunk c, int x, int y, int z, int meta, Block at) {
		if (meta > 3)
			return;
		if (at == GeoBlocks.LAVAROCK.getBlockInstance() && meta >= c.getBlockMetadata(x, y, z)) {
			return;
		}
		c.func_150807_a(x, y, z, GeoBlocks.LAVAROCK.getBlockInstance(), meta);
		if (meta < 3) {
			int d = 1;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+d*dir.offsetX;
				int dy = y+d*dir.offsetY;
				int dz = z+d*dir.offsetZ;
				if (dy >= 0 && dx >= 0 && dz >= 0 && dx < 16 && dz < 16) {
					Block b = c.getBlock(dx, dy, dz);
					if (b.isReplaceableOreGen(c.worldObj, x+c.xPosition*16, y, z+c.zPosition*16, Blocks.stone) || b == GeoBlocks.LAVAROCK.getBlockInstance()) {
						//ReikaJavaLibrary.pConsole("Placing "+(meta+1)+" from "+meta+" at "+new Coordinate(dx, dy, dz));
						this.placeBlock(c, dx, dy, dz, meta+this.getMetaStep(c, dx, dy, dz), b);
					}
				}
			}
		}
	}

	private int getMetaStep(Chunk c, int cx, int y, int cz) {
		int x = cx+c.xPosition*16;
		int z = cz+c.zPosition*16;
		lavaRockThickness.setFrequency(0.1);
		double val = lavaRockThickness.getValue(x, z);
		return (int)MathHelper.clamp_double(ReikaMathLibrary.normalizeToBounds(val, 0.5, 3.5), 1, 3);
	}

	private int getLavaDistance(Chunk c, int x, int y, int z) {
		//int ret = Integer.MAX_VALUE;
		for (int d = 1; d <= 4; d++) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+d*dir.offsetX;
				int dy = y+d*dir.offsetY;
				int dz = z+d*dir.offsetZ;
				if (dy > 0 && dy <= 11 && dx >= 0 && dz >= 0 && dx < 16 && dz < 16) {
					Block b = c.getBlock(dx, dy, dz);
					int meta = c.getBlockMetadata(dx, dy, dz);
					if ((b == Blocks.lava || b == Blocks.flowing_lava) && meta == 0) {
						return d;//ret = Math.min(ret, d);
					}/*
					else if (b == GeoBlocks.LAVAROCK.getBlockInstance() && meta < 3) {
						if (ret != Integer.MAX_VALUE && ret > meta+2) //+2 because one is subtracted again above
							ReikaJavaLibrary.pConsole("Found lava rock with meta "+meta+", setting ret from "+ret+" to "+(meta+2));
						ret = Math.min(ret, meta+2);
					}*/
				}
			}
			//if (ret != Integer.MAX_VALUE)
			//	return ret;
		}
		return Integer.MAX_VALUE;//ret;
	}

	private int getLavaDistance(World world, int x, int y, int z) {
		for (int d = 1; d <= 4; d++) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+d*dir.offsetX;
				int dy = y+d*dir.offsetY;
				int dz = z+d*dir.offsetZ;
				Block b = world.getBlock(dx, dy, dz);
				if (b == Blocks.lava || b == Blocks.flowing_lava)
					return d;
			}
		}
		return -1;
	}

	private void generate(World world, int x, int y, int z) {
		BlockArray b = new BlockArray();
		b.maxDepth = 40;
		b.taxiCabDistance = true;
		//b.extraSpread = true;
		b.recursiveAddWithBounds(world, x, y, z, world.getBlock(x, y, z), x-16, y-8, z-24, x+16, y+8, z+24);
		BlockArray[] arrays = new BlockArray[4];
		for (int i = 0; i < 4; i++) {
			BlockArray pre = i == 0 ? b : arrays[i-1];
			arrays[i] = pre.copy();
			arrays[i].expand(1, false);
		}
		for (int i = 0; i < 4; i++) {
			BlockArray pre = i == 0 ? b : arrays[i-1];
			//arrays[i].XORWith(pre);
		}
		for (int i = 3; i >= 0; i--) {
			for (Coordinate c : arrays[i].keySet()) {
				Block bk = c.getBlock(world);
				if (bk.isReplaceableOreGen(world, c.xCoord, c.yCoord, c.zCoord, Blocks.stone) || bk == GeoBlocks.LAVAROCK.getBlockInstance()) {
					c.setBlock(world, GeoBlocks.LAVAROCK.getBlockInstance(), i);
				}
			}
		}
	}

	private boolean isValidBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return b == Blocks.lava || b == Blocks.flowing_lava;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata Lava Rock";
	}

}
