/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;

public class LavaRockGenerator implements RetroactiveGenerator {

	public static final LavaRockGenerator instance = new LavaRockGenerator();

	private static final int BASE_CHANCE = Math.max(1, (int)(1F/GeoOptions.getLavaRockDensity()));
	private static final int COUNT_CHANCE = Math.max(1, (int)(2F/GeoOptions.getLavaRockDensity()));

	private LavaRockGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT && Math.abs(world.provider.dimensionId) != 1 && random.nextInt(BASE_CHANCE) == 0) {
			chunkX *= 16;
			chunkZ *= 16;
			int n = random.nextInt(COUNT_CHANCE) == 0 ? 1 : 2;
			for (int i = 0; i < n; i++) {
				int x = chunkX + random.nextInt(16);
				int z = chunkZ + random.nextInt(16);
				int maxy = 12;
				int y = 4+random.nextInt(maxy-4);
				if (this.isValidBlock(world, x, y, z)) {
					this.generate(world, x, y, z);
				}
			}
		}
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
