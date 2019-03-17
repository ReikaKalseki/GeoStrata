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

import net.minecraft.world.World;

import Reika.GeoStrata.API.RockGenerationPatterns.RockGenerationPattern;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockTypes;

public class BasicRockGenerator implements RockGenerationPattern {

	private final WorldGenGeoRock[] generators = new WorldGenGeoRock[RockTypes.rockList.length];

	public BasicRockGenerator() {
		for (int i = 0; i < generators.length; i++) {
			generators[i] = new WorldGenGeoRock(this, RockTypes.rockList[i], RockGenerator.VEIN_SIZE);
			RockGenerator.instance.registerProfilingSubgenerator(RockTypes.rockList[i], this, generators[i]);
		}
	}

	public void generateRockType(RockTypes geo, World world, Random random, int chunkX, int chunkZ) {
		double max = RockGenerator.BASE_GEN*geo.rarity*this.getDensityFactor(geo);
		//ReikaJavaLibrary.pConsole("Genning "+geo+" "+max+" times.");
		for (int i = 0; i < max; i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int posY = geo.minY + random.nextInt(geo.maxY-geo.minY);
			//GeoStrata.logger.debug(geo.name()+":"+geo.canGenerateAt(world, posX, posY, posZ, random));
			if (geo.canGenerateAt(world, posX, posY, posZ, random)) {
				//(new WorldGenMinable(geo.getID(RockShapes.SMOOTH), VEIN_SIZE, Blocks.stone)).generate(world, random, posX, posY, posZ);
				generators[geo.ordinal()].generate(world, random, posX, posY, posZ);
				//GeoStrata.logger.log("Generating "+geo+" at "+posX+", "+posY+", "+posZ);
			}
		}
	}

	/** if compressed in small y, or lots of coincident rocks, reduce density */
	protected final double getDensityFactor(RockTypes rock) {
		float f = GeoOptions.getRockDensity();
		int h = rock.maxY-rock.minY;
		int d = 1;
		if (rock == RockTypes.ONYX)
			d *= 2;
		return f*d*h/64D*3D/(1+rock.getCoincidentTypes().size()); //+1 since it used to include itself in that list
	}

	@Override
	public int getOrderingIndex() {
		return 0;
	}

}
