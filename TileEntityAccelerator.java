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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.GeoStrata.API.AcceleratorBlacklist.BlacklistReason;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Rendering.EntitySparkleFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAccelerator extends TileEntityBase {

	public static final long MAX_LAG = 1000000L; //1ms

	public static final int MAX_TIER = 7;

	private static List<Class<? extends TileEntity>> blacklist = new ArrayList<Class<? extends TileEntity>>();

	static {
		addEntry("appeng.me.tile.TileController", ModList.APPENG, BlacklistReason.BUGS); //very likely to break
		addEntry("icbm.sentry.turret.block.TileTurret", ModList.ICBM, BlacklistReason.BUGS); //by request
		addEntry("bluedart.tile.decor.TileForceTorch", ModList.DARTCRAFT, BlacklistReason.CRASH); //StackOverflow
	}

	public static void addEntry(Class<? extends TileEntity> cl) {
		blacklist.add(cl);
	}

	private static void addEntry(String name, ModList mod, BlacklistReason r) {
		Class cl;
		if (!mod.isLoaded())
			return;
		try {
			cl = Class.forName(name);
			GeoStrata.logger.log("TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
			blacklist.add(cl);
		}
		catch (ClassNotFoundException e) {
			GeoStrata.logger.logError("Could not add "+name+" to the Accelerator blacklist: Class not found!");
		}
	}

	public static int getAccelFromTier(int tier) {
		return ReikaMathLibrary.intpow2(2, tier+1)-1;
	}

	public int getTier() {
		return this.getBlockMetadata();
	}

	@Override
	public int getTileEntityBlockID() {
		return GeoBlocks.ACCELERATOR.getBlockID();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (this.canAccelerate(te)) {
				long time = System.nanoTime();
				for (int k = 0; k < this.getAccelFromTier(meta) && !te.isInvalid() && System.nanoTime()-time < MAX_LAG; k++) {
					te.updateEntity();
				}
			}
		}

		if (world.isRemote) {
			this.spawnParticles(world, x, y, z, meta);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, int meta) {
		int p = meta > 0 ? (this.getTier()+1)/2 : rand.nextBoolean() ? 1 : 0;
		for (int i = 0; i < p; i++) {
			double dx = rand.nextDouble();
			double dy = rand.nextDouble();
			double dz = rand.nextDouble();
			double v = 0.125;
			double vx = v*(dx-0.5);
			double vy = v*(dy-0.5);
			double vz = v*(dz-0.5);
			//ReikaParticleHelper.FLAME.spawnAt(world, x-0.5+dx*2, y-0.5+dy*2, z-0.5+dz*2, v*(-1+dx*2), v*(-1+dy*2), v*(-1+dz*2));
			//Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, x+0.5, y+0.5, z+0.5, vx, vy, vz));
			//Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, x+0.5, y+0.5, z+0.5, vx, vy, vz));

			dx = x-2+dx*4;
			dy = y-2+dy*4;
			dz = z-2+dz*4;

			vx = -(dx-x)/8;
			vy = -(dy-y)/8;
			vz = -(dz-z)/8;
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, dx+0.5, dy+0.5, dz+0.5, vx, vy, vz));
		}
	}

	private boolean canAccelerate(TileEntity te) {
		if (te == null)
			return false;
		if (te instanceof TileEntityAccelerator)
			return false;
		if (!te.canUpdate() || te.isInvalid())
			return false;
		String s = te.getClass().getSimpleName();
		if (s.contains("conduit") || s.contains("wire") || s.contains("cable")) //almost always part of a network object
			return false;
		if (blacklist.contains(te.getClass()))
			return false;
		return true;
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
	public final boolean hasModel() {
		return true;
	}

}
