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
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.GeoGen.GeoGen;
import Reika.GeoGen.Base.CrystalBlock;
import Reika.GeoGen.Blocks.BlockCaveCrystal;
import Reika.GeoGen.Blocks.BlockCrystalLamp;
import Reika.GeoGen.Blocks.BlockRockBrick;
import Reika.GeoGen.Blocks.BlockRockCobble;
import Reika.GeoGen.Blocks.BlockSmooth;
import Reika.GeoGen.Items.ItemBlockCrystal;
import Reika.GeoGen.Items.ItemBlockRock;

public enum GeoBlocks implements RegistrationList, IDRegistry {

	SMOOTH(BlockSmooth.class, ItemBlockRock.class, "Smooth Rock"),
	COBBLE(BlockRockCobble.class, ItemBlockRock.class, "Rock Cobble"),
	BRICK(BlockRockBrick.class, ItemBlockRock.class, "Rock Brick"),
	CRYSTAL(BlockCaveCrystal.class, ItemBlockCrystal.class, "Cave Crystal"), //Comes in all dye colors
	LAMP(BlockCrystalLamp.class, ItemBlockCrystal.class, "Crystal Lamp");/*
	SLAB(),
	STAIR();*/

	private Class blockClass;
	private String blockName;
	private Class itemBlock;

	public static final GeoBlocks[] blockList = GeoBlocks.values();

	private GeoBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
	}

	public int getBlockID() {
		return GeoGen.config.getBlockID(this.ordinal());
	}

	public Material getBlockMaterial() {
		if (CrystalBlock.class.isAssignableFrom(blockClass))
			return Material.glass;
		return Material.rock;
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class, Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getBlockID(), this.getBlockMaterial()};
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaJavaLibrary.stripSpaces(blockName);
	}

	@Override
	public Class getObjectClass() {
		return blockClass;
	}

	@Override
	public String getBasicName() {
		return blockName;
	}

	@Override
	public String getMultiValuedName(int meta) {
		return null;
	}

	@Override
	public boolean hasMultiValuedName() {
		return false;
	}

	@Override
	public int getNumberMetadatas() {
		return 1;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return itemBlock;
	}

	@Override
	public boolean hasItemBlock() {
		return true;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 800+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	@Override
	public boolean isItem() {
		return false;
	}

	@Override
	public String getCategory() {
		return "Rock Blocks";
	}
}
