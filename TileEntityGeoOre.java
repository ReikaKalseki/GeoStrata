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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import Reika.DragonAPI.Interfaces.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class TileEntityGeoOre extends TileEntity {

	private RockTypes type;
	private ReikaOreHelper ore;
	private ModOreList modore;
	private Block block;
	private int metadata;

	@Override
	public boolean canUpdate() {
		return false;
	}

	public void initialize(RockTypes rock, Block b, int meta) {
		type = rock;
		ItemStack is = new ItemStack(b, 1, meta);
		ore = ReikaOreHelper.getFromVanillaOre(b);
		if (ore == null)
			ore = ReikaOreHelper.getEntryByOreDict(is);
		modore = ModOreList.getModOreFromOre(is);
		block = b;
		metadata = meta;
	}

	public RockTypes getType() {
		return type != null ? type : RockTypes.SHALE;
	}

	public IIcon getOreIcon(int side) {
		return block.getIcon(worldObj, xCoord, yCoord, zCoord, side);
	}

	public Block getOreBlock() {
		return block != null ? block : type != null ? type.getID(RockShapes.SMOOTH) : Blocks.stone;
	}

	public int getOreMeta() {
		return metadata;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("type", type.ordinal());
		if (ore != null)
			NBT.setInteger("ore", ore.ordinal());
		if (modore != null)
			NBT.setInteger("more", modore.ordinal());
		NBT.setInteger("orem", metadata);
		NBT.setInteger("blockid", Block.getIdFromBlock(block));
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		type = RockTypes.rockList[NBT.getInteger("type")];
		modore = NBT.hasKey("more") ? ModOreList.oreList[NBT.getInteger("more")] : null;
		ore = NBT.hasKey("ore") ? ReikaOreHelper.oreList[NBT.getInteger("ore")] : null;
		metadata = NBT.getInteger("orem");
		block = Block.getBlockById(NBT.getInteger("blockid"));
		if (block == null || block == Blocks.air) { //in case of NBT failure
			this.calculateBlock();
		}
	}

	private void calculateBlock() {
		if (ore != null) {
			block = ore.getOreBlockInstance();
		}
		else if (modore != null) {
			ItemStack is = modore.getFirstOreBlock();
			block = Block.getBlockFromItem(is.getItem());
			metadata = is.getItemDamage();
		}
	}

	/** Single char names to minimize packet size */
	private void writeToPacket(NBTTagCompound NBT) {
		NBT.setInteger("r", this.getType().ordinal());
		if (ore != null)
			NBT.setInteger("a", ore.ordinal());
		if (modore != null)
			NBT.setInteger("b", modore.ordinal());
		NBT.setInteger("m", metadata);
		NBT.setInteger("i", Block.getIdFromBlock(block));
	}

	private void readFromPacket(NBTTagCompound NBT) {
		type = RockTypes.rockList[NBT.getInteger("r")];
		modore = NBT.hasKey("b") ? ModOreList.oreList[NBT.getInteger("b")] : null;
		ore = NBT.hasKey("a") ? ReikaOreHelper.oreList[NBT.getInteger("a")] : null;
		metadata = NBT.getInteger("m");
		int id = NBT.getInteger("i");
		block = Block.getBlockById(id);
		if (block == null || block == Blocks.air) { //in case of NBT failure
			this.calculateBlock();
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound NBT = new NBTTagCompound();
		this.writeToPacket(NBT);
		S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
		return pack;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
		this.readFromPacket(p.field_148860_e);
	}

	public OreType getOreType() {
		return modore != null ? modore : ore != null ? ore : null;
	}

	@Override
	public String toString() {
		return type+" "+block+":"+metadata+" ("+ore+" & "+modore+")";
	}

}
