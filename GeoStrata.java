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
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.RetroGenController;
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ControlledConfig;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.GeoStrata.Registry.DecoBlocks;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.World.CrystalGenerator;
import Reika.GeoStrata.World.RetroCrystalGenerator;
import Reika.GeoStrata.World.RockGenerator;
import Reika.RotaryCraft.API.GrinderAPI;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = GeoStrata.MOD_NAME, name=GeoStrata.MOD_NAME, version="Gamma", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { GeoStrata.MOD_NAME+"Data" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { GeoStrata.MOD_NAME+"Data" }, packetHandler = ServerPackets.class))

public class GeoStrata extends DragonAPIMod {

	public static final String MOD_NAME = "GeoStrata";

	@Instance(GeoStrata.MOD_NAME)
	public static GeoStrata instance = new GeoStrata();

	public static final ControlledConfig config = new ControlledConfig(instance, GeoOptions.optionList, GeoBlocks.blockList, GeoItems.itemList, null, 1);

	public static final String packetChannel = GeoStrata.MOD_NAME+"Data";

	public static CreativeTabs tabGeo = new GeoTab(CreativeTabs.getNextID(), GeoStrata.MOD_NAME);

	public static ModLogger logger;

	public static Item[] items = new Item[GeoItems.itemList.length];
	public static Block[] blocks = new Block[GeoBlocks.blockList.length];

	@SidedProxy(clientSide="Reika.GeoStrata.GeoClient", serverSide="Reika.GeoStrata.GeoCommon")
	public static GeoCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, GeoOptions.LOGLOADING.getState(), GeoOptions.DEBUGMODE.getState(), false);
		proxy.registerSounds();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.loadClasses();
		this.loadDictionary();
		this.genRocks();
		this.addRecipes();
		proxy.registerRenderers();
		GameRegistry.registerTileEntity(TileEntityCrystalBrewer.class, "GeoBrewer");
		NetworkRegistry.instance().registerGuiHandler(instance, new GeoGuiHandler());
		if (GeoOptions.RETROGEN.getState()) {
			RetroGenController.getInstance().addRetroGenerator(new RetroCrystalGenerator());
			//Set state back
		}
	}

	@Override
	@EventHandler // Like the modsLoaded thing from ModLoader
	public void postload(FMLPostInitializationEvent evt) {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				ItemStack smooth = new ItemStack(GeoBlocks.SMOOTH.getBlockID(), 1, i);
				ItemStack cobble = new ItemStack(GeoBlocks.COBBLE.getBlockID(), 1, i);
				GrinderAPI.addRecipe(smooth, cobble);
				GrinderAPI.addRecipe(cobble, new ItemStack(Block.gravel));
			}
		}
	}

	public static void loadClasses() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, GeoBlocks.blockList, blocks);
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, GeoItems.itemList, items);
	}

	public static void loadDictionary() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			ItemStack cobble = new ItemStack(GeoBlocks.COBBLE.getBlockID(), 1, i);
			OreDictionary.registerOre("blockCobble", cobble);
		}
	}

	public static void genRocks() {
		GameRegistry.registerWorldGenerator(new RockGenerator());
		GameRegistry.registerWorldGenerator(new CrystalGenerator());
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

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = GeoItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i);
			GameRegistry.addRecipe(lamp, " s ", "sss", "SSS", 's', shard, 'S', ReikaItemHelper.stoneSlab);
		}

		DecoBlocks.EMERALDBRICKS.addSizedCrafting(4, "BB", "BB", 'B', Block.blockEmerald);
		DecoBlocks.REDBRICKS.addSizedCrafting(4, "BB", "BB", 'B', Block.blockRedstone);
		DecoBlocks.QUARTZBRICKS.addSizedCrafting(4, "BB", "BB", 'B', Block.blockNetherQuartz);
		DecoBlocks.OBSIDIBRICKS.addSizedCrafting(4, "BB", "BB", 'B', Block.obsidian);
		DecoBlocks.GLOWBRICKS.addSizedCrafting(4, "BB", "BB", 'B', Block.glowStone);
		DecoBlocks.LAPISBRICKS.addSizedCrafting(4, "BB", "BB", 'B', Block.blockLapis);

		GameRegistry.addRecipe(new ItemStack(GeoBlocks.BREWER.getBlockID(), 1, 0), "NNN", "NBN", "SSS", 'N', Item.netherQuartz, 'S', Block.stone, 'B', Item.brewingStand);
	}

	@Override
	public String getDisplayName() {
		return GeoStrata.MOD_NAME;
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return true;
	}

	@Override
	public String getVersionName() {
		return "Gamma";
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}
}
