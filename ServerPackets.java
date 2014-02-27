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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

public class ServerPackets extends GeoPackets {

	@Override
	public void process(Packet250CustomPayload packet, EntityPlayer ep) {
		World world = ep.worldObj;
		this.handleData(packet, world);
	}

}
