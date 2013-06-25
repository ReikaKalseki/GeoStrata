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
import Reika.GeoGen.Base.CrystalBlock;

public class BlockCrystalLamp extends CrystalBlock {

	public BlockCrystalLamp(int ID, Material mat) {
		super(ID, mat);
		this.setLightValue(1F);
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
}
