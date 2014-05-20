/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Bees;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import cpw.mods.fml.common.IWorldGenerator;

public class HiveGenerator implements IWorldGenerator {

	public static final HiveGenerator instance = new HiveGenerator();

	public static final int PER_CHUNK = 20;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.terrainType == WorldType.FLAT) //do not generate in superflat
			return;
		int id = world.provider.dimensionId;
		if (id == 0 || id == ReikaTwilightHelper.getDimensionID()) {
			for (int i = 0; i < PER_CHUNK; i++) {
				int x = chunkX*16+random.nextInt(16);
				int z = chunkZ*16+random.nextInt(16);
				int maxy = 64;
				if (world.provider.isHellWorld)
					maxy = 128;
				int posY = 4+random.nextInt(maxy-4);
				int block = GeoBlocks.HIVE.getBlockID();
				int meta = 0;
				if (this.canGenerateAt(world, x, posY, z)) {
					world.setBlock(x, posY, z, block, meta, 3);
				}
			}
		}
	}

	private boolean canGenerateAt(World world, int x, int y, int z) {
		if (world.getBlockId(x, y, z) != 0 && !ReikaWorldHelper.softBlocks(world, x, y, z))
			return false;
		int idb = world.getBlockId(x, y-1, z);
		int ida = world.getBlockId(x, y+1, z);
		if (idb == 0 && ida == 0)
			return false;
		if (Block.blocksList[idb] instanceof BlockFluid)
			return false;
		if (Block.blocksList[idb] instanceof BlockFluidBase)
			return false;
		return ReikaWorldHelper.checkForAdjBlock(world, x, y, z, 0) != null;
	}

}
