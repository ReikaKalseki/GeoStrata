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

import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Guardian.GuardianStoneManager;
import Reika.GeoStrata.Guardian.TileEntityGuardianStone;

public class BlockGuardianStone extends Block {

	private Icon outerIcon;
	private Icon innerIcon;
	private Icon middleIcon;

	public BlockGuardianStone(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setLightValue(1F);
		stepSound = new StepSound("stone", 1.0F, 0.5F);
	}

	@Override
	public int getRenderType() {
		stepSound = new StepSound("stone", 1.0F, 0.5F);
		return 0;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return this.hasTileEntity(meta) ? new TileEntityGuardianStone() : null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return outerIcon;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		outerIcon = ico.registerIcon("geostrata:guardian_outer");
		innerIcon = ico.registerIcon("geostrata:guardian_inner");
		middleIcon = ico.registerIcon("geostrata:guardian_middle");
	}

	public Icon getOuterIcon() {
		return outerIcon;
	}

	public Icon getInnerIcon() {
		return innerIcon;
	}

	public Icon getMiddleIcon() {
		return middleIcon;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int s) {
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int oldid, int oldmeta) {
		TileEntityGuardianStone te = (TileEntityGuardianStone)world.getBlockTileEntity(x, y, z);
		if (te != null) {
			GuardianStoneManager.instance.removeAreasForStone(te);
		}
		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

}
