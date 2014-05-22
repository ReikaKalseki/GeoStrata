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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityAccelerator;
import Reika.GeoStrata.Registry.GeoBlocks;

public class BlockAccelerator extends Block {

	private Icon top;
	private Icon side;
	private Icon bottom;
	private float w = 0.75F;

	private Icon sparkle;
	private Icon glow;

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
	public int getRenderType() {
		return -1;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null)
			li.add(new ItemStack(GeoBlocks.ACCELERATOR.getBlockID(), 1, te.getBlockMetadata()));
		return li;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		double r = 0.3125;
		return ReikaAABBHelper.getBlockAABB(x, y, z).contract(r, r, r);
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		if (!player.capabilities.isCreativeMode)
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlock(x, y, z, 0);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		return new ItemStack(GeoBlocks.ACCELERATOR.getBlockID(), 1, te.getBlockMetadata());
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float par7, float par8, float par9)
	{
		return false;
	}

	@Override
	public void registerIcons(IconRegister ico)
	{
		top = ico.registerIcon("GeoStrata:accel_top");
		side = ico.registerIcon("GeoStrata:accel_side");
		bottom = ico.registerIcon("GeoStrata:accel_bottom");

		sparkle = ico.registerIcon("GeoStrata:sparkle-particle");
		glow = ico.registerIcon("GeoStrata:glowsections");
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return sparkle;
	}

	public Icon getGlowIcon() {
		return glow;
	}

	public Icon getSparkleIcon() {
		return sparkle;
	}


}
