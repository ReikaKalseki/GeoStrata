/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.DecimalConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;
import Reika.GeoStrata.GeoStrata;

public enum GeoOptions implements BooleanConfig, IntegerConfig, DecimalConfig, UserSpecificConfig {

	TFGEN("Generate Rock in the Twilight Forest", true),
	DIMGEN("Generate Rock in Other Dimensions", true),
	BOXRECIPES("Alternate Brick Recipes", false),
	DENSITY("Rock Density", 1F),
	VENTDENSITY("Vent Density", 1F),
	DECODENSITY("Decoration Density", 1F),
	CRYSTALDENSITY("Crystal Density", 1F),
	LAVAROCKDENSITY("Lava Rock Density", 1F),
	VINEDENSITY("Glowing Vine Density", 1F),
	RFDENSITY("Flux Crystal Density", 1F),
	GEOORE("Ore Mode", 0),
	RETROGEN("Retrogeneration", false),
	WAILA("Waila Overlay", true),
	BANDED("Banded Generation", false),
	OPALFREQ("Opal Color Frequency", 1F),
	OPALHUE("Opal Hue Offset (degrees)", 0);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private Class type;

	public static final GeoOptions[] optionList = GeoOptions.values();

	private GeoOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private GeoOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	private GeoOptions(String l, float d) {
		label = l;
		defaultFloat = d;
		type = float.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isDecimal() {
		return type == float.class;
	}

	public float getFloat() {
		return (Float)GeoStrata.config.getControl(this.ordinal());
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)GeoStrata.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)GeoStrata.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public float getDefaultFloat() {
		return defaultFloat;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return false;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	public static float getVentDensity() {
		return MathHelper.clamp_float(VENTDENSITY.getFloat(), 0.25F, 4F);
	}

	public static float getRockDensity() {
		return MathHelper.clamp_float(DENSITY.getFloat(), 0.5F, 4F);
	}

	public static float getLavaRockDensity() {
		return MathHelper.clamp_float(LAVAROCKDENSITY.getFloat(), 0.25F, 1F);
	}

	public static float getDecoDensity() {
		return MathHelper.clamp_float(DECODENSITY.getFloat(), 0.25F, 4F);
	}

	public static float getCrystalDensity() {
		return MathHelper.clamp_float(CRYSTALDENSITY.getFloat(), 0.25F, 2F);
	}

	public static float getVineDensity() {
		return MathHelper.clamp_float(VINEDENSITY.getFloat(), 0.125F, 8F);
	}

	public static float getRFCrystalDensity() {
		return MathHelper.clamp_float(RFDENSITY.getFloat(), 0, 2F);
	}

	@Override
	public boolean isUserSpecific() {
		switch(this) {
			case WAILA:
			case OPALFREQ:
			case OPALHUE:
				return true;
			default:
				return false;
		}
	}

}
