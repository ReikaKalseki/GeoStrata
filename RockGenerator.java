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
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.common.IWorldGenerator;

public class RockGenerator implements IWorldGenerator {

	public static final int BASE_GEN = 4;
	public static final int VEIN_SIZE = 32;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		if (GeoOptions.ROCKGEN.getState() && world.provider.dimensionId == 0) {
			this.generateOverworld(world, random, chunkX, chunkZ);
		}
	}

	private void generateOverworld(World world, Random random, int chunkX, int chunkZ) {
		chunkX *= 16;
		chunkZ *= 16;
		ReikaJavaLibrary.pConsole("Calling chunk at "+chunkX+", "+chunkZ);
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
		for (int k = 0; k < RockTypes.rockList.length; k++) {
			RockTypes geo = RockTypes.rockList[k];
			for (int i = 0; i < BASE_GEN*geo.rarity*this.getDensityFactor(geo); i++) {
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				int posY = geo.minY + random.nextInt(geo.maxY-geo.minY);
				if (geo.canGenerateAt(world, posX, posY, posZ, random)) {
					int id = GeoBlocks.SMOOTH.getBlockID();
					(new WorldGenMinable(id, k, VEIN_SIZE, Block.stone.blockID)).generate(world, random, posX, posY, posZ);
					//GeoStrata.logger.log("Generating "+geo+" at "+posX+", "+posY+", "+posZ);
				}
			}
		}
	}

	private double getDensityFactor(RockTypes rock) {
		int h = rock.maxY-rock.minY;
		return 1F*h/64D;
	}

}
