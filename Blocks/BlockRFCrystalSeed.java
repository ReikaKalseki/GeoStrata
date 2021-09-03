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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.CurvedTrajectory;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.CurvedTrajectory.InitialAngleProvider;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.CurvedTrajectory.TrailShape;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;


public class BlockRFCrystalSeed extends BlockRFCrystal {

	public BlockRFCrystalSeed(Material mat) {
		super(mat);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileRFCrystal();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		if (this.hasTileEntity(oldmeta))
			((TileRFCrystal)world.getTileEntity(x, y, z)).breakEntireCrystal(false);
		super.breakBlock(world, x, y, z, old, oldmeta);
	}
	/*
	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)  {
		return 1;
	}*/

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ItemStack is = new ItemStack(this);
		if (GeoOptions.RFACTIVATE.getState()) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileRFCrystal && ((TileRFCrystal)te).isActivated) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setBoolean("activated", true);
			}
		}
		return ReikaJavaLibrary.makeListFrom(is);
	}

	public static class TileRFCrystal extends TileEntity implements IEnergyHandler, TrailShape, InitialAngleProvider {

		//private SimplexNoiseGenerator XYCrystalShape;
		//private SimplexNoiseGenerator XZCrystalShape;
		//private SimplexNoiseGenerator YZCrystalShape;

		//private final Simplex3DGenerator crystalShape = new Simplex3DGenerator(0);

		//private HashSet<Coordinate> crystalShape;

		//private final Simplex3DGenerator crystalShapeA = new Simplex3DGenerator(0);
		//private final Simplex3DGenerator crystalShapeB = new Simplex3DGenerator(0);

		private HashSet<Coordinate> crystalShape;

		private boolean isActivated = false;

		private long energy;
		private BlockArray crystal = new BlockArray();

		@Override
		public final boolean canUpdate() {
			return true;
		}

		public void breakEntireCrystal(boolean skipSelf) {
			ArrayList<Coordinate> li = new ArrayList(crystal.keySet());
			crystal.clear();
			if (skipSelf)
				crystal.addBlockCoordinate(xCoord, yCoord, zCoord);
			for (Coordinate c : li) {
				if (skipSelf && c.equals(xCoord, yCoord, zCoord))
					continue;
				ReikaWorldHelper.dropAndDestroyBlockAt(worldObj, c.xCoord, c.yCoord, c.zCoord, null, true, true);
			}
		}

		@Override
		public void updateEntity() {
			if (crystalShape == null && !worldObj.isRemote) {
				CurvedTrajectory cv = new CurvedTrajectory(xCoord, yCoord, zCoord);
				cv.trailCount = 9;
				cv.trailForkChance = 0;//0.01F;
				cv.bounds = BlockBox.block(this).expand(48, 24, 48);
				cv.generatePaths(worldObj.getSeed() ^ new Coordinate(this).hashCode(), this, this);
				crystalShape = cv.getLocations();
			}


			if (!worldObj.isRemote) {
				if (isActivated) {
					long cap = this.getCapacity();
					if (energy > cap)
						energy = cap;
					//ReikaJavaLibrary.pConsole(String.format("%.4f", energy/(float)cap)+" @ "+crystal.getSize()+" : "+energy+" / "+cap);
					if (energy > cap*4/5 && crystal.getSize() < 2000) {
						this.growNewCrystal();
					}

					if (energy > 0 && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
						TileEntity te = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
						if (te instanceof IEnergyReceiver) {
							int pushable = this.getEnergyStored(null);
							IEnergyReceiver ier = (IEnergyReceiver)te;
							pushable = Math.min(pushable, ier.receiveEnergy(ForgeDirection.UP, pushable, true));
							if (pushable > 0) {
								pushable = ier.receiveEnergy(ForgeDirection.UP, pushable, false);
								energy -= pushable;
							}
						}
					}
				}
				else {
					if (crystal.getSize() > 1) {
						this.breakEntireCrystal(true);
					}
				}
			}
		}

		private void growNewCrystal() {
			Coordinate loc = null;
			ArrayList<Coordinate> li = new ArrayList(crystal.list());
			Coordinate th = new Coordinate(this);
			li.add(th);
			Collections.shuffle(li);
			for (Coordinate c : li) {
				for (Coordinate c2 : c.getAdjacentCoordinates()) {
					if (this.canGrowInto(worldObj, c2) && this.isValidLocation(c2)) {
						//if (c2.getTaxicabDistanceTo(th) < 20 && c2.yCoord-yCoord < 4) {
						loc = c2;
						break;
						//}
					}
				}
			}
			if (loc != null) {
				//loc.setBlock(worldObj, GeoBlocks.RFCRYSTAL.getBlockInstance());
				//crystal.addBlockCoordinate(loc.xCoord, loc.yCoord, loc.zCoord);
				BlockRFCrystal.place(worldObj, loc.xCoord, loc.yCoord, loc.zCoord, this);
				energy = Math.max(energy-this.getGrowthCost(), 0);
			}
		}

		private long getGrowthCost() {
			return MathHelper.clamp_int(crystal.getSize()*1000, 2000, 600000);
		}

		private boolean canGrowInto(World worldObj, Coordinate c2) {
			Block b = c2.getBlock(worldObj);
			return b == Blocks.air || b == Blocks.water || b.isAir(worldObj, c2.xCoord, c2.yCoord, c2.zCoord);
		}

		private boolean isValidLocation(Coordinate c) {
			if (crystal.getSize() < 12)
				return true;
			/*
			double x = c.xCoord/8D;
			double y = c.yCoord/8D;
			double z = c.zCoord/8D;
			double v1 = new SimplexNoiseGenerator(seed).getValue(x, y);//XYCrystalShape.getValue(x, y);
			double v2 = new SimplexNoiseGenerator(-seed).getValue(x, z);//XZCrystalShape.getValue(x, z);
			double v3 = new SimplexNoiseGenerator(~seed).getValue(y, z);//YZCrystalShape.getValue(y, z);
			//ReikaJavaLibrary.pConsole(v2, c.xCoord == 525 && c.zCoord == 150);
			return Math.abs(v1) < 0.125 || Math.abs(v2) < 0.125 || Math.abs(v3) < 0.125;
			 */
			//return Math.abs(crystalShape.getValue(c.xCoord/4D, c.yCoord/4D, c.zCoord/4D)) <= 0.0625*1.5;
			//return crystalShape.contains(c);
			/*
			double phi = ReikaMathLibrary.normalizeToBounds(crystalShapeA.getValue(c.xCoord/16D, c.yCoord/16D, c.zCoord/16D), 0, 360);
			double theta = ReikaMathLibrary.normalizeToBounds(crystalShapeB.getValue(c.xCoord/16D, c.yCoord/16D, c.zCoord/16D), 0, 360);
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(c.xCoord-from.xCoord, c.yCoord-from.yCoord, c.zCoord-from.zCoord);
			return ReikaMathLibrary.approxr(angs[2], phi, 35) && ReikaMathLibrary.approxr(angs[1], theta, 35);
			 */
			return crystalShape.contains(c);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBT.setLong("energy", energy);
			crystal.writeToNBT("blocks", NBT);
			NBT.setBoolean("activated", isActivated);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			energy = NBT.getLong("energy");
			crystal.readFromNBT("blocks", NBT);
			isActivated = NBT.getBoolean("activated") || !GeoOptions.RFACTIVATE.getState();
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
		}

		public long getEnergy() {
			return energy;
		}

		public long getCapacity() {
			int s = crystal.getSize();
			double sigmoid = ReikaMathLibrary.cosInterpolation(0, 200, Math.min(s, 200));
			double linear = 100000*Math.pow(1.03125, s);//ReikaMathLibrary.roundUpToX(1000, (int)(100000*Math.pow(1.03125, s)));
			double factor = Math.min(1, 0.8*ReikaMathLibrary.logbase(2+s, 64));
			int round = 1000*ReikaMathLibrary.intpow2(10, 1+(int)(Math.log10(1+s)));
			return (long)(2500+1000000000*sigmoid*factor+linear);
			//return ReikaMathLibrary.roundUpToX(round, (int)(2500+1000000000*sigmoid*factor+linear));
		}

		@Override
		public boolean canConnectEnergy(ForgeDirection from) {
			return true;
		}

		@Override
		public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
			int amt = Math.min((int)Math.min(this.getCapacity()-this.getEnergy(), Integer.MAX_VALUE), maxReceive);
			if (!simulate)
				energy += amt;
			return amt;
		}

		@Override
		public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
			int amt = Math.min((int)Math.min(this.getEnergy(), Integer.MAX_VALUE), maxExtract);
			if (!simulate)
				energy -= amt;
			return amt;
		}

		@Override
		public int getEnergyStored(ForgeDirection from) {
			return (int)Math.min(this.getEnergy(), Integer.MAX_VALUE);
		}

		@Override
		public int getMaxEnergyStored(ForgeDirection from) {
			return (int)Math.min(this.getCapacity(), Integer.MAX_VALUE);
		}

		@Override
		public Collection<Coordinate> getBlocks(DecimalPosition pos) {
			ArrayList<Coordinate> li = new ArrayList();
			Coordinate c = pos.getCoordinate();
			li.add(c);
			li.addAll(c.getAdjacentCoordinates());
			return li;
		}

		@Override
		public double getInitialTheta(Random rand, int trail) {
			return -5+rand.nextDouble()*10+rand.nextDouble()*80-40;
		}

		@Override
		public double getInitialPhi(Random rand, int trail) {
			double base = 360D/9*trail;
			return base-10+rand.nextDouble()*20;
		}

		public void addLocation(Coordinate c) {
			crystal.addBlockCoordinate(c.xCoord, c.yCoord, c.zCoord);
		}

		public void removeLocation(Coordinate c) {
			if (crystal.hasBlock(c)) {
				crystal.remove(c.xCoord, c.yCoord, c.zCoord);
				BlockArray b = new BlockArray();
				b.recursiveMultiAddWithBounds(worldObj, xCoord, yCoord, zCoord, crystal.getMinX(), crystal.getMinY(), crystal.getMinZ(), crystal.getMaxX(), crystal.getMaxY(), crystal.getMaxZ(), this.getBlockType(), GeoBlocks.RFCRYSTAL.getBlockInstance());
				List<Coordinate> li = new ArrayList(crystal.list());
				for (Coordinate c2 : li) {
					if (!b.hasBlock(c2.xCoord, c2.yCoord, c2.zCoord)) {
						crystal.remove(c2.xCoord, c2.yCoord, c2.zCoord);
						ReikaWorldHelper.dropAndDestroyBlockAt(worldObj, c2.xCoord, c2.yCoord, c2.zCoord, null, true, true);
					}
				}
			}
		}

		public void activate() {
			isActivated = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

	}

}
