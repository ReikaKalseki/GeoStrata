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

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;
import cpw.mods.fml.common.IWorldGenerator;

public class CrystalGenerator implements IWorldGenerator {

	public static final int PER_CHUNK = 60; //calls per chunk; vast majority fail

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		chunkX *= 16;
		chunkZ *= 16;
		for (int i = 0; i < PER_CHUNK*this.getDensityFactor(world, chunkX, chunkZ); i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int maxy = 64;
			if (world.provider.isHellWorld)
				maxy = 128;
			int posY = 4+random.nextInt(maxy-4);
			int id = GeoBlocks.CRYSTAL.getBlockID();
			int meta = random.nextInt(16);
			if (this.canGenerateAt(world, posX, posY, posZ)) {
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

	public static boolean canGenerateAt(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int idb = world.getBlockId(x, y-1, z);
		int metab = world.getBlockMetadata(x, y-1, z);
		if (id != 0 && !ReikaWorldHelper.softBlocks(world, x, y, z))
			return false;
		if (Block.blocksList[id] instanceof BlockFluid)
			return false;
		if (!canGenerateOn(idb, metab))
			return false;
		return ReikaWorldHelper.checkForAdjBlock(world, x, y, z, 0) != null;
	}

	public static boolean canGenerateOn(int id, int meta) {
		if (id == Block.stone.blockID)
			return true;
		if (id == Block.dirt.blockID)
			return true;
		if (id == Block.gravel.blockID)
			return true;
		if (id == Block.planks.blockID) //mineshafts
			return true;
		if (id == Block.bedrock.blockID)
			return true;
		if (id == Block.obsidian.blockID)
			return true;
		if (id == Block.stoneBrick.blockID) //strongholds
			return true;
		if (id == Block.silverfish.blockID)
			return true;
		if (id == Block.cobblestone.blockID)
			return true;
		if (id == Block.cobblestoneMossy.blockID)
			return true;
		if (id == Block.netherrack.blockID)
			return true;
		if (id == GeoBlocks.SMOOTH.getBlockID() || id == GeoBlocks.SMOOTH2.getBlockID())
			return true;
		if (ReikaBlockHelper.isOre(id, meta))
			return true;
		return false;
	}

	public static float getDensityFactor(World world, int x, int z) {
		if (world.provider.terrainType == WorldType.FLAT) //do not generate in superflat
			return 0;
		if (world.provider.dimensionId == 1)
			return 0;
		if (world.provider.isHellWorld)
			return GeoOptions.NETHER.getState() ? 0.25F : 0;
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		if (world.provider.dimensionId == ReikaTwilightHelper.getDimensionID())
			return 2F;
		if (biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore)
			return 1.5F;
		if (biome == BiomeGenBase.ocean || biome == BiomeGenBase.frozenOcean)
			return 1.25F;
		if (biome == BiomeGenBase.extremeHills || biome == BiomeGenBase.extremeHillsEdge)
			return 1.125F;
		return 1F;
	}

}
