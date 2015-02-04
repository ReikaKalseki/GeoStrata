/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.MekToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public enum DecoBlocks {
	OBSIDIBRICKS("Obsidian Bricks", 30F, 2000F, 1, ToolMaterial.EMERALD, Blocks.obsidian),
	QUARTZBRICKS("Nether Quartz Bricks", 1.2F, 5F, 2, ToolMaterial.IRON, Blocks.quartz_block),
	GLOWBRICKS("Glowstone Bricks", 0.75F, 3F, 2, ToolMaterial.WOOD, Blocks.glowstone),
	REDBRICKS("Redstone Bricks", 1F, 4F, 4, ToolMaterial.IRON, Blocks.redstone_block),
	LAPISBRICKS("Lapis Lazuli Bricks", 1F, 4F, 4, ToolMaterial.STONE, Blocks.lapis_block),
	EMERALDBRICKS("Emerald Bricks", 1F, 4F, 8, ToolMaterial.IRON, Blocks.emerald_block);

	private String name;
	private float blockHardness;
	private float blastResistance;
	private ToolMaterial mat;
	public final Block material;
	public final int recipeMultiplier;

	public static final DecoBlocks[] list = values();

	private DecoBlocks(String n, float hard, float blast, int recipe, ToolMaterial tool, Block b) {
		name = n;
		mat = tool;
		blastResistance = blast;
		blockHardness = hard;
		material = b;
		recipeMultiplier = recipe;
	}

	public void addCrafting(Object... data) {
		GameRegistry.addRecipe(this.getItem(), data);
	}

	public void addSizedCrafting(int num, Object... data) {
		GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getItem(), num), data);
	}

	public ItemStack getItem() {
		return new ItemStack(GeoBlocks.DECO.getBlockInstance(), 1, this.ordinal());
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

	public ToolMaterial getHarvestMin() {
		return mat;
	}

	public boolean isHarvestable(ItemStack held) {
		if (held == null)
			return mat == null;
		if (MekToolHandler.getInstance().isPickTypeTool(held))
			return true;
		if (TinkerToolHandler.getInstance().isPick(held) || TinkerToolHandler.getInstance().isHammer(held)) {
			switch(mat) {
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
		switch (mat) {
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

	public static DecoBlocks getTypeAtCoords(World world, int x, int y, int z) {
		return list[world.getBlockMetadata(x, y, z)];
	}

	public static DecoBlocks getTypeFromMetadata(int meta) {
		return list[meta];
	}
}
