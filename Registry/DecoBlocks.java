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

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;

public enum DecoBlocks {
	OBSIDIBRICKS("Obsidian Bricks", 30F, 2000F, EnumToolMaterial.EMERALD);

	private String name;
	private float blockHardness;
	private float blastResistance;
	private EnumToolMaterial mat;

	public static final DecoBlocks[] list = values();

	private DecoBlocks(String n, float hard, float blast, EnumToolMaterial tool) {
		name = n;
		mat = tool;
		blastResistance = blast;
		blockHardness = hard;
	}

	public String getName() {
		return name;
	}

	public String getTex() {
		return this.name().toLowerCase();
	}

	public float getHardness() {
		return blockHardness;
	}

	public float getResistance() {
		return blastResistance;
	}

	public EnumToolMaterial getHarvestMin() {
		return mat;
	}

	public boolean isHarvestable(ItemStack held) {
		if (held == null)
			return mat == null;
		if (TinkerToolHandler.getInstance().isPick(held)) {
			switch(mat) {
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
		switch (mat) {
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

	public static DecoBlocks getTypeAtCoords(World world, int x, int y, int z) {
		return list[world.getBlockMetadata(x, y, z)];
	}

	public static DecoBlocks getTypeFromMetadata(int meta) {
		return list[meta];
	}
}
