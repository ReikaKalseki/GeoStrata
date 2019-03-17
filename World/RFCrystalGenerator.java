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

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;

public class RFCrystalGenerator implements RetroactiveGenerator {

	public static final RFCrystalGenerator instance = new RFCrystalGenerator();

	private static final int PER_CHUNK = getCrystalAttemptsPerChunk(); //calls per chunk; vast majority fail

	private RFCrystalGenerator() {

	}

	private static int getCrystalAttemptsPerChunk() {
		return (int)(8*GeoOptions.getRFCrystalDensity());
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
			chunkX *= 16;
			chunkZ *= 16;
			for (int i = 0; i < PER_CHUNK; i++) {
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				int maxy = 18;
				int posY = 4+random.nextInt(maxy-4);
				if (this.canGenerateAt(world, posX, posY, posZ)) {
					world.setBlock(posX, posY, posZ, GeoBlocks.RFCRYSTALSEED.getBlockInstance(), 1, 2);
				}
			}
		}
	}

	public static boolean canGenerateAt(World world, int x, int y, int z) {
		return world.getBlock(x, y, z) == Blocks.redstone_ore && ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.air) == null;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata Flux Crystals";
	}

}
