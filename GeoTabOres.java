/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.util.Comparator;

import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.GUI.SortedCreativeTab;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.Blocks.BlockOreTile;
import Reika.GeoStrata.Registry.RockTypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GeoTabOres extends SortedCreativeTab {

	public GeoTabOres(String tabID) {
		super(tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		int list = ReikaOreHelper.oreList.length+ModOreList.oreList.length;
		int meta = BlockOreTile.getMetadataByTypes(RockTypes.BASALT, ReikaOreHelper.REDSTONE);
		return null;//new ItemStack(GeoBlocks.ORETILE.getBlockInstance(), 1, meta);
	}

	@Override
	protected Comparator<ItemStack> getComparator() {
		return sorter;
	}

	private static final RockOreSorter sorter = new RockOreSorter();

	private static class RockOreSorter implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return o1.getItemDamage()-o2.getItemDamage();
		}

	}

}
