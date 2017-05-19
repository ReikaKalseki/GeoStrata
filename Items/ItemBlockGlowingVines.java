/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.GeoStrata.Blocks.BlockGlowingVines.TileGlowingVines;


public class ItemBlockGlowingVines extends ItemBlock {

	public ItemBlockGlowingVines(Block b) {
		super(b);
	}

	@Override
	public boolean placeBlockAt(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		Block b = world.getBlock(x, y, z);
		if (b == field_150939_a) {
			return ((TileGlowingVines)world.getTileEntity(x, y, z)).addVine(ForgeDirection.VALID_DIRECTIONS[side].getOpposite());
		}
		else {
			if (super.placeBlockAt(is, ep, world, x, y, z, side, hitX, hitY, hitZ, meta)) {
				((TileGlowingVines)world.getTileEntity(x, y, z)).addVine(ForgeDirection.VALID_DIRECTIONS[side].getOpposite());
				return true;
			}
			return false;
		}
	}

}
