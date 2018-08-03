package Reika.GeoStrata.Registry;

import java.util.Arrays;
import java.util.Locale;

import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.API.RockGenerationPatterns.RockGenerationPattern;
import Reika.GeoStrata.World.BandedGenerator;
import Reika.GeoStrata.World.BasicRockGenerator;
import Reika.GeoStrata.World.SimplexRockGenerator;


public enum RockGeneratorTypes {

	LEGACY(BasicRockGenerator.class),
	BANDED(BandedGenerator.class),
	SIMPLEX(SimplexRockGenerator.class);

	private final Class<? extends RockGenerationPattern> type;

	private RockGeneratorTypes(Class<? extends RockGenerationPattern> c) {
		type = c;
	}

	public static RockGeneratorTypes getType(String config) {
		try {
			return RockGeneratorTypes.valueOf(config.toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException e) {
			throw new InstallationException(GeoStrata.instance, "Invalid selected rock generation pattern '"+config+"'; choose one of the following: "+Arrays.toString(values()));
		}
	}

	public RockGenerationPattern getGenerator() {
		try {
			return type.newInstance();
		}
		catch (InstantiationException e) {
			throw new RegistrationException(GeoStrata.instance, "Could not create rock generator for type "+this+"!", e);
		}
		catch (IllegalAccessException e) {
			throw new RegistrationException(GeoStrata.instance, "Could not access constructor for rock generator for type "+this+"!", e);
		}
	}
}
