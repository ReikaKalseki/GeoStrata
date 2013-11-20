/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;

public enum RockTypes {
	//Generic makeup: Igneous 0-24; Metamorphic 16-40; Sedimentary 40+;

	//NAME------BST-HARD----LO--HI------RARE----HARVESTABILITY
	GRANITE(	60, 10, 	16, 48, 	1, 		EnumToolMaterial.IRON), //Near lava?
	BASALT(		30, 5, 		48, 128, 	1, 		EnumToolMaterial.STONE), //Near lava
	MARBLE(		45, 2.5F, 	16, 32, 	1, 		EnumToolMaterial.STONE), //??
	LIMESTONE(	15, 1, 		48, 128, 	1, 		EnumToolMaterial.WOOD), //Near water bodies
	SHALE(		5, 	1, 		48, 64, 	1, 		EnumToolMaterial.WOOD), //Near water
	SANDSTONE(	10, 2, 		48, 128, 	1, 		EnumToolMaterial.WOOD), //Near sand
	PUMICE(		20, 5, 		0, 	16, 	0.6F, 	EnumToolMaterial.WOOD), //Near water & lava
	SLATE(		30, 5, 		32, 48, 	1, 		EnumToolMaterial.STONE), //Can shale gen
	GNEISS(		30, 7.5F, 	16, 32, 	0.8F, 	EnumToolMaterial.IRON), //Can granite gen
	PERIDOTITE(	30, 5, 		0, 	24, 	0.6F, 	EnumToolMaterial.STONE), //Near lava?
	QUARTZ(		40, 4, 		0, 	64, 	0.5F, 	EnumToolMaterial.IRON), //??
	GRANULITE(	30, 5, 		16, 32, 	0.7F, 	EnumToolMaterial.STONE), //?
	HORNFEL(	60, 10, 	0, 	64, 	0.8F, 	EnumToolMaterial.STONE), //snow biomes?
	MIGMATITE(	30, 5, 		0, 	16, 	0.6F, 	EnumToolMaterial.STONE), //near lava?
	SCHIST(		30, 7.5F,	16, 48,		0.8F,	EnumToolMaterial.STONE),
	ONYX(		40, 6F,		0,	24,		1F,		EnumToolMaterial.IRON);

	public final float blockHardness; //stone has 30
	public final float blastResistance; //stone has 5
	private EnumToolMaterial harvestTool; //null for hand break
	public final int minY;
	public final int maxY;
	public final float rarity;
	private boolean allBiomes = false;

	public static final RockTypes[] rockList = RockTypes.values();

	private RockTypes(float hard, float blast, int ylo, int yhi, float rare, EnumToolMaterial tool) {
		blastResistance = blast;
		blockHardness = hard;
		harvestTool = tool;
		minY = ylo;
		maxY = yhi;
		rarity = rare;
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	public Block instantiate() {
		return null;
	}

	public static RockTypes getTypeFromMetadata(int meta) {
		return rockList[meta];
	}

	public static RockTypes getTypeAtCoords(World world, int x, int y, int z) {
		return rockList[world.getBlockMetadata(x, y, z)];
	}

	public EnumToolMaterial getHarvestMin() {
		return harvestTool;
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

}
