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
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Blocks.BlockCaveCrystal;
import Reika.GeoStrata.Blocks.BlockCrystalBrewer;
import Reika.GeoStrata.Blocks.BlockCrystalLamp;
import Reika.GeoStrata.Blocks.BlockRockBrick;
import Reika.GeoStrata.Blocks.BlockRockCobble;
import Reika.GeoStrata.Blocks.BlockRockDeco;
import Reika.GeoStrata.Blocks.BlockSmooth;
import Reika.GeoStrata.Items.ItemBlockCrystal;
import Reika.GeoStrata.Items.ItemBlockRock;
import Reika.GeoStrata.Items.ItemBlockRockDeco;

public enum GeoBlocks implements RegistrationList, IDRegistry {

	SMOOTH(BlockSmooth.class, ItemBlockRock.class, "Smooth Rock"),
	COBBLE(BlockRockCobble.class, ItemBlockRock.class, "Rock Cobble"),
	BRICK(BlockRockBrick.class, ItemBlockRock.class, "Rock Brick"),
	CRYSTAL(BlockCaveCrystal.class, ItemBlockCrystal.class, "Cave Crystal"), //Comes in all dye colors
	LAMP(BlockCrystalLamp.class, ItemBlockCrystal.class, "Crystal Lamp"),
	DECO(BlockRockDeco.class, ItemBlockRockDeco.class, "Deco Blocks"),
	BREWER(BlockCrystalBrewer.class, null, "Crystal Brewery");

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
		if (this.isCrystal())
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
		return ReikaStringParser.stripSpaces(blockName);
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
		switch(this) {
		case SMOOTH:
			return RockTypes.rockList[meta].getName();
		case COBBLE:
			return RockTypes.rockList[meta].getName()+" Cobblestone";
		case BRICK:
			return RockTypes.rockList[meta].getName()+" Bricks";
		case CRYSTAL:
			return ReikaDyeHelper.dyes[meta].getName()+" "+GeoBlocks.CRYSTAL.getBasicName();
		case LAMP:
			return ReikaDyeHelper.dyes[meta].getName()+" "+GeoBlocks.LAMP.getBasicName();
		case DECO:
			return DecoBlocks.list[meta].getName();
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		switch(this) {
		case BREWER:
			return false;
		default:
			return true;
		}
	}

	@Override
	public int getNumberMetadatas() {
		if (this.isCrystal())
			return ReikaDyeHelper.dyes.length;
		if (this.isRock())
			return RockTypes.rockList.length;
		if (this == DECO)
			return DecoBlocks.list.length;
		return 1;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return itemBlock;
	}

	@Override
	public boolean hasItemBlock() {
		return itemBlock != null;
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

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Block getBlockInstance() {
		return GeoStrata.blocks[this.ordinal()];
	}

	public int getID() {
		return this.getBlockID();
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}
}
