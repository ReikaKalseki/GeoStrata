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
import java.util.Locale;
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
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;

public class GlowCrystalGenerator implements RetroactiveGenerator {

	public static final GlowCrystalGenerator instance = new GlowCrystalGenerator();

	private static final int BASE_CHANCE = (int)(96/GeoOptions.getCrystalDensity());

	private GlowCrystalGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT && Math.abs(world.provider.dimensionId) != 1 && random.nextInt(BASE_CHANCE) == 0) {
			chunkX *= 16;
			chunkZ *= 16;
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			if (biome.theBiomeDecorator.treesPerChunk > 0 || biome.biomeName.toLowerCase(Locale.ENGLISH).contains("forest")) {
				if (biome.theBiomeDecorator.treesPerChunk <= 4) {
					if (random.nextInt(3) == 0)
						return;
				}
				else if (biome.theBiomeDecorator.treesPerChunk <= 2) {
					if (random.nextInt(2) == 0)
						return;
				}
			}
			else {
				if (random.nextInt(3) > 0)
					return;
			}
			/*
			int maxy = 60;
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (b instanceof BiomeGenHills || b.rootHeight >= 1)
				maxy = 200;
			else if (b instanceof BiomeGenOcean || b.rootHeight < -0.5)
				maxy = 40;
			else if (b instanceof BiomeGenRiver || b.rootHeight < 0)
				maxy = 50;
			int y = 8+random.nextInt(maxy);
			 */
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaBlockHelper.isGroundType(world, x, y-1, z) && world.getBlock(x, y+1, z).isAir(world, x, y+2, z)) { //allow up to 3 blocks of liquid
				if (this.generate(world, x, y, z, random))
					;//ReikaJavaLibrary.pConsole("Genned at "+x+", "+y+", "+z);
			}
		}
	}

	private boolean generate(World world, int x, int y, int z, Random rand) {
		double x1 = x+rand.nextDouble();
		double x2 = x1;
		double z1 = z+rand.nextDouble();
		double z2 = z1;
		double y1 = y-2-rand.nextDouble()*12;
		double y2 = y+2+rand.nextDouble()*12;
		double dx = rand.nextDouble()*16-8;
		double dz = rand.nextDouble()*16-8;
		x1 -= dx;
		x2 += dx;
		z2 -= dz;
		z2 += dz;
		HashSet<Coordinate> li = this.generateLine(world, x, y, z, x1, y1, z1, x2, y2, z2);

		int air = 0;
		int stone = 0;
		int size = li.size();
		if (size == 0)
			return false;
		for (Coordinate c : li) {
			Block bk = c.getBlock(world);
			if (bk.isAir(world, c.xCoord, c.yCoord, c.zCoord)) {
				air++;
			}
			else if (ReikaBlockHelper.isGroundType(world, c.xCoord, c.yCoord, c.zCoord)) {
				stone++;
			}
		}
		if (air*100/size < 25 || stone*100/size < 25) {
			return false;
		}
		int meta = rand.nextInt(4); //random color
		for (Coordinate c : li) {
			Block bk = c.getBlock(world);
			this.setBlock(world, c.xCoord, c.yCoord, c.zCoord, GeoBlocks.GLOWCRYS.getBlockInstance(), meta);
		}

		return true;
	}

	private HashSet<Coordinate> generateLine(World world, double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2, double z2) {
		double lx1 = x1-x0;
		double ly1 = y1-y0;
		double lz1 = z1-z0;
		double lx2 = x2-x0;
		double ly2 = y2-y0;
		double lz2 = z2-z0;
		HashSet<Coordinate> c = new HashSet();
		//double d1 = ReikaMathLibrary.py3d(lx1, ly1, lz1);
		//double d2 = ReikaMathLibrary.py3d(lx2, ly2, lz2);
		for (double d = 0; d <= 1; d += 0.03125) {
			double dx = x0+lx1*d;
			double dy = y0+ly1*d;
			double dz = z0+lz1*d;
			this.genAt(world, dx, dy, dz, 1-d, c);
		}
		for (double d = 0; d <= 1; d += 0.03125) {
			double dx = x0+lx2*d;
			double dy = y0+ly2*d;
			double dz = z0+lz2*d;
			this.genAt(world, dx, dy, dz, 1-d, c);
		}
		return c;
	}

	private void genAt(World world, double dx, double dy, double dz, double d, HashSet<Coordinate> blocks) {
		if (d < 0.125) {
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy), MathHelper.floor_double(dz)));
		}
		else if (d < 0.25) {
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.ceiling_double_int(dx), MathHelper.floor_double(dy), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.ceiling_double_int(dy), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy), MathHelper.ceiling_double_int(dz)));
		}
		else if (d < 0.5) {
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx+1), MathHelper.floor_double(dy), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx-1), MathHelper.floor_double(dy), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy+1), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy-1), MathHelper.floor_double(dz)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy), MathHelper.floor_double(dz+1)));
			blocks.add(new Coordinate(MathHelper.floor_double(dx), MathHelper.floor_double(dy), MathHelper.floor_double(dz-1)));
		}
		else if (d < 0.75) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						blocks.add(new Coordinate(MathHelper.floor_double(dx+i), MathHelper.floor_double(dy+j), MathHelper.floor_double(dz+k)));
					}
				}
			}
		}
		else {
			this.genAt(world, dx, dy, dz, 0.7, blocks);
			for (int i = -2; i <= 2; i++) {
				blocks.add(new Coordinate(MathHelper.floor_double(dx+i), MathHelper.floor_double(dy+i/2), MathHelper.floor_double(dz+i/2)));
				blocks.add(new Coordinate(MathHelper.floor_double(dx+i/2), MathHelper.floor_double(dy+i), MathHelper.floor_double(dz+i/2)));
				blocks.add(new Coordinate(MathHelper.floor_double(dx+i/2), MathHelper.floor_double(dy+i/2), MathHelper.floor_double(dz+i)));
			}
		}
	}

	private void setBlock(World world, int x, int y, int z, Block b, int meta) {
		Block at = world.getBlock(x, y, z);
		if (ReikaBlockHelper.isGroundType(world, x, y, z) || at.isReplaceableOreGen(world, x, y, z, Blocks.cobblestone) || at.isReplaceableOreGen(world, x, y, z, Blocks.dirt) || at.isReplaceableOreGen(world, x, y, z, Blocks.grass) || at.isReplaceableOreGen(world, x, y, z, Blocks.gravel) || at.isReplaceableOreGen(world, x, y, z, Blocks.ice) || at.isReplaceableOreGen(world, x, y, z, Blocks.snow) || at == GeoBlocks.GLOWCRYS.getBlockInstance() || ReikaWorldHelper.softBlocks(world, x, y, z) || ReikaBlockHelper.isLeaf(world, x, y, z) || at.canBeReplacedByLeaves(world, x, y, z) || at.getMaterial() == Material.plants) {
			world.setBlock(x, y, z, b, meta, 3);
			world.func_147451_t(x, y, z);
		}
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata GlowCrystal";
	}

}
