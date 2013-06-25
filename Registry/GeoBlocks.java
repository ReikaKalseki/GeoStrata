/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import Reika.GeoGen.Base.CrystalBlock;
import Reika.GeoGen.Blocks.BlockCaveCrystal;
import Reika.GeoGen.Blocks.BlockCrystalLamp;
import Reika.GeoGen.Blocks.BlockRockBrick;
import Reika.GeoGen.Blocks.BlockRockCobble;
import Reika.GeoGen.Blocks.BlockSmooth;

public enum GeoBlocks {

	SMOOTH(BlockSmooth.class, "Smooth Rock"),
	COBBLE(BlockRockCobble.class, "Rock Cobble"),
	BRICK(BlockRockBrick.class, "Rock Brick"),
	CRYSTAL(BlockCaveCrystal.class, "Cave Crystal"), //Comes in all dye colors
	LAMP(BlockCrystalLamp.class, "Crystal Lamp");

	private Class blockClass;
	private String blockName;

	private GeoBlocks(Class <? extends Block> cl, String n) {
		blockClass = cl;
		blockName = n;
	}

	public int getBlockID() {
		return 0;
	}

	public Material getBlockMaterial() {
		if (CrystalBlock.class.isAssignableFrom(blockClass))
			return Material.glass;
		return Material.rock;
	}

}
