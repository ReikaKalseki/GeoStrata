/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

public enum CobbleRecipes {

	FURNACE("###", "# #", "###"),
	PICK("###", " S ", " S "),
	AXE("## ", "#S ", " S "),
	SWORD(" # ", " # ", " S "),
	SHOVEL(" # ", " S ", " S "),
	HOE("## ", " S ", " S "),
	LEVER("  ", " S ", " # "),
	BREWING("   ", " B ", "###"),
	PISTON("WWW", "#I#", "#R#"),
	DISPENSER("###", "#B#", "#R#"),
	DROPPER("###", "# #", "#R#"),
	//SLABS(), add my own
	//STAIRS(), add my own
	WALLS("   ", "###", "###");

	private String topRow;
	private String middleRow;
	private String bottomRow;

	private CobbleRecipes(String top, String middle, String bottom) {
		topRow = top;
		middleRow = middle;
		bottomRow = bottom;
	}
}
