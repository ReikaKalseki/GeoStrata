/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.RedstoneArsenalHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;

public enum RockTypes {
	//Generic makeup: Igneous 0-24; Metamorphic 16-40; Sedimentary 40+;

	//NAME------BST-HARD----LO--HI------RARE----HARVESTABILITY------GPR COLOR
	GRANITE(	75, 10, 	16, 48, 	1, 		ToolMaterial.IRON,	0xC4825E), //Near lava?
	BASALT(		40, 5, 		48, 128, 	1, 		ToolMaterial.STONE,	0x252525), //Near lava
	MARBLE(		55, 2.5F, 	16, 32, 	1, 		ToolMaterial.STONE,	0xB4B4BC), //??
	LIMESTONE(	18, 1, 		48, 128, 	1, 		ToolMaterial.WOOD,	0xD0C4B3), //Near water bodies
	SHALE(		6, 	1, 		48, 64, 	1, 		ToolMaterial.WOOD,	0x676970), //Near water
	SANDSTONE(	12, 2, 		48, 128, 	1, 		ToolMaterial.WOOD,	0xD0AE90), //Near sand
	PUMICE(		25, 5, 		0, 	16, 	0.6F, 	ToolMaterial.WOOD,	0xD6D4CB), //Near water & lava
	SLATE(		40, 5, 		32, 48, 	1, 		ToolMaterial.STONE,	0x484B53), //Can shale gen
	GNEISS(		40, 7.5F, 	16, 32, 	0.8F, 	ToolMaterial.IRON,	0x7A7B79), //Can granite gen
	PERIDOTITE(	40, 5, 		0, 	24, 	0.6F, 	ToolMaterial.STONE,	0x485A4E), //Near lava?
	QUARTZ(		50, 4, 		0, 	64, 	0.5F, 	ToolMaterial.STONE,	0xCCD5DC), //??
	GRANULITE(	40, 5, 		16, 32, 	0.7F, 	ToolMaterial.STONE,	0xC1BF9E), //?
	HORNFEL(	75, 10, 	0, 	64, 	0.8F, 	ToolMaterial.IRON,	0x7B7E87), //snow biomes?
	MIGMATITE(	40, 5, 		0, 	16, 	0.6F, 	ToolMaterial.STONE,	0xA09F94), //near lava?
	SCHIST(		40, 7.5F,	16, 48,		0.8F,	ToolMaterial.STONE,	0x3C3C44),
	ONYX(		50, 6F,		0,	24,		1F,		ToolMaterial.IRON,	0x111111), //Near lava
	OPAL(		30, 3F,		32, 60,		0.125F,	ToolMaterial.STONE,	0xffddff);

	public final float blockHardness;
	public final float blastResistance;
	public final ToolMaterial harvestTool;
	public final int minY;
	public final int maxY;
	public final float rarity;
	public final int rockColor;

	private final HashSet<RockTypes> coincidentTypes = new HashSet();

	private static final HashMap<Block, RockTypes> mappings = new HashMap();

	public static final RockTypes[] rockList = RockTypes.values();

