/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.net.URL;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.RecipeHandlers.ThermalRecipeHelper;
import Reika.GeoStrata.Blocks.BlockVent.TileEntityVent;
import Reika.GeoStrata.Blocks.BlockVent.VentType;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.Rendering.OreRenderer;
import Reika.GeoStrata.World.BandedGenerator;
import Reika.GeoStrata.World.RockGenerator;
import Reika.GeoStrata.World.VentGenerator;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.API.RecipeInterface;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = GeoStrata.MOD_NAME, name = GeoStrata.MOD_NAME, certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class GeoStrata extends DragonAPIMod {

	public static final String MOD_NAME = "GeoStrata";

	@Instance(GeoStrata.MOD_NAME)
	public static GeoStrata instance = new GeoStrata();

	public static final GeoConfig config = new GeoConfig(instance, GeoOptions.optionList, null, 1);

	public static final String packetChannel = GeoStrata.MOD_NAME+"Data";

	public static CreativeTabs tabGeo = new GeoTab(GeoStrata.MOD_NAME);
	public static CreativeTabs tabGeoStairs = new GeoTabStairs(GeoStrata.MOD_NAME+" Stairs");
	public static CreativeTabs tabGeoSlabs = new GeoTabSlab(GeoStrata.MOD_NAME+" Slabs");
	public static CreativeTabs tabGeoOres = new GeoTabOres(GeoStrata.MOD_NAME+" Ores");

	public static ModLogger logger;

	public static Block[] blocks = new Block[GeoBlocks.blockList.length];
	public static final ArrayList<Block> rockBlocks = new ArrayList();

	@SidedProxy(clientSide="Reika.GeoStrata.GeoClient", serverSide="Reika.GeoStrata.GeoCommon")
	public static GeoCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, false);
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");
		proxy.registerSounds();

		this.loadClasses();

		if (ModList.CHISEL.isLoaded()) {
			GeoChisel.loadChiselCompat();
		}

		this.basicSetup(evt);
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);
		this.loadDictionary();
		RockGenerator gen = GeoOptions.BANDED.getState() ? BandedGenerator.instance : RockGenerator.instance;
		RetroGenController.instance.addHybridGenerator(gen, Integer.MIN_VALUE, GeoOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(VentGenerator.instance, 0, GeoOptions.RETROGEN.getState());

		GeoRecipes.addRecipes();
		proxy.registerRenderers();

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.obsidian);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.stone);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.quartz_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.glowstone);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.redstone_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.lapis_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.emerald_block);

		DonatorController.instance.addDonation(instance, "sophieguerette", 10.00F);

		if (ModList.THERMALEXPANSION.isLoaded()) {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes r = RockTypes.rockList[i];
				ItemStack smooth = r.getItem(RockShapes.SMOOTH);
				ItemStack cobble = r.getItem(RockShapes.COBBLE);
				int energy = (int)(200+800*(r.blockHardness-1));
				ThermalRecipeHelper.addPulverizerRecipe(smooth, cobble, energy); //make proportional to hardness
				ThermalRecipeHelper.addPulverizerRecipe(cobble, new ItemStack(Blocks.sand), new ItemStack(Blocks.gravel), 20, energy);
			}
		}

		//FMP and Facades
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			for (int k = 0; k < RockShapes.shapeList.length; k++) {
				ItemStack is = RockTypes.rockList[i].getItem(RockShapes.shapeList[k]);
				FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", is);
				FMLInterModComms.sendMessage(ModList.BCTRANSPORT.modLabel, "add-facade", is);
			}
		}

		this.finishTiming();
	}

	@Override
	@EventHandler // Like the modsLoaded thing from ModLoader
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);

		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes rock = RockTypes.rockList[i];
				ItemStack smooth = rock.getItem(RockShapes.SMOOTH);
				ItemStack cobble = rock.getItem(RockShapes.COBBLE);
				RecipeInterface.grinder.addAPIRecipe(smooth, cobble);
				RecipeInterface.grinder.addAPIRecipe(cobble, new ItemStack(Blocks.gravel));

				for (int k = 0; k < RockShapes.shapeList.length; k++) {
					RockShapes shape = RockShapes.shapeList[k];
					BlockColorInterface.addGPRBlockColor(rock.getID(shape), rock.rockColor);
				}
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes rock = RockTypes.rockList[i];
				ItemStack rockblock = rock.getItem(RockShapes.SMOOTH);
				//ReikaThaumHelper.addAspects(rockblock, Aspect.STONE, (int)(rock.blockHardness/5));
			}

			ReikaThaumHelper.addAspectsToBlockMeta(GeoBlocks.VENT.getBlockInstance(), VentType.WATER.ordinal(), Aspect.WATER, 2, Aspect.EARTH, 2);
			ReikaThaumHelper.addAspectsToBlockMeta(GeoBlocks.VENT.getBlockInstance(), VentType.FIRE.ordinal(), Aspect.EARTH, 2, Aspect.FIRE, 3);
			ReikaThaumHelper.addAspectsToBlockMeta(GeoBlocks.VENT.getBlockInstance(), VentType.GAS.ordinal(), Aspect.AIR, 2, Aspect.EARTH, 2, Aspect.POISON, 5);
			ReikaThaumHelper.addAspectsToBlockMeta(GeoBlocks.VENT.getBlockInstance(), VentType.SMOKE.ordinal(), Aspect.AIR, 2, Aspect.EARTH, 2);
			ReikaThaumHelper.addAspectsToBlockMeta(GeoBlocks.VENT.getBlockInstance(), VentType.STEAM.ordinal(), Aspect.AIR, 2, Aspect.WATER, 2, Aspect.EARTH, 2, Aspect.FIRE, 2);
			ReikaThaumHelper.addAspectsToBlockMeta(GeoBlocks.VENT.getBlockInstance(), VentType.LAVA.ordinal(), Aspect.EARTH, 4, Aspect.FIRE, 5);
		}

		if (ModList.EXTRAUTILS.isLoaded()) {
			Block id = ExtraUtilsHandler.getInstance().decoID;
			if (id != null) {
				ItemStack burned = new ItemStack(id, 1, ExtraUtilsHandler.getInstance().burntQuartz);
				ReikaRecipeHelper.addSmelting(RockTypes.QUARTZ.getItem(RockShapes.SMOOTH), burned, 0.05F);
			}
		}

		this.finishTiming();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void generateDynamicOreTextures(TextureStitchEvent.Pre evt) {
		OreRenderer.regenIcons(evt);
	}

	private static void loadClasses() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, GeoBlocks.blockList, blocks);
		ArrayList<Block> blocks = new ArrayList();
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes r = RockTypes.rockList[i];
			for (int k = 0; k < RockShapes.shapeList.length; k++) {
				RockShapes s = RockShapes.shapeList[k];
				Block b;
				if (s.needsOwnBlock) {
					b = s.register(r);
				}
				else {
					if (s.isRegistered(r)) {
						b = s.getBlock(r);
					}
					else {
						b = s.register(r);
					}
				}
				blocks.add(b);
			}
		}
		rockBlocks.addAll(blocks);
		RockShapes.initalize();
		RockTypes.loadMappings();
		GameRegistry.registerTileEntity(TileEntityGeoBlocks.class, "geostratatile");
		GameRegistry.registerTileEntity(TileEntityVent.class, "geostratavent");
		GameRegistry.registerTileEntity(TileEntityGeoOre.class, "geostrataore");
		//GameRegistry.registerTileEntity(TileEntityOreConverter.class, "geostrataoreconvert");
	}

	public static void loadDictionary() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes type = RockTypes.rockList[i];
			ItemStack cobble = type.getItem(RockShapes.COBBLE);
			ItemStack rock = type.getItem(RockShapes.SMOOTH);
			OreDictionary.registerOre("cobblestone", cobble);
			OreDictionary.registerOre("stone", rock);
			OreDictionary.registerOre("rock"+type.getName(), rock);
			OreDictionary.registerOre("stone"+type.getName(), rock);
			if (type != RockTypes.QUARTZ) //Nether quartz is "quartz"
				OreDictionary.registerOre(type.getName().toLowerCase(), rock);
		}
		OreDictionary.registerOre("sandstone", RockTypes.SANDSTONE.getItem(RockShapes.SMOOTH));
		OreDictionary.registerOre("sandstone", Blocks.sandstone);
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
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getWiki() {
		return null;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}
}
