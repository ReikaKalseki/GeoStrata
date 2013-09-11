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

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import cpw.mods.fml.common.IWorldGenerator;

public class CrystalGenerator implements IWorldGenerator {

	public static final int PER_CHUNK = 32;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == 0) {
			for (int i = 0; i < PER_CHUNK; i++) {
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				int posY = random.nextInt(64);
				int id = GeoBlocks.CRYSTAL.getBlockID();
				int meta = random.nextInt(16);
				if (world.getBlockId(posX, posY, posZ) == 0 || ReikaWorldHelper.checkForAdjBlock(world, posX, posY, posZ, 0) != -1) {
					world.setBlock(posX, posY, posZ, id, meta, 3);
					ReikaJavaLibrary.pConsole("Generating "+ReikaDyeHelper.dyes[meta].getName()+" Crystal at "+posX+", "+posY+", "+posZ);
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
	}

}
