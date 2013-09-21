/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.World;

import java.util.Random;

import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.GeoStrata.Registry.GeoBlocks;

public class RetroCrystalGenerator implements RetroactiveGenerator {

	@Override
	public void generate(Random rand, World world, int chunkX, int chunkZ) {
		for (int i = 0; i < CrystalGenerator.PER_CHUNK*CrystalGenerator.getDensityFactor(world, chunkX, chunkZ); i++) {
			int posX = chunkX + rand.nextInt(16);
			int posZ = chunkZ + rand.nextInt(16);
			int posY = 4+rand.nextInt(64-4);
			int id = GeoBlocks.CRYSTAL.getBlockID();
			int meta = rand.nextInt(16);
			if (CrystalGenerator.canGenerateAt(world, posX, posY, posZ)) {
				world.setBlock(posX, posY, posZ, id, meta, 3);
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

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrataCrystal";
	}

}
