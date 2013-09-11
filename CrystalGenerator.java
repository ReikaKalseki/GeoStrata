/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import cpw.mods.fml.common.IWorldGenerator;

public class CrystalGenerator implements IWorldGenerator {

	public static final int PER_CHUNK = 60; //calls per chunk; vast majority fail

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		chunkX *= 16;
		chunkZ *= 16;
		//ReikaJavaLibrary.pConsole("Calling chunk "+chunkX+", "+chunkZ);
		for (int i = 0; i < PER_CHUNK*this.getDensityFactor(world, chunkX, chunkZ); i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int posY = random.nextInt(64);
			int id = GeoBlocks.CRYSTAL.getBlockID();
			int meta = random.nextInt(16);
			if (world.getBlockId(posX, posY, posZ) == 0 && world.getBlockId(posX, posY-1, posZ) == Block.stone.blockID) {
				world.setBlock(posX, posY, posZ, id, meta, 3);
				//ReikaJavaLibrary.pConsole("Generating "+ReikaDyeHelper.dyes[meta].getName()+" Crystal at "+posX+", "+posY+", "+posZ);
			}
			int r = 3;
			for (int k = -r; k <= r; k++) {
				for (int l = -r; l <= r; l++) {
					for (int m = -r; m <= r; m++) {
						world.markBlockForRenderUpdate(posX, posY, posZ);
					}
				}
			}
		}
	}

	public float getDensityFactor(World world, int x, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		if (world.provider.dimensionId == ReikaTwilightHelper.TWILIGHT_ID)
			return 2F;
		if (biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore)
			return 1.5F;
		if (biome == BiomeGenBase.ocean || biome == BiomeGenBase.frozenOcean)
			return 1.25F;
		return 1F;
	}

}
