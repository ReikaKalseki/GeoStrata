/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;



public enum RockShapes {

	SMOOTH(GeoBlocks.SMOOTH, "smooth"),
	COBBLESTONE(GeoBlocks.COBBLE, "cobblestone"),
	BRICK(GeoBlocks.BRICK, "brick"),
	ROUND(GeoBlocks.ROUND, "round"),
	FITTED(GeoBlocks.FITTED, "fitted"),
	TILE(GeoBlocks.TILE, "tile"),
	ENGRAVED(GeoBlocks.ENGRAVED, "engraved"),
	INSCRIBED(GeoBlocks.INSCRIBED, "inscribed"),
	CONNECTED(GeoBlocks.CONNECTED, "connected");

	public final String typeName;
	private final GeoBlocks blockType;

	public static final RockShapes[] shapeList = values();

	private RockShapes(GeoBlocks b, String s) {
		typeName = s;
		blockType = b;
	}

	public GeoBlocks getBlockType(RockTypes rock) {
		int offset = rock.getBlockOffset();
		return blockType.getFromOffset(offset);
	}

}
