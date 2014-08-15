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

import Reika.GeoStrata.GeoStrata;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockVent extends Block {

	public BlockVent(Material par2Material) {
		super(par2Material);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setTickRandomly(true);
		//this.setResistance(Blocks.stone.blockResistance);
		//this.setHardness(Blocks.stone.blockHardness);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

	@Override
	public int tickRate(World world) {
		return 20;
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return Blocks.stone.getItemDropped(id, r, fortune);
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}


}