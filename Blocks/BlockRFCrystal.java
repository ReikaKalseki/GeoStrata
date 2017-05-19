package Reika.GeoStrata.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Blocks.BlockRFCrystalSeed.TileRFCrystal;
import Reika.GeoStrata.Registry.GeoBlocks;


public class BlockRFCrystal extends Block {

	public BlockRFCrystal(Material mat) {
		super(mat);

		this.setResistance(60000);
		this.setHardness(2.5F);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setLightLevel(6);
		stepSound = soundTypeGlass;
		slipperiness = 0.99F;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return super.shouldSideBeRendered(world, x, y, z, side) && !(world.getBlock(x, y, z) instanceof BlockRFCrystal);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileRFCrystalAux();
	}

	public static void place(World world, int x, int y, int z, TileRFCrystal parent) {
		world.setBlock(x, y, z, GeoBlocks.RFCRYSTAL.getBlockInstance());
		TileRFCrystalAux te = (TileRFCrystalAux)world.getTileEntity(x, y, z);
		te.controller = new Coordinate(parent);
		te.addToParent();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		if (this.hasTileEntity(oldmeta))
			if (this == GeoBlocks.RFCRYSTAL.getBlockInstance())
				((TileRFCrystalAux)world.getTileEntity(x, y, z)).removeFromParent();
		super.breakBlock(world, x, y, z, old, oldmeta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("geostrata:rfcrystal");
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Items.redstone;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)  {
		return (1+random.nextInt(6))*(1+random.nextInt(1+fortune));
	}

	public static class TileRFCrystalAux extends TileEntity {

		private Coordinate controller;

		@Override
		public final boolean canUpdate() {
			return false;
		}

		public void removeFromParent() {
			if (controller == null)
				return;
			TileEntity te = controller.getTileEntity(worldObj);
			if (te instanceof TileRFCrystal) {
				((TileRFCrystal)te).removeLocation(new Coordinate(this));
			}
		}

		public void addToParent() {
			if (controller == null)
				return;
			TileEntity te = controller.getTileEntity(worldObj);
			if (te instanceof TileRFCrystal) {
				((TileRFCrystal)te).addLocation(new Coordinate(this));
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (controller != null)
				controller.writeToNBT("parent", NBT);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			controller = Coordinate.readFromNBT("parent", NBT);
		}

	}

}
