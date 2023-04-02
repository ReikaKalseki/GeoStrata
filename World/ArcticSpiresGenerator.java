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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Blocks.BlockDecoGen.Types;
import Reika.GeoStrata.Registry.GeoBlocks;

public class ArcticSpiresGenerator implements RetroactiveGenerator {

	public static final ArcticSpiresGenerator instance = new ArcticSpiresGenerator();

	private static final int BASE_CHANCE = 32;

	private SimplexNoiseGenerator mainNoise;
	private long seed;

	private final HashMap<Coordinate, Integer> columns = new HashMap();

	private ArcticSpiresGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT && Math.abs(world.provider.dimensionId) != 1 && random.nextInt(9) == 0) {
			chunkX *= 16;
			chunkZ *= 16;
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			if (biome == BiomeGenBase.icePlains) {
				this.setSeed(world);
				int y = world.getTopSolidOrLiquidBlock(x, z);
				if (this.generate(world, x, y, z, random)) {
					for (Entry<Coordinate, Integer> e : columns.entrySet()) {
						Coordinate c = e.getKey();
						//int dy = e.getValue();
						//world.setBlock(c.xCoord, dy+1, c.zCoord, Blocks.snow_layer);
						for (int i = 55; i <= y+7; i++) {
							Block at = world.getBlock(c.xCoord, i, c.zCoord);
							if (at == Blocks.grass || at == Blocks.sand || at == Blocks.dirt) {
								if (world.getBlock(c.xCoord, i+1, c.zCoord).isAir(world, c.xCoord, i+1, c.zCoord)) {
									world.setBlock(c.xCoord, i+1, c.zCoord, Blocks.snow_layer);
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	private void setSeed(World world) {
		long s = world.getSeed();
		if (seed != s || mainNoise == null) {
			seed = s;
			Random rand = new Random(seed);
			rand.nextBoolean();
			mainNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed).setFrequency(0.003);
		}
	}

	private boolean generate(World world, int x, int y0, int z, Random rand) {
		columns.clear();
		int minY = 999;
		int maxY = 0;

		canGen noise

		double r0 = ReikaRandomHelper.getRandomBetween(6D, 10D, rand);
		int r = MathHelper.ceiling_double_int(r0)+4;

		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				int y = world.getTopSolidOrLiquidBlock(dx, dz)-1;
				while (y > 0 && (ReikaWorldHelper.softBlocks(world, dx, y, dz) || world.getBlock(dx, y, dz).isWood(world, dx, y, dz) || world.getBlock(dx, y, dz).isLeaves(world, dx, y, dz)))
					y--;
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
				double d = ReikaMathLibrary.py3d(i, 0, k);
				if (d <= r0) {
					if (!this.isValidGroundBlock(world, dx, y, dz))
						return false;
					for (int h = 1; h <= 6; h++) {
						int dy = y+h;
						if (!this.isValidAirBlock(world, dx, dy, dz))
							return false;
					}
				}
			}
		}
		if (maxY-minY > 4)
			return false;
		int y = maxY;

		r = MathHelper.ceiling_double_int(r0);
		double tilt = mainNoise.getValue(x, z)*180;
		double dT = ReikaRandomHelper.getRandomBetween(0.2, 0.5, rand);
		double tiltX = dT*Math.cos(Math.toRadians(tilt));
		double tiltZ = dT*Math.sin(Math.toRadians(tilt));
		double rLip = ReikaRandomHelper.getRandomBetween(r0+3, r0+5, rand);
		double r1 = Math.min(rLip-3, ReikaRandomHelper.getRandomBetween(8D, 12D, rand));
		int h1 = ReikaRandomHelper.getRandomBetween(2, 4, rand);
		int h2 = ReikaRandomHelper.getRandomBetween(4, 6, rand);
		double h3 = ReikaRandomHelper.getRandomBetween(18D, 32D, rand);
		int dy = y;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				double d = ReikaMathLibrary.py3d(i, 0, k);
				if (d <= r0) {
					for (int j = minY-y; j < h1; j++) {
						this.setBlock(world, x+i, dy+j, z+k, Blocks.packed_ice, d <= r0-1.5 ? 2 : 1);
					}
				}
			}
		}
		dy += h1;
		r = MathHelper.ceiling_double_int(rLip);
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				double d = ReikaMathLibrary.py3d(i, 0, k);
				for (int j = 0; j < h2; j++) {
					double rL = ReikaMathLibrary.linterpolate(j, 0, h2-1, rLip, r1);
					if (d <= rL) {
						this.setBlock(world, x+i, dy+j, z+k, Blocks.packed_ice, d <= rL-1.5 ? 2 : 1);
						if (j == 0 && d >= rL-0.5 && rand.nextInt(2) == 0) {
							if (world.getBlock(x+i, dy-1, z+k).isAir(world, x+i, dy-1, z+k))
								this.setBlock(world, x+i, dy-1, z+k, GeoBlocks.DECOGEN.getBlockInstance(), Types.ICICLE.ordinal());
						}
					}
				}
			}
		}
		dy += h2;
		r = MathHelper.ceiling_double_int(r1);
		for (int j = 0; j < h3; j++) {
			boolean ring = Math.abs(dy+j-84) < 2; noise for height
			double rL = ReikaMathLibrary.linterpolate(j, 0, h3-1, r1, 0.2);
					if (ring)
						rL -= 0.75;
					for (int i = -r; i <= r; i++) {
						for (int k = -r; k <= r; k++) {
							double d = ReikaMathLibrary.py3d(i, 0, k);
							if (d <= rL) {
								this.setBlock(world, x+i+MathHelper.floor_double(tiltX*j), dy+j, z+k+MathHelper.floor_double(tiltZ*j), Blocks.packed_ice, d <= rL-1.5 || ring ? 2 : 1);
							}
						}
					}
		}
		return true;
	}

	private void setBlock(World world, int x, int y, int z, Block b, int m) {
		Block at = world.getBlock(x, y, z);
		if (at.isReplaceableOreGen(world, x, y, z, Blocks.stone))
			return;
		world.setBlock(x, y, z, b, m, 2);
		Coordinate c = new Coordinate(x, 0, z);
		Integer has = columns.get(c); cache grass
		columns.put(c, has == null ? y : Math.max(y, has.intValue()));
	}

	private boolean isValidGroundBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return b == Blocks.grass || b == Blocks.dirt || b == Blocks.sand || b == Blocks.stone || b == Blocks.gravel || b.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	private boolean isValidAirBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return ReikaWorldHelper.softBlocks(world, x, y, z) || b.getMaterial() == Material.plants || b.isWood(world, x, y, z) || b.isLeaves(world, x, y, z);
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata Arctic Spires";
	}

}
