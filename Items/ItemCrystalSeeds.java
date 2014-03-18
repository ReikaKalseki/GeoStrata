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
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.GeoItem;
import Reika.GeoStrata.Registry.GeoBlocks;

public class ItemCrystalSeeds extends GeoItem {

	public ItemCrystalSeeds(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < 16; i++) {
			icons[i] = ico.registerIcon("GeoStrata:seed_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
		}
	}

	@Override
	public int getNumberTypes() {
		return ReikaDyeHelper.dyes.length;
	}

	@Override
	public String getItemDisplayName(ItemStack is) {
		return ReikaDyeHelper.dyes[is.getItemDamage()].colorName+" Crystal Seeds";
	}

	@Override
	public boolean onItemUse(ItemStack items, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world.getBlockId(x, y, z))) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
		}
		int idbelow = world.getBlockId(x, y-1, z);
		if ((!ReikaWorldHelper.softBlocks(world.getBlockId(x, y, z))) || !ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		if (!player.canPlayerEdit(x, y, z, 0, items))
			return false;
		else
		{
			if (!player.capabilities.isCreativeMode)
				--items.stackSize;
			world.setBlock(x, y, z, GeoBlocks.PLANT.getBlockID(), items.getItemDamage(), 3);
			ReikaSoundHelper.playPlaceSound(world, x, y, z, Block.grass);
			return true;
		}
	}

}
