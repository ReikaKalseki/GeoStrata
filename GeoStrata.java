/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ControlledConfig;
import Reika.DragonAPI.Libraries.ReikaItemHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockTypes;
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
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = "GeoStrata", name="GeoStrata", version="beta", certificateFingerprint = "@GET_FINGERPRINT@")
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "GeoStrataData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "GeoStrataData" }, packetHandler = ServerPackets.class))

public class GeoStrata extends DragonAPIMod {

	@Instance("GeoStrata")
	public static GeoStrata instance = new GeoStrata();

	public static final ControlledConfig config = new ControlledConfig(instance, GeoOptions.optionList, GeoBlocks.blockList, GeoItems.itemList, null, 1);

	public static final String packetChannel = "GeoStrataData";

	public static CreativeTabs tabGeo = new GeoTab(CreativeTabs.getNextID(),"GeoStrata");

	public static Item[] items = new Item[GeoItems.itemList.length];
	public static Block[] blocks = new Block[GeoBlocks.blockList.length];

	@Override
	@PreInit
	public void preload(FMLPreInitializationEvent evt) {
		config.initProps(evt);
	}

	@Override
	@Init
	public void load(FMLInitializationEvent event) {
		this.loadClasses();
		this.loadNames();
		this.genRocks();
		this.addRecipes();
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
		OreDictionary.initVanillaEntries();
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			ItemStack cobble = new ItemStack(GeoBlocks.COBBLE.getBlockID(), 1, i);
			OreDictionary.registerOre("blockCobble", cobble);
		}
	}

	public static void genRocks() {
		GameRegistry.registerWorldGenerator(new RockGenerator());
	}

	public static void addRecipes() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			ItemStack smooth = new ItemStack(GeoBlocks.SMOOTH.getBlockID(), 1, i);
			ItemStack cobble = new ItemStack(GeoBlocks.COBBLE.getBlockID(), 1, i);
			ItemStack brick = new ItemStack(GeoBlocks.BRICK.getBlockID(), 1, i);
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(brick, 4), new Object[]{
				"SS", "SS", 'S', smooth});
			FurnaceRecipes.smelting().addSmelting(cobble.itemID, cobble.getItemDamage(), smooth, 0.2F);
		}
	}

	@Override
	public String getDisplayName() {
		return "GeoStrata";
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
