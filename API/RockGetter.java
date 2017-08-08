/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.API;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class RockGetter {

	private static Class modClass;
	private static Block[] geoBlocks;

	private static ArrayList<Block> rockBlocks;

	private static Class typeRegistry;
	private static Class shapeRegistry;

	private static Method getStackByShape;
	private static Method getBlockByShape;

	private static boolean init = false;

	static {
		try {
			modClass = Class.forName("Reika.GeoStrata.GeoStrata", false, RockGetter.class.getClassLoader());
			geoBlocks = (Block[])modClass.getField("blocks").get(null);
			rockBlocks = (ArrayList)modClass.getField("rockBlocks").get(null);

			typeRegistry = Class.forName("Reika.GeoStrata.Registry.RockTypes");
			shapeRegistry = Class.forName("Reika.GeoStrata.Registry.RockShapes");

			getStackByShape = typeRegistry.getMethod("getItem", shapeRegistry);
			getBlockByShape = typeRegistry.getMethod("getID", shapeRegistry);

			init = true;
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
		catch (NoSuchMethodException e) {
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

	/** Returns an itemstack of the specified rock type and shape. Refer to the GeoStrata "RockTypes" and "RockShapes" classes for valid options.
	 * Many, but not all, shapes of the same rock are submetadatas of that parent block type. No block type represents two rock types. */
	public static ItemStack getRockItem(String type, String shape) {
		if (!init)
			return null;
		try {
			Object rtype = Enum.valueOf(typeRegistry, type.toUpperCase());
			Object rshape = Enum.valueOf(shapeRegistry, shape.toUpperCase());
			return (ItemStack)getStackByShape.invoke(rtype, rshape);
		}
		catch (IllegalArgumentException e) {
			System.out.println("Invalid enum parameter!");
			System.out.println("Valid rock types: ");
			Object[] types = typeRegistry.getEnumConstants();
			for (int i = 0; i < types.length; i++)
				System.out.println("\t"+((Enum)types[i]).name());
			System.out.println("");
			System.out.println("Valid rock shapes: ");
			Object[] shapes = shapeRegistry.getEnumConstants();
			for (int i = 0; i < shapes.length; i++)
				System.out.println("\t"+((Enum)shapes[i]).name());
			e.printStackTrace();
			return null;
		}
		catch (Exception e) {
			System.out.println("GeoStrata rock type "+type+" and shape "+shape+" threw exception while fetching!");
			e.printStackTrace();
			return null;
		}
	}

	/** Returns the block of the specified rock type and shape. Same result, but faster, as calling Block.getBlockFromItem(getRockItem().getItem()).
	 * Args: String names of the two enum constants. */
	public static Block getRockBlock(String type, String shape) {
		if (!init)
			return null;
		try {
			Object rtype = Enum.valueOf(typeRegistry, type.toUpperCase());
			Object rshape = Enum.valueOf(shapeRegistry, shape.toUpperCase());
			return (Block)getBlockByShape.invoke(rtype, rshape);
		}
		catch (IllegalArgumentException e) {
			System.out.println("Invalid enum parameter!");
			System.out.println("Valid rock types: ");
			Object[] types = typeRegistry.getEnumConstants();
			for (int i = 0; i < types.length; i++)
				System.out.println("\t"+((Enum)types[i]).name());
			System.out.println("");
			System.out.println("Valid rock shapes: ");
			Object[] shapes = shapeRegistry.getEnumConstants();
			for (int i = 0; i < shapes.length; i++)
				System.out.println("\t"+((Enum)shapes[i]).name());
			e.printStackTrace();
			return null;
		}
		catch (Exception e) {
			System.out.println("GeoStrata rock type "+type+" and shape "+shape+" threw exception while fetching!");
			e.printStackTrace();
			return null;
		}
	}

	/** Shortcut for getting smooth (worldgen) rock. */
	public static Block getSmoothRock(String type) {
		return getRockBlock(type, "SMOOTH");
	}

	/** Returns all blocks representing the given rock type. */
	public static Collection<Block> getAllBlocksFor(String type) {
		if (!init)
			return null;
		Object rtype = null;
		Collection<Block> c = new ArrayList();
		try {
			rtype = Enum.valueOf(typeRegistry, type.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			System.out.println("Invalid rock type "+type+"!");
			System.out.println("Valid rock types: ");
			Object[] types = typeRegistry.getEnumConstants();
			for (int i = 0; i < types.length; i++)
				System.out.println("\t"+((Enum)types[i]).name());
			return null;
		}
		Object[] shapes = shapeRegistry.getEnumConstants();
		for (int i = 0; i < shapes.length; i++) {
			try {
				Block b = (Block)getBlockByShape.invoke(rtype, shapes[i]);
				if (!c.contains(b))
					c.add(b);
			}
			catch (Exception e) {
				System.out.println("GeoStrata rock type "+type+" and shape "+shapes[i]+" threw exception while fetching its blocks!");
				e.printStackTrace();
				return null;
			}
		}
		return c;
	}

	public static boolean isGeoStrataRock(Block b) {
		if (!init)
			return false;
		return rockBlocks.contains(b);
	}

}
