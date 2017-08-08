/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.API;

import java.util.Random;

import net.minecraft.world.World;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.World.RockGenerator;

/** Use this to create custom rock generation "patterns". Note that multiple may be run sequentially. */
public class RockGenerationPatterns {

	public static void registerPattern(RockGenerationPattern p) {
		RockGenerator.instance.registerGenerationPattern(p);
	}

	public static interface RockGenerationPattern {

		/** Called once per chunk per rock type to generate the rock. X and Z are the block coordinates of the Northwest (negative XZ) corner. */
		public void generateRockType(RockTypes geo, World world, Random random, int chunkX, int chunkZ);

		/** Used in standard comparator logic. Higher numbers run later. */
		public int getOrderingIndex();

	}

}
