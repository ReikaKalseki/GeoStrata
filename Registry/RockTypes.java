/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Registry;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public enum RockTypes {

	GRANITE(60, 10, EnumToolMaterial.IRON),
	BASALT(30, 5, EnumToolMaterial.STONE),
	MARBLE(45, 2.5F, EnumToolMaterial.STONE),
	LIMESTONE(15, 1, EnumToolMaterial.WOOD),
	SHALE(5, 1, EnumToolMaterial.WOOD),
	SANDSTONE(10, 2, EnumToolMaterial.WOOD),
	PUMICE(20, 5, EnumToolMaterial.WOOD),
	SLATE(30, 5, EnumToolMaterial.STONE),
	GNEISS(30, 7.5F, EnumToolMaterial.IRON),
	PERIDOTITE(30, 5, EnumToolMaterial.STONE),
	QUARTZ(40, 4, EnumToolMaterial.IRON),
	GRANULITE(30, 5, EnumToolMaterial.STONE),
	HORNFEL(30, 5, EnumToolMaterial.STONE),
	MIGMATITE(30, 5, EnumToolMaterial.STONE);

	private float blockHardness; //stone has 30
	private float blastResistance; //stone has 5
	private EnumToolMaterial harvestTool; //null for hand break

	private static final RockTypes[] rockList = RockTypes.values();

	private RockTypes(float hard, float blast, EnumToolMaterial tool) {
		blastResistance = blast;
		blockHardness = hard;
		harvestTool = tool;
	}

	public String getName() {
		return this.name().substring(0, 1)+this.name().substring(1).toLowerCase()+" Rock";
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
		switch (harvestTool) {
		case EMERALD: //Diamond
			return held.itemID == Item.pickaxeDiamond.itemID;// || (held instanceof ItemPickaxe && (((ItemPickaxe)held).));
		case GOLD:
			return held.getItem() instanceof ItemPickaxe;
		case IRON:
			return (held.itemID == Item.pickaxeDiamond.itemID || held.itemID == Item.pickaxeIron.itemID);
		case STONE:
			return held.getItem() instanceof ItemPickaxe && held.itemID != Item.pickaxeWood.itemID;
		case WOOD:
			return held.getItem() instanceof ItemPickaxe;
		}
		return false;
	}

}
