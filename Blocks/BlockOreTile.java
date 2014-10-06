/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.PluralMap;
import Reika.DragonAPI.Interfaces.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockOreTile extends Block {

	private static PluralMap<Integer> metaMap = new PluralMap(2);
	private static HashMap<Integer, ItemStack> oreMap = new HashMap();
	private static boolean init = false;

	public BlockOreTile(Material mat) {
		super(mat);
		this.setHardness(Blocks.iron_ore.blockHardness);
		this.setResistance(Blocks.iron_ore.blockResistance);
		this.setCreativeTab(GeoStrata.tabGeoOres);
	}

	private static void initSubs(Item item) {
		ArrayList<ItemStack> li = new ArrayList();
		int k = 0;
		for (int r = 0; r < RockTypes.rockList.length; r++) {
			RockTypes rock = RockTypes.rockList[r];
			for (int i = 0; i < ReikaOreHelper.oreList.length; i++) {
				ReikaOreHelper ore = ReikaOreHelper.oreList[i];
				if (ore.canGenerateIn(Blocks.stone)) {
					Collection<ItemStack> c = ore.getAllOreBlocks();
					int f = 0;
					for (ItemStack is : c) {
						Block b = Block.getBlockFromItem(is.getItem());
						IIcon ico = b.getIcon(1, is.getItemDamage());
						li.add(new ItemStack(item, 1, k));
						setMappings(ore, f, rock, k, is);
						f++;
						k++;
					}
				}
			}
			for (int i = 0; i < ModOreList.oreList.length; i++) {
				ModOreList ore = ModOreList.oreList[i];
				if (ore.canGenerateIn(Blocks.stone)) {
					Collection<ItemStack> c = ore.getAllOreBlocks();
					int f = 0;
					for (ItemStack is : c) {
						Block b = Block.getBlockFromItem(is.getItem());
						IIcon ico = b.getIcon(1, is.getItemDamage());
						li.add(new ItemStack(item, 1, k));
						setMappings(ore, f, rock, k, is);
						f++;
						k++;
					}
				}
			}
		}
		init = true;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List li) {
		if (!init) {
			this.initSubs(item);
		}
		for (int meta : oreMap.keySet()) {
			li.add(new ItemStack(item, 1, meta));
		}
	}

	private static void setMappings(OreType ore, int index, RockTypes r, int meta, ItemStack is) {
		oreMap.put(meta, is);
		metaMap.put(meta, ore, index, r);
	}

	public static ItemStack getOreByItemMetadata(int meta) {
		return oreMap.get(meta).copy();
	}

	public static ItemStack getOreByItemBlock(ItemStack is) {
		return getOreByItemMetadata(is.getItemDamage());
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityGeoOre();
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		Block b = te.getOreBlock();
		int metadata = te.getOreMeta();
		return b.getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		Block b = te.getOreBlock();
		int metadata = te.getOreMeta();
		b.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldid, int oldmeta) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		Block b = te.getOreBlock();
		int metadata = te.getOreMeta();
		b.breakBlock(world, x, y, z, b, metadata);
		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity tile = iba.getTileEntity(x, y, z);
		if (tile instanceof TileEntityGeoOre) {
			TileEntityGeoOre te = (TileEntityGeoOre)tile;
			return te.getType().getIcon();
		}
		else {
			return Blocks.stone.getIcon(0, 0);
		}
	}

	@Override
	public int getRenderType() {
		return GeoStrata.proxy.oreRender;
	}

}
