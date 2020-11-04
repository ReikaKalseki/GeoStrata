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

import net.minecraft.world.World;

public class GeoCommon {

	public static int connectedRender;
	public static int ventRender;
	public static int oreRender;
	public static int lavarockRender;
	public static int decoRender;
	public static int vineRender;
	//public static int shapedRender;
	public static int voidopalRender;

	/**
	 * Client side only register stuff...
	 */
	public void registerRenderers()
	{
		//unused server side. -- see ClientProxy for implementation
	}

	public void addArmorRenders() {}

	public World getClientWorld() {
		return null;
	}

	public void registerRenderInformation() {

	}

	public void registerSounds() {
	}

}
