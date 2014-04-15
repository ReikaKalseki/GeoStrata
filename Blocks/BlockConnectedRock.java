/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockConnectedRock extends RockBlock {

	private final ArrayList<Integer> allDirs = new ArrayList();

	private Icon[][] edges = new Icon[10][16];

	public BlockConnectedRock(int ID, Material mat) {
		super(ID, mat);

		for (int i = 1; i < 10; i++) {
			allDirs.add(i);
		}
	}

	@Override
	public final int idDropped(int id, Random r, int fortune) {
		return blockID;
	}

	@Override
	public final int getRenderType() {
		return GeoStrata.proxy.connectedRender;
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < RockTypes.getTypesForID(blockID); i++) {
			String name = RockTypes.getTypeFromIDandMeta(blockID, i).getName();
			icons[i] = ico.registerIcon("GeoStrata:"+name.toLowerCase());
			GeoStrata.logger.debug("Adding "+name+" rock icon "+icons[i].getIconName());

			for (int k = 0; k < 10; k++) {
				edges[k][i] = ico.registerIcon("GeoStrata:connected/"+k);
			}
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
		int id = iba.getBlockId(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		int meta = iba.getBlockMetadata(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		return id != blockID || meta != iba.getBlockMetadata(x, y, z);
	}

	/** Returns the unconnected sides. Each integer represents one of 8 adjacent corners to a face, with the same
	 * numbering convention as is found on a calculator or computer number pad. */
	public ArrayList<Integer> getEdgesForFace(IBlockAccess world, int x, int y, int z, ForgeDirection face, RockTypes rock) {
		ArrayList<Integer> li = new ArrayList();
		li.addAll(allDirs);

		if (face.offsetX != 0) { //test YZ
			//sides; removed if have adjacent on side
			if (RockTypes.getTypeAtCoords(world, x, y, z+1) == rock)
				li.remove(new Integer(2));
			if (RockTypes.getTypeAtCoords(world, x, y, z-1) == rock)
				li.remove(new Integer(8));
			if (RockTypes.getTypeAtCoords(world, x, y+1, z) == rock)
				li.remove(new Integer(4));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z) == rock)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (RockTypes.getTypeAtCoords(world, x, y+1, z+1) == rock && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z-1) == rock && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (RockTypes.getTypeAtCoords(world, x, y+1, z-1) == rock && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z+1) == rock && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetY != 0) { //test XZ
			//sides; removed if have adjacent on side
			if (RockTypes.getTypeAtCoords(world, x, y, z+1) == rock)
				li.remove(new Integer(2));
			if (RockTypes.getTypeAtCoords(world, x, y, z-1) == rock)
				li.remove(new Integer(8));
			if (RockTypes.getTypeAtCoords(world, x+1, y, z) == rock)
				li.remove(new Integer(4));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z) == rock)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (RockTypes.getTypeAtCoords(world, x+1, y, z+1) == rock && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z-1) == rock && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (RockTypes.getTypeAtCoords(world, x+1, y, z-1) == rock && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z+1) == rock && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetZ != 0) { //test XY
			//sides; removed if have adjacent on side
			if (RockTypes.getTypeAtCoords(world, x, y+1, z) == rock)
				li.remove(new Integer(4));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z) == rock)
				li.remove(new Integer(6));
			if (RockTypes.getTypeAtCoords(world, x+1, y, z) == rock)
				li.remove(new Integer(2));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z) == rock)
				li.remove(new Integer(8));

			//Corners; only removed if have adjacent on side AND corner
			if (RockTypes.getTypeAtCoords(world, x+1, y+1, z) == rock && !li.contains(2) && !li.contains(4))
				li.remove(new Integer(1));
			if (RockTypes.getTypeAtCoords(world, x-1, y-1, z) == rock && !li.contains(8) && !li.contains(6))
				li.remove(new Integer(9));
			if (RockTypes.getTypeAtCoords(world, x+1, y-1, z) == rock && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
			if (RockTypes.getTypeAtCoords(world, x-1, y+1, z) == rock && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
		}
		return li;
	}

	public Icon getIconForEdge(int edge, RockTypes rock) {
		return edges[edge][rock.getBlockMetadata()];
	}

}
