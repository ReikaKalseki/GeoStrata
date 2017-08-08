/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.Rendering.ConnectedStoneRenderer;

public class BlockConnectedRock extends RockBlock {

	private final ArrayList<Integer> allDirs = new ArrayList();

	private IIcon[][] edges = new IIcon[10][RockTypes.rockList.length];
	private IIcon[][] sections = new IIcon[10][RockTypes.rockList.length];

	public BlockConnectedRock() {
		super();

		for (int i = 1; i < 10; i++) {
			allDirs.add(i);
		}
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(this);
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
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return RockTypes.getTypeFromID(this).getIcon();
	}

	@Override
	public final boolean canRenderInPass(int pass) {
		ConnectedStoneRenderer.renderPass = pass;
		return true;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes r = RockTypes.rockList[i];
			String name = r.getName();
			icons[r.ordinal()] = ico.registerIcon("GeoStrata:rock/"+name.toLowerCase(Locale.ENGLISH));
			GeoStrata.logger.debug("Adding "+name+" rock icon "+icons[r.ordinal()].getIconName());

			for (int k = 0; k < 10; k++) {
				String e = ReikaStringParser.stripSpaces("GeoStrata:"+RockShapes.getShape(this, 0).name.toLowerCase(Locale.ENGLISH)+"/"+k);
				String s = ReikaStringParser.stripSpaces("GeoStrata:"+RockShapes.getShape(this, 0).name.toLowerCase(Locale.ENGLISH)+"/"+k+"_sec");
				edges[k][r.ordinal()] = ico.registerIcon(e);
				sections[k][r.ordinal()] = ico.registerIcon(s);
			}
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
		Block id = iba.getBlock(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		//ReikaJavaLibrary.pConsole(dir+" % "+iba.getBlock(x, y, z)+":"+iba.getBlockMetadata(x, y, z)+" != "+id+":"+meta);
		return id != this || meta != iba.getBlockMetadata(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ);
	}

	public boolean rendersFrontTextureIndividually() {
		return RockShapes.getShape(this, 0) == RockShapes.CONNECTED2;
	}

	/** Returns the unconnected sides. Each integer represents one of 8 adjacent corners to a face, with the same
	 * numbering convention as is found on a calculator or computer number pad. */
	public ArrayList<Integer> getEdgesForFace(IBlockAccess world, int x, int y, int z, ForgeDirection face, RockTypes rock) {
		ArrayList<Integer> li = new ArrayList();
		li.addAll(allDirs);

		if (this.rendersFrontTextureIndividually())
			li.remove(new Integer(5));

		if (face.offsetX != 0) { //test YZ
			//sides; removed if have adjacent on side
			if (RockTypes.getTypeAtCoords(world, x, y, z+1) == rock && RockShapes.getShape(world, x, y, z+1) == RockShapes.getShape(this, 0))
				li.remove(new Integer(2));
			if (RockTypes.getTypeAtCoords(world, x, y, z-1) == rock && RockShapes.getShape(world, x, y, z-1) == RockShapes.getShape(this, 0))
				li.remove(new Integer(8));
			if (RockTypes.getTypeAtCoords(world, x, y+1, z) == rock && RockShapes.getShape(world, x, y+1, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(4));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z) == rock && RockShapes.getShape(world, x, y-1, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (RockTypes.getTypeAtCoords(world, x, y+1, z+1) == rock && RockShapes.getShape(world, x, y+1, z+1) == RockShapes.getShape(this, 0) && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z-1) == rock && RockShapes.getShape(world, x, y-1, z-1) == RockShapes.getShape(this, 0) && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (RockTypes.getTypeAtCoords(world, x, y+1, z-1) == rock && RockShapes.getShape(world, x, y+1, z-1) == RockShapes.getShape(this, 0) && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z+1) == rock && RockShapes.getShape(world, x, y-1, z+1) == RockShapes.getShape(this, 0) && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetY != 0) { //test XZ
			//sides; removed if have adjacent on side
			if (RockTypes.getTypeAtCoords(world, x, y, z+1) == rock && RockShapes.getShape(world, x, y, z+1) == RockShapes.getShape(this, 0))
				li.remove(new Integer(2));
			if (RockTypes.getTypeAtCoords(world, x, y, z-1) == rock && RockShapes.getShape(world, x, y, z-1) == RockShapes.getShape(this, 0))
				li.remove(new Integer(8));
			if (RockTypes.getTypeAtCoords(world, x+1, y, z) == rock && RockShapes.getShape(world, x+1, y, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(4));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z) == rock && RockShapes.getShape(world, x-1, y, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (RockTypes.getTypeAtCoords(world, x+1, y, z+1) == rock && RockShapes.getShape(world, x+1, y, z+1) == RockShapes.getShape(this, 0) && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z-1) == rock && RockShapes.getShape(world, x-1, y, z-1) == RockShapes.getShape(this, 0) && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (RockTypes.getTypeAtCoords(world, x+1, y, z-1) == rock && RockShapes.getShape(world, x+1, y, z-1) == RockShapes.getShape(this, 0) && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z+1) == rock && RockShapes.getShape(world, x-1, y, z+1) == RockShapes.getShape(this, 0) && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetZ != 0) { //test XY
			//sides; removed if have adjacent on side
			if (RockTypes.getTypeAtCoords(world, x, y+1, z) == rock && RockShapes.getShape(world, x, y+1, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(4));
			if (RockTypes.getTypeAtCoords(world, x, y-1, z) == rock && RockShapes.getShape(world, x, y-1, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(6));
			if (RockTypes.getTypeAtCoords(world, x+1, y, z) == rock && RockShapes.getShape(world, x+1, y, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(2));
			if (RockTypes.getTypeAtCoords(world, x-1, y, z) == rock && RockShapes.getShape(world, x-1, y, z) == RockShapes.getShape(this, 0))
				li.remove(new Integer(8));

			//Corners; only removed if have adjacent on side AND corner
			if (RockTypes.getTypeAtCoords(world, x+1, y+1, z) == rock && RockShapes.getShape(world, x+1, y+1, z) == RockShapes.getShape(this, 0) && !li.contains(2) && !li.contains(4))
				li.remove(new Integer(1));
			if (RockTypes.getTypeAtCoords(world, x-1, y-1, z) == rock && RockShapes.getShape(world, x-1, y-1, z) == RockShapes.getShape(this, 0) && !li.contains(8) && !li.contains(6))
				li.remove(new Integer(9));
			if (RockTypes.getTypeAtCoords(world, x+1, y-1, z) == rock && RockShapes.getShape(world, x+1, y-1, z) == RockShapes.getShape(this, 0) && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
			if (RockTypes.getTypeAtCoords(world, x-1, y+1, z) == rock && RockShapes.getShape(world, x-1, y+1, z) == RockShapes.getShape(this, 0) && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
		}
		return li;
	}

	public ArrayList<Integer> getSectionsForTexture(IBlockAccess world, int x, int y, int z, ForgeDirection face, RockTypes rock) {
		ArrayList<Integer> li = new ArrayList();
		li.addAll(allDirs);
		li.removeAll(this.getEdgesForFace(world, x, y, z, face, rock));
		return li;
	}

	public boolean hasCentralTexture(RockTypes rock) {
		return this == RockShapes.CONNECTED2.getBlock(rock);
	}

	public IIcon getIconForEdge(int edge, RockTypes rock) {
		return edges[edge][rock.ordinal()];
	}

	public IIcon getSectionForTexture(int sec, RockTypes rock) {
		return sections[sec][rock.ordinal()];
	}

}
