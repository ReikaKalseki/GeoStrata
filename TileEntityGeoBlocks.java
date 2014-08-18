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

import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class TileEntityGeoBlocks extends TileEntity {

	private RockShapes shape;
	private RockTypes type;

	@Override
	public boolean canUpdate() {
		return false;
	}

	public Block getImitatedBlock() {
		if (shape == null || type == null)
			return Blocks.stone;
		return type.getID(shape);
	}

	public IIcon getIcon() {
		if (type == null || shape == null)
			return Blocks.stone.getIcon(0, 0);
		return type.getIcon(shape);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("type", type.ordinal());
		NBT.setInteger("shape", shape.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		type = RockTypes.rockList[NBT.getInteger("type")];
		shape = RockShapes.shapeList[NBT.getInteger("shape")];
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

	public void setTypes(RockTypes rock, RockShapes s) {
		type = rock;
		shape = s;
	}

	public RockTypes getRockType() {
		return type;
	}

}
