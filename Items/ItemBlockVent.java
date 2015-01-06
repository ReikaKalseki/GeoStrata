/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.GeoStrata.Blocks.BlockVent.VentType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockVent extends ItemBlock {

	public ItemBlockVent(Block b) {
		super(b);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) //Adds the metadata blocks to the creative inventory
	{
		for (int i = 0; i < VentType.list.length; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	@Override
	public final String getUnlocalizedName(ItemStack is) {
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + d;
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		String n = VentType.list[is.getItemDamage()].name();
		return ReikaStringParser.capFirstChar(n)+" Vent";
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

}
