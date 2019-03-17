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

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenForest;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenTaiga;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.GeoStrata.Blocks.BlockGlowingVines;
import Reika.GeoStrata.Registry.GeoOptions;

public class GlowingVineGenerator implements RetroactiveGenerator {

	public static final GlowingVineGenerator instance = new GlowingVineGenerator();

	private static final int PER_CHUNK = getVineAttemptsPerChunk(); //calls per chunk; vast majority fail

	private GlowingVineGenerator() {

	}

	private static int getVineAttemptsPerChunk() {
		return (int)(2*GeoOptions.getVineDensity());
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
			chunkX *= 16;
			chunkZ *= 16;
			for (int i = 0; i < PER_CHUNK; i++) {
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				int maxy = 60;
				int posY = 4+random.nextInt(maxy-4);
				if (this.canGenerateAt(world, posX, posY, posZ)) {
					if (BlockGlowingVines.place(world, posX, posY, posZ, null)) {
						//ReikaJavaLibrary.pConsole(posX+" "+posY+" "+posZ);
					}
				}
			}
		}
	}

	public static boolean canGenerateAt(World world, int x, int y, int z) {
		return isValidBiome(world, x, z) && world.getBlock(x, y, z).isAir(world, x, y, z) && ReikaWorldHelper.checkForAdjSolidBlock(world, x, y, z);
	}

	private static boolean isValidBiome(World world, int x, int z) {
		if (world.provider.dimensionId == TwilightForestHandler.getInstance().dimensionID)
			return true;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ModList.CHROMATICRAFT.isLoaded()) {
			return isGlowingCliffs(b);
		}
		return b instanceof BiomeGenForest || b instanceof BiomeGenTaiga || b instanceof BiomeGenJungle || BiomeDictionary.isBiomeOfType(b, Type.FOREST) || BiomeDictionary.isBiomeOfType(b, Type.JUNGLE);
	}

	@ModDependent(ModList.CHROMATICRAFT)
	private static boolean isGlowingCliffs(BiomeGenBase b) {
		return BiomeGlowingCliffs.isGlowingCliffs(b);
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "GeoStrata Glowing Vines";
	}

}
