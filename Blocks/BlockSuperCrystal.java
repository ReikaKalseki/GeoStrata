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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Registry.GeoOptions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSuperCrystal extends CrystalBlock {

	public BlockSuperCrystal(int ID, Material mat) {
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

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("GeoStrata:"+"lamp_"+ReikaDyeHelper.dyes[i].name().toLowerCase());
		}
	}

	@Override
	public boolean shouldMakeNoise() {
		return GeoOptions.NOISE.getState();
	}

	@Override
	public boolean shouldGiveEffects() {
		return true;
	}

	@Override
	public int getRange() {
		return 12;
	}

	@Override
	public int getDuration() {
		return 6000;
	}

	@Override
	public boolean renderBase() {
		return true;
	}

	@Override
	public Block getBaseBlock(ForgeDirection side) {
		return Block.obsidian;
	}

	@Override
	public int getPotionLevel() {
		return 2;
	}

}
