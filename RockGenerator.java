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
import cpw.mods.fml.common.IWorldGenerator;

public class RockGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		switch(world.provider.dimensionId) {
		case 0:
			this.generateOverworld (world, random, chunkX * 16, chunkZ * 16);
			break;
		case 1:
			//generateEnd	(world, random, chunkX * 16, chunkZ * 16);
			break;
		case -1:
			//generateNether (world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateOverworld(World world, Random random, int chunkX, int chunkZ) {

	}

}
