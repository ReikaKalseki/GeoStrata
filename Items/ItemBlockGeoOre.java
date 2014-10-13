/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Blocks.BlockOreTile;
import Reika.GeoStrata.Registry.RockTypes;

public class ItemBlockGeoOre extends ItemBlock {

	public ItemBlockGeoOre(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag) {
			TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
			ItemStack is = BlockOreTile.getOreByItemBlock(stack);
			Block b = Block.getBlockFromItem(is.getItem());
			RockTypes rock = BlockOreTile.getRockFromItem(stack.getItemDamage());
			te.initialize(rock, b, is.getItemDamage());
		}
		return flag;
	}

	@Override
	public int getMetadata(int meta) {
		return 0;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ItemStack is2 = BlockOreTile.getOreByItemBlock(is);
		Block b = Block.getBlockFromItem(is2.getItem());
		int meta = is2.getItemDamage();
		return is2.getDisplayName()+" "+BlockOreTile.getRockFromItem(is.getItemDamage()).getName();
	}

}
