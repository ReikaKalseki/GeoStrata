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

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Libraries.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.GeoGen.Registry.GeoBlocks;
import Reika.GeoGen.Registry.GeoItems;
import Reika.GeoGen.Registry.RockTypes;
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
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod( modid = "GeoGen", name="GeoGen", version="beta", certificateFingerprint = "@GET_FINGERPRINT@")
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "GeoGenData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "GeoGenData" }, packetHandler = ServerPackets.class))

public class GeoGen extends DragonAPIMod {

	@Instance("GeoGen")
	public static GeoGen instance = new GeoGen();

	public static final String packetChannel = "GeoGenData";

	public static CreativeTabs tabGeo = new GeoTab(CreativeTabs.getNextID(),"GeoGen");

	public static Item[] items = new Item[GeoItems.itemList.length];
	public static Block[] blocks = new Block[GeoBlocks.blockList.length];

	@Override
	@PreInit
	public void preload(FMLPreInitializationEvent evt) {
		GeoConfig.initProps(evt);
	}

	@Override
	@Init
	public void load(FMLInitializationEvent event) {
		this.loadClasses();
		this.loadNames();
	}

	@Override
	@PostInit // Like the modsLoaded thing from ModLoader
	public void postload(FMLPostInitializationEvent evt) {

	}

	public static void loadClasses() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, GeoBlocks.blockList, blocks, true);
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, GeoItems.itemList, items, true);
	}

	public static void loadNames() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			ItemStack smooth = new ItemStack(GeoBlocks.SMOOTH.getBlockID(), 1, i);
			ItemStack cobble = new ItemStack(GeoBlocks.COBBLE.getBlockID(), 1, i);
			ItemStack brick = new ItemStack(GeoBlocks.BRICK.getBlockID(), 1, i);
			LanguageRegistry.addName(smooth, "Smooth "+RockTypes.rockList[i].getName());
			LanguageRegistry.addName(cobble, RockTypes.rockList[i].getName()+" Cobblestone");
			LanguageRegistry.addName(brick, RockTypes.rockList[i].getName()+" Bricks");
		}
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack crystal = new ItemStack(GeoBlocks.CRYSTAL.getBlockID(), 1, i);
			ItemStack lamp = new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i);
			LanguageRegistry.addName(crystal, ReikaDyeHelper.dyes[i].getName()+" "+GeoBlocks.CRYSTAL.getBasicName());
			LanguageRegistry.addName(lamp, ReikaDyeHelper.dyes[i].getName()+" "+GeoBlocks.LAMP.getBasicName());
		}
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