	private RockTypes(float blast, float hard, int ylo, int yhi, float rare, ToolMaterial tool, int color) {
		blastResistance = blast;
		blockHardness = hard*0.675F;
		harvestTool = tool;
		minY = ylo;
		maxY = yhi;
		rarity = rare;
		rockColor = color;
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	public static RockTypes getTypeAtCoords(IBlockAccess world, int x, int y, int z) {
		return getTypeFromID(world.getBlock(x, y, z));
	}

	public static RockTypes getTypeFromID(Block id) {
		return mappings.get(id);
	}

	public ToolMaterial getHarvestMin() {
		return harvestTool;
	}

	public Block getID(RockShapes shape) {
		return shape.getBlock(this);
	}

	public ItemStack getItem(RockShapes shape) {
		return new ItemStack(this.getID(shape), 1, shape.metadata);
	}

	public ItemStack getStair(RockShapes shape) {
		int meta = this.ordinal()*RockShapes.shapeList.length;
		//meta += shape.needsOwnBlock ? shape.metadata : shape.metadata+1;
		meta += shape.ordinal();
		return new ItemStack(GeoBlocks.STAIR.getBlockInstance(), 1, meta);
	}

	public ItemStack getSlab(RockShapes shape) {
		int meta = this.ordinal()*RockShapes.shapeList.length;
		//meta += shape.metadata+1;
		meta += shape.ordinal();
		return new ItemStack(GeoBlocks.SLAB.getBlockInstance(), 1, meta);
	}

	public IIcon getIcon() {
		return this.getIcon(RockShapes.SMOOTH);
	}

	public IIcon getIcon(RockShapes s) {
		return this.getID(s).getIcon(0, s.metadata);
	}

	public boolean isHarvestable(ItemStack held) {
		if (held == null)
			return harvestTool == null;
		if (TinkerToolHandler.getInstance().isPick(held) || TinkerToolHandler.getInstance().isHammer(held)) {
			switch(harvestTool) {
				case WOOD:
					return true;
				case STONE:
				case GOLD:
					return TinkerToolHandler.getInstance().isStoneOrBetter(held);
				case IRON:
					return TinkerToolHandler.getInstance().isIronOrBetter(held);
				case EMERALD:
					return TinkerToolHandler.getInstance().isDiamondOrBetter(held);
				default:
					return false;
			}
		}
		if (held.getItem() == RedstoneArsenalHandler.getInstance().pickID) {
			return RedstoneArsenalHandler.getInstance().pickLevel >= harvestTool.getHarvestLevel();
		}
		Item i = held.getItem();
		switch (harvestTool) {
			case EMERALD: //Diamond
				return held.func_150998_b(Blocks.obsidian);
			case GOLD:
				return held.func_150998_b(Blocks.stone);
			case IRON:
				return held.func_150998_b(Blocks.gold_ore);
			case STONE:
				return held.func_150998_b(Blocks.iron_ore);
			case WOOD:
				return held.func_150998_b(Blocks.stone);
		}
		return false;
	}

	public boolean canGenerateAtXZ(World world, int x, int z, Random r) {
		switch(this) {
			case BASALT:
				return true;
			case GRANITE:
			case GNEISS:
				return true;
			case GRANULITE:
				break;
			case HORNFEL:
				return world.getBiomeGenForCoords(x, z).getEnableSnow();
			case LIMESTONE:
				return true;
			case MARBLE:
				break;
			case MIGMATITE:
				return true;
			case PERIDOTITE:
				break;
			case PUMICE:
				return true;
			case QUARTZ:
				break;
			case SANDSTONE:
				return true;
			case SHALE:
			case SLATE:
				if (BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), Type.SANDY))
					return false;
				if (BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), Type.DRY))
					return false;
				if (BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), Type.SAVANNA))
					return false;
				return true;
			case ONYX:
				return true;
			case SCHIST:
				break;
			case OPAL:
				break;
		}
		return true;
	}

	public boolean canGenerateAtSkipXZ(World world, int x, int y, int z, Random r) {
		if (y > maxY)
			return false;
		if (y < minY)
			return false;
		switch(this) {
			case BASALT:
				return true;
			case GRANITE:
			case GNEISS:
				return true;
			case GRANULITE:
				break;
			case HORNFEL:
				return true;
			case LIMESTONE:
				return true;
			case MARBLE:
				break;
			case MIGMATITE:
				return true;
			case PERIDOTITE:
				break;
			case PUMICE:
				return true;
			case QUARTZ:
				break;
			case SANDSTONE:
				return true;
			case SHALE:
			case SLATE:
				return true;
			case ONYX:
				return ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.lava) != null;
			case SCHIST:
				break;
			case OPAL:
				break;
		}
		return true;
	}

	public boolean canGenerateAt(World world, int x, int y, int z, Random r) {
		if (y > maxY)
			return false;
		if (y < minY)
			return false;
		switch(this) {
			case BASALT:
				return true;
			case GRANITE:
			case GNEISS:
				return true;
			case GRANULITE:
				break;
			case HORNFEL:
				return world.getBiomeGenForCoords(x, z).getEnableSnow();
			case LIMESTONE:
				return true;
			case MARBLE:
				break;
			case MIGMATITE:
				return true;
			case PERIDOTITE:
				break;
			case PUMICE:
				return true;
			case QUARTZ:
				break;
			case SANDSTONE:
				return true;
			case SHALE:
			case SLATE:
				if (BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), Type.SANDY))
					return false;
				if (BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), Type.DRY))
					return false;
				if (BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), Type.SAVANNA))
					return false;
				return true;
			case ONYX:
				return ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.lava) != null;
			case SCHIST:
				break;
			case OPAL:
				break;
		}
		return true;
	}

	public static List<RockTypes> getGennableTypesAt(World world, int x, int y, int z) {
		List<RockTypes> types = new ArrayList();
		Random r = new Random();
		for (int i = 0; i < rockList.length; i++) {
			RockTypes rock = rockList[i];
			if (rock.canGenerateAt(world, x, y, z, r))
				types.add(rock);
		}
		return types;
	}

	public static int getNumberTypesGennableAt(World world, int x, int y, int z) {
		int types = 0;
		Random r = new Random();
		for (int i = 0; i < rockList.length; i++) {
			RockTypes rock = rockList[i];
			if (rock.canGenerateAt(world, x, y, z, r))
				types++;
		}
		return types;
	}

	public Set<RockTypes> getCoincidentTypes() {
		return Collections.unmodifiableSet(coincidentTypes);
	}

	private void calcCoincidentTypes() {
		for (int i = 0; i < rockList.length; i++) {
			RockTypes rock = rockList[i];
			if (rock != this)
				if (ReikaMathLibrary.doRangesOverLap(minY, maxY, rock.minY, rock.maxY))
					coincidentTypes.add(rock);
		}
	}

	public static void loadMappings() {
		for (int i = 0; i < rockList.length; i++) {
			RockTypes rock = rockList[i];
			for (int k = 0; k < RockShapes.shapeList.length; k++) {
				RockShapes s = RockShapes.shapeList[k];
				Block b = s.getBlock(rock);
				mappings.put(b, rock);
			}
			rock.calcCoincidentTypes();
		}
	}

}
