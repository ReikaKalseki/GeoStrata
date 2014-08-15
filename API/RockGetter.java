/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.API;

import net.minecraft.block.Block;

public class RockGetter {

	private static Class modClass;
	private static Block[] geoBlocks;

	public static final int ROCK_INDEX = 0;

	static {
		try {
			modClass = Class.forName("Reika.GeoStrata.GeoStrata", false, RockGetter.class.getClassLoader());
			geoBlocks = (Block[])modClass.getField("blocks").get(null);
		}
		catch (ClassNotFoundException e) {
			System.out.println("GeoStrata class not found!");
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			System.out.println("GeoStrata class not read correctly!");
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			System.out.println("GeoStrata class not read correctly!");
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
			System.out.println("GeoStrata class not read correctly!");
			e.printStackTrace();
		}
		catch (SecurityException e) {
			System.out.println("GeoStrata class not read correctly!");
			e.printStackTrace();
		}
	}

}