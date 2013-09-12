/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.RockTypes;

public abstract class RockBlock extends Block {

	protected Icon[] icons = new Icon[RockTypes.rockList.length];

	public RockBlock(int ID, Material mat) {
		super(ID, mat);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public final float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		ItemStack is = ep.getCurrentEquippedItem();
		int meta = world.getBlockMetadata(x, y, z);
		if (!this.canHarvestBlock(ep, meta))
			return 0.1F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness;
		if (is == null)
			return 0.4F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness;
		return 0.1875F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness*is.getItem().getStrVsBlock(is, this);
	}

	@Override
	public final float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ) {
		return RockTypes.getTypeAtCoords(world, x, y, z).blastResistance/5F; // /5F is in vanilla code
	}

	@Override
	protected final boolean canSilkHarvest() {
		return true;
	}

	@Override
	public final boolean canHarvestBlock(EntityPlayer player, int meta) {
		if (player.capabilities.isCreativeMode)
			return false;
		return RockTypes.getTypeFromMetadata(meta).isHarvestable(player.getCurrentEquippedItem());
	}

	@Override
	public final Icon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public abstract int idDropped(int id, Random r, int fortune);

	@Override
	public abstract int damageDropped(int meta);

	@Override
	public abstract int quantityDropped(Random r);

}
