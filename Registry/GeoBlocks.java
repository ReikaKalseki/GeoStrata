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
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Blocks.BlockCaveCrystal;
import Reika.GeoStrata.Blocks.BlockCrystalLamp;
import Reika.GeoStrata.Blocks.BlockRockBrick;
import Reika.GeoStrata.Blocks.BlockRockCobble;
import Reika.GeoStrata.Blocks.BlockSmooth;
import Reika.GeoStrata.Items.ItemBlockCrystal;
import Reika.GeoStrata.Items.ItemBlockRock;

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
		return GeoStrata.config.getBlockID(this.ordinal());
	}

	public Material getBlockMaterial() {
		if (CrystalBlock.class.isAssignableFrom(blockClass))
			return Material.glass;
		return Material.rock;
	}

	public boolean isRock() {
		return RockBlock.class.isAssignableFrom(blockClass);
	}

	public boolean isCrystal() {
		return CrystalBlock.class.isAssignableFrom(blockClass);
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
		if (this == SMOOTH)
			return 	"Smooth "+RockTypes.rockList[meta].getName();
		if (this == COBBLE)
			return RockTypes.rockList[meta].getName()+" Cobblestone";
		if (this == BRICK)
			return RockTypes.rockList[meta].getName()+" Bricks";
		if (this == CRYSTAL)
			return ReikaDyeHelper.dyes[meta].getName()+" "+GeoBlocks.CRYSTAL.getBasicName();
		if (this == LAMP)
			return ReikaDyeHelper.dyes[meta].getName()+" "+GeoBlocks.LAMP.getBasicName();
		return null;
	}

	@Override
	public boolean hasMultiValuedName() {
		return true;
	}

	@Override
	public int getNumberMetadatas() {
		if (this.isCrystal())
			return ReikaDyeHelper.dyes.length;
		if (this.isRock())
			return RockTypes.rockList.length;
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
