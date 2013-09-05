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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;

public enum RockTypes {

	GRANITE(60, 10, 16, 48, 1, EnumToolMaterial.IRON, new BiomeGenBase[]{}, true),
	BASALT(30, 5, 48, 128, 1, EnumToolMaterial.STONE, new BiomeGenBase[]{}, true),
	MARBLE(45, 2.5F, 16, 32, 1, EnumToolMaterial.STONE, new BiomeGenBase[]{}, true),
	LIMESTONE(15, 1, 48, 128, 1, EnumToolMaterial.WOOD),
	SHALE(5, 1, 48, 64, 1, EnumToolMaterial.WOOD, new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.desertHills}, true),
	SANDSTONE(10, 2, 48, 64, 1, EnumToolMaterial.WOOD, new BiomeGenBase[]{}, true),
	PUMICE(20, 5, 0, 16, 0.6F, EnumToolMaterial.WOOD, new BiomeGenBase[]{}, true),
	SLATE(30, 5, 32, 48, 1, EnumToolMaterial.STONE, new BiomeGenBase[]{}, true),
	GNEISS(30, 7.5F, 16, 32, 0.8F, EnumToolMaterial.IRON, new BiomeGenBase[]{}, true),
	PERIDOTITE(30, 5, 0, 32, 0.6F, EnumToolMaterial.STONE, new BiomeGenBase[]{}, true),
	QUARTZ(40, 4, 0, 16, 0.5F, EnumToolMaterial.IRON, new BiomeGenBase[]{}, true),
	GRANULITE(30, 5, 16, 32, 0.7F, EnumToolMaterial.STONE, new BiomeGenBase[]{}, true),
	HORNFEL(30, 5, 16, 48, 0.8F, EnumToolMaterial.STONE, new BiomeGenBase[]{}, true),
	MIGMATITE(30, 5, 0, 16, 0.6F, EnumToolMaterial.STONE, new BiomeGenBase[]{}, true);

	private float blockHardness; //stone has 30
	private float blastResistance; //stone has 5
	private EnumToolMaterial harvestTool; //null for hand break
	private int minY;
	private int maxY;
	private float rarity;
	private boolean allBiomes = false;
	private List<BiomeGenBase> biomeList;

	public static final RockTypes[] rockList = RockTypes.values();

	private RockTypes(float hard, float blast, int ylo, int yhi, float rare, EnumToolMaterial tool) {
		blastResistance = blast;
		blockHardness = hard;
		harvestTool = tool;
		minY = ylo;
		maxY = yhi;
		rarity = rare;
		allBiomes = true;
	}

	private RockTypes(float hard, float blast, int ylo, int yhi, float rare, EnumToolMaterial tool, BiomeGenBase[] biomes, boolean ex) {
		blastResistance = blast;
		blockHardness = hard;
		harvestTool = tool;
		minY = ylo;
		maxY = yhi;
		rarity = rare;
		if (ex) {
			biomeList = ReikaBiomeHelper.getAllBiomes();
			for (int i = 0; i < biomes.length; i++) {
				biomeList.remove(biomes[i]);
			}
		}
		else {
			for (int i = 0; i < biomes.length; i++) {
				biomeList.add(biomes[i]);
			}
		}
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

	public float getHardness() {
		return blockHardness;
	}

	public float getResistance() {
		return blastResistance;
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

	public float getRarity() {
		return rarity;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinY() {
		return minY;
	}

	public boolean canGenerateInBiome(BiomeGenBase biome) {
		return allBiomes || biomeList.contains(biome);
	}

}
