/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockSmooth extends RockBlock {

	public BlockSmooth(int ID, Material mat) {
		super(ID, mat);
	}

	@Override
	public final int idDropped(int id, Random r, int fortune) {
		return RockTypes.getTypeFromIDandMeta(blockID, 0).getCobbleID();
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
			icons[i] = ico.registerIcon("GeoStrata:"+RockTypes.getTypeFromIDandMeta(blockID, i).getName().toLowerCase());
			GeoStrata.logger.debug("Adding "+RockTypes.getTypeFromIDandMeta(blockID, i).getName()+" rock icon "+icons[i].getIconName());
		}
	}

	@Override
	public boolean isGenMineableReplaceable(World world, int x, int y, int z, int target)
	{
		return target == blockID || target == Block.stone.blockID;
	}
}
