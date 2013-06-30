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
import net.minecraft.util.Icon;
import Reika.DragonAPI.Libraries.ReikaDyeHelper;
import Reika.GeoStrata.Base.CrystalBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalLamp extends CrystalBlock {

	public static Icon[] icons = new Icon[ReikaDyeHelper.dyes.length];

	public BlockCrystalLamp(int ID, Material mat) {
		super(ID, mat);
		this.setLightValue(1F);
	}

	@Override
	public final int idDropped(int id, Random r, int fortune) {
		return blockID;
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("GeoStrata:"+"lamp_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta];
	}
}
