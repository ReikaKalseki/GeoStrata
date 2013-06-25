/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen;

import java.net.URL;

import net.minecraft.item.Item;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.RotaryCraft.ClientPackets;
import Reika.RotaryCraft.ServerPackets;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@Mod( modid = "GeoGen", name="GeoGen", version="beta", certificateFingerprint = "@GET_FINGERPRINT@")
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "GeoGenData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "GeoGenData" }, packetHandler = ServerPackets.class))

public class GeoGen extends DragonAPIMod {

	@Instance("GeoGen")
	public static GeoGen instance = new GeoGen();

	public static Item shard;

	@Override
	@PreInit
	public void preload(FMLPreInitializationEvent evt) {
		GeoConfig.initProps(evt);

	}

	@Override
	@Init
	public void load(FMLInitializationEvent event) {

	}

	@Override
	@PostInit // Like the modsLoaded thing from ModLoader
	public void postload(FMLPostInitializationEvent evt) {

	}

	public static void loadClasses() {
		shard = new ItemCrystalShard(3000);
	}

	@Override
	public String getDisplayName() {
		return "GeoGen";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return null;
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}
}
