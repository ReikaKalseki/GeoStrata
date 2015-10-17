/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.World;

import java.util.Random;

import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockTypes;

public class BandedGenerator extends RockGenerator {

	public static final BandedGenerator instance = new BandedGenerator();

	private BandedGenerator() {
		super();
	}

	@Override
	protected void generateRockType(RockTypes geo, World world, Random random, int chunkX, int chunkZ) {
		double max = BASE_GEN*geo.rarity*GeoOptions.getRockDensity()*2;
		//ReikaJavaLibrary.pConsole("Genning "+geo+" "+max+" times.");
		for (int i = 0; i < max; i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int posY = GeoStrata.config.getRockBand(geo);
			//GeoStrata.logger.debug(geo.name()+":"+geo.canGenerateAt(world, posX, posY, posZ, random));
			if (geo.canGenerateAt(world, posX, posY, posZ, random)) {
				//(new WorldGenMinable(geo.getID(RockShapes.SMOOTH), VEIN_SIZE, Blocks.stone)).generate(world, random, posX, posY, posZ);
				(new WorldGenMinableOreAbsorber(geo, VEIN_SIZE)).generate(world, random, posX, posY, posZ);
				//GeoStrata.logger.log("Generating "+geo+" at "+posX+", "+posY+", "+posZ);
			}
		}
	}

}
