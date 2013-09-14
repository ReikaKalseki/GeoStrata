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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.CrystalPotionController;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCrystalPotion extends ItemPotion {

	public ItemCrystalPotion(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public List getEffects(ItemStack is)
	{
		PotionEffect pot = CrystalPotionController.getEffectFromColor(ReikaDyeHelper.getColorFromDamage(this.getUsableDamage(is)), 3600, 0);
		if (pot == null)
			return new ArrayList();
		if (ReikaPotionHelper.isSplashPotion(is.getItemDamage()))
			pot.setSplashPotion(true);
		if (ReikaPotionHelper.isExtended(is.getItemDamage()))
			pot = new PotionEffect(pot.getPotionID(), 9600, pot.getAmplifier());
		if (ReikaPotionHelper.isBoosted(is.getItemDamage()))
			pot = new PotionEffect(pot.getPotionID(), pot.getDuration(), 1);
		return ReikaJavaLibrary.makeListFrom(pot);
	}

	@Override
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ReikaDyeHelper color = ReikaDyeHelper.dyes[i];
			if (CrystalPotionController.requiresCustomPotion(color)) {

				li.add(new ItemStack(ID, 1, i));
				li.add(new ItemStack(ID, 1, ReikaPotionHelper.EXTENDED_BIT+i));
				li.add(new ItemStack(ID, 1, ReikaPotionHelper.BOOST_BIT+i));

				//li.add(new ItemStack(ID, 1, ReikaPotionHelper.SPLASH_BIT+i));
				//li.add(new ItemStack(ID, 1, ReikaPotionHelper.EXTENDED_BIT+ReikaPotionHelper.SPLASH_BIT+i));
				//li.add(new ItemStack(ID, 1, ReikaPotionHelper.BOOST_BIT+ReikaPotionHelper.SPLASH_BIT+i));

				//li.add(new ItemStack(ID, 1, ReikaPotionHelper.EXTENDED_BIT+ReikaPotionHelper.BOOST_BIT+i));
			}
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose)
	{
		int dmg = this.getUsableDamage(is);
		ReikaDyeHelper color = ReikaDyeHelper.getColorFromDamage(this.getUsableDamage(is));
		StringBuilder name = new StringBuilder();
		name.append(CrystalPotionController.getPotionName(color));
		if (ReikaPotionHelper.isBoosted(is.getItemDamage()))
			name.append(" II");
		if (ReikaPotionHelper.isExtended(is.getItemDamage()))
			name.append(" 8:00");
		else
			name.append(" 3:00");
		li.add(name.toString());
	}

	@Override
	public String getItemDisplayName(ItemStack is)
	{
		return GeoItems.POTION.getMultiValuedName(this.getUsableDamage(is));
	}

	protected static int getUsableDamage(ItemStack is) {
		return is.getItemDamage()&15;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromDamage(int dmg)
	{
		return ReikaDyeHelper.getColorFromDamage(dmg&15).getJavaColor().getRGB();
	}

}
