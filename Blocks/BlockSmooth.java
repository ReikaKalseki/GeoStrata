/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import Reika.GeoGen.Base.RockBlock;
import Reika.GeoGen.Registry.GeoBlocks;

public class BlockSmooth extends RockBlock {

	public BlockSmooth(int ID, Material mat) {
		super(ID, mat);
	}

	@Override
	public final int idDropped(int id, Random r, int fortune) {
		return GeoBlocks.COBBLE.getBlockID();
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1;
	}
}
