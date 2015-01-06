/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class WorldGenMinableOreAbsorber extends WorldGenerator {

	private final int size;
	private final RockTypes rock;
	private final Block overwrite;
	private final Block id;

	public WorldGenMinableOreAbsorber(RockTypes r, int size) {
		this.size = size;
		overwrite = Blocks.stone;
		rock = r;
		id = rock.getID(RockShapes.SMOOTH);
	}

	@Override
	public boolean generate(World world, Random r, int x, int y, int z) {
		int count = 0;
		float f = r.nextFloat() * (float)Math.PI;
		double d0 = x + 8 + MathHelper.sin(f) * size / 8.0F;
		double d1 = x + 8 - MathHelper.sin(f) * size / 8.0F;
		double d2 = z + 8 + MathHelper.cos(f) * size / 8.0F;
		double d3 = z + 8 - MathHelper.cos(f) * size / 8.0F;
		double d4 = y + r.nextInt(3) - 2;
		double d5 = y + r.nextInt(3) - 2;

		for (int l = 0; l <= size; ++l)
		{
			double d6 = d0 + (d1 - d0) * l / size;
			double d7 = d4 + (d5 - d4) * l / size;
			double d8 = d2 + (d3 - d2) * l / size;
			double d9 = r.nextDouble() * size / 16.0D;
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
									Block b = world.getBlock(dx, dy, dz);
									int meta = world.getBlockMetadata(dx, dy, dz);
									if (b.isReplaceableOreGen(world, dx, dy, dz, overwrite)) {
										world.setBlock(dx, dy, dz, id, 0, 2);
										count++;
									}
									else if (RockGenerator.instance.generateOres() && ReikaBlockHelper.isOre(b, meta)) {
										TileEntityGeoOre te = new TileEntityGeoOre();
										te.initialize(rock, b, meta);
										world.setBlock(dx, dy, dz, GeoBlocks.ORETILE.getBlockInstance());
										world.setTileEntity(dx, dy, dz, te);
									}
								}
							}
						}
					}
				}
			}
		}

		return count > 0;
	}

}
