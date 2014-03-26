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

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Base.GeoItem;
import Reika.GeoStrata.Registry.GeoItems;

public class ItemPendant extends GeoItem {

	public ItemPendant(int ID) {
		super(ID);
		hasSubtypes = true;
		maxStackSize = 1;
	}

	@Override
	public int getNumberTypes() {
		return ReikaDyeHelper.dyes.length;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		String s = this.isEnhanced() ? "enhanced_" : "";
		for (int i = 0; i < this.getNumberTypes(); i++) {
			icons[i] = ico.registerIcon("geostrata:pendant_"+s+ReikaDyeHelper.dyes[i].name().toLowerCase());
		}
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		int level = this.isEnhanced() ? 2 : 0;
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) e;
			ReikaDyeHelper dye = ReikaDyeHelper.getColorFromItem(is);
			if (dye != ReikaDyeHelper.PURPLE) {
				CrystalBlock.applyEffectFromColor(3, level, ep, dye);
			}
		}
	}

	public boolean isEnhanced() {
		return itemID == GeoItems.PENDANT3.getShiftedItemID();
	}

	@Override
	public Icon getIconFromDamage(int dmg) {
		return icons[dmg];
	}

	@Override
	public String getItemDisplayName(ItemStack is) {
		String s = this.isEnhanced() ? "Enhanced " : "";
		return s+ReikaDyeHelper.getColorFromItem(is).colorName+" "+"Crystal Pendant";
	}

}
