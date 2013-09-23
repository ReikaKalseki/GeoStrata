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
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.MekanismHandler;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public enum DecoBlocks {
	OBSIDIBRICKS("Obsidian Bricks", 30F, 2000F, EnumToolMaterial.EMERALD),
	QUARTZBRICKS("Nether Quartz Bricks", 1.2F, 5F, EnumToolMaterial.IRON),
	GLOWBRICKS("Glowstone Bricks", 0.75F, 3F, EnumToolMaterial.WOOD),
	REDBRICKS("Redstone Bricks", 1F, 4F, EnumToolMaterial.IRON),
	LAPISBRICKS("Lapis Lazuli Bricks", 1F, 4F, EnumToolMaterial.STONE),
	EMERALDBRICKS("Emerald Bricks", 1F, 4F, EnumToolMaterial.IRON);

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

	public void addCrafting(Object... data) {
		GameRegistry.addRecipe(this.getItem(), data);
	}

	public void addSizedCrafting(int num, Object... data) {
		GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getItem(), num), data);
	}

	public ItemStack getItem() {
		return new ItemStack(GeoBlocks.DECO.getBlockID(), 1, this.ordinal());
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
		if (MekanismHandler.getInstance().isPaxel(held))
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
