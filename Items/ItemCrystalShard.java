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

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.CrystalPotionController;
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
			GeoStrata.logger.debug("Adding "+ReikaDyeHelper.dyes[i].colorName+" shard icon "+icons[i].getIconName());
		}
	}

	@Override
	public int getNumberTypes() {
		return ReikaDyeHelper.dyes.length;
	}

	@Override
	public boolean isPotionIngredient()
	{
		return false;
	}

	@Override
	public String getPotionEffect()
	{
		return "";
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
			return PotionHelper.redstoneEffect;
		case CYAN: //water breathing
			return "";
		case GRAY: //slowness
			return PotionHelper.sugarEffect;
		case GREEN:
			return PotionHelper.spiderEyeEffect;
		case LIGHTBLUE:
			return PotionHelper.sugarEffect;
		case LIGHTGRAY: //weakness
			return PotionHelper.blazePowderEffect;
		case LIME: //jump boost
			return "";
		case MAGENTA:
			return PotionHelper.ghastTearEffect;
		case ORANGE:
			return PotionHelper.magmaCreamEffect;
		case PINK:
			return PotionHelper.blazePowderEffect;
		case PURPLE: //xp -> level2?
			return PotionHelper.glowstoneEffect;
		case RED: //resistance
			return "";
		case WHITE:
			return PotionHelper.goldenCarrotEffect;
		case YELLOW: //haste
			return "";
		default:
			return "";
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Good for ");
		ReikaDyeHelper color = ReikaDyeHelper.getColorFromDamage(is.getItemDamage());
		sb.append(CrystalPotionController.getPotionName(color));
		sb.append(" Potions");
		li.add(sb.toString());
	}
}
