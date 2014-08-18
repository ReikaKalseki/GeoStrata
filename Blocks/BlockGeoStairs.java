/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityGeoBlocks;
import Reika.GeoStrata.Registry.RockTypes;

import java.awt.Color;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGeoStairs extends BlockStairs {

	public BlockGeoStairs(Material mat) {
		super(Blocks.stone, 0);
		this.setCreativeTab(GeoStrata.tabGeoStairs);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return Blocks.stone.getIcon(0, 0);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te instanceof TileEntityGeoBlocks ? ((TileEntityGeoBlocks)te).getIcon() : Blocks.stone.getIcon(0, 0);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityGeoBlocks();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		TileEntityGeoBlocks te = (TileEntityGeoBlocks)iba.getTileEntity(x, y, z);
		RockTypes rock = te.getRockType();
		//ReikaJavaLibrary.pConsole(rock);
		if (rock == RockTypes.OPAL) {
			int sc = 48;
			float hue1 = (float)(ReikaMathLibrary.py3d(x, y*4, z+x)%sc)/sc;
			//float hue2 = (float)(Math.cos(x/24D)+Math.sin(z/24D))+(y%360)*0.05F;
			return Color.HSBtoRGB(hue1, 0.4F, 1F);
		}
		else {
			return super.colorMultiplier(iba, x, y, z);
		}
	}
}
