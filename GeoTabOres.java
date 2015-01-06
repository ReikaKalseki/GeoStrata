/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GeoTabOres extends CreativeTabs {

	public GeoTabOres(int position, String tabID) {
		super(position, tabID); //The constructor for your tab
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		int list = ReikaOreHelper.oreList.length+ModOreList.oreList.length;
		int meta = RockTypes.BASALT.ordinal()*list+ReikaOreHelper.REDSTONE.ordinal();
		return new ItemStack(GeoBlocks.ORETILE.getBlockInstance(), 1, meta);
	}

	@Override
	public String getTranslatedTabLabel() {
		return GeoStrata.MOD_NAME+" Ores";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return null;
	}

}
