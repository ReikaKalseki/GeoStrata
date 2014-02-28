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

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockRock extends ItemBlock {

	public ItemBlockRock(int ID) {
		super(ID);
		hasSubtypes = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) //Adds the metadata blocks to the creative inventory
	{
		for (int i = 0; i < RockTypes.getTypesForID(par1); i++) {
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
	public final String getItemDisplayName(ItemStack is) {
		GeoBlocks b = GeoBlocks.getFromID(is.itemID);
		return b.getMultiValuedName(is.getItemDamage());
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		RockTypes rock = RockTypes.getTypeFromIDandMeta(is.itemID, is.getItemDamage());
		float blast = rock.blastResistance;
		float more = blast/Block.stone.blockResistance;
		li.add(String.format("Blast Resistance: %.1f (%.1fx stone)", blast, more));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int par2)
	{
		return 0xffffff;
	}

}
