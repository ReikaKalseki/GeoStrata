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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import Reika.DragonAPI.Interfaces.PlayerSpecificTrade;


public class LTDTrade extends MerchantRecipe implements PlayerSpecificTrade {

	public LTDTrade() {
		super(new ItemStack(GeoStrata.lowTempDiamonds), new ItemStack(Items.emerald, 12, 0));
	}

	@Override
	public void incrementToolUses() {
		//No-op to prevent expiry
	}

	@Override
	public boolean isValid(EntityPlayer ep) {
		return true;
	}

	@Override
	public boolean hasSameIDsAs(MerchantRecipe mr) {
		return mr instanceof LTDTrade;
	}

	@Override
	public boolean hasSameItemsAs(MerchantRecipe mr) {
		return mr instanceof LTDTrade;
	}

}
