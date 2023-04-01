/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.MetadataItemBlock;
import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Blocks.BlockCreepvine;
import Reika.GeoStrata.Blocks.BlockDecoGen;
import Reika.GeoStrata.Blocks.BlockGeoSlab;
import Reika.GeoStrata.Blocks.BlockGeoStairs;
import Reika.GeoStrata.Blocks.BlockGlowCrystal;
import Reika.GeoStrata.Blocks.BlockGlowingVines;
import Reika.GeoStrata.Blocks.BlockLavaRock;
import Reika.GeoStrata.Blocks.BlockOreTile;
import Reika.GeoStrata.Blocks.BlockPartialBounds;
import Reika.GeoStrata.Blocks.BlockRFCrystal;
import Reika.GeoStrata.Blocks.BlockRFCrystalSeed;
import Reika.GeoStrata.Blocks.BlockRockDeco;
import Reika.GeoStrata.Blocks.BlockShapedRock;
import Reika.GeoStrata.Blocks.BlockSmooth;
import Reika.GeoStrata.Blocks.BlockVent;
import Reika.GeoStrata.Blocks.BlockVoidOpal;
import Reika.GeoStrata.Items.ItemBlockAnyGeoVariant;
import Reika.GeoStrata.Items.ItemBlockFluxCrystal;
import Reika.GeoStrata.Items.ItemBlockGeoOre;
import Reika.GeoStrata.Items.ItemBlockGlowingVines;
import Reika.GeoStrata.Items.ItemBlockRockDeco;
import Reika.GeoStrata.Items.ItemBlockVent;

public enum GeoBlocks implements BlockEnum {

	DECO(BlockRockDeco.class, ItemBlockRockDeco.class, "Deco Blocks"),
	VENT(BlockVent.class, ItemBlockVent.class, "Vent"),
	STAIR(BlockGeoStairs.class, ItemBlockAnyGeoVariant.class, "Stairs"),
	SLAB(BlockGeoSlab.class, ItemBlockAnyGeoVariant.class, "Slab"),
	ORETILE(BlockOreTile.class, ItemBlockGeoOre.class, "Ore"),
	LAVAROCK(BlockLavaRock.class, MetadataItemBlock.class, "Lava Rock"),
	DECOGEN(BlockDecoGen.class,	MetadataItemBlock.class, "geo.decogen"),
	GLOWCRYS(BlockGlowCrystal.class, MetadataItemBlock.class, "Luminous Crystal"),
	PARTIAL(BlockPartialBounds.class, null, "Partial Bounds Block"),
	GLOWVINE(BlockGlowingVines.class, ItemBlockGlowingVines.class, "Glowing Vines"),
	RFCRYSTAL(BlockRFCrystal.class,	null, "Flux Crystals"),
	RFCRYSTALSEED(BlockRFCrystalSeed.class,	ItemBlockFluxCrystal.class, "Flux Crystal Seed"),
	VOIDOPAL(BlockVoidOpal.class, null, "Void Opals"),
	CREEPVINE(BlockCreepvine.class, MetadataItemBlock.class, "Creepvine"),
	;

	private final Class blockClass;
	private final String blockName;
	private final Class itemBlock;
	private final String typeName;

	public static final GeoBlocks[] blockList = values();

	private static final HashMap<Block, GeoBlocks> IDMap = new HashMap();

	private GeoBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n) {
		this(cl, ib, n, null);
	}

	private GeoBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n, String s) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		typeName = s;
	}

	public Block getBlockInstance() {
		return GeoStrata.blocks[this.ordinal()];
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

	public static GeoBlocks getFromID(Item id) {
		return getFromID(Block.getBlockFromItem(id));
	}

	public static GeoBlocks getFromID(Block id) {
		GeoBlocks block = IDMap.get(id);
		if (block == null) {
			for (int i = 0; i < blockList.length; i++) {
				GeoBlocks g = blockList[i];
				Block blockID = g.getBlockInstance();
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
		if (BlockRFCrystal.class.isAssignableFrom(blockClass))
			return Material.glass;
		return Material.rock;
	}

	public boolean isRock() {
		return RockBlock.class.isAssignableFrom(blockClass);
	}

	@Override
	public Class[] getConstructorParamTypes() {
		if (this == GLOWVINE || this == CREEPVINE)
			return new Class[0];
		if (typeName != null)
			return new Class[]{Material.class, String.class};
		return new Class[]{Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		if (this == GLOWVINE || this == CREEPVINE)
			return new Object[0];
		if (typeName != null)
			return new Object[]{this.getBlockMaterial(), typeName};
		return new Object[]{this.getBlockMaterial()};
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(blockName);
	}

	public boolean isSmoothBlock() {
		return BlockSmooth.class.isAssignableFrom(blockClass);
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
	public String getMultiValuedName(int meta) {/*
		if (this == COBBLE || this == COBBLE2)
			return RockTypes.getTypeFromID(this.getBlockInstance(), meta).getName()+" Cobblestone";
		if (this == BRICK || this == BRICK2)
			return RockTypes.getTypeFromID(this.getBlockInstance(), meta).getName()+" Bricks";
		if (this.isShapedRock()) {
			return "";//((BlockShapedRock)this.getBlockInstance()).getDisplayName()+" "+RockTypes.getTypeFromID(this.getBlockInstance(), meta).getName();
		}*/
		switch(this) {
			//case SMOOTH:
			//case SMOOTH2:
			//	return RockTypes.getTypeFromID(this.getBlockInstance(), meta).getName();
			case DECO:
				return DecoBlocks.list[meta].getName();
				//case CONNECTED:
				//case CONNECTED2:
				//	return "Connected "+RockTypes.getTypeFromID(this.getBlockInstance(), meta).getName();
			case DECOGEN:
				return BlockDecoGen.Types.list[meta].name;
			default:
				return "";
		}
	}

	public boolean isShapedRock() {
		return BlockShapedRock.class.isAssignableFrom(blockClass);
	}

	@Override
	public boolean hasMultiValuedName() {
		switch(this) {
			case LAVAROCK:
			case PARTIAL:
			case GLOWCRYS:
			case GLOWVINE:
			case RFCRYSTAL:
			case RFCRYSTALSEED:
			case VOIDOPAL:
			case CREEPVINE:
				return false;
			default:
				return true;
		}
	}

	@Override
	public int getNumberMetadatas() {
		//if (this.isRock())
		//	return RockTypes.getTypesForID(this.getBlockInstance());
		if (this == DECO)
			return DecoBlocks.list.length;
		if (this == DECOGEN)
			return BlockDecoGen.Types.list.length;
		if (this == CREEPVINE)
			return 4;
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

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Item getItem() {
		return Item.getItemFromBlock(this.getBlockInstance());
	}

	@Override
	public ItemStack getStackOfMetadata(int meta) {
		return new ItemStack(this.getBlockInstance(), 1, meta);
	}
}
