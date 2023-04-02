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
import java.util.HashSet;
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
import Reika.DragonAPI.Instantiable.Math.LobulatedCurve;
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

	private long seed;
	private SimplexNoiseGenerator mainNoise;
	private SimplexNoiseGenerator iceLayerNoiseLarge;
	private SimplexNoiseGenerator iceLayerNoiseSharp;

	private final HashMap<Coordinate, Integer> columns = new HashMap();
	private final HashSet<Coordinate> lipRing = new HashSet();

	private ArcticSpiresGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT && Math.abs(world.provider.dimensionId) != 1) {
			chunkX *= 16;
			chunkZ *= 16;
			int x = chunkX + 8;
			int z = chunkZ + 8;
			BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			if (biome == BiomeGenBase.icePlains) {
				this.setSeed(world);
				if (mainNoise.getValue(x, z) >= 0.72) {
					this.generateCluster(world, x, z, random);
				}
			}
		}
	}

	private void setSeed(World world) {
		long s = world.getSeed();
		mainNoise = null;
		if (seed != s || mainNoise == null) {
			seed = s;
			Random rand = new Random(seed);
			rand.nextBoolean();
			mainNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed).setFrequency(0.003);
			iceLayerNoiseLarge = (SimplexNoiseGenerator)new SimplexNoiseGenerator(-seed).setFrequency(0.015);
			iceLayerNoiseSharp = (SimplexNoiseGenerator)new SimplexNoiseGenerator(~seed).setFrequency(0.2);
		}
	}

	private int generateCluster(World world, int x0, int z0, Random random) {
		int count = 0;
		double baseTilt = random.nextDouble()*360;
		for (int i = 0; i < 18; i++) {
			int x = ReikaRandomHelper.getRandomPlusMinus(x0, 64, random);
			int z = ReikaRandomHelper.getRandomPlusMinus(z0, 64, random);
			int y = world.getTopSolidOrLiquidBlock(x, z);
			double tilt = ReikaRandomHelper.getRandomPlusMinus(baseTilt, 10, random);
			if (this.generate(world, x, y, z, tilt, random)) {
				count++;
				for (Entry<Coordinate, Integer> e : columns.entrySet()) {
					int dy = e.getValue();
					if (dy < 80) {
						Coordinate c = e.getKey();
						world.setBlock(c.xCoord, dy, c.zCoord, Blocks.packed_ice, 1, 2);
					}
				}
				for (Coordinate c : lipRing) {
					for (int dy = c.yCoord; dy >= c.yCoord-7; dy--) {
						Block at = world.getBlock(c.xCoord, dy, c.zCoord);
						if (at.isWood(world, c.xCoord, dy, c.zCoord) || at.isLeaves(world, c.xCoord, dy, c.zCoord)) {
							world.setBlock(c.xCoord, dy, c.zCoord, Blocks.air);
						}
						else if (at == Blocks.grass || at == Blocks.sand || at == Blocks.dirt) {
							if (world.getBlock(c.xCoord, dy+1, c.zCoord).isAir(world, c.xCoord, dy+1, c.zCoord)) {
								world.setBlock(c.xCoord, dy+1, c.zCoord, Blocks.snow_layer);
							}
						}
						if (at != GeoBlocks.DECOGEN.getBlockInstance() && ReikaWorldHelper.softBlocks(world, c.xCoord, dy, c.zCoord))
							break;
					}
				}
			}
		}
		return count;
	}

	private boolean generate(World world, int x, int y0, int z, double tilt, Random rand) {
		columns.clear();
		lipRing.clear();
		int minY = 999;
		int maxY = 0;

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
		double dT = ReikaRandomHelper.getRandomBetween(0.2, 0.5, rand);
		double tiltX = dT*Math.cos(Math.toRadians(tilt));
		double tiltZ = dT*Math.sin(Math.toRadians(tilt));
		double rLip = ReikaRandomHelper.getRandomBetween(r0+4, r0+6, rand);
		double r1 = Math.min(Math.max(r0, rLip-4.5), ReikaRandomHelper.getRandomBetween(8D, 12D, rand));
		int h1 = ReikaRandomHelper.getRandomBetween(2, 4, rand);
		int h2 = ReikaRandomHelper.getRandomBetween(4, 6, rand);
		double h3 = ReikaRandomHelper.getRandomBetween(30D, 48D, rand);
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
		LobulatedCurve lb = LobulatedCurve.fromMinMaxRadii(rLip-3, rLip+2, 4, true);
		lb.generate(rand);
		LobulatedCurve slopeFactor = LobulatedCurve.fromMinMaxRadii(0.7, 1.25, 3, true);
		slopeFactor.generate(rand);
		r = MathHelper.ceiling_double_int(rLip)+2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				double d = ReikaMathLibrary.py3d(i, 0, k);
				double ang = Math.toDegrees(Math.atan2(k, i));
				double rAt = lb.getRadius(ang);
				double slopeAt = slopeFactor.getRadius(ang);
				for (int j = 0; j < h2; j++) {
					double rL = ReikaMathLibrary.linterpolate(j*slopeAt, 0, h2-1, rAt, r1);
					if (d <= rL) {
						int dx = x+i;
						int dz = z+k;
						this.setBlock(world, dx, dy+j, dz, Blocks.packed_ice, d <= rL-1.5 ? 2 : 1);
						if (j == 0 && d > r0)
							lipRing.add(new Coordinate(dx, dy, dz));
						if (j == 0 && d >= rL-0.5 && rand.nextInt(2) == 0) {
							if (world.getBlock(dx, dy-1, dz).isAir(world, dx, dy-1, dz))
								this.setBlock(world, dx, dy-1, dz, GeoBlocks.DECOGEN.getBlockInstance(), Types.ICICLE.ordinal());
						}
					}
				}
			}
		}
		dy += h2;
		r = MathHelper.ceiling_double_int(r1);
		for (int j = 0; j < h3; j++) {
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					double d = ReikaMathLibrary.py3d(i, 0, k);
					int dx = x+i;
					int dz = z+k;
					boolean ring = Math.abs(dy+j-this.getIceLayerHeight(dx, dz)) < 2;
					double rL = ReikaMathLibrary.linterpolate(j, 0, h3-1, r1, 0.2);
					if (ring)
						rL -= 0.875;
					if (d <= rL && rL >= 1) {
						this.setBlock(world, dx+MathHelper.floor_double(tiltX*j), dy+j, dz+MathHelper.floor_double(tiltZ*j), Blocks.packed_ice, d <= rL-1.5 || ring ? 2 : 1);
					}
				}
			}
		}
		return true;
	}

	private double getIceLayerHeight(int x, int z) {
		double base = iceLayerNoiseLarge.getValue(x, z);
		double noise = iceLayerNoiseSharp.getValue(x, z);
		double off = Math.abs(noise) <= 0.25 ? 0 : Math.signum(noise);
		return 88+2.5*base+2*off;
	}

	private void setBlock(World world, int x, int y, int z, Block b, int m) {
		Block at = world.getBlock(x, y, z);
		if (at.isReplaceableOreGen(world, x, y, z, Blocks.stone))
			return;
		world.setBlock(x, y, z, b, m, 2);

		Coordinate c = new Coordinate(x, 0, z);
		Integer has = columns.get(c);
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
