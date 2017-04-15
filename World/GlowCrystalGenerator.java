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

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.DoublePolygon;
import Reika.DragonAPI.Instantiable.Math.DoubleRectangle;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
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
		/*
		Crystal cry = new Crystal(x, y, z);
		cry.rootHeight = 12+rand.nextInt(18);//6+rand.nextInt(7);
		cry.rootSize = 4+rand.nextInt(6);//1+rand.nextInt(4);
		cry.rotX = 0;//rand.nextDouble()*360;
		cry.rotY = rand.nextDouble()*360;
		cry.rotZ = 30+rand.nextDouble()*30;//rand.nextDouble()*360;
		cry.calculate();
		cry.randomize(rand);
		Collection<Coordinate> li = cry.getCoordinates();
		 */
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

	private static class Crystal {

		private final int centerX;
		private final int centerY;
		private final int centerZ;

		private double rootHeight = 8; //radius, not -ve to +ve
		private double rootSize = 3; //radius, not -ve to +ve

		private double rotX;
		private double rotY;
		private double rotZ;

		private DecimalPosition lowerPoint;
		private DecimalPosition upperPoint;
		private DecimalPosition[] edgePoints;

		private Crystal(int x, int y, int z) {
			centerX = x;
			centerY = y;
			centerZ = z;
		}

		private void calculate() {
			Vec3 lowerPointVec = Vec3.createVectorHelper(0, 0-rootHeight, 0);
			Vec3 upperPointVec = Vec3.createVectorHelper(0, 0+rootHeight, 0);
			Vec3[] edgePointsVec = new Vec3[4];
			edgePointsVec[0] = Vec3.createVectorHelper(0+rootSize, 0, 0);
			edgePointsVec[1] = Vec3.createVectorHelper(0, 			0, 0+rootSize);
			edgePointsVec[2] = Vec3.createVectorHelper(0-rootSize, 0, 0);
			edgePointsVec[3] = Vec3.createVectorHelper(0, 			0, 0-rootSize);

			lowerPointVec = ReikaVectorHelper.rotateVector(lowerPointVec, rotX, rotY, rotZ);
			upperPointVec = ReikaVectorHelper.rotateVector(upperPointVec, rotX, rotY, rotZ);
			for (int i = 0; i < 4; i++) {
				edgePointsVec[i] = ReikaVectorHelper.rotateVector(edgePointsVec[i], rotX, rotY, rotZ);
			}

			edgePoints = new DecimalPosition[4];
			lowerPoint = new DecimalPosition(lowerPointVec).offset(centerX, centerY, centerZ);
			upperPoint = new DecimalPosition(upperPointVec).offset(centerX, centerY, centerZ);
			for (int i = 0; i < 4; i++) {
				edgePoints[i] = new DecimalPosition(edgePointsVec[i]).offset(centerX, centerY, centerZ);
			}
		}

		private void randomize(Random rand) {
			lowerPoint = lowerPoint.offset(rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
			upperPoint = upperPoint.offset(rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
			for (int i = 0; i < 4; i++) {
				edgePoints[i] = edgePoints[i].offset(rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5);
			}
		}

		private Collection<Coordinate> getCoordinates() {
			HashSet<Coordinate> set = new HashSet();
			AxisAlignedBB box = ReikaAABBHelper.fromPoints(upperPoint, lowerPoint, edgePoints[0], edgePoints[1], edgePoints[2], edgePoints[3]);
			int y0 = MathHelper.floor_double(box.minY);
			int y1 = MathHelper.ceiling_double_int(box.maxY);
			for (int y = y0; y < y1; y++) {
				double dy = y+0.5;
				DoublePolygon slice = new DoublePolygon();
				for (int i = 0; i < 4; i++) {
					DecimalPosition p = edgePoints[i];
					double f = dy < centerY ? (centerY-dy)/(centerY-lowerPoint.yCoord) : 1-((dy-centerY)/(upperPoint.yCoord-centerY));
					double cx = dy < centerY ? lowerPoint.xCoord : upperPoint.xCoord;
					double cz = dy < centerY ? lowerPoint.zCoord : upperPoint.zCoord;
					double x = cx+(p.xCoord-cx)*(dy < centerY ? 1-f : f);
					double z = cz+(p.zCoord-cz)*(dy < centerY ? 1-f : f);
					slice.addPoint(x, z);
				}
				DoubleRectangle r = slice.getBounds();
				int x0 = MathHelper.floor_double(r.x);
				int x1 = MathHelper.ceiling_double_int(r.x+r.width);
				int z0 = MathHelper.floor_double(r.y);
				int z1 = MathHelper.ceiling_double_int(r.y+r.height);
				for (int x = x0; x <= x1; x++) {
					for (int z = z0; z <= z1; z++) {
						double dx = x+0.5;
						double dz = z+0.5;
						if (slice.contains(dx, dz) || slice.contains(dx-0.5, dz-0.5) || slice.contains(dx+0.5, dz+0.5)) {
							set.add(new Coordinate(x, y, z));
						}
					}
				}
			}
			return set;
		}

	}

}
