/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Registry;

import net.minecraftforge.common.Configuration;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.GeoGen.GeoConfig;
import Reika.GeoGen.GeoGen;

public enum GeoOptions {

	NULL("", true);

	private String label;
	private boolean defaultState;
	private int defaultValue;
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

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public int setValue(Configuration config) {
		if (!this.isNumeric())
			throw new RegistrationException(GeoGen.instance, "Config Property \""+this.getLabel()+"\" is not numerical!");
		return config.get("Control Setup", this.getLabel(), defaultValue).getInt();
	}

	public String getLabel() {
		return label;
	}

	public boolean setState(Configuration config) {
		if (!this.isBoolean())
			throw new RegistrationException(GeoGen.instance, "Config Property \""+this.getLabel()+"\" is not boolean!");
		return config.get("Control Setup", this.getLabel(), defaultState).getBoolean(defaultState);
	}

	public boolean getState() {
		return (Boolean)GeoConfig.controls[this.ordinal()];
	}

	public int getValue() {
		return (Integer)GeoConfig.controls[this.ordinal()];
	}

}
