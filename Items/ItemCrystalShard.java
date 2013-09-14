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
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
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

	@Override
	public boolean isPotionIngredient()
	{
		return true;
	}

	@Override
	public String getPotionEffect(ItemStack is)
	{
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(is.getItemDamage());
		switch(dye) {
		case BLACK:
			return PotionHelper.fermentedSpiderEyeEffect;
		case BLUE:
			return PotionHelper.goldenCarrotEffect;
		case BROWN:
			return null;
		case CYAN: //water breathing
			return null;
		case GRAY: //slowness
			return null;
		case GREEN:
			return PotionHelper.spiderEyeEffect;
		case LIGHTBLUE:
			return PotionHelper.sugarEffect;
		case LIGHTGRAY: //weakness
			return null;
		case LIME: //jump boost
			return null;
		case MAGENTA:
			return PotionHelper.ghastTearEffect;
		case ORANGE:
			return PotionHelper.magmaCreamEffect;
		case PINK:
			return PotionHelper.blazePowderEffect;
		case PURPLE: //xp
			return null;
		case RED: //resistance
			return null;
		case WHITE:
			return "+4";
		case YELLOW: //haste
			return null;
		default:
			return null;
		}
	}
}
