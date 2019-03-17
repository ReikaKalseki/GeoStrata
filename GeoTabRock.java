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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.GUI.SortedCreativeTab;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GeoTabRock extends SortedCreativeTab {

	public GeoTabRock(String tabID) {
		super(tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return RockTypes.GRANITE.getItem(RockShapes.SMOOTH);
	}

	@Override
	protected Comparator<ItemStack> getComparator() {
		return sorter;
	}

	private static final RockSorter sorter = new RockSorter();

	private static class RockSorter implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return this.getIndex(o1)-this.getIndex(o2);
		}

		private int getIndex(ItemStack o1) {
			Block b = Block.getBlockFromItem(o1.getItem());
			if (!(b instanceof RockBlock)) {
				return -1000000+1000*ReikaRegistryHelper.getRegistryForObject(b).ordinal()+o1.getItemDamage();
			}
			RockTypes r = RockTypes.getTypeFromID(b);
			RockShapes s = RockShapes.getShape(b, o1.getItemDamage());
			return r.ordinal()*1000+s.ordinal();
		}

	}

}
