/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.World.RockGenerator;

public class BlockSmooth extends RockBlock {

	public BlockSmooth() {
		super();
		if (RockGenerator.instance.postConvertOres()) {
			this.setTickRandomly(true);
		}
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(RockTypes.getTypeFromID(this).getID(RockShapes.COBBLE));
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1;
	}
	/*
	@Override
	public int tickRate(World world)
	{
		return 1;
	}*/
	/*
	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityOreConverter();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	public static class TileEntityOreConverter extends TileEntity {

		@Override
		public void updateEntity() {
			BlockSmooth b = (BlockSmooth)worldObj.getBlock(xCoord, yCoord, zCoord);
			b.tick(worldObj, xCoord, yCoord, zCoord);
			worldObj.setTileEntity(xCoord, yCoord, zCoord, null); //delete self
		}

	}*/

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (RockGenerator.instance.postConvertOres() && rand.nextInt(100) == 0) {
			RockTypes r = RockTypes.getTypeFromID(this);
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				Block b = world.getBlock(dx, dy, dz);
				int meta = world.getBlockMetadata(dx, dy, dz);
				if (ReikaBlockHelper.isOre(b, meta)) {
					this.checkAndConvertOre(world, dx, dy, dz, b, meta, r);
				}
			}
		}
	}

	private void checkAndConvertOre(World world, int x, int y, int z, Block b, int meta, RockTypes r) {
		int count = 0;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b2 = world.getBlock(dx, dy, dz);
			if (b2 instanceof BlockSmooth) {
				count++;
			}
		}
		if (count >= 2) {
			TileEntityGeoOre te = new TileEntityGeoOre();
			te.initialize(r, b, meta);
			world.setBlock(x, y, z, GeoBlocks.ORETILE.getBlockInstance());
			world.setTileEntity(x, y, z, te);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes r = RockTypes.rockList[i];
			icons[i] = ico.registerIcon("GeoStrata:"+r.getName().toLowerCase(Locale.ENGLISH));
			GeoStrata.logger.debug("Adding "+r.getName()+" rock icon "+icons[i].getIconName());
		}
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target)
	{
		return target == this || target == Blocks.stone;
	}
}
