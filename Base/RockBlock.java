/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.GeoGen.GeoGen;
import Reika.GeoGen.Registry.RockTypes;

public abstract class RockBlock extends Block {

	public RockBlock(int ID, Material mat) {
		super(ID, mat);
		this.setCreativeTab(GeoGen.tabGeo);
	}

	@Override
	public final float getBlockHardness(World world, int x, int y, int z) {
		return RockTypes.getTypeAtCoords(world, x, y, z).getHardness();
	}

	@Override
	public final float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ) {
		//return this.getExplosionResistance(e);
		return RockTypes.getTypeAtCoords(world, x, y, z).getResistance()/5F; // /5F is in vanilla code
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
	public abstract int idDropped(int id, Random r, int fortune);

	@Override
	public abstract int damageDropped(int meta);

	@Override
	public abstract int quantityDropped(Random r);

}
