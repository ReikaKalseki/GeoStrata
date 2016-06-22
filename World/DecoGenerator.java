/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.World;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.GeoStrata.Registry.GeoBlocks;

public class DecoGenerator implements RetroactiveGenerator {

	public static final DecoGenerator instance = new DecoGenerator();

	private DecoGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		chunkX *= 16;
		chunkZ *= 16;

		if (this.generateIn(world)) {
			for (int i = 0; i < Decorations.list.length; i++) {
				Decorations p = Decorations.list[i];
				if (random.nextInt(p.getGenerationChance()) == 0) {
					int x = chunkX+random.nextInt(16)+8;
					int z = chunkZ+random.nextInt(16)+8;
					int y = world.getTopSolidOrLiquidBlock(x, z);
					if (p.isValidLocation(world, x, y, z)) {
						if (p.generate(world, x, y, z, random))
							break;
					}
				}
			}
		}
	}

	private boolean generateIn(World world) {
		/*
		if (Math.abs(world.provider.dimensionId) <= 1)
			return true;
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			return true;
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			if (!MystPages.Pages.PLANTS.existsInWorld(world)) {
				return false;
			}
		}*/
		return true;
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "Decorations";
	}

	public static enum Decorations {
		OCEANSPIKE(20),
		OCEANSPIKES(80);

		public final int chancePerChunk;

		public static final Decorations[] list = values();

		private Decorations(int c) {
			chancePerChunk = c;
		}

		public int getGenerationChance() {
			return chancePerChunk;
		}

		private boolean isValidLocation(World world, int x, int y, int z) {
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			switch(this) {
				case OCEANSPIKE:
				case OCEANSPIKES:
					return ReikaBiomeHelper.isOcean(b);
				default:
					return true;
			}
		}

		private boolean generate(World world, int x, int y, int z, Random rand) {
			switch(this) {
				case OCEANSPIKE:
					int h = 0;
					int d = 1+rand.nextInt(4);
					while (h < 12 && world.getBlock(x, y+h+1, z) == Blocks.water) {
						world.setBlock(x, y+h, z, GeoBlocks.DECOGEN.getBlockInstance(), 0, 3);
						h++;
					}
					return true;
				case OCEANSPIKES:
					int n = 4+rand.nextInt(9);
					for (int i = 0; i < n; i++) {
						int dx = x+rand.nextInt(17)-8;
						int dz = z+rand.nextInt(17)-8;
						int dy = world.getTopSolidOrLiquidBlock(dx, dz);
						OCEANSPIKE.generate(world, dx, dy, dz, rand);
					}
					return true;
			}
			return false;
		}
	}

}
