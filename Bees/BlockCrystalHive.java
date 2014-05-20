/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Bees;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalHive extends Block {

	private final Icon[][] icons = new Icon[16][6];

	public BlockCrystalHive(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public boolean canDragonDestroy(World world, int x, int y, int z)
	{
		return false;
	}

	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z) {
		ArrayList<ItemStack> li = new ArrayList();

		return li;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ico)
	{
		for (int i = 0; i < 2; i++) {
			icons[0][i] = ico.registerIcon("geostrata:hives/crystal_top");
		}
		for (int i = 2; i < 6; i++) {
			icons[0][i] = ico.registerIcon("geostrata:hives/crystal_side");
		}

		for (int i = 0; i < 2; i++) {
			icons[1][i] = ico.registerIcon("geostrata:hives/pure_top");
		}
		for (int i = 2; i < 6; i++) {
			icons[1][i] = ico.registerIcon("geostrata:hives/pure_side");
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta][s];
	}

}
