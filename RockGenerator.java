/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.GeoGen.Registry.GeoBlocks;
import Reika.GeoGen.Registry.RockTypes;
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
		for (int k = 0; k < RockTypes.rockList.length; k++) {
			RockTypes rock = RockTypes.rockList[k];
			if (rock.canGenerateInBiome(world.getBiomeGenForCoords(chunkX, chunkZ))) {
				int spawnChance = (int)(100*rock.getRarity());
				for(int i = 0; i < spawnChance; i ++){
					int posX = chunkX + random.nextInt(16);
					int posZ = chunkZ + random.nextInt(16);
					int posY = rock.getMinY() + random.nextInt(rock.getMaxY()-rock.getMinY());
					(new WorldGenMinable(GeoBlocks.SMOOTH.getBlockID(), k, 16, Block.stone.blockID)).generate(world, random, posX, posY, posZ);
				}
			}
		}
	}

}
