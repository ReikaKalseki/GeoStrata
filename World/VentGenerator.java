/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Blocks.BlockVent.VentType;
import Reika.GeoStrata.Registry.GeoBlocks;
import cpw.mods.fml.common.IWorldGenerator;

public class VentGenerator implements IWorldGenerator {

	public static final int PER_CHUNK = 60; //calls per chunk; vast majority fail

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
			chunkX *= 16;
			chunkZ *= 16;
			for (int i = 0; i < PER_CHUNK; i++) {
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				int maxy = 64;
				int posY = 4+random.nextInt(maxy-4);
				if (this.canGenerateAt(world, posX, posY, posZ)) {
					Block id = GeoBlocks.VENT.getBlockInstance();
					int meta = this.getVentTypeFor(world, posX, posY, posZ, random).ordinal();
					world.setBlock(posX, posY, posZ, id, meta, 3);
				}
			}
		}
	}

	private VentType getVentTypeFor(World world, int posX, int posY, int posZ, Random random) {
		if (posY < 20 && random.nextInt(4) == 0)
			return VentType.LAVA;
		if (posY < 32 && random.nextInt(4) == 0)
			return VentType.FIRE;
		if (posY < 32 && random.nextInt(6) == 0)
			return VentType.GAS;
		if (posY > 24 && random.nextInt(4) == 0)
			return VentType.WATER;
		return random.nextBoolean() ? VentType.STEAM : VentType.SMOKE;
	}

	public static boolean canGenerateAt(World world, int x, int y, int z) {
		Block ida = world.getBlock(x, y+1, z);
		if (ida != Blocks.air && !ReikaWorldHelper.softBlocks(world, x, y+1, z))
			return false;
		return canGenerateIn(world, x, y, z);
	}

	public static boolean canGenerateIn(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == Blocks.air)
			return false;
		if (id == Blocks.stone)
			return true;
		if (id == Blocks.dirt)
			return true;
		if (id == Blocks.gravel)
			return true;
		if (id == Blocks.planks) //mineshafts
			;//return true;
		if (id == Blocks.bedrock)
			;//return true;
		if (id == Blocks.obsidian)
			;//return true;
		if (id == Blocks.stonebrick) //strongholds
			;//return true;
		if (id == Blocks.monster_egg)
			return true;
		if (id == Blocks.cobblestone)
			return true;
		if (id == Blocks.mossy_cobblestone)
			return true;
		return id.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

}
