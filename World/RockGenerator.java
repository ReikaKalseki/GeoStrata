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
import java.util.Comparator;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.DragonAPI.Auxiliary.Trackers.WorldgenProfiler;
import Reika.DragonAPI.Auxiliary.Trackers.WorldgenProfiler.WorldProfilerParent;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.API.RockGenerationPatterns.RockGenerationPattern;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockTypes;

public class RockGenerator implements RetroactiveGenerator {

	protected static final int BASE_GEN = 24;
	protected static final int VEIN_SIZE = 32;

	public static final RockGenerator instance = new RockGenerator();

	//private final int oreControl;

	private final Comparator<RockGenerationPattern> genSorter = new RockGenComparator();

	private final ArrayList<RockGenerationPattern> generators = new ArrayList();

	private final RockParent[] parents = new RockParent[RockTypes.rockList.length];

	protected RockGenerator() {
		//oreControl = GeoOptions.GEOORE.getValue();

		for (int i = 0; i < parents.length; i++) {
			parents[i] = new RockParent(RockTypes.rockList[i]);
		}
	}

	public void registerProfilingSubgenerator(RockTypes r, RockGenerationPattern p, Object generator) {
		parents[r.ordinal()].pattern = p;
		WorldgenProfiler.registerGeneratorAsSubGenerator(parents[r.ordinal()], generator);
	}

	public void registerGenerationPattern(RockGenerationPattern p) {
		generators.add(p);
		Collections.sort(generators, genSorter);
		GeoStrata.logger.log("Adding rock generator "+p);
	}

	public void removeGenerator(RockGenerationPattern p) {
		generators.remove(p);
		GeoStrata.logger.log("Removing rock generator "+p);
	}

	@Override
	public final void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		if (generators.isEmpty()) {
			throw new IllegalStateException("No generators to run!");
		}
		if (this.canGenInDimension(world.provider.dimensionId)) {
			this.generateRock(world, random, chunkX, chunkZ);
		}
	}

	private boolean canGenInDimension(int id) {
		if (id == 0)
			return true;
		if (id == 1 || id == -1)
			return false;
		if (id == ReikaTwilightHelper.getDimensionID())
			return GeoOptions.TFGEN.getState();
		return GeoOptions.DIMGEN.getState();
	}

	private void generateRock(World world, Random random, int chunkX, int chunkZ) {
		chunkX *= 16;
		chunkZ *= 16;
		//ReikaJavaLibrary.pConsole("Calling chunk at "+chunkX+", "+chunkZ);
		//BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
		for (int k = 0; k < RockTypes.rockList.length; k++) {
			RockTypes geo = RockTypes.rockList[k];
			this.generateRockType(geo, world, random, chunkX, chunkZ);
		}
	}

	protected void generateRockType(RockTypes geo, World world, Random random, int chunkX, int chunkZ) {
		for (RockGenerationPattern p : generators) {
			if (WorldgenProfiler.profilingEnabled())
				WorldgenProfiler.startGenerator(world, parents[geo.ordinal()], chunkX >> 4, chunkZ >> 4);
			p.generateRockType(geo, world, random, chunkX, chunkZ);
			if (WorldgenProfiler.profilingEnabled())
				WorldgenProfiler.onRunGenerator(world, parents[geo.ordinal()], chunkX >> 4, chunkZ >> 4);
		}
	}
	/*
	public final boolean postConvertOres() {
		return oreControl == 2;
	}

	public final boolean generateOres() {
		return oreControl >= 1;
	}

	public final boolean destroyOres() {
		return oreControl == -1;
	}
	 */
	@Override
	public final boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public final String getIDString() {
		return "GeoStrata Rock";
	}

	private static class RockGenComparator implements Comparator<RockGenerationPattern> {

		@Override
		public int compare(RockGenerationPattern o1, RockGenerationPattern o2) {
			return o1.getOrderingIndex() == o2.getOrderingIndex() ? 0 : (o1.getOrderingIndex() > o2.getOrderingIndex() ? 1 : -1);
		}

	}

	private static class RockParent implements WorldProfilerParent {

		private final RockTypes type;
		private RockGenerationPattern pattern;

		private RockParent(RockTypes r) {
			this(r, null);
		}

		private RockParent(RockTypes r, RockGenerationPattern p) {
			type = r;
			pattern = p;
		}

		public final String getWorldgenProfilerID() {
			return type.getName()+" "+pattern.getClass().getName();
		}

	}

}
