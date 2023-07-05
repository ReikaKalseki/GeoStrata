/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.CustomCropHandler;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
import Reika.GeoStrata.Blocks.BlockCreepvine.Pieces;
import Reika.GeoStrata.Registry.GeoBlocks;

public class CreepvineHandler implements CustomCropHandler {

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return Pieces.CORE_EMPTY.ordinal();
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id == GeoBlocks.CREEPVINE.getBlockInstance() && Pieces.list[meta].isCore();
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		return Pieces.list[world.getBlockMetadata(x, y, z)].canBeHarvested();
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, Pieces.CORE_5.ordinal(), 3);
		world.func_147451_t(x, y, z);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return is != null && is.getItem() == GeoStrata.creepvineSeeds;
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

	@Override
	public void editTileDataForHarvest(World world, int x, int y, int z) {

	}

	@Override
	public boolean initializedProperly() {
		return GeoBlocks.CREEPVINE.getBlockInstance() != null;
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		int n = Pieces.list[meta].getSeedCount();
		for (int i = 0; i < n; i++) {
			li.add(new ItemStack(GeoStrata.creepvineSeeds));
		}
		return li;
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return Pieces.list[world.getBlockMetadata(x, y, z)].getSeedCount();
	}

	@Override
	public ModEntry getMod() {
		return ModList.GEOSTRATA;
	}

	@Override
	public int getColor() {
		return 0xFFC700;
	}

	@Override
	public String getEnumEntryName() {
		return "CREEPVINE";
	}

	@Override
	public boolean isTileEntity() {
		return false;
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return true;
	}
	/*
	@Override
	public CropFormat getShape() {
		return CropFormat.PLANT;
	}*/

}
