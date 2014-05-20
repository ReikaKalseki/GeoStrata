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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityAccelerator;

public class BlockAccelerator extends Block {

	private Icon top;
	private Icon side;
	private Icon bottom;
	private float w = 0.75F;

	public BlockAccelerator(int par1, Material mat) {
		super(par1, mat);
		this.setCreativeTab(GeoStrata.tabGeo);
		//this.setBlockBounds(0.5F-w/2, 0, 0.5F-w/2, 0.5F+w/2, 0.875F, 0.5F+w/2);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityAccelerator();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float par7, float par8, float par9)
	{
		ep.openGui(GeoStrata.instance, 0, world, x, y, z);
		return true;
	}

	@Override
	public void registerIcons(IconRegister ico)
	{
		top = ico.registerIcon("GeoStrata:accel_top");
		side = ico.registerIcon("GeoStrata:accel_side");
		bottom = ico.registerIcon("GeoStrata:accel_bottom");
	}

	@Override
	public Icon getIcon(int s, int meta) {
		if (s == 0)
			return bottom;
		if (s == 1)
			return top;
		return side;
	}


}
