/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;
import Reika.DragonAPI.Instantiable.Data.Maps.TileEntityCache;
import Reika.DragonAPI.Interfaces.Block.SpecialOreBlock;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Registry.GeoISBRH;
import Reika.GeoStrata.Registry.RockTypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockOreTile extends Block implements SpecialOreBlock {

	private static PluralMap<Integer> metaMap = new PluralMap(3);
	private static HashMap<Integer, ItemStack> oreMap = new HashMap();
	private static HashMap<Integer, RockTypes> rockMap = new HashMap();
	private static HashMap<Integer, OreType> enumMap = new HashMap();
	private static boolean init = false;

	private IIcon[] icons = new IIcon[RockTypes.rockList.length];

	private final TileEntityCache<TileEntityGeoOre> tileCache = new TileEntityCache();

	public BlockOreTile(Material mat) {
		super(mat);
		this.setHardness(Blocks.iron_ore.blockHardness);
		this.setResistance(Blocks.iron_ore.blockResistance);
		//this.setCreativeTab(GeoStrata.tabGeoOres);
		//if (RockGenerator.instance.destroyOres()) {
		//	this.setTickRandomly(true);
		//}
	}

	private static void initSubs() {
		metaMap.clear();
		oreMap.clear();
		int k = 0;
		for (int r = 0; r < RockTypes.rockList.length; r++) {
			RockTypes rock = RockTypes.rockList[r];
			for (int i = 0; i < ReikaOreHelper.oreList.length; i++) {
				ReikaOreHelper ore = ReikaOreHelper.oreList[i];
				if (ore.canGenerateIn(Blocks.stone)) {
					Collection<ItemStack> c = ore.getAllOreBlocks();
					Collection<ItemStack> has = new ArrayList();
					int f = 0;
					for (ItemStack is : c) {
						if (!ReikaItemHelper.collectionContainsItemStack(has, is)) {
							Block b = Block.getBlockFromItem(is.getItem());
							IIcon ico = b.getIcon(1, is.getItemDamage());
							setMappings(ore, f, rock, k, is);
							f++;
							k++;
							has.add(is);
						}
					}
				}
			}
			for (int i = 0; i < ModOreList.oreList.length; i++) {
				ModOreList ore = ModOreList.oreList[i];
				if (ore.canGenerateIn(Blocks.stone)) {
					Collection<ItemStack> c = ore.getAllOreBlocks();
					Collection<ItemStack> has = new ArrayList();
					int f = 0;
					for (ItemStack is : c) {
						if (!ReikaItemHelper.collectionContainsItemStack(has, is)) {
							Block b = Block.getBlockFromItem(is.getItem());
							IIcon ico = b.getIcon(1, is.getItemDamage());
							setMappings(ore, f, rock, k, is);
							f++;
							k++;
							has.add(is);
						}
					}
				}
			}
		}
		init = true;
		//ReikaJavaLibrary.pConsole(metaMap.get(ModOreList.PLATINUM, 0, RockTypes.BASALT));
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List li) {
		if (!init) {
			this.initSubs();
		}
		for (int meta : oreMap.keySet()) {
			li.add(new ItemStack(item, 1, meta));
		}
	}

	private static void setMappings(OreType ore, int index, RockTypes r, int meta, ItemStack is) {
		oreMap.put(meta, is);
		metaMap.put(meta, ore, index, r);
		rockMap.put(meta, r);
		enumMap.put(meta, ore);
		//ReikaJavaLibrary.pConsole(ore+" "+index+" in "+r+": "+meta);
	}

	public static RockTypes getRockFromItem(int meta) {
		if (!init) {
			initSubs();
		}
		return rockMap.get(meta);
	}

	public static OreType getOreFromItem(int meta) {
		if (!init) {
			initSubs();
		}
		return enumMap.get(meta);
	}

	public static int getMetadataByTypes(RockTypes r, OreType o) {
		if (!init) {
			initSubs();
		}
		return metaMap.get(o, 0, r);
	}

	public static ItemStack getOreByItemMetadata(Item item, int meta) {
		if (!init) {
			initSubs();
		}
		return oreMap.get(meta).copy();
	}

	public static ItemStack getOreByItemBlock(ItemStack is) {
		return getOreByItemMetadata(is.getItem(), is.getItemDamage());
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityGeoOre();
	}
	/*
	public void updateTick(World world, int x, int y, int z) {
		if (RockGenerator.instance.destroyOres()) {
			TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
			Block b = te.getOreBlock();
			int meta = te.getOreMeta();
			world.setBlock(x, y, z, b, meta, 3);
		}
	}
	 */
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		return te != null && te.getOreBlock() != this ? te.getOreBlock().getDrops(world, x, y, z, te.getOreMeta(), fortune) : new ArrayList();
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
		TileEntityGeoOre te = tileCache.get(world, x, y, z);
		if (te != null) {
			Block b = te.getOreBlock();
			int metadata = te.getOreMeta();
			if (b != this)
				b.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldid, int oldmeta) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		if (te != null) {
			Block b = te.getOreBlock();
			int metadata = te.getOreMeta();
			if (b != this)
				b.breakBlock(world, x, y, z, b, metadata);
		}
		super.breakBlock(world, x, y, z, oldid, oldmeta);
		//this.initSubs(Item.getItemFromBlock(this));
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntity tile = iba.getTileEntity(x, y, z);
		if (tile instanceof TileEntityGeoOre) {
			TileEntityGeoOre te = (TileEntityGeoOre)tile;
			//return te.getType().getIcon();
			return icons[te.getType().ordinal()];
		}
		else {
			return Blocks.stone.getIcon(0, 0);
		}
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		TileEntityGeoOre te = tileCache.get(world, x, y, z);
		return te != null && te.getOreBlock() != this && te.getOreBlock().canSilkHarvest(world, player, x, y, z, metadata);
	}
	/*
	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack is) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		te.getOreBlock().dropBlockAsItem(world, x, y, z, is);
	}
	 */
	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer ep) {
		tileCache.put(world, x, y, z, (TileEntityGeoOre)world.getTileEntity(x, y, z));
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		return super.removedByPlayer(world, player, x, y, z, willHarvest); //setToAir
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		ep.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
		ep.addExhaustion(0.025F);
		TileEntityGeoOre te = tileCache.get(world, x, y, z);
		if (this.canSilkHarvest(world, ep, x, y, z, meta) && EnchantmentHelper.getSilkTouchModifier(ep)) {
			ArrayList<ItemStack> items = new ArrayList();
			items.add(new ItemStack(te.getOreBlock(), 1, te.getOreMeta()));
			//ForgeEventFactory.fireBlockHarvesting(items, world, this, x, y, z, meta, 0, 1.0f, true, ep);
			for (ItemStack is : items) {
				this.dropBlockAsItem(world, x, y, z, is);
			}
		}
		else {
			harvesters.set(ep);
			int i1 = EnchantmentHelper.getFortuneModifier(ep);
			this.dropBlockAsItem(world, x, y, z, meta, i1);
			harvesters.set(null);
		}
		tileCache.remove(world, x, y, z);
	}

	@Override
	public int getRenderType() {
		return GeoISBRH.ore.getRenderID();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes r = RockTypes.rockList[i];
			icons[i] = ico.registerIcon("geostrata:ore/"+r.name().toLowerCase(Locale.ENGLISH));
		}
	}

	public IIcon getRockIcon(RockTypes r) {
		return icons[r.ordinal()];
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition mov, World world, int x, int y, int z) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		int meta = this.getMetadataByTypes(te.getType(), te.getOreType());
		return new ItemStack(this, 1, meta);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int dx, int dy, int dz, int s) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		Block b = world.getBlock(dx, dy, dz);
		if (b.isOpaqueCube())
			return false;
		switch(dir) {
			case EAST:
			case WEST:
			case SOUTH:
			case NORTH:
			case UP:
			case DOWN:
			default:
				return true;
		}
	}

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		TileEntity tile = iba.getTileEntity(x, y, z);
		if (tile instanceof TileEntityGeoOre) {
			TileEntityGeoOre te = (TileEntityGeoOre)tile;
			RockTypes rock = te.getType();
			//ReikaJavaLibrary.pConsole(rock);
			if (rock == RockTypes.OPAL) {
				int sc = 48;
				float hue1 = (float)(ReikaMathLibrary.py3d(x, y*4, z+x)%sc)/sc;
				//float hue2 = (float)(Math.cos(x/24D)+Math.sin(z/24D))+(y%360)*0.05F;
				return Color.HSBtoRGB(hue1, 0.4F, 1F);
			}
			else {
				return super.colorMultiplier(iba, x, y, z);
			}
		}
		else {
			/*
			String s1 = "Ore block @ "+x+", "+y+", "+z+" had its TileEntity overwritten by "+tile.getClass().getName()+"!";
			String s2 = "This is caused by that tile's mod setting the TileEntity without also setting the block type!";
			String s3 = "This is very dangerous and is a bug on the part of that mod, NOT GeoStrata!";
			ReikaChatHelper.writeString(s1);
			ReikaChatHelper.writeString(s2);
			ReikaChatHelper.writeString(s3);
			GeoStrata.logger.logError(s1);
			GeoStrata.logger.logError(s2);
			GeoStrata.logger.logError(s3);*/
			return super.colorMultiplier(iba, x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final int getRenderColor(int dmg) {
		RockTypes rock = rockMap.get(dmg);
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		if (rock == RockTypes.OPAL) {
			int sc = 48;
			float hue1 = (float)(ReikaMathLibrary.py3d(x, y*4, z+x)%sc)/sc;
			//float hue2 = (float)(Math.cos(x/24D)+Math.sin(z/24D))+(y%360)*0.05F;
			return Color.HSBtoRGB(hue1, 0.4F, 1F);
		}
		else {
			return super.getRenderColor(dmg);
		}
	}

	@Override
	public OreType getOre(IBlockAccess world, int x, int y, int z) {
		return this.getOreFromItem(world.getBlockMetadata(x, y, z));
	}

	@Override
	public ItemStack getSilkTouchVersion(World world, int x, int y, int z) {
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		return new ItemStack(te.getOreBlock(), 1, te.getOreMeta());
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune) {
		return this.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), fortune);
	}

	@Override
	public BlockKey getReplacementBlock(World world, int x, int y, int z) {
		return new BlockKey(Blocks.stone);
	}

	@Override
	public ItemStack getDisplayItem(World world, int x, int y, int z) {
		return this.getSilkTouchVersion(world, x, y, z);
	}

}
