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

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.common.IWorldGenerator;

public class RockGenerator implements IWorldGenerator {

	public static final int BASE_GEN = 24;
	public static final int VEIN_SIZE = 32;
	public static final int REPLACE_PERCENT = 15;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		if (this.canGenInDimension(world.provider.dimensionId)) {
			this.generateRock(world, random, chunkX, chunkZ);
		}
	}

	private boolean canGenInDimension(int id) {
		if (id == 0)
			return true;
		if (id == 1 || id == -1)
			return false;
		if (id == ReikaTwilightHelper.getDimensionID())
			return GeoOptions.TFGEN.getState();
		return GeoOptions.DIMGEN.getState();
	}

	private void generateRock(World world, Random random, int chunkX, int chunkZ) {
		chunkX *= 16;
		chunkZ *= 16;
		//ReikaJavaLibrary.pConsole("Calling chunk at "+chunkX+", "+chunkZ);
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
		for (int k = 0; k < RockTypes.rockList.length; k++) {
			RockTypes geo = RockTypes.rockList[k];
			for (int i = 0; i < BASE_GEN*geo.rarity*this.getDensityFactor(geo); i++) {
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				int posY = geo.minY + random.nextInt(geo.maxY-geo.minY);
				if (geo.canGenerateAt(world, posX, posY, posZ, random)) {
					int id = geo.getID(RockShapes.SMOOTH);
					int meta = geo.getBlockMetadata();
					(new WorldGenMinable(id, meta, VEIN_SIZE, Block.stone.blockID)).generate(world, random, posX, posY, posZ);
					//GeoStrata.logger.log("Generating "+geo+" at "+posX+", "+posY+", "+posZ);
				}
			}
		}
	}

	/** if compressed in small y, or lots of coincident rocks, reduce density */
	private double getDensityFactor(RockTypes rock) {
		List<RockTypes> types = rock.getCoincidentTypes();
		int h = rock.maxY-rock.minY;
		if (rock == RockTypes.ONYX)
			h *= 2;
		return 1F*h/64D*(3D/types.size());
	}

}
