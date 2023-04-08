package Reika.GeoStrata.World;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSnow;

import Reika.DragonAPI.Interfaces.CustomMapColorBiome;
import Reika.DragonAPI.Interfaces.CustomTemperatureBiome;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class BiomeArcticSpires extends BiomeGenSnow implements CustomMapColorBiome, CustomTemperatureBiome {

	public BiomeArcticSpires(int id) {
		super(id, false);
		biomeName = "Arctic Spires";
		this.setColor(BiomeGenBase.icePlains.color);
		this.setTemperatureRainfall(0.0F, 0.5F);
		this.setHeight(height_LowPlains);
		this.setEnableSnow();
	}

	@Override
	public int getMapColor(World world, int x, int z) {
		return 0xB2FFF7;
	}

	@Override
	public int getBaseAmbientTemperature() {
		return -25; //vs -20
	}

	@Override
	public float getSeasonStrength() {
		return 2;
	}

	@Override
	public int getSurfaceTemperatureModifier(World world, int x, int y, int z, float temp, float sun) {
		int ret = (int)(-25+sun*20); //-25 at night, -5 in day -> -50C at night, -30 in day
		if (world.isRaining())
			ret -= 10;
		return ret;
	}

	@Override
	public int getAltitudeTemperatureModifier(World world, int x, int y, int z, float temp, int dy) {
		return (int)ReikaMathLibrary.linterpolate(dy, 30, 90, 0, -30, true);
	}

	@Override
	public float getNoiseVariationStrength(World world, int x, int y, int z, float orig) {
		return orig*0.5F;
	}

}
