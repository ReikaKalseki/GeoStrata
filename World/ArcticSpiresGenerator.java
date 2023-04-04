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

import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.LobulatedCurve;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.API.ArcticSpireGenerationEvent;
import Reika.GeoStrata.Blocks.BlockDecoGen.Types;
import Reika.GeoStrata.Blocks.BlockOreVein.VeinType;
import Reika.GeoStrata.Registry.GeoBlocks;

public class ArcticSpiresGenerator implements RetroactiveGenerator {

	public static final ArcticSpiresGenerator instance = new ArcticSpiresGenerator();

	private long seed;
	private SimplexNoiseGenerator mainNoise;
	private VoronoiNoiseGenerator zoneNoise;
	private SimplexNoiseGenerator iceLayerNoiseLarge;
	private SimplexNoiseGenerator iceLayerNoiseSharp;


	private DecimalPosition currentClosestZone;

	private ArcticSpiresGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT && Math.abs(world.provider.dimensionId) != 1) {
			chunkX *= 16;
			chunkZ *= 16;
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			if (biome == BiomeGenBase.icePlains || biome == BiomeGenBase.iceMountains) {
				this.setSeed(world);
				if (this.isGennableZone(x, z)) {
					this.generateCluster(world, x, z, random, 2); //was 18
				}

				/*
				DecimalPosition pos = zoneNoise.getClosestRoot(x, 64, z);
				int val = pos.hashCode();
				int m = ((val%16)+16)%16;
				ReikaJavaLibrary.pConsole(x+","+z+">"+pos+">"+val+">"+m);
				for (int dx = chunkX; dx < chunkX+16; dx++) {
					for (int dz = chunkZ; dz < chunkZ+16; dz++) {
						int y = world.getTopSolidOrLiquidBlock(dx, dz)-1;
						while (y > 0 && (ReikaWorldHelper.softBlocks(world, dx, y, dz) || world.getBlock(dx, y, dz).isWood(world, dx, y, dz) || world.getBlock(dx, y, dz).isLeaves(world, dx, y, dz)))
							y--;
						world.setBlock(dx, y, dz, Blocks.wool, m, 2);/*
						world.setBlock(dx, 91, dz, Blocks.standing_sign);
						TileEntitySign te = (TileEntitySign)world.getTileEntity(dx, 91, dz);
						te.signText[0] = String.format("%.1f", pos.getDistanceTo(x, pos.yCoord, z));*//*
			}
		}*/
			}
		}
	}

	private boolean isGennableZone(int x, int z) {
		//return mainNoise.getValue(x, z) >= 0.72;
		currentClosestZone = zoneNoise.getClosestRoot(x, 64, z);
		return currentClosestZone != null && currentClosestZone.getDistanceTo(x, currentClosestZone.yCoord, z) <= 120;
	}

	private void setSeed(World world) {
		long s = world.getSeed();
		if (seed != s || mainNoise == null) {
			seed = s;
			Random rand = new Random(seed);
			rand.nextBoolean();
			mainNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed).setFrequency(0.003);
			zoneNoise = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(seed).setFrequency(0.0024D);
			iceLayerNoiseLarge = (SimplexNoiseGenerator)new SimplexNoiseGenerator(-seed).setFrequency(0.01);
			iceLayerNoiseSharp = (SimplexNoiseGenerator)new SimplexNoiseGenerator(~seed).setFrequency(0.15);
			zoneNoise.randomFactor = 0.45;
		}
	}

	private int generateCluster(World world, int x0, int z0, Random random, int amt) {
		int count = 0;
		double baseTilt = /*random.nextDouble()*360;*/(currentClosestZone.hashCode()*2387.183)%360D;
		HashSet<Coordinate> genned = new HashSet();
		for (int i = 0; i < amt; i++) {
			int x = ReikaRandomHelper.getRandomPlusMinus(x0, 64, random);
			int z = ReikaRandomHelper.getRandomPlusMinus(z0, 64, random);
			boolean flag = true;
			while (flag) {
				flag = false;
				for (Coordinate c : genned) {
					if (c.getTaxicabDistanceTo(x, c.yCoord, z) < 24) {
						flag = true;
						x = ReikaRandomHelper.getRandomPlusMinus(x0, 64, random);
						z = ReikaRandomHelper.getRandomPlusMinus(z0, 64, random);
						break;
					}
				}
			}
			int y = world.getTopSolidOrLiquidBlock(x, z);
			double tilt = ReikaRandomHelper.getRandomPlusMinus(baseTilt, 12, random);
			ArcticSpire sp = new ArcticSpire();
			if (sp.generate(world, x, y, z, tilt, random)) {
				genned.add(new Coordinate(x, y, z));
				count++;
				HashSet<Coordinate> snowCover = new HashSet();
				if (VeinType.ICE.isEnabled()) {
					ArrayList<Coordinate> veinAttempts = new ArrayList(sp.core);
					int veins = 0;
					int max = ReikaRandomHelper.getRandomBetween(3, 9, random);
					while (veins < max && !veinAttempts.isEmpty()) {
						Coordinate c = ReikaJavaLibrary.getAndRemoveRandomCollectionEntry(random, veinAttempts);
						if (c.yCoord == 0 || c.yCoord >= sp.lipYBottom || c.yCoord >= Math.max(sp.lipYBottom-2, y+3))
							continue;
						boolean flag2 = false;
						int air = 0;
						int self = 0;
						for (Coordinate c2 : c.getAdjacentCoordinates()) {
							if (!sp.core.contains(c2.to2D()))
								flag2 = true;
							if (c2.isEmpty(world)) {
								air++;
							}
							else if (c2.getBlock(world) == GeoBlocks.OREVEIN.getBlockInstance()) {
								flag2 = false;
								break;
							}
						}
						if (flag2 && air == 1) {
							c.setBlock(world, GeoBlocks.OREVEIN.getBlockInstance(), VeinType.ICE.ordinal(), 2);
							veins++;
						}
					}
				}
				for (Entry<Coordinate, Integer> e : sp.columns.entrySet()) {
					Coordinate c = e.getKey();
					int ty = e.getValue();
					if (ty <= 82) {
						world.setBlock(c.xCoord, ty, c.zCoord, Blocks.packed_ice, 1, 2);
					}
					if (sp.core.contains(c.to2D())) {
						for (int dy = 50; dy < sp.lipYBottom; dy++) {
							Block at = world.getBlock(c.xCoord, dy, c.zCoord);
							if (at == Blocks.grass || at == Blocks.sand || at == Blocks.dirt) {
								world.setBlock(c.xCoord, dy, c.zCoord, Blocks.packed_ice, 1, 2);
							}
						}
					}
					else {
						for (int dy = ty-1; dy >= 60; dy--) {
							Block at = world.getBlock(c.xCoord, dy, c.zCoord);
							if (at.isWood(world, c.xCoord, dy, c.zCoord) || at.isLeaves(world, c.xCoord, dy, c.zCoord)) {
								world.setBlock(c.xCoord, dy, c.zCoord, Blocks.air);
							}
							else if (at == Blocks.grass || at == Blocks.sand || at == Blocks.dirt) {
								Block ab = world.getBlock(c.xCoord, dy+1, c.zCoord);
								if (ab == Blocks.snow_layer || ab.isAir(world, c.xCoord, dy+1, c.zCoord)) {
									if (ab != Blocks.snow_layer)
										world.setBlock(c.xCoord, dy+1, c.zCoord, Blocks.snow_layer);
									snowCover.add(c.setY(dy+1));
								}
							}
							if (at != GeoBlocks.DECOGEN.getBlockInstance() && at != Blocks.packed_ice && !ReikaWorldHelper.softBlocks(world, c.xCoord, dy, c.zCoord))
								break;
						}
					}
				}
				MinecraftForge.EVENT_BUS.post(new ArcticSpireGenerationEvent(world, x, y, z, random, Collections.unmodifiableSet(sp.core), Collections.unmodifiableSet(snowCover)));
			}
		}
		return count;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata Arctic Spires";
	}

	public void clear() {
		seed = -1;
		mainNoise = null;
		zoneNoise = null;
		iceLayerNoiseSharp = null;
		iceLayerNoiseLarge = null;
	}

	private class ArcticSpire {

		private final HashMap<Coordinate, Integer> columns = new HashMap();
		private final HashSet<Coordinate> core = new HashSet();

		private int lipYBottom;

		private boolean isValidGroundBlock(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			return b == Blocks.grass || b == Blocks.dirt || b == Blocks.sand || b == Blocks.stone || b == Blocks.gravel || b.isReplaceableOreGen(world, x, y, z, Blocks.stone);
		}

		private boolean isValidAirBlock(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			return ReikaWorldHelper.softBlocks(world, x, y, z) || b.getMaterial() == Material.plants || b.isWood(world, x, y, z) || b.isLeaves(world, x, y, z);
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

		private double getIceLayerHeight(int x, int z) {
			double base = iceLayerNoiseLarge.getValue(x, z);
			double noise = iceLayerNoiseSharp.getValue(x, z);
			double off = Math.abs(noise) <= 0.25 ? 0 : Math.signum(noise);
			return 88+2.5*base+2*off;
		}

		private boolean generate(World world, int x, int y0, int z, double tilt, Random rand) {
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
			double dT = ReikaRandomHelper.getRandomBetween(0.35, 0.7, rand);
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
						int dx = x+i;
						int dz = z+k;
						core.add(new Coordinate(dx, 0, dz));
						for (int j = minY-y; j < h1; j++) {
							core.add(new Coordinate(dx, dy+j, dz));
							this.setBlock(world, dx, dy+j, dz, Blocks.packed_ice, d <= r0-(j == minY-y ? 3 : 1.75) ? 2 : 1);
						}
					}
				}
			}
			dy += h1;
			lipYBottom = dy;
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
							this.setBlock(world, dx, dy+j, dz, Blocks.packed_ice, d <= rL-1.75 ? 2 : 1);
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
						if (rL < 1.25)
							continue;
						if (ring)
							rL -= 0.875;
						if (d <= rL ) {
							this.setBlock(world, dx+MathHelper.floor_double(tiltX*j), dy+j, dz+MathHelper.floor_double(tiltZ*j), Blocks.packed_ice, d <= rL-1.75 || ring ? 2 : 1);
						}
					}
				}
			}
			return true;
		}


	}

}
