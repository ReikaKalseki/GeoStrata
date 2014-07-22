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

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockShapedRock extends RockBlock {

	public final String shapeType;

	//private static final Icon[][] textures = new Icon[RockTypes.rockList.length][RockShapes.shapeList.length];

	public BlockShapedRock(int ID, Material mat, String type) {
		super(ID, mat);
		shapeType = type.toLowerCase();
	}

	@Override
	public final int idDropped(int id, Random r, int fortune) {
		return blockID;
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
	public int getRenderType() {
		return 0;//GeoStrata.proxy.shapedRender;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < RockTypes.getTypesForID(blockID); i++) {
			RockTypes type = RockTypes.getTypeFromIDandMeta(blockID, i);
			int a = RockShapes.getShape(this).ordinal();
			int b = type.ordinal();
			icons[i] = ico.registerIcon("GeoStrata:shaped/unstitched/tile"+a+"_"+b);
			GeoStrata.logger.debug("Adding "+type.getName()+" "+shapeType+" icon "+icons[i].getIconName());
		}
		/*
		for (int i = 0; i < blends.length; i++) {
			blendicons[i] = ico.registerIcon("GeoStrata:shaped/"+blends[i]+"/"+shapeType+"base");
		}*/
		/*
		int cols = RockShapes.shapeList.length;
		int rows = RockTypes.rockList.length;
		ReikaImageLoader.unstitchIconsFromSheet(textures, ico, "geostrata:shaped/sheet", cols, rows);
		 */
	}
	/*
	@Override
	public Icon getIcon(int s, int meta) {
		RockTypes rock = RockTypes.getTypeFromIDandMeta(blockID, meta);
		RockShapes shape = RockShapes.getShape(this);
		return textures[rock.ordinal()][shape.ordinal()];
	}*/

	public String getDisplayName() {
		return ReikaStringParser.capFirstChar(shapeType);
	}

	@Override
	public void whenInBeam(World world, int x, int y, int z, long power, int range) {
		if (shapeType.equals("cobblestone") || shapeType.equals("smooth"))
			super.whenInBeam(world, x, y, z, power, range);
	}
}
