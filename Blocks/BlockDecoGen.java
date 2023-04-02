/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import Reika.DragonAPI.Interfaces.Block.Submergeable;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoISBRH;


public class BlockDecoGen extends Block implements Submergeable {

	private final IIcon[] icons = new IIcon[Types.list.length];

	public BlockDecoGen(Material mat) {
		super(mat);

		this.setHardness(2);
		this.setResistance(20);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	public static enum Types {
		CRYSTALSPIKE("Crystal Spike"),
		ICICLE("Icicle");

		public final String name;

		public static final Types[] list = values();

		private Types(String s) {
			name = s;
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < Types.list.length; i++) {
			icons[i] = ico.registerIcon("geostrata:deco/"+i);
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return GeoISBRH.deco.getRenderID();
	}

	@Override
	public boolean isSubmergeable(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlockMetadata(x, y, z) == 0;
	}

	@Override
	public boolean renderLiquid(int meta) {
		return meta == 0;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		GeoISBRH.deco.setRenderPass(pass);
		return pass <= 1;
	}

}
