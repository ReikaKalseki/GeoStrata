/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.DecoBlocks;

public class BlockRockDeco extends Block {

	private Icon[] icons = new Icon[DecoBlocks.list.length];

	public BlockRockDeco(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < DecoBlocks.list.length; i++) {
			icons[i] = ico.registerIcon("GeoStrata:"+DecoBlocks.list[i].getTex());
			GeoStrata.logger.log("Adding "+DecoBlocks.list[i].getName()+" decorative block icon "+icons[i].getIconName());
		}
	}

	@Override
	public Icon getIcon(int s, int m) {
		return icons[m];
	}

	@Override
	public final float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		ItemStack is = ep.getCurrentEquippedItem();
		int meta = world.getBlockMetadata(x, y, z);
		if (!this.canHarvestBlock(ep, meta))
			return 0.1F/DecoBlocks.getTypeAtCoords(world, x, y, z).getHardness();
		if (is == null)
			return 0.4F/DecoBlocks.getTypeAtCoords(world, x, y, z).getHardness();
		return 0.1F/DecoBlocks.getTypeAtCoords(world, x, y, z).getHardness()*is.getItem().getStrVsBlock(is, this);
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

}
