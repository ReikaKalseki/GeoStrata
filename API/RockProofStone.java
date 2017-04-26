/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.API;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/** Implement this on your block to prevent GeoStrata rock from generating through it. */
public interface RockProofStone {

	public boolean blockRockGeneration(World world, int x, int y, int z, Block b, int meta);

}
