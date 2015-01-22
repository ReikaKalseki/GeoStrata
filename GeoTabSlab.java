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

import net.minecraft.item.ItemStack;
import Reika.GeoStrata.Base.RockWrapperSortedTab;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GeoTabSlab extends RockWrapperSortedTab {

	public GeoTabSlab(String tabID) {
		super(tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return RockTypes.MARBLE.getSlab(RockShapes.ENGRAVED);
	}

}
