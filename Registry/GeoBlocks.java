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

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Blocks.BlockCaveCrystal;
import Reika.GeoStrata.Blocks.BlockCrystalBrewer;
import Reika.GeoStrata.Blocks.BlockCrystalLamp;
import Reika.GeoStrata.Blocks.BlockGuardianStone;
import Reika.GeoStrata.Blocks.BlockRockBrick;
import Reika.GeoStrata.Blocks.BlockRockCobble;
import Reika.GeoStrata.Blocks.BlockRockDeco;
import Reika.GeoStrata.Blocks.BlockSmooth;
import Reika.GeoStrata.Blocks.BlockSuperCrystal;
import Reika.GeoStrata.Items.ItemBlockCrystal;
import Reika.GeoStrata.Items.ItemBlockRock;
import Reika.GeoStrata.Items.ItemBlockRockDeco;

public enum GeoBlocks implements RegistryEnum {

	SMOOTH(BlockSmooth.class, ItemBlockRock.class, "Smooth Rock"),
	COBBLE(BlockRockCobble.class, ItemBlockRock.class, "Rock Cobble"),
	BRICK(BlockRockBrick.class, ItemBlockRock.class, "Rock Brick"),
	CRYSTAL(BlockCaveCrystal.class, ItemBlockCrystal.class, "Cave Crystal"), //Comes in all dye colors
	LAMP(BlockCrystalLamp.class, ItemBlockCrystal.class, "Crystal Lamp"),
	DECO(BlockRockDeco.class, ItemBlockRockDeco.class, "Deco Blocks"),
	BREWER(BlockCrystalBrewer.class, null, "Crystal Brewery"),
	SUPER(BlockSuperCrystal.class, ItemBlockCrystal.class, "Potion Crystal"),
	SMOOTH2(BlockSmooth.class, ItemBlockRock.class, "Smooth Rock 2"),
	COBBLE2(BlockRockCobble.class, ItemBlockRock.class, "Rock Cobble 2"),
	BRICK2(BlockRockBrick.class, ItemBlockRock.class, "Rock Brick 2"),
	GUARDIAN(BlockGuardianStone.class, null, "Guardian Stone");

	private Class blockClass;
	private String blockName;
	private Class itemBlock;

	public static final GeoBlocks[] blockList = values();

	private static final HashMap<Integer, GeoBlocks> IDMap = new HashMap();

	private GeoBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
	}

	public int getBlockID() {
		return GeoStrata.config.getBlockID(this.ordinal());
	}

	public GeoBlocks getFromOffset(int offset) {
		if (offset == 0)
			return this;
		String look = this.name();
		for (int i = 0; i < blockList.length; i++) {
			GeoBlocks b = blockList[i];
			String name = b.name();
			String lastChar = name.substring(name.length()-1);
			int off = ReikaJavaLibrary.safeIntParse(lastChar)-1;
			if (off == offset && name.substring(0, name.length()-1).equals(look))
				return b;
		}
		return null;
	}

	public static GeoBlocks getFromID(int id) {
		GeoBlocks block = IDMap.get(id);
		if (block == null) {
			for (int i = 0; i < blockList.length; i++) {
				GeoBlocks g = blockList[i];
				int blockID = g.getBlockID();
				if (id == blockID) {
					IDMap.put(id, g);
					return g;
				}
			}
		}
		else {
			return block;
		}
		return null;
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
		case SMOOTH2:
			return RockTypes.getTypeFromIDandMeta(this.getBlockID(), meta).getName();
		case COBBLE:
		case COBBLE2:
			return RockTypes.getTypeFromIDandMeta(this.getBlockID(), meta).getName()+" Cobblestone";
		case BRICK:
		case BRICK2:
			return RockTypes.getTypeFromIDandMeta(this.getBlockID(), meta).getName()+" Bricks";
		case CRYSTAL:
			return ReikaDyeHelper.dyes[meta].colorName+" "+GeoBlocks.CRYSTAL.getBasicName();
		case SUPER:
			return ReikaDyeHelper.dyes[meta].colorName+" "+GeoBlocks.SUPER.getBasicName();
		case LAMP:
			return ReikaDyeHelper.dyes[meta].colorName+" "+GeoBlocks.LAMP.getBasicName();
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
			return RockTypes.getTypesForID(this.getBlockID());
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
