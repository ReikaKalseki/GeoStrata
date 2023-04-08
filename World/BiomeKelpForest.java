package Reika.GeoStrata.World;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenOcean;

import Reika.DragonAPI.Interfaces.CustomMapColorBiome;


public class BiomeKelpForest extends BiomeGenOcean implements CustomMapColorBiome {

	public BiomeKelpForest(int id) {
		super(id);
		biomeName = "Kelp Forest";
		this.setColor(BiomeGenBase.ocean.color);
		this.setHeight(height_Oceans);
	}

	@Override
	public int getWaterColorMultiplier() {
		return 0x8EE595;
	}

	@Override
	public int getMapColor(World world, int x, int z) {
		return 0x6EB5C2;
	}

}
