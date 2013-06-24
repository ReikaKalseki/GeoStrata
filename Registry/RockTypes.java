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

import net.minecraft.block.Block;

public enum RockTypes {

	GRANITE(60, 10),
	BASALT(30, 5),
	MARBLE(45, 2.5F),
	LIMESTONE(15, 1),
	SHALE(5, 1),
	SANDSTONE(10, 2),
	PUMICE(20, 5),
	SLATE(30, 5),
	GNEISS(30, 7.5F),
	PERIDOTITE(30, 5),
	QUARTZ(40, 4),
	GRANULITE(30, 5),
	HORNFEL(30, 5),
	MIGMATITE(30, 5);

	private float blockHardness; //stone has 30
	private float blastResistance; //stone has 5

	private RockTypes(float hard, float blast) {
		blastResistance = blast;
		blockHardness = hard;
	}

	public String getName() {
		return this.name().substring(0, 1)+this.name().substring(1).toLowerCase()+" Rock";
	}

	public Block instantiate() {
		return null;
	}

}
