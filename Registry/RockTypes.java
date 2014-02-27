/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.RedstoneArsenalHandler;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;

public enum RockTypes {
	//Generic makeup: Igneous 0-24; Metamorphic 16-40; Sedimentary 40+;

	//NAME------BST-HARD----LO--HI------RARE----HARVESTABILITY
	GRANITE(	75, 10, 	16, 48, 	1, 		EnumToolMaterial.IRON,	0xC4825E), //Near lava?
	BASALT(		40, 5, 		48, 128, 	1, 		EnumToolMaterial.STONE,	0x252525), //Near lava
	MARBLE(		55, 2.5F, 	16, 32, 	1, 		EnumToolMaterial.STONE,	0xB4B4BC), //??
	LIMESTONE(	18, 1, 		48, 128, 	1, 		EnumToolMaterial.WOOD,	0xD0C4B3), //Near water bodies
	SHALE(		6, 	1, 		48, 64, 	1, 		EnumToolMaterial.WOOD,	0x676970), //Near water
	SANDSTONE(	12, 2, 		48, 128, 	1, 		EnumToolMaterial.WOOD,	0xD0AE90), //Near sand
	PUMICE(		25, 5, 		0, 	16, 	0.6F, 	EnumToolMaterial.WOOD,	0xD6D4CB), //Near water & lava
	SLATE(		40, 5, 		32, 48, 	1, 		EnumToolMaterial.STONE,	0x484B53), //Can shale gen
	GNEISS(		40, 7.5F, 	16, 32, 	0.8F, 	EnumToolMaterial.IRON,	0x7A7B79), //Can granite gen
	PERIDOTITE(	40, 5, 		0, 	24, 	0.6F, 	EnumToolMaterial.STONE,	0x485A4E), //Near lava?
	QUARTZ(		50, 4, 		0, 	64, 	0.5F, 	EnumToolMaterial.STONE,	0xCCD5DC), //??
	GRANULITE(	40, 5, 		16, 32, 	0.7F, 	EnumToolMaterial.STONE,	0xC1BF9E), //?
	HORNFEL(	75, 10, 	0, 	64, 	0.8F, 	EnumToolMaterial.IRON,	0x7B7E87), //snow biomes?
	MIGMATITE(	40, 5, 		0, 	16, 	0.6F, 	EnumToolMaterial.STONE,	0xA09F94), //near lava?
	SCHIST(		40, 7.5F,	16, 48,		0.8F,	EnumToolMaterial.STONE,	0x3C3C44),
	ONYX(		50, 6F,		0,	24,		1F,		EnumToolMaterial.IRON,	0x111111), //Near lava
	OPAL(		30, 3F,		32, 60,		0.125F,	EnumToolMaterial.STONE,	0xffddff);

	public final float blockHardness;
	public final float blastResistance;
	private EnumToolMaterial harvestTool;
	public final int minY;
	public final int maxY;
	public final float rarity;
	private boolean allBiomes = false;
	public final int rockColor;

	private static HashMap<RockTypes, ArrayList<GeoBlocks>> rockMappings = new HashMap();
	private static HashMap<GeoBlocks, ArrayList<RockTypes>> IDMappings = new HashMap();

	public static final RockTypes[] rockList = RockTypes.values();

	private RockTypes(float blast, float hard, int ylo, int yhi, float rare, EnumToolMaterial tool, int color) {
		blastResistance = blast;
		blockHardness = hard;
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
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return getTypeFromIDandMeta(id, meta);
	}

	public static RockTypes getTypeFromIDandMeta(int ID, int meta) {
		GeoBlocks g = GeoBlocks.getFromID(ID);
		if (g == null)
			return null;
		ArrayList<RockTypes> li = IDMappings.get(g);
		if (meta >= li.size())
			return null;
		return li.get(meta);
	}

	public EnumToolMaterial getHarvestMin() {
		return harvestTool;
	}

	public int getBlockOffset() {
		return this.ordinal()/16;
	}

	public int getBlockMetadata() {
		return this.ordinal()%16;
	}

	public static int getTypesForID(int id) {
		GeoBlocks g = GeoBlocks.getFromID(id);
		return g != null && IDMappings.get(g) != null ? IDMappings.get(g).size() : 0;
	}

	public GeoBlocks getBlock(RockShapes shape) {
		return rockMappings.get(this).get(shape.ordinal());
	}

	public int getID(RockShapes shape) {
		return this.getBlock(shape).getBlockID();
	}

	public ItemStack getItem(RockShapes shape) {
		return new ItemStack(this.getID(shape), 1, this.ordinal()%16);
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
		if (held.itemID == RedstoneArsenalHandler.getInstance().pickID) {
			return RedstoneArsenalHandler.getInstance().pickLevel >= harvestTool.getHarvestLevel();
		}
		Item i = held.getItem();
		switch (harvestTool) {
		case EMERALD: //Diamond
			return held.canHarvestBlock(Block.obsidian);
		case GOLD:
			return held.canHarvestBlock(Block.stone);
		case IRON:
			return held.canHarvestBlock(Block.oreGold);
		case STONE:
			return held.canHarvestBlock(Block.oreIron);
		case WOOD:
			return held.canHarvestBlock(Block.stone);
		}
		return false;
	}

	public boolean canGenerateAt(World world, int x, int y, int z, Random r) {
		int dist = 12;
		if (y > maxY)
			return false;
		if (y < minY)
			return false;
		switch(this) {
		case BASALT:
			return true;
		case GRANITE:
			return true;
		case GNEISS:
			return GRANITE.canGenerateAt(world, x, y, z, r);
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
			return !BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), Type.DESERT);
		case SLATE:
			return SHALE.canGenerateAt(world, x, y, z, r);
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

	public List<RockTypes> getCoincidentTypes() {
		List<RockTypes> types = new ArrayList();
		Random r = new Random();
		for (int i = 0; i < rockList.length; i++) {
			RockTypes rock = rockList[i];
			if (ReikaMathLibrary.doRangesOverLap(minY, maxY, rock.minY, rock.maxY))
				types.add(rock);
		}
		return types;
	}

	static {
		for (int i = 0; i < rockList.length; i++) {
			RockTypes rock = rockList[i];
			int offset = rock.getBlockOffset();
			ArrayList<GeoBlocks> li = new ArrayList();
			GeoBlocks smooth = RockShapes.SMOOTH.getBlockType(rock);
			ArrayList<RockTypes> li2 = IDMappings.get(smooth);
			boolean fill = li2 == null;
			if (fill) {
				li2 = new ArrayList();
			}
			for (int k = 0; k < RockShapes.shapeList.length; k++) {
				RockShapes shape = RockShapes.shapeList[k];
				GeoBlocks b = shape.getBlockType(rock);
				li.add(b);
				if (fill) {
					IDMappings.put(b, li2);
				}
			}
			rockMappings.put(rock, li);
			li2.add(rock);
		}
	}

}
