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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import Reika.GeoStrata.Base.GeoItem;

public class ItemCluster extends GeoItem {

	private final Icon[] icons = new Icon[this.getNumberTypes()];

	public ItemCluster(int ID) {
		super(ID);
		hasSubtypes = true;
	}

	@Override
	public int getNumberTypes() {
		return 8;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < this.getNumberTypes(); i++) {
			icons[i] = ico.registerIcon("geostrata:cluster_"+i);
		}
	}

	@Override
	public Icon getIconFromDamage(int dmg) {
		return icons[dmg];
	}

	@Override
	public String getItemDisplayName(ItemStack is) {
		if (is.getItemDamage() < 4)
			return "Crystal Group";
		if (is.getItemDamage() == 6)
			return "Crystal Core";
		return is.getItemDamage() == 7 ? "Crystal Star" : "Crystal Cluster";
	}

}
