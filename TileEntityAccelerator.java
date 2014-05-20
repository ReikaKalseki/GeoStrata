/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.GeoStrata.Registry.GeoBlocks;

public class TileEntityAccelerator extends TileEntityBase {

	private static final long MAX_LAG = 1000000L; //1ms
	private int tier;

	@Override
	public int getTileEntityBlockID() {
		return GeoBlocks.ACCELERATOR.getBlockID();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te != null && te.canUpdate() && !te.isInvalid()) {
				long time = System.nanoTime();
				for (int k = 0; k < tier && !te.isInvalid() && System.nanoTime()-time < MAX_LAG; k++) {
					te.updateEntity();
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	protected String getTEName() {
		return "TileEntity Accelerator";
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	@Override
	protected void onTileCreationInWorld(World world, int x, int y, int z) {
		tier = world.getBlockMetadata(x, y, z);
	}

}
