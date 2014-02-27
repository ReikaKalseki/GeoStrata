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
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockShapedRock extends RockBlock {

	public final String shapeType;
	private final String iconLetter;

	public BlockShapedRock(int ID, Material mat, String type) {
		super(ID, mat);
		shapeType = type.toLowerCase();
		iconLetter = type.toLowerCase().substring(0, 1);
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
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < RockTypes.getTypesForID(blockID); i++) {
			RockTypes type = RockTypes.getTypeFromIDandMeta(blockID, i);
			icons[i] = ico.registerIcon("GeoStrata:"+type.getName().toLowerCase()+"_"+iconLetter);
			GeoStrata.logger.debug("Adding "+type.getName()+" "+shapeType+" icon "+icons[i].getIconName());
		}
	}

	public String getDisplayName() {
		return ReikaStringParser.capFirstChar(shapeType);
	}
}
