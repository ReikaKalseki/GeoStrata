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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Math.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.Block.ShearablePlant;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Rendering.GlowVineRenderer;


public class BlockGlowingVines extends BlockContainer implements ShearablePlant {

	private final SimplexNoiseGenerator lightNoise = new SimplexNoiseGenerator(~System.currentTimeMillis());

	//private final SimplexNoiseGenerator hueNoise = new SimplexNoiseGenerator(System.currentTimeMillis());
	//private final SimplexNoiseGenerator hueNoise2 = new SimplexNoiseGenerator(-System.currentTimeMillis());

	public BlockGlowingVines() {
		super(Material.plants);
		this.setLightLevel(1);
		this.setHardness(0.2F);
		stepSound = soundTypeGrass;

		this.setCreativeTab(GeoStrata.tabGeo);

		this.setTickRandomly(true);
	}

	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileGlowingVines();
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) { //this should be interesting
		return (int)ReikaMathLibrary.normalizeToBounds(lightNoise.getValue(x/8D, z/8D), 12, 15);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (rand.nextInt(8) > 0)
			return;
		TileGlowingVines te = (TileGlowingVines)world.getTileEntity(x, y, z);
		ForgeDirection dir = ReikaJavaLibrary.getRandomCollectionEntry(rand, te.filledSides);
		if (dir != null) {
			double dx = x+0.5;
			double dy = y+0.5;
			double dz = z+0.5;
			double o = 0.03125;
			switch(dir) {
				case DOWN:
					dy = y+o;
					dx = x+rand.nextDouble();
					dz = z+rand.nextDouble();
					break;
				case EAST:
					dx = x+1-o;
					dy = y+rand.nextDouble();
					dz = z+rand.nextDouble();
					break;
				case NORTH:
					dz = z+o;
					dy = y+rand.nextDouble();
					dx = x+rand.nextDouble();
					break;
				case SOUTH:
					dz = z+1-o;
					dy = y+rand.nextDouble();
					dx = x+rand.nextDouble();
					break;
				case UP:
					dy = y+1-o-0.125;
					dx = x+rand.nextDouble();
					dz = z+rand.nextDouble();
					break;
				case WEST:
					dx = x+o;
					dy = y+rand.nextDouble();
					dz = z+rand.nextDouble();
					break;
				default:
					break;
			}
			//float f = (float)ReikaRandomHelper.getRandomBetween(0.25, 0.375);
			//EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setScale(f).setColor(0x22aaff).setLife(ReikaRandomHelper.getRandomBetween(6, 15));
			//Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			int c = 0x22aaff;
			int r = ReikaColorAPI.getRed(c);
			int g = ReikaColorAPI.getGreen(c);
			int b = ReikaColorAPI.getBlue(c);
			ReikaParticleHelper.spawnColoredParticleAt(world, dx, dy, dz, r, g, b);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isRemote)
			return;
		if (rand.nextInt(4) > 0)
			return;
		boolean flag = false;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b == Blocks. stone || b == Blocks.dirt || b == Blocks.grass){
				flag = true;
				break;
			}
		}
		if (flag) {
			TileGlowingVines te = (TileGlowingVines)world.getTileEntity(x, y, z);
			if (rand.nextInt(4) == 0) { //spread outside coord
				for (ForgeDirection surface : te.filledSides) {
					ArrayList<ForgeDirection> li = ReikaDirectionHelper.getRandomOrderedDirections(true);
					for (ForgeDirection dir : li) {
						if (dir != surface && dir != surface.getOpposite()) {
							int dx = x+dir.offsetX;
							int dy = y+dir.offsetY;
							int dz = z+dir.offsetZ;
							if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz) && te.isValidSide(world, dx, dy, dz, surface)) {
								this.place(world, dx, dy, dz, surface);
								ReikaSoundHelper.playBreakSound(world, x, y, z, this, 1, 1);
								ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, x, y, z, 32, Block.getIdFromBlock(this), 0);
								return;
							}
						}
					}
				}
			}
			else {
				ForgeDirection fill = null;
				ArrayList<ForgeDirection> li = ReikaDirectionHelper.getRandomOrderedDirections(true);
				for (ForgeDirection dir : li) {
					if (!te.hasSide(dir) && te.isValidSide(world, x, y, z, dir)) {
						fill = dir;
						break;
					}
				}
				if (fill != null) {
					te.addVine(fill);
					ReikaSoundHelper.playBreakSound(world, x, y, z, this, 1, 1);
					ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, x, y, z, 32, Block.getIdFromBlock(this), 0);
				}
			}
		}
	}

	public static boolean place(World world, int x, int y, int z, ForgeDirection side) {
		if (side == null)
			side = tryFindValidSide(world, x, y, z);
		if (side == null)
			return false;
		if (TileGlowingVines.isValidSide(world, x, y, z, side)) {
			world.setBlock(x, y, z, GeoBlocks.GLOWVINE.getBlockInstance());
			TileEntity te = world.getTileEntity(x, y, z);
			if (!(te instanceof TileGlowingVines)) {
				world.setBlock(x, y, z, Blocks.air);
				return false;
			}
			((TileGlowingVines)te).addVine(side);
			return true;
		}
		return false;
	}

	private static ForgeDirection tryFindValidSide(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (TileGlowingVines.isValidSide(world, x, y, z, dir)) {
				return dir;
			}
		}
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		GlowVineRenderer.renderPass = pass;
		return pass == 1;
	}

	@Override
	public int getRenderType() {
		return GeoStrata.proxy.vineRender;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("geostrata:glowvine_anim4");
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z)  {
		return true;//super.isReplaceable(world, x, y, z);
	}

	@Override
	public boolean canReplace(World world, int x, int y, int z, int s, ItemStack is)  {
		//ReikaJavaLibrary.pConsole(world.getBlock(x, y, z));
		return super.canReplace(world, x, y, z, s, is) || world.getBlock(x, y, z) == this;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return super.getDrops(world, x, y, z, meta, fortune);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	/*
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 from, Vec3 to) {
		return null;
	}
	 */

	@Override
	public boolean canCollideCheck(int meta, boolean liq) {
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block n) {
		TileGlowingVines te = (TileGlowingVines)world.getTileEntity(x, y, z);
		te.updateAndDropSides(world, x, y, z);
		if (te.filledSides.isEmpty()) {
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public void shearAll(World world, int x, int y, int z, EntityPlayer ep) {
		TileGlowingVines te = (TileGlowingVines)world.getTileEntity(x, y, z);
		ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(this, te.filledSides.size(), 0));
		world.setBlock(x, y, z, Blocks.air);
	}

	@Override
	public void shearSide(World world, int x, int y, int z, ForgeDirection dir, EntityPlayer ep) {
		TileGlowingVines te = (TileGlowingVines)world.getTileEntity(x, y, z);
		if (te.filledSides.contains(dir)) {
			te.filledSides.remove(dir);
			world.markBlockForUpdate(x, y, z);
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(this));
			if (te.filledSides.isEmpty())
				world.setBlock(x, y, z, Blocks.air);
		}
	}

	/*
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getColor(iba.getBlockMetadata(x, y, z), x, y, z);
	}

	@Override
	public int getRenderColor(int meta) {
		double d = System.currentTimeMillis()/200D+meta*50;
		return this.getColor(meta, RenderManager.renderPosX+d, RenderManager.renderPosY+d, RenderManager.renderPosZ+d);
	}

	private int getColor(int meta, double x, double y, double z) {
		double n0 = hueNoise.getValue(x/8D, z/8D);
		double n1 = hueNoise2.getValue(x/8D, z/8D);
		double f = y%16 >= 8 ? y%8/8D : 1-(((y-8)%8)/8D);
		double n = f*n0+(1-f)*n1;
		int hue = hueRange.left+(int)(hueRange.right*n*1);//hueNoiseY.getValue(0, y/4D));
		return ReikaColorAPI.getModifiedHue(0xff0000, hue);
	}
	 */

	public static class TileGlowingVines extends TileEntity {

		private final HashSet<ForgeDirection> filledSides = new HashSet();

		public boolean addVine(ForgeDirection side) {
			if (filledSides.add(side)) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				return true;
			}
			return false;
		}

		public void updateAndDropSides(World world, int x, int y, int z) {
			if (world.isRemote)
				return;
			Iterator<ForgeDirection> it = filledSides.iterator();
			boolean flag = false;
			while (it.hasNext()) {
				ForgeDirection dir = it.next();
				if (this.isValidSide(world, x, y, z, dir)) {

				}
				else {
					it.remove();
					ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(GeoBlocks.GLOWVINE.getBlockInstance()));
					flag = true;
				}
			}
			if (flag) {
				world.markBlockForUpdate(x, y, z);
			}
		}

		private static boolean isValidSide(World world, int x, int y, int z, ForgeDirection dir) {
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			return b.isSideSolid(world, dx, dy, dz, dir.getOpposite());
		}

		@Override
		public boolean canUpdate() {
			return false;
		}

		public boolean hasSide(int side) {
			return this.hasSide(ForgeDirection.VALID_DIRECTIONS[side]);
		}

		public boolean hasSide(ForgeDirection dir) {
			return filledSides.contains(dir);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBTTagList li = new NBTTagList();
			for (ForgeDirection dir : filledSides) {
				li.appendTag(new NBTTagInt(dir.ordinal()));
			}
			NBT.setTag("sides", li);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			filledSides.clear();
			NBTTagList li = NBT.getTagList("sides", NBTTypes.INT.ID);
			for (Object o : li.tagList) {
				filledSides.add(ForgeDirection.VALID_DIRECTIONS[((NBTTagInt)o).func_150287_d()]);
			}

			if (worldObj != null && worldObj.isRemote) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
		}
	}

}
