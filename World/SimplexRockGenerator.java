package Reika.GeoStrata.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Math.Simplex3DGenerator;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.API.RockGenerationPatterns.RockGenerationPattern;
import Reika.GeoStrata.API.RockProofStone;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class SimplexRockGenerator implements RockGenerationPattern {

	private final RockEntry[] data = new RockEntry[RockTypes.rockList.length];

	public SimplexRockGenerator() {

	}

	private RockEntry initData(World world, RockTypes geo) {
		RockEntry gen = data[geo.ordinal()];
		if (gen == null || gen.noise.seed != world.getSeed() || true) {
			gen = new RockEntry(world, geo);
		}
		return gen;
	}

	@Override
	public void generateRockType(RockTypes geo, World world, Random rand, int chunkX, int chunkZ) {
		RockEntry gen = this.initData(world, geo);
		for (int x = chunkX; x < chunkX+16; x++) {
			for (int z = chunkZ; z < chunkZ+16; z++) {
				if (geo.canGenerateAtXZ(world, x, z, rand)) {
					for (int y = geo.minY; y <= geo.maxY; y++) {
						double val = gen.noise.getValue(x, y, z);
						if (val > gen.noiseThreshold && geo.canGenerateAtSkipXZ(world, x, y, z, rand)) {
							Block b = world.getBlock(x, y, z);
							int meta = world.getBlockMetadata(x, y, z);
							if (this.canGenerateIn(world, x, y, z, b, meta)) {
								world.setBlock(x, y, z, gen.blockID, 0, 2);
							}
							else if (RockGenerator.instance.generateOres() && ReikaBlockHelper.isOre(b, meta)) {
								TileEntityGeoOre te = new TileEntityGeoOre();
								te.initialize(geo, b, meta);
								world.setBlock(x, y, z, GeoBlocks.ORETILE.getBlockInstance());
								world.setTileEntity(x, y, z, te);
							}
						}
					}
				}
			}
		}
	}

	private boolean canGenerateIn(World world, int x, int y, int z, Block b, int meta) {
		if (b instanceof RockProofStone && ((RockProofStone)b).blockRockGeneration(world, x, y, z, b, meta))
			return false;
		return b.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	@Override
	public int getOrderingIndex() {
		return 0;
	}

	private static class RockEntry {

		private final Simplex3DGenerator noise;
		private final Block blockID;
		private final double noiseThreshold;
		private final double noiseFrequency;

		private RockEntry(World world, RockTypes geo) {
			noise = new Simplex3DGenerator(geo.name().hashCode());
			noiseThreshold = 0.5/geo.rarity; //was 0.5 then 0.75 in all cases
			noiseFrequency = 1/8D; //was 1/64 then 1/16
			noise.setFrequency(noiseFrequency);
			blockID = geo.getID(RockShapes.SMOOTH);
		}

	}

}
