/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Items;

import net.minecraft.client.renderer.texture.IconRegister;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.GeoItem;

public class ItemCrystalShard extends GeoItem {

	public ItemCrystalShard(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < 16; i++) {
			icons[i] = ico.registerIcon("GeoStrata:shard_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
			GeoStrata.logger.log("Adding "+ReikaDyeHelper.dyes[i].getName()+" shard icon "+icons[i].getIconName());
		}
	}

	@Override
	public int getNumberTypes() {
		return ReikaDyeHelper.dyes.length;
	}
}
