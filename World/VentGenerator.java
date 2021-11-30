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
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom.DynamicWeight;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Blocks.BlockVent.VentType;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;

public class VentGenerator implements RetroactiveGenerator {

	public static final VentGenerator instance = new VentGenerator();

	private static final int PER_CHUNK = getVentAttemptsPerChunk(); //calls per chunk; vast majority fail

	private final WeightedRandom<VentGen> ventTypes = new WeightedRandom();
	private final WeightedRandom<VentGen> ventTypesNether = new WeightedRandom();

	private VentGenerator() {
		for (VentType v : VentType.list) {
			if (v.canGenerateInOverworld())
				ventTypes.addDynamicEntry(new VentGen(v));
			if (v.canGenerateInNether())
				ventTypesNether.addDynamicEntry(new VentGen(v));
		}
	}

	private String getRatiosAt(int y, boolean nether) {
		WeightedRandom<VentGen> wr = nether ? ventTypesNether : ventTypes;
		Proportionality<VentType> p = new Proportionality();
		for (VentGen gr : wr.getValues()) {
			double w = gr.type.getSpawnWeight(y, nether);
			p.addValue(gr.type, w);
		}
		return p.toString();
	}

	private static int getVentAttemptsPerChunk() {
		return (int)(60*GeoOptions.getVentDensity());
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
			chunkX *= 16;
			chunkZ *= 16;
			for (int i = 0; i < PER_CHUNK; i++) {
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				int maxy = world.provider.dimensionId == -1 ? 128 : (world.provider.dimensionId == 1 ? 72 : 64);
				int posY = ReikaRandomHelper.getRandomBetween(4, maxy, random);
				if (random.nextBoolean()) {
					posY *= random.nextFloat();
				}
				if (this.canGenerateAt(world, posX, posY, posZ)) {
					VentType v = this.getVentTypeFor(world, posX, posY, posZ, random);
					Block id = GeoBlocks.VENT.getBlockInstance();
					int meta = v.ordinal();
					world.setBlock(posX, posY, posZ, id, meta, 3);
				}
			}
		}
	}

	private VentType getVentTypeFor(World world, int posX, int posY, int posZ, Random random) {
		if (world.provider.dimensionId == 1) {
			return VentType.ENDER;
		}

		WeightedRandom<VentGen> wr = world.provider.dimensionId == -1 ? ventTypesNether : ventTypes;
		wr.setRNG(random);
		for (VentGen gr : wr.getValues()) {
			gr.calcWeight(world, posX, posY, posZ);
		}

		return wr.getRandomEntry().type;
	}

	public static boolean canGenerateAt(World world, int x, int y, int z) {
		Block ida = world.getBlock(x, y+1, z);
		if (ida != Blocks.air && !ReikaWorldHelper.softBlocks(world, x, y+1, z))
			return false;
		return canGenerateIn(world, x, y, z);
	}

	public static boolean canGenerateIn(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == Blocks.air)
			return false;
		if (id == Blocks.stone)
			return true;
		if (id == Blocks.dirt)
			return true;
		if (id == Blocks.gravel)
			return true;
		if (id == Blocks.netherrack)
			return true;
		if (id == Blocks.end_stone)
			return true;
		if (id == Blocks.cobblestone)
			return y < world.provider.getAverageGroundLevel()-10;
		return id.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata Vents";
	}

	private static class VentGen implements DynamicWeight {

		private final VentType type;

		private double weight;

		private VentGen(VentType v) {
			type = v;
		}

		private void calcWeight(World world, int x, int y, int z) {
			float f = Math.min(1, Math.max(0.25F, world.provider.getAverageGroundLevel()/64F));
			weight = type.getSpawnWeight((int)(y/f), world.provider.dimensionId == -1);
			if (type == VentType.CRYO && !ReikaBiomeHelper.isSnowBiome(world.getBiomeGenForCoords(x, z))) {
				weight = 0;
			}
		}

		@Override
		public double getWeight() {
			return weight;
		}

	}

}
