/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Base;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import Reika.GeoStrata.GeoStrata;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class GeoItem extends Item {

	protected final Icon[] icons = new Icon[this.getNumberTypes()];

	public GeoItem(int ID) {
		super(ID);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) //Adds the metadata blocks to the creative inventory
	{
		for (int i = 0; i < this.getNumberTypes(); i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + d;
	}

	public abstract int getNumberTypes();

	@Override
	public Icon getIconFromDamage(int dmg) {
		if (dmg >= icons.length)
			return null;
		return icons[dmg];
	}
}
