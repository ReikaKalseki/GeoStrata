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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import Reika.GeoStrata.GeoStrata;

public class BlockRockOre extends Block {

	public BlockRockOre(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public int getRenderType() {
		return GeoStrata.proxy.oreRender;
	}

}
