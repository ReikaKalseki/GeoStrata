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

import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.DecoBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRockDeco extends Block {

	private IIcon[] icons = new IIcon[DecoBlocks.list.length];

	public BlockRockDeco(Material par2Material) {
		super(par2Material);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < DecoBlocks.list.length; i++) {
			icons[i] = ico.registerIcon("GeoStrata:"+DecoBlocks.list[i].getTex());
			GeoStrata.logger.debug("Adding "+DecoBlocks.list[i].getName()+" decorative block icon "+icons[i].getIconName());
		}
	}

	@Override
	public IIcon getIcon(int s, int m) {
		return icons[m];
	}

	@Override
	public int damageDropped(int dmg) {
		return dmg;
	}

	@Override
	public final float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		ItemStack is = ep.getCurrentEquippedItem();
		int meta = world.getBlockMetadata(x, y, z);
		if (!this.canHarvestBlock(ep, meta))
			return 0.1F/DecoBlocks.getTypeAtCoords(world, x, y, z).getHardness();
		if (is == null)
			return 0.4F/DecoBlocks.getTypeAtCoords(world, x, y, z).getHardness();
		return 0.1F/DecoBlocks.getTypeAtCoords(world, x, y, z).getHardness()*is.getItem().func_150893_a(is, this);
	}

	@Override
	public final float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ) {
		return DecoBlocks.getTypeAtCoords(world, x, y, z).getResistance()/5F; // /5F is in vanilla code
	}

	@Override
	public final boolean canHarvestBlock(EntityPlayer player, int meta) {
		if (player.capabilities.isCreativeMode)
			return false;
		return DecoBlocks.getTypeFromMetadata(meta).isHarvestable(player.getCurrentEquippedItem());
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int side)
	{
		return iba.getBlockMetadata(x, y, z) == DecoBlocks.REDBRICKS.ordinal() ? 15 : 0;
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z)
	{
		return iba.getBlockMetadata(x, y, z) == DecoBlocks.GLOWBRICKS.ordinal() ? 15 : 0;
	}

}
