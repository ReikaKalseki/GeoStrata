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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;

public class BlockVent extends Block {

	public BlockVent(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setTickRandomly(true);
		this.setResistance(Block.stone.blockResistance);
		this.setHardness(Block.stone.blockHardness);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate(world));
	}

	@Override
	public int tickRate(World world) {
		return 20;
	}

	@Override
	public int idDropped(int id, Random r, int fortune) {
		return 0;
	}

}
