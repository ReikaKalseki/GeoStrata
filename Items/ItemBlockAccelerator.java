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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.GeoStrata.TileEntityAccelerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockAccelerator extends ItemBlock {

	public ItemBlockAccelerator(int par1) {
		super(par1);
		hasSubtypes = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i <= TileEntityAccelerator.MAX_TIER; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag) {
			TileEntityAccelerator te = (TileEntityAccelerator)world.getBlockTileEntity(x, y, z);
			te.setBlockMetadata(stack.getItemDamage());
		}
		return flag;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		li.add("Accelerates time by "+(TileEntityAccelerator.getAccelFromTier(is.getItemDamage())+1)+"x for TileEntities adjacent to it.");
		if (DragonAPICore.isSinglePlayer() || ReikaPlayerAPI.isAdmin(ep)) {
			long max = TileEntityAccelerator.MAX_LAG/1000000;
			li.add(EnumChatFormatting.GOLD+"Admin Note:"+EnumChatFormatting.WHITE+" Will not cause more than "+max+"ms lag.");
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei)
	{
		ei.rotationYaw = 0;
		ei.age = 0;
		return false;
	}

}
