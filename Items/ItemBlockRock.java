/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockRock extends ItemBlock {

	public ItemBlockRock(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List li) //Adds the metadata blocks to the creative inventory
	{
		Block b = Block.getBlockFromItem(par1);
		RockTypes type = RockTypes.getTypeFromID(b);
		for (int k = 0; k < 16; k++) {
			RockShapes shape = RockShapes.getShape(b, k);
			if (shape != null) {
				ItemStack item = type.getItem(shape);
				li.add(item);
			}
		}
	}

	@Override
	public final String getUnlocalizedName(ItemStack is) {
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + d;
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		RockTypes r = RockTypes.getTypeFromID(field_150939_a);
		RockShapes s = RockShapes.getShape(field_150939_a, is.getItemDamage());
		if (s == null || r == null) {
			return "ERROR";
		}
		return s.nameFirst ? r.getName()+" "+s.name : s.name+" "+r.getName();
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		RockTypes rock = RockTypes.getTypeFromID(field_150939_a);
		float blast = rock.blastResistance;
		float more = blast/Blocks.stone.blockResistance;
		li.add(String.format("Blast Resistance: %.1f (%.1fx stone)", blast, more));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int par2)
	{
		return 0xffffff;
	}

}
