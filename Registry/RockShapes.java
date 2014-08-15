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

import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Blocks.BlockConnectedRock;
import Reika.GeoStrata.Blocks.BlockShapedRock;
import Reika.GeoStrata.Blocks.BlockSmooth;
import Reika.GeoStrata.Items.ItemBlockRock;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.common.registry.GameRegistry;



public enum RockShapes {

	SMOOTH(BlockSmooth.class),
	COBBLE(BlockShapedRock.class, 		0),
	BRICK(BlockShapedRock.class, 		1),
	ROUND(BlockShapedRock.class, 		2),
	FITTED(BlockShapedRock.class, 		3),
	TILE(BlockShapedRock.class, 		4),
	ENGRAVED(BlockShapedRock.class, 	5),
	INSCRIBED(BlockShapedRock.class, 	6),
	CUBED(BlockShapedRock.class, 		7),
	LINED(BlockShapedRock.class, 		8),
	EMBOSSED(BlockShapedRock.class, 	9),
	CENTERED(BlockShapedRock.class, 	10),
	RAISED(BlockShapedRock.class, 		11),
	ETCHED(BlockShapedRock.class, 		12),
	CONNECTED(BlockConnectedRock.class);

	//public final String typeName;
	//private final GeoBlocks blockType;
	private final Class blockClass;
	public final boolean needsOwnBlock;
	public final int metadata;
	private final int offset;

	public static final RockShapes[] shapeList = values();
	private static final BlockMap<RockShapes> shapeMap = new BlockMap();
	private static final EnumMap<RockShapes, EnumMap<RockTypes, Block>> blockMap = new EnumMap(RockShapes.class);
	private static final HashMap<Integer, ArrayList<RockShapes>> offsetMap = new HashMap();

	/*
	private RockShapes(GeoBlocks b, String s) {
		typeName = s;
		blockType = b;
	}*/

	private RockShapes(Class block, int meta) {
		blockClass = block;
		metadata = meta >= 0 ? meta%16 : 0;
		offset = meta/16;
		needsOwnBlock = meta == -1;
	}

	private RockShapes(Class block) {
		this(block, -1);
	}

	public static RockShapes getShape(IBlockAccess world, int x, int y, int z) {
		return getShape(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public static RockShapes getShape(Block id, int meta) {
		return shapeMap.get(id, meta);
	}

	public Block register(RockTypes r) {
		EnumMap<RockTypes, Block> map = blockMap.get(this);
		if (map == null) {
			map = new EnumMap(RockTypes.class);
			blockMap.put(this, map);
			if (!needsOwnBlock) {
				ArrayList<RockShapes> li = offsetMap.get(offset);
				if (li == null)
					throw new RegistrationException(GeoStrata.instance, "Rock shape "+this+" has no pair mappings!");
				for (int i = 0; i < li.size(); i++) {
					RockShapes s = li.get(i);
					blockMap.put(s, map);
				}
			}
		}

		if (map.containsKey(r)) {
			throw new RegistrationException(GeoStrata.instance, "Block type for "+r+" "+this+" was created twice!");
		}
		else {
			try {
				Block b = (Block)blockClass.newInstance();
				map.put(r, b);

				String name = "geostrata_rock_"+(r.name()+"_"+this.name()).toLowerCase();
				GameRegistry.registerBlock(b, ItemBlockRock.class, name);
				return b;
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RegistrationException(GeoStrata.instance, "Block type for "+r+" "+this+" could not be created!");
			}
		}
	}

	public static void initalize() {
		for (int k = 0; k < RockTypes.rockList.length; k++) {
			RockTypes r = RockTypes.rockList[k];
			for (int i = 0; i < shapeList.length; i++) {
				RockShapes s = shapeList[i];
				shapeMap.put(s.getBlock(r), s.metadata, s);
			}
		}
	}

	public boolean isRegistered(RockTypes r) {
		return this.getBlock(r) != null;
	}

	public Block getBlock(RockTypes r) {
		EnumMap<RockTypes, Block> map = blockMap.get(this);
		if (map == null || !map.containsKey(r))
			return null; //throw new RegistrationException(GeoStrata.instance, "Rock shape "+this+" has no block for "+r+"!");
		else
			return map.get(r);
	}

	static {
		for (int i = 0; i < shapeList.length; i++) {
			RockShapes s = shapeList[i];
			int offset = s.needsOwnBlock ? -1 : s.offset;
			ArrayList<RockShapes> li = offsetMap.get(offset);
			if (li == null) {
				li = new ArrayList();
				offsetMap.put(offset, li);
			}
			li.add(s);
		}
	}

	public static int getNumberBlockTypes() {
		int count = 0;
		for (int i = 0; i < shapeList.length; i++) {
			RockShapes s = shapeList[i];
			if (s.needsOwnBlock) {
				count++;
			}
		}
		count += 1+getHighestOffset();
		return count;
	}

	private static int getHighestOffset() {
		int max = -1;
		for (int i = 0; i < shapeList.length; i++) {
			RockShapes s = shapeList[i];
			max = Math.max(s.offset, max);
		}
		return max;
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

}