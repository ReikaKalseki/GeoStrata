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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.GeoStrata.TileAccelerator;
import Reika.GeoStrata.Base.GeoItem;

public class ItemTimeAccelerator extends GeoItem {

	public ItemTimeAccelerator(int par1) {
		super(par1);
		hasSubtypes = true;
		maxStackSize = 1;
	}

	@Override
	public int getNumberTypes() {
		return 8;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null) {
			if (te.canUpdate()) {
				return TileAccelerator.instance.addTileEntity(te, is.getItemDamage());
			}
		}
		return false;
	}

}
