/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import Reika.GeoGen.Base.RockBlock;
import Reika.GeoGen.Registry.RockTypes;

public class BlockRockCobble extends RockBlock {

	public BlockRockCobble(int ID, Material mat) {
		super(ID, mat);
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

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			icons[i] = ico.registerIcon("GeoGen:"+RockTypes.rockList[i].getName().toLowerCase()+"_c");
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta];
	}
}
