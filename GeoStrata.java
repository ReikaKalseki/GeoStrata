/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry.InterceptionException;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.RecipeHandlers.ThermalRecipeHelper;
import Reika.GeoStrata.Blocks.BlockGlowingVines.TileGlowingVines;
import Reika.GeoStrata.Blocks.BlockPartialBounds.TilePartialBounds;
import Reika.GeoStrata.Blocks.BlockRFCrystal.TileRFCrystalAux;
import Reika.GeoStrata.Blocks.BlockRFCrystalSeed.TileRFCrystal;
import Reika.GeoStrata.Blocks.BlockVent.TileEntityVent;
import Reika.GeoStrata.Blocks.BlockVent.VentType;
import Reika.GeoStrata.Items.ItemBlockAnyGeoVariant;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockGeneratorTypes;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.Rendering.OreRenderer;
import Reika.GeoStrata.World.DecoGenerator;
import Reika.GeoStrata.World.GlowCrystalGenerator;
import Reika.GeoStrata.World.GlowingVineGenerator;
import Reika.GeoStrata.World.LavaRockGeneratorRedesign;
import Reika.GeoStrata.World.RFCrystalGenerator;
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
import forestry.api.storage.BackpackManager;
import thaumcraft.api.aspects.Aspect;

@Mod(modid = GeoStrata.MOD_NAME, name = GeoStrata.MOD_NAME, version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class GeoStrata extends DragonAPIMod {

	public static final String MOD_NAME = "GeoStrata";

	@Instance(GeoStrata.MOD_NAME)
	public static GeoStrata instance = new GeoStrata();

	public static final GeoConfig config = new GeoConfig(instance, GeoOptions.optionList, null);

	public static final String packetChannel = GeoStrata.MOD_NAME+"Data";

	public static CreativeTabs tabGeo = new GeoTab(GeoStrata.MOD_NAME);
	public static CreativeTabs tabGeoRock = new GeoTabRock(GeoStrata.MOD_NAME+" Stone");
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

		if (ModList.CHISEL.isLoaded() && ReikaJavaLibrary.doesClassExist("com.cricketcraft.chisel.api.carving.ICarvingGroup")) {
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

		RockGeneratorTypes type = RockGeneratorTypes.getType(GeoOptions.ROCKGEN.getString());
		RockGenerator.instance.registerGenerationPattern(type.getGenerator());

		RetroGenController.instance.addHybridGenerator(RockGenerator.instance, Integer.MIN_VALUE, GeoOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(VentGenerator.instance, 0, GeoOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(LavaRockGeneratorRedesign.instance, 0, GeoOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(DecoGenerator.instance, 0, GeoOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(GlowCrystalGenerator.instance, 0, GeoOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(GlowingVineGenerator.instance, 0, GeoOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(RFCrystalGenerator.instance, 0, GeoOptions.RETROGEN.getState());

		GeoRecipes.addRecipes();
		proxy.registerRenderers();

		MinecraftForge.EVENT_BUS.register(GeoEvents.instance);

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.obsidian);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.stone);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.quartz_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.glowstone);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.redstone_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.lapis_block);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Blocks.emerald_block);

		DonatorController.instance.registerMod(this, DonatorController.reikaURL);

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

		if (ModList.FORESTRY.isLoaded()) {
			if (BackpackManager.backpackItems != null && BackpackManager.backpackItems.length > 0) {
				for (int i = 0; i < RockTypes.rockList.length; i++) {
					RockTypes r = RockTypes.rockList[i];
					BackpackManager.backpackItems[1].add(r.getItem(RockShapes.COBBLE));
					BackpackManager.backpackItems[1].add(r.getItem(RockShapes.SMOOTH));
				}
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

		WorldGenInterceptionRegistry.instance.addException(new InterceptionException() { //performance
			@Override
			public boolean doesExceptionApply(World world, int x, int y, int z, Block set, int meta) {
				return RockTypes.getTypeFromID(set) != null;
			}

		});

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
		GameRegistry.registerTileEntity(TilePartialBounds.class, "geostratapartial");
		GameRegistry.registerTileEntity(TileGlowingVines.class, "geostratavines");
		GameRegistry.registerTileEntity(TileRFCrystal.class, "geostratarf");
		GameRegistry.registerTileEntity(TileRFCrystalAux.class, "geostratarfaux");
	}

	public static void loadDictionary() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes type = RockTypes.rockList[i];
			ItemStack cobble = type.getItem(RockShapes.COBBLE);
			ItemStack rock = type.getItem(RockShapes.SMOOTH);
			ItemStack slab = new ItemStack(GeoBlocks.SLAB.getBlockInstance(), 1, ItemBlockAnyGeoVariant.getStack(type, RockShapes.COBBLE));
			OreDictionary.registerOre("cobblestone", cobble);
			OreDictionary.registerOre("stone", rock);
			OreDictionary.registerOre("rock"+type.getName(), rock);
			OreDictionary.registerOre("stone"+type.getName(), rock);
			OreDictionary.registerOre("slabCobblestone", slab);
			if (type != RockTypes.QUARTZ) //Nether quartz is "quartz"
				OreDictionary.registerOre(type.getName().toLowerCase(Locale.ENGLISH), rock);
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

	@Override
	public File getConfigFolder() {
		return config.getConfigFolder();
	}

	public static int getOpalPositionColor(IBlockAccess iba, double x, double y, double z) {
		//int sc = 48;

		//float hue1 = (float)(ReikaMathLibrary.py3d(offX, y*4, offX+offZ)%sc)/sc;

		//float hue1 = (float)(ReikaMathLibrary.py3d(x, y*4, z+x)%sc)/sc;

		double d = GeoOptions.OPALFREQ.getFloat();
		double n = 7;//+1*Math.cos(Math.toRadians((x+y+z)/500D*360D));
		//double n = 7+0.0625*Math.cos(Math.toRadians((x+y+z)/500D*360D));
		//double n = 7+1*Math.cos(Math.toRadians(x%360));
		//float hue1 = (float)((n-6)/2);
		//float hue1 = (float)(y+(n*2)+(z+(n*6)+0.5+0.5*Math.sin((x)/(n*2))));
		float hue1 = (float)(y/(n*2)+(d*z/(n*6)+0.5+0.5*Math.sin((d*x)/(n*2))));
		hue1 += GeoOptions.OPALHUE.getValue()/360F;
		//ReikaJavaLibrary.pConsole("@ "+x+","+z+": N="+n+"; hue = "+hue1);

		//float hue2 = (float)(Math.cos(x/24D)+Math.sin(z/24D))+(y%360)*0.05F;
		return Color.HSBtoRGB(hue1, 0.4F, 1F);
	}
}
