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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.DragonAPI.Interfaces.Subgenerator;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.API.RockGenerationPatterns.RockGenerationPattern;
import Reika.GeoStrata.API.RockProofStone;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class WorldGenGeoRock extends WorldGenerator implements Subgenerator {

	private final int size;
	private final RockTypes rock;
	private final Block overwrite;
	private final Block id;
	private final RockGenerationPattern generator;

	public WorldGenGeoRock(RockGenerationPattern p, RockTypes r, int size) {
		this.size = size;
		overwrite = Blocks.stone;
		rock = r;
		id = rock.getID(RockShapes.SMOOTH);
		generator = p;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int count = 0;
		float f = rand.nextFloat() * (float)Math.PI;
		double d0 = x + 8 + MathHelper.sin(f) * size / 8.0F;
		double d1 = x + 8 - MathHelper.sin(f) * size / 8.0F;
		double d2 = z + 8 + MathHelper.cos(f) * size / 8.0F;
		double d3 = z + 8 - MathHelper.cos(f) * size / 8.0F;
		double d4 = y + rand.nextInt(3) - 2;
		double d5 = y + rand.nextInt(3) - 2;

		for (int l = 0; l <= size; ++l)
		{
			double d6 = d0 + (d1 - d0) * l / size;
			double d7 = d4 + (d5 - d4) * l / size;
			double d8 = d2 + (d3 - d2) * l / size;
			double d9 = rand.nextDouble() * size / 16.0D;
			double d10 = (MathHelper.sin(l * (float)Math.PI / size) + 1.0F) * d9 + 1.0D;
			double d11 = (MathHelper.sin(l * (float)Math.PI / size) + 1.0F) * d9 + 1.0D;
			int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
			int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
			int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
			int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
			int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
			int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

			for (int dx = i1; dx <= l1; dx++) {
				double d12 = (dx + 0.5D - d6) / (d10 / 2.0D);
				if (d12 * d12 < 1.0D) {
					for (int dy = j1; dy <= i2; dy++) {
						double d13 = (dy + 0.5D - d7) / (d11 / 2.0D);
						if (d12 * d12 + d13 * d13 < 1.0D) {
							for (int dz = k1; dz <= j2; dz++) {
								double d14 = (dz + 0.5D - d8) / (d10 / 2.0D);
								if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
									//if (this.isInChunk(x, z, dx, dz)) {
									Block b = world.getBlock(dx, dy, dz);
									int meta = world.getBlockMetadata(dx, dy, dz);
									if (this.canGenerateIn(world, dx, dy, dz, b, meta)) {
										//long time = System.nanoTime();

										world.setBlock(dx, dy, dz, id, 0, 2);
										count++;

										//long time2 = System.nanoTime();
										//long dur = time2-time;
										//ReikaJavaLibrary.pConsole(rock+" block set time for "+dx+", "+dy+", "+dz+": "+dur+" nanos: "+this.isInChunk(x, z, dx, dz));
									}
									else if (RockGenerator.instance.generateOres() && ReikaBlockHelper.isOre(b, meta)) {
										TileEntityGeoOre te = new TileEntityGeoOre();
										te.initialize(rock, b, meta);
										world.setBlock(dx, dy, dz, GeoBlocks.ORETILE.getBlockInstance());
										world.setTileEntity(dx, dy, dz, te);
									}
									//}
								}
							}
						}
					}
				}
			}
		}

		return count > 0;
	}

	/*
	private boolean isInChunk(int x, int z, int dx, int dz) {
		int cx = x >> 4;
							int cz = z >> 4;
					int dcx = dx >> 4;
			int dcz = dz >> 4;
			return cx == dcx && cz == dcz;
	}*/

	private boolean canGenerateIn(World world, int x, int y, int z, Block b, int meta) {
		if (b instanceof RockProofStone && ((RockProofStone)b).blockRockGeneration(world, x, y, z, b, meta))
			return false;
		if (!GeoOptions.OVERGEN.getState() && b instanceof RockBlock)
			return false;
		return b.isReplaceableOreGen(world, x, y, z, overwrite);
	}

	@Override
	public Object getParentGenerator() {
		return generator;
	}

}
