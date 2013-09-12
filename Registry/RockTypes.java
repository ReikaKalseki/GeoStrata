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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
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
	MIGMATITE(	30, 5, 		0, 	16, 	0.6F, 	EnumToolMaterial.STONE); //near lava?

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
		if (TinkerToolHandler.getInstance().isPick(held)) {
			switch(harvestTool) {
			case WOOD:
				return true;
			case STONE:
			case GOLD:
				return TinkerToolHandler.getInstance().isStoneOrBetterPick(held);
			case IRON:
				return TinkerToolHandler.getInstance().isIronOrBetterPick(held);
			case EMERALD:
				return TinkerToolHandler.getInstance().isDiamondOrBetterPick(held);
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
		switch(this) {
		case BASALT:
			break;
		case GNEISS:
			break;
		case GRANITE:
			break;
		case GRANULITE:
			break;
		case HORNFEL:
			break;
		case LIMESTONE:
			break;
		case MARBLE:
			break;
		case MIGMATITE:
			break;
		case PERIDOTITE:
			break;
		case PUMICE:
			break;
		case QUARTZ:
			break;
		case SANDSTONE:
			break;
		case SHALE:
			break;
		case SLATE:
			break;
		default:
			return true;
		}
		return true;
	}

}
