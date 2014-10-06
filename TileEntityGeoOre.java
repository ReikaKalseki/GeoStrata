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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.Registry.RockTypes;

public class TileEntityGeoOre extends TileEntity {

	private RockTypes type;
	private ReikaOreHelper ore;
	private ModOreList modore;
	private Block block;
	private int metadata;

	public void initialize(RockTypes rock, Block b, int meta) {
		type = rock;
		ItemStack is = new ItemStack(b, meta);
		ore = ReikaOreHelper.getFromVanillaOre(b);
		if (ore == null)
			ore = ReikaOreHelper.getEntryByOreDict(is);
		modore = ModOreList.getModOreFromOre(is);
		block = b;
		metadata = meta;
	}

	public RockTypes getType() {
		return type;
	}

	public IIcon getOreIcon(int side) {
		return block.getIcon(worldObj, xCoord, yCoord, zCoord, side);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("type", type.ordinal());
		if (ore != null)
			NBT.setInteger("ore", ore.ordinal());
		if (modore != null)
			NBT.setInteger("more", modore.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		type = RockTypes.rockList[NBT.getInteger("type")];
		modore = NBT.hasKey("more") ? ModOreList.oreList[NBT.getInteger("more")] : null;
		ore = NBT.hasKey("ore") ? ReikaOreHelper.oreList[NBT.getInteger("ore")] : null;
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

}
