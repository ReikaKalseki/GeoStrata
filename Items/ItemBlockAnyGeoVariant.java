/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.GeoStrata.TileEntityGeoBlocks;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class ItemBlockAnyGeoVariant extends ItemBlock {

	public ItemBlockAnyGeoVariant(Block b) {
		super(b);
		this.setHasSubtypes(true);
	}

	public static RockTypes getRock(ItemStack is) {
		return RockTypes.rockList[is.getItemDamage()/RockShapes.shapeList.length];
	}

	public static RockShapes getShape(ItemStack is) {
		return RockShapes.shapeList[is.getItemDamage()%RockShapes.shapeList.length];
	}

	public static int getStack(RockTypes r, RockShapes s) {
		return r.ordinal()*RockShapes.shapeList.length+s.ordinal();
	}

	@Override
	public boolean placeBlockAt(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c, int meta)
	{
		boolean flag = super.placeBlockAt(is, ep, world, x, y, z, s, a, b, c, meta);
		if (flag) {
			TileEntityGeoBlocks te = (TileEntityGeoBlocks)world.getTileEntity(x, y, z);
			te.setTypes(this.getRock(is), this.getShape(is));
		}
		return flag;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			for (int k = 0; k < RockShapes.shapeList.length; k++) {
				int index = i*RockShapes.shapeList.length+k;
				li.add(new ItemStack(item, 1, index));
			}
		}
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		return field_150939_a instanceof BlockStairs ? "Rock Stairs" : "Rock Slab";
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		RockTypes r = this.getRock(is);
		RockShapes s = this.getShape(is);
		String s1 = s.name;
		String s2 = r.getName();
		if (s.nameFirst) {
			String sg = s1;
			s1 = s2;
			s2 = sg;
		}
		li.add(String.format("Type: %s %s", s1, s2));
	}

}
