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
import Reika.GeoStrata.Blocks.BlockVent.VentType;
import Reika.GeoStrata.Registry.GeoBlocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GeoTab extends SortedCreativeTab {

	public GeoTab(String tabID) {
		super(tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return GeoBlocks.VENT.getStackOfMetadata(VentType.LAVA.ordinal());//GeoBlocks.GLOWCRYS.getStackOfMetadata(0);
	}

	@Override
	protected Comparator<ItemStack> getComparator() {
		return null;
	}

}
