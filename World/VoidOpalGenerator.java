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

import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.GeoStrata.Registry.GeoBlocks;

public class VoidOpalGenerator implements RetroactiveGenerator {

	public static final VoidOpalGenerator instance = new VoidOpalGenerator();

	private VoidOpalGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateAt(world, chunkX, chunkZ) && random.nextInt(3) == 0) {
			chunkX *= 16;
			chunkZ *= 16;
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int miny = 12;
			int maxy = 72;
			int posY = miny+random.nextInt(maxy-miny+1);
			if (this.tryGenerateAt(world, posX, posY, posZ, random)) {

			}
		}
	}

	public static boolean tryGenerateAt(World world, int x, int y, int z, Random rand) {
		Block ida = world.getBlock(x, y, z);
		if (ida != Blocks.air)
			return false;
		for (int i = 4; i <= 80; i++) {
			ida = world.getBlock(x, i, z);
			if (ida != Blocks.air)
				return false;
		}
		VoidOpalDeposit dep = new VoidOpalDeposit(rand.nextLong());
		dep.calculate(world, x, y, z);
		if (dep.isEmpty(world)) {
			dep.generate(world);
			return true;
		}
		return false;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return world.provider.dimensionId == 1;
	}

	@Override
	public String getIDString() {
		return "Void Opal";
	}

	private static class VoidOpalDeposit {

		private final Random seed;

		private final double encasedThickness;

		private final HashSet<Coordinate> blocks = new HashSet();
		private final HashSet<Coordinate> casing = new HashSet();

		private VoidOpalDeposit(long s) {
			seed = new Random(s);
			seed.nextBoolean();
			seed.nextBoolean();
			encasedThickness = seed.nextDouble()*4-0.5;
		}

		private void calculate(World world, int x, int y, int z) {
			/*
			double rx = 0;
			double ry = 0;
			double rz = 0;
			int rxi = MathHelper.ceiling_double_int(rx);
			int ryi = MathHelper.ceiling_double_int(ry);
			int rzi = MathHelper.ceiling_double_int(rz);
			for (int i = -rxi; i <= rxi; i++) {
				for (int j = -ryi; j <= ryi; j++) {
					for (int k = -rzi; k <= rzi; k++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i+0.5, j+0.5, k+0.5, rx, ry, rz)) {
							int dx = x+i;
							int dy = y+j;
							int dz = z+k;
							blocks.add(new Coordinate(dx, dy, dz));
						}
					}
				}
			}
			 */
			int nspikes = 5+seed.nextInt(6);
			for (int i = 0; i < nspikes; i++) {
				double phi = seed.nextDouble()*360;
				double theta = seed.nextDouble()*360;
				double len = 3+5*seed.nextDouble();
				double r0 = 0.375+seed.nextDouble()*0.5;
				double r1 = r0+0.5+seed.nextDouble();
				double[] xyz = ReikaPhysicsHelper.polarToCartesian(1, theta, phi);
				for (double d = 0; d <= len; d += 0.25) {
					double dx = x+xyz[0]*d;
					double dy = y+xyz[1]*d;
					double dz = z+xyz[2]*d;
					this.generateBallAt(world, dx, dy, dz, ReikaMathLibrary.linterpolate(d, 0, len, r1, r0));
				}
			}
		}

		private void generateBallAt(World world, double x, double y, double z, double r) {
			for (double i = -r; i <= r; i += 0.5) {
				for (double j = -r; j <= r; j += 0.5) {
					for (double k = -r; k <= r; k += 0.5) {
						if (ReikaMathLibrary.py3d(i, j, k) <= r) {
							int dx = MathHelper.floor_double(x+i);
							int dy = MathHelper.floor_double(y+j);
							int dz = MathHelper.floor_double(z+k);
							blocks.add(new Coordinate(dx, dy, dz));
						}
					}
				}
			}
			if (encasedThickness > 0) {
				r += encasedThickness;
				for (double i = -r; i <= r; i += 0.5) {
					for (double j = -r; j <= r; j += 0.5) {
						for (double k = -r; k <= r; k += 0.5) {
							if (ReikaMathLibrary.py3d(i, j, k) <= r) {
								int dx = MathHelper.floor_double(x+i);
								int dy = MathHelper.floor_double(y+j);
								int dz = MathHelper.floor_double(z+k);
								Coordinate cc = new Coordinate(dx, dy, dz);
								if (!blocks.contains(cc))
									casing.add(cc);
							}
						}
					}
				}
			}
		}

		private boolean isEmpty(World world) {
			for (Coordinate c : blocks) {
				if (!c.isEmpty(world))
					return false;
			}
			return true;
		}

		private void generate(World world) {
			for (Coordinate c : casing) {
				c.setBlock(world, Blocks.end_stone, 0, 2);
			}
			for (Coordinate c : blocks) {
				c.setBlock(world, GeoBlocks.VOIDOPAL.getBlockInstance(), 0, 2);
			}
		}

	}

}
