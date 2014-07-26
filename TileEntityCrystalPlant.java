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

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.GeoOptions;

public class TileEntityCrystalPlant extends TileEntity {

	private final Random random = new Random();

	private int growthTick = 2;

	public boolean renderPod() {
		return growthTick <= 1;
	}

	public boolean emitsLight() {
		return growthTick == 0;
	}

	public void grow() {
		if (growthTick > 0) {
			growthTick--;
			this.updateLight();
			for (int i = 2; i < 6; i++) {
				if (ReikaRandomHelper.doWithChance(25)) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					int dx = xCoord+dir.offsetX;
					int dy = yCoord+dir.offsetY;
					int dz = zCoord+dir.offsetZ;
					int id = worldObj.getBlockId(dx, dy, dz);
					int meta = worldObj.getBlockMetadata(dx, dy, dz);
					if (id == GeoBlocks.PLANT.getBlockID() && meta == this.getColor().ordinal()) {
						TileEntityCrystalPlant te = (TileEntityCrystalPlant)worldObj.getBlockTileEntity(dx, dy, dz);
						te.grow();
					}
				}
			}
		}
	}

	public void updateLight() {
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void harvest() {
		growthTick = 2;
		int rand = random.nextInt(20);
		int num = 0;
		if (rand == 0) {
			num = 2;
		}
		else if (rand < 5) {
			num = 1;
		}
		int meta = this.getColor().ordinal();
		for (int i = 0; i < num; i++)
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, GeoItems.SEED.getStackOfMetadata(meta+16));
		if (GeoOptions.CRYSTALFARM.getState() && ReikaRandomHelper.doWithChance(2))
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, GeoItems.SHARD.getStackOfMetadata(meta));
		this.updateLight();
	}

	public boolean canHarvest() {
		return growthTick == 0;
	}

	public ReikaDyeHelper getColor() {
		return ReikaDyeHelper.getColorFromDamage(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		this.readFromNBT(pkt.data);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
		growthTick = NBT.getInteger("growth");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("growth", growthTick);
	}

}
