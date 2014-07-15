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

import java.net.URL;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.DonatorController;
import Reika.DragonAPI.Auxiliary.RetroGenController;
import Reika.DragonAPI.Auxiliary.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.VanillaIntegrityTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.EnhancedFluid;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ThermalRecipeHelper;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Bees.CrystalBees;
import Reika.GeoStrata.Guardian.GuardianCommand;
import Reika.GeoStrata.Guardian.GuardianStoneManager;
import Reika.GeoStrata.Guardian.TileEntityGuardianStone;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.World.CrystalGenerator;
import Reika.GeoStrata.World.RetroCrystalGenerator;
import Reika.GeoStrata.World.RockGenerator;
import Reika.RotaryCraft.API.BlockColorInterface;
import Reika.RotaryCraft.API.GrinderAPI;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod( modid = GeoStrata.MOD_NAME, name=GeoStrata.MOD_NAME, version="Gamma", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")
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

	public static EnhancedFluid crystal = (EnhancedFluid)new EnhancedFluid("potion crystal").setColor(0x66aaff).setGameName("Crystal").setLuminosity(15).setTemperature(500);

	@SidedProxy(clientSide="Reika.GeoStrata.GeoClient", serverSide="Reika.GeoStrata.GeoCommon")
	public static GeoCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(GuardianStoneManager.instance);
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, false);
		proxy.registerSounds();
		this.basicSetup(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.loadClasses();
		this.loadDictionary();
		this.genRocks();

		FluidRegistry.registerFluid(crystal);

		GeoRecipes.addRecipes();
		proxy.registerRenderers();

		GameRegistry.registerTileEntity(TileEntityCrystalBrewer.class, "GeoBrewer");
		GameRegistry.registerTileEntity(TileEntityGuardianStone.class, "GeoGuardianStone");
		GameRegistry.registerTileEntity(TileEntityCrystalPlant.class, "GeoCrystalPlant");
		GameRegistry.registerTileEntity(TileEntityAccelerator.class, "GeoAccelerator");

		NetworkRegistry.instance().registerGuiHandler(instance, new GeoGuiHandler());
		if (GeoOptions.RETROGEN.getState()) {
			RetroGenController.getInstance().addRetroGenerator(new RetroCrystalGenerator());
			//Set state back
		}

		//TickRegistry.registerTickHandler(TileAccelerator.instance, Side.SERVER);
		//TickRegistry.registerTickHandler(TileAccelerator.instance, Side.CLIENT);

		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Block.obsidian);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Block.stone);
		VanillaIntegrityTracker.instance.addWatchedBlock(instance, Block.blockNetherQuartz);

		DonatorController.instance.addDonation(instance, "sophieguerette", 10.00F);

		if (ModList.THERMALEXPANSION.isLoaded()) {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes r = RockTypes.rockList[i];
				ItemStack smooth = r.getItem(RockShapes.SMOOTH);
				ItemStack cobble = r.getItem(RockShapes.COBBLESTONE);
				int energy = (int)(200+800*(r.blockHardness-1));
				ThermalRecipeHelper.addPulverizerRecipe(smooth, cobble, energy); //make proportional to hardness
				ThermalRecipeHelper.addPulverizerRecipe(cobble, new ItemStack(Block.sand), new ItemStack(Block.gravel), 20, energy);
			}
		}

		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.FORESTRY, "Access to crystal bees which have valuable genetics");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.TWILIGHT, "Dense crystal generation");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.THAUMCRAFT, "High crystal aspect values");
	}

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.textureType == 0) {
			logger.log("Loading Liquid Icons");
			Icon cry = event.map.registerIcon("GeoStrata:liqcrystal3");
			crystal.setIcons(cry);
		}
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new GuardianCommand());
	}

	@Override
	@EventHandler // Like the modsLoaded thing from ModLoader
	public void postload(FMLPostInitializationEvent evt) {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes rock = RockTypes.rockList[i];
				ItemStack smooth = rock.getItem(RockShapes.SMOOTH);
				ItemStack cobble = rock.getItem(RockShapes.COBBLESTONE);
				GrinderAPI.addRecipe(smooth, cobble);
				GrinderAPI.addRecipe(cobble, new ItemStack(Block.gravel));

				for (int k = 0; k < RockShapes.shapeList.length; k++) {
					RockShapes shape = RockShapes.shapeList[k];
					BlockColorInterface.addGPRBlockColor(rock.getID(shape), rock.getBlockMetadata(), rock.rockColor);
				}
			}

			for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				BlockColorInterface.addGPRBlockColor(GeoBlocks.CRYSTAL.getBlockID(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(GeoBlocks.LAMP.getBlockID(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(GeoBlocks.SUPER.getBlockID(), i, dye.color);
				ItemStack shard = GeoItems.SHARD.getStackOfMetadata(i);
				GrinderAPI.addRecipe(new ItemStack(GeoBlocks.CRYSTAL.getBlockID(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 12));
				GrinderAPI.addRecipe(new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i), ReikaItemHelper.getSizedItemStack(shard, 4));
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				ItemStack crystal = new ItemStack(GeoBlocks.CRYSTAL.getBlockID(), 1, i);
				ItemStack lamp = new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i);
				ItemStack shard = GeoItems.SHARD.getStackOfMetadata(i);
				ArrayList<Aspect> li = CrystalPotionController.getAspects(dye);

				ReikaThaumHelper.addAspects(shard, Aspect.CRYSTAL, 1);
				ReikaThaumHelper.addAspects(crystal, Aspect.CRYSTAL, 20);
				ReikaThaumHelper.addAspects(crystal, Aspect.AURA, 4);
				ReikaThaumHelper.addAspects(crystal, Aspect.LIGHT, 3);
				ReikaThaumHelper.addAspects(crystal, Aspect.MAGIC, 6);
				ReikaThaumHelper.addAspects(lamp, Aspect.LIGHT, 8);

				for (int k = 0; k < li.size(); k++) {
					Aspect as = li.get(k);
					ReikaThaumHelper.addAspects(shard, as, 2);
					ReikaThaumHelper.addAspects(crystal, as, 16);
				}
			}

			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes rock = RockTypes.rockList[i];
				ItemStack rockblock = rock.getItem(RockShapes.SMOOTH);
				ReikaThaumHelper.addAspects(rockblock, Aspect.STONE, (int)(rock.blockHardness/5));
			}
		}

		if (ModList.EXTRAUTILS.isLoaded()) {
			int id = ExtraUtilsHandler.getInstance().decoID;
			if (id > 0) {
				ItemStack burned = new ItemStack(id, 1, ExtraUtilsHandler.getInstance().burntQuartz);
				ReikaRecipeHelper.addSmelting(RockTypes.QUARTZ.getItem(RockShapes.SMOOTH), burned, 0.05F);
			}
		}

		if (ModList.FORESTRY.isLoaded()) {
			CrystalBees.register();
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void extraXP(AttackEntityEvent ev) {
		EntityPlayer ep = ev.entityPlayer;
		Entity tg = ev.target;
		if (tg instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)tg;
			for (int i = 0; i < ep.inventory.mainInventory.length; i++) {
				ItemStack is = ep.inventory.mainInventory[i];
				if (is != null) {
					if (is.itemID == GeoItems.PENDANT3.getShiftedItemID()) {
						CrystalBlock.applyEffectFromColor(100, 3, elb, ReikaDyeHelper.getColorFromItem(is));
					}
					else if (is.itemID == GeoItems.PENDANT.getShiftedItemID()) {
						CrystalBlock.applyEffectFromColor(100, 1, elb, ReikaDyeHelper.getColorFromItem(is));
					}
				}
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void extraXP(LivingDropsEvent ev) {
		EntityLivingBase e = ev.entityLiving;
		DamageSource src = ev.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			int meta = ReikaDyeHelper.PURPLE.ordinal();
			int val = e instanceof EntityPlayer ? 25 : e instanceof EntityLiving ? ((EntityLiving)e).experienceValue : 5;
			if (val == 0)
				val = 5;
			if (e instanceof EntityDragon)
				val = 10000;
			if (ReikaInventoryHelper.checkForItemStack(GeoItems.PENDANT3.getStackOfMetadata(meta), ep.inventory, false)) {
				for (int i = 0; i < 3; i++) {
					double px = e.posX;
					double pz = e.posZ;
					EntityXPOrb xp = new EntityXPOrb(e.worldObj, px, e.posY, pz, val);
					if (!e.worldObj.isRemote)
						e.worldObj.spawnEntityInWorld(xp);
				}
			}
			else if (ReikaInventoryHelper.checkForItemStack(GeoItems.PENDANT.getStackOfMetadata(meta), ep.inventory, false)) {
				double px = e.posX;
				double pz = e.posZ;
				EntityXPOrb xp = new EntityXPOrb(e.worldObj, px, e.posY, pz, val);
				if (!e.worldObj.isRemote)
					e.worldObj.spawnEntityInWorld(xp);
			}
		}
	}

	public static void loadClasses() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, GeoBlocks.blockList, blocks);
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, GeoItems.itemList, items);
	}

	public static void loadDictionary() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes type = RockTypes.rockList[i];
			ItemStack cobble = type.getItem(RockShapes.COBBLESTONE);
			ItemStack rock = type.getItem(RockShapes.SMOOTH);
			OreDictionary.registerOre("cobblestone", cobble);
			OreDictionary.registerOre("stone", rock);
			OreDictionary.registerOre("rock"+type.getName(), rock);
			OreDictionary.registerOre("stone"+type.getName(), rock);
			OreDictionary.registerOre(type.getName().toLowerCase(), rock);
		}
		OreDictionary.registerOre("sandstone", RockTypes.SANDSTONE.getItem(RockShapes.SMOOTH));
		OreDictionary.registerOre("sandstone", Block.sandStone);

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ReikaDyeHelper color = ReikaDyeHelper.dyes[i];
			ItemStack crystal = new ItemStack(GeoBlocks.CRYSTAL.getBlockID(), 1, i);
			ItemStack shard = new ItemStack(GeoItems.SHARD.getShiftedItemID(), 1, i);
			OreDictionary.registerOre(color.getOreDictName()+"Crystal", crystal);
			OreDictionary.registerOre(color.getOreDictName()+"CrystalShard", shard);
			OreDictionary.registerOre("dyeCrystal", crystal);
			OreDictionary.registerOre("caveCrystal", crystal);
			OreDictionary.registerOre("shardCrystal", shard);
		}
	}

	public static void genRocks() {
		GameRegistry.registerWorldGenerator(new RockGenerator());
		GameRegistry.registerWorldGenerator(new CrystalGenerator());
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
