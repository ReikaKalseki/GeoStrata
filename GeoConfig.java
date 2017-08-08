/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.util.ArrayList;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.GeoStrata.Registry.RockTypes;


public class GeoConfig extends ControlledConfig {

	private static final ArrayList<String> entries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(RockTypes.class);
	private final DataElement<Integer>[] rockBands = new DataElement[entries.size()];

	public GeoConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id) {
		super(mod, option, id);

		for (int i = 0; i < entries.size(); i++) {
			String name = entries.get(i);
			rockBands[i] = this.registerAdditionalOption("Rock Band Heights", name, 0);
		}
	}

	public int getRockBand(RockTypes r) {
		return rockBands[r.ordinal()].getData();
	}

}
