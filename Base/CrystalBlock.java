/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class CrystalBlock extends Block {

	public CrystalBlock(int ID, Material mat) {
		super(ID, mat);
	}
}
