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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.GeoStrata.Base.CrystalBlock;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public abstract class GeoPackets implements IPacketHandler {

	protected PacketTypes packetType;
	protected int pack;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		this.process(packet, (EntityPlayer)player);
	}

	public abstract void process(Packet250CustomPayload packet, EntityPlayer ep);

	public void handleData(Packet250CustomPayload packet, World world) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		int x,y,z;
		String stringdata = null;
		//System.out.print(packet.length);
		try {
			packetType = PacketTypes.getPacketType(inputStream.readInt());
			switch(packetType) {
			case SOUND:
				return;
			case STRING:
				control = inputStream.readInt();
				pack = control;
				stringdata = Packet.readString(inputStream, Short.MAX_VALUE);
				break;
			case DATA:
				control = inputStream.readInt();
				pack = control;
				len = 0;//pack.getNumberDataInts();
				data = new int[len];
				for (int i = 0; i < len; i++)
					data[i] = inputStream.readInt();
				break;
			case UPDATE:
				control = inputStream.readInt();
				pack = control;
				break;
			case FLOAT:
				break;
			case SYNC:
				break;
			case TANK:
				break;
			}
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		switch (control) {
		case 0:
			Block b = Block.blocksList[world.getBlockId(x, y, z)];
			if (b instanceof CrystalBlock) {
				CrystalBlock cb = (CrystalBlock)b;
				cb.updateEffects(world, x, y, z);
			}
			break;
		case 1:
			TileEntityCrystalPlant te = (TileEntityCrystalPlant)world.getBlockTileEntity(x, y, z);
			te.updateLight();
			break;
		}
	}

}
