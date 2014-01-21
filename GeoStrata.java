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
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.RetroGenController;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ExpandedFluid;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;
import Reika.GeoStrata.Registry.DecoBlocks;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.GeoOptions;
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
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
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

	public static ExpandedFluid crystal = (ExpandedFluid)new ExpandedFluid("potion crystal").setColor(0x66aaff).setGameName("Crystal").setLuminosity(15);

	@SidedProxy(clientSide="Reika.GeoStrata.GeoClient", serverSide="Reika.GeoStrata.GeoCommon")
	public static GeoCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(this);
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, GeoOptions.LOGLOADING.getState(), GeoOptions.DEBUGMODE.getState(), false);
		proxy.registerSounds();

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.loadClasses();
		this.loadDictionary();
		this.genRocks();

		FluidRegistry.registerFluid(crystal);

		this.addRecipes();
		proxy.registerRenderers();
		GameRegistry.registerTileEntity(TileEntityCrystalBrewer.class, "GeoBrewer");
		NetworkRegistry.instance().registerGuiHandler(instance, new GeoGuiHandler());
		if (GeoOptions.RETROGEN.getState()) {
			RetroGenController.getInstance().addRetroGenerator(new RetroCrystalGenerator());
			//Set state back
		}
	}

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		logger.log("Loading Liquid Icons");
		Icon cry = event.map.registerIcon("GeoStrata:liqcrystal3");
		crystal.setIcons(cry);
	}

	@Override
	@EventHandler // Like the modsLoaded thing from ModLoader
	public void postload(FMLPostInitializationEvent evt) {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < RockTypes.rockList.length; i++) {
				RockTypes rock = RockTypes.rockList[i];
				ItemStack smooth = rock.getSmoothItem();
				ItemStack cobble = rock.getCobbleItem();
				GrinderAPI.addRecipe(smooth, cobble);
				GrinderAPI.addRecipe(cobble, new ItemStack(Block.gravel));

				BlockColorInterface.addGPRBlockColor(rock.getSmoothID(), i, rock.rockColor);
				BlockColorInterface.addGPRBlockColor(rock.getCobbleID(), i, rock.rockColor);
				BlockColorInterface.addGPRBlockColor(rock.getBrickID(), i, rock.rockColor);
			}
			/*
			for (int i = 0; i < DecoBlocks.list.length; i++) {
				DecoBlocks block = DecoBlocks.list[i];
				int color = BlockColorInterface.getColor(block.material.blockID, 0);
				BlockColorInterface.addGPRBlockColor(GeoBlocks.DECO.getBlockID(), i, color);
			}*/

			for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				BlockColorInterface.addGPRBlockColor(GeoBlocks.CRYSTAL.getBlockID(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(GeoBlocks.LAMP.getBlockID(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(GeoBlocks.SUPER.getBlockID(), i, dye.color);
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
				ItemStack rockblock = rock.getSmoothItem();
				ReikaThaumHelper.addAspects(rockblock, Aspect.STONE, (int)(rock.blockHardness/5));
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
			ItemStack cobble = type.getCobbleItem();
			ItemStack rock = type.getSmoothItem();
			OreDictionary.registerOre("cobblestone", cobble);
			OreDictionary.registerOre("stone", rock);
			OreDictionary.registerOre("rock"+type.getName(), rock);
			OreDictionary.registerOre(type.getName().toLowerCase(), rock);
		}
		OreDictionary.registerOre("sandstone", RockTypes.SANDSTONE.getSmoothItem());
		OreDictionary.registerOre("sandstone", Block.sandStone);

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ReikaDyeHelper color = ReikaDyeHelper.dyes[i];
			ItemStack crystal = new ItemStack(GeoBlocks.CRYSTAL.getBlockID(), 1, i);
			ItemStack shard = new ItemStack(GeoItems.SHARD.getShiftedItemID(), 1, i);
			OreDictionary.registerOre(color.getOreDictName()+"Crystal", crystal);
			OreDictionary.registerOre(color.getOreDictName()+"CrystalShard", shard);
		}
	}

	public static void genRocks() {
		GameRegistry.registerWorldGenerator(new RockGenerator());
		GameRegistry.registerWorldGenerator(new CrystalGenerator());
	}

	public static void addRecipes() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes type = RockTypes.rockList[i];
			ItemStack smooth = type.getSmoothItem();
			ItemStack cobble = type.getCobbleItem();
			ItemStack brick = type.getBrickItem();
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(brick, 4), new Object[]{
				"SS", "SS", 'S', smooth});
			FurnaceRecipes.smelting().addSmelting(cobble.itemID, cobble.getItemDamage(), smooth, 0.2F);
			//GameRegistry.addShapelessRecipe(new ItemStack(Block.cobblestone), cobble);
		}

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = GeoItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i);
			GameRegistry.addRecipe(lamp, " s ", "sss", "SSS", 's', shard, 'S', ReikaItemHelper.stoneSlab);
		}

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = GeoItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i);
			ItemStack potion = new ItemStack(GeoBlocks.SUPER.getBlockID(), 1, i);
			GameRegistry.addRecipe(potion, "RsG", "sss", "SDS", 's', shard, 'S', Block.obsidian, 'D', Block.blockGold, 'R', Block.blockRedstone, 'G', Block.glowStone);
			GameRegistry.addRecipe(potion, "RlG", "SDS", 'l', lamp, 'S', Block.obsidian, 'D', Block.blockGold, 'R', Block.blockRedstone, 'G', Block.glowStone);
		}

		for (int i = 0; i < DecoBlocks.list.length; i++) {
			DecoBlocks block = DecoBlocks.list[i];
			block.addSizedCrafting(4, "BB", "BB", 'B', block.material);
		}

		GameRegistry.addRecipe(new ItemStack(GeoBlocks.BREWER.getBlockID(), 1, 0), "NNN", "NBN", "SSS", 'N', Item.netherQuartz, 'S', Block.stone, 'B', Item.brewingStand);


		if (ModList.THERMALEXPANSION.isLoaded()) {
			FluidStack crystal = FluidRegistry.getFluidStack("potion crystal", 8000);
			int energy = 40000;
			for (int i = 0; i < 16; i++) {
				NBTTagCompound toSend = new NBTTagCompound();
				toSend.setInteger("energy", energy);
				toSend.setCompoundTag("input", new NBTTagCompound());
				toSend.setCompoundTag("output", new NBTTagCompound());
				GeoItems.SHARD.getStackOfMetadata(i).writeToNBT(toSend.getCompoundTag("input"));
				crystal.writeToNBT(toSend.getCompoundTag("output"));
				FMLInterModComms.sendMessage(ModList.THERMALEXPANSION.modLabel, "CrucibleRecipe", toSend);
			}
		}
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
