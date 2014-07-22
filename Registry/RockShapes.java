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

import java.util.HashMap;

import net.minecraft.block.Block;



public enum RockShapes {

	SMOOTH(GeoBlocks.SMOOTH, "smooth"),
	COBBLE(GeoBlocks.COBBLE, "cobble"),
	BRICK(GeoBlocks.BRICK, "brick"),
	ROUND(GeoBlocks.ROUND, "round"),
	FITTED(GeoBlocks.FITTED, "fitted"),
	TILE(GeoBlocks.TILE, "tile"),
	ENGRAVED(GeoBlocks.ENGRAVED, "engraved"),
	INSCRIBED(GeoBlocks.INSCRIBED, "inscribed"),
	CUBED(GeoBlocks.CUBED, "cubed"),
	LINED(GeoBlocks.LINED, "lined"),
	EMBOSSED(GeoBlocks.EMBOSSED, "embossed"),
	CENTERED(GeoBlocks.CENTERED, "centered"),
	RAISED(GeoBlocks.RAISED, "raised"),
	ETCHED(GeoBlocks.ETCHED, "etched"),
	CONNECTED(GeoBlocks.CONNECTED, "connected");

	public final String typeName;
	private final GeoBlocks blockType;

	public static final RockShapes[] shapeList = values();
	private static final HashMap<GeoBlocks, RockShapes> shapeMap = new HashMap();

	private RockShapes(GeoBlocks b, String s) {
		typeName = s;
		blockType = b;
	}

	public GeoBlocks getBlockType(RockTypes rock) {
		int offset = rock.getBlockOffset();
		return blockType.getFromOffset(offset);
	}

	public static RockShapes getShape(Block block) {
		GeoBlocks b = GeoBlocks.getFromID(block.blockID);
		if (b != null) {
			RockShapes s = shapeMap.get(b);
			if (s != null) {
				return s;
			}
			for (int i = 0; i < shapeList.length; i++) {
				RockShapes r = shapeList[i];
				GeoBlocks b1 = r.blockType;
				GeoBlocks b2 = b1.getFromOffset(1);
				if (b1 == b || b2 == b) {
					shapeMap.put(b1, r);
					shapeMap.put(b2, r);
					return r;
				}
			}
			return null;
		}
		return null;
	}

}
