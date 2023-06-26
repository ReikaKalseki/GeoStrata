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

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Blocks.BlockOreVein.VeinType;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;

public class DecoGenerator implements RetroactiveGenerator {

	public static final DecoGenerator instance = new DecoGenerator();

	private static final int BASE_CHANCE = (int)(1/GeoOptions.getDecoDensity());

	private DecoGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		chunkX *= 16;
		chunkZ *= 16;

		if (this.generateIn(world)) {
			for (int i = 0; i < Decorations.list.length; i++) {
				Decorations p = Decorations.list[i];
				if (random.nextInt(Math.max(1, (int)(p.getGenerationChance()/GeoOptions.getDecoDensity()))) == 0) {
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
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata Decorations";
	}

	public static enum Decorations {
		OCEANSPIKE(20),
		OCEANSPIKES(80),
		OREVEINS(1),
		;

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
					return ReikaBiomeHelper.isOcean(b) && ReikaWorldHelper.getDepthFromBelow(world, x, y-1, z, FluidRegistry.WATER) > 2;
				default:
					return true;
			}
		}

		private boolean generate(World world, int x, int y, int z, Random rand) {
			switch(this) {
				case OCEANSPIKE:
					int h = 0;
					int d = rand.nextInt(8);
					int min = ReikaRandomHelper.getRandomBetween(4, 7, rand);
					while (h < 15 && (world.getBlock(x, y+h+d, z) == Blocks.water || (h < min && world.getBlock(x, y+h+1, z) == Blocks.water))) {
						world.setBlock(x, y+h, z, GeoBlocks.DECOGEN.getBlockInstance(), 0, 3);
						h++;
					}
					return true;
				case OCEANSPIKES:
					int n = 4+rand.nextInt(9);
					for (int i = 0; i < n; i++) {
						int dx = ReikaRandomHelper.getRandomPlusMinus(x, 8, rand);
						int dz = ReikaRandomHelper.getRandomPlusMinus(z, 8, rand);
						int dy = world.getTopSolidOrLiquidBlock(dx, dz);
						OCEANSPIKE.generate(world, dx, dy, dz, rand);
					}
					return true;
				case OREVEINS:
					int amt = 16;
					int minY = 4;
					int maxY = 56;
					VeinType vein = VeinType.STONE;
					if (world.provider.dimensionId == -1) {
						vein = VeinType.NETHER;
						amt = 4;
						maxY = 126;
					}
					else if (world.provider.dimensionId == 1) {
						vein = VeinType.END;
						amt = 3;
						minY = 8;
						maxY = 64;
					}
					if (!vein.isEnabled())
						return false;
					if (vein == VeinType.END && ModList.CHROMATICRAFT.isLoaded())
						amt = ReikaMathLibrary.py3d(x, 0, z) <= 850 ? 0 : 6;
					for (int i = 0; i < amt; i++) {
						int dy = ReikaRandomHelper.getRandomBetween(minY, maxY, rand);
						int dx = ReikaRandomHelper.getRandomPlusMinus(x, 8, rand);
						int dz = ReikaRandomHelper.getRandomPlusMinus(z, 8, rand);
						if (world.getBlock(dx, dy, dz) == vein.template) { //exact block since texture match
							int adj = ReikaWorldHelper.countAdjacentBlocks(world, dx, dy, dz, Blocks.air, false);
							if (adj > 0 && adj < 3)
								world.setBlock(dx, dy, dz, GeoBlocks.OREVEIN.getBlockInstance(), vein.ordinal(), 3);
						}
					}
					return true;
			}
			return false;
		}
	}

}
