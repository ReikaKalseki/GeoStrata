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

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Registry.GeoItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCaveCrystal extends CrystalBlock {

	public BlockCaveCrystal(int ID, Material mat) {
		super(ID, mat);
		this.setLightValue(0.65F);
	}

	@Override
	public final int idDropped(int id, Random r, int fortune) {
		return GeoItems.SHARD.getShiftedItemID();
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1+r.nextInt(6)+r.nextInt(3);
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			//icons[i] = ico.registerIcon("GeoStrata:"+"crystal_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
			//if (GeoOptions.OUTLINE.getState())
			//	icons[i] = ico.registerIcon("GeoStrata:crystal_outline");
			//else
			icons[i] = ico.registerIcon("GeoStrata:crystal");
		}
	}
}
