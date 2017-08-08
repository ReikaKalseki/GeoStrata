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

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.CarpenterBlockHandler;
import Reika.DragonAPI.ModRegistry.InterfaceCache;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockPartialBounds extends BlockContainer {

	public static IIcon fenceOverlay;

	public BlockPartialBounds(Material mat) {
		super(mat);

		this.setHardness(1);
		this.setResistance(5);
		this.setCreativeTab(GeoStrata.tabGeo);
		useNeighborBrightness = true;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
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
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null || te.renderBlock == null || te.renderBlock.blockID instanceof BlockPartialBounds)
			return 0;
		return te.renderBlock.blockID.getLightValue(new IconDelegateAccess(world, x, y, z), x, y, z); //delegate since calls getBlock()
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null || te.renderBlock == null || te.renderBlock.blockID instanceof BlockPartialBounds)
			return 0;
		return te.renderBlock.blockID.getLightOpacity(world, x, y, z);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null || te.renderBlock == null || te.renderBlock.blockID instanceof BlockPartialBounds)
			return 0xffffff;
		Block b = te.renderBlock.blockID;
		return b instanceof RockBlock ? ((RockBlock)b).getColor(world, x, y, z, RockTypes.getTypeFromID(b)) : b.colorMultiplier(world, x, y, z);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null || te.renderBlock == null) {
			return this.getIcon(s, 0);
		}
		return te.renderBlock.blockID.getIcon(new IconDelegateAccess(world, x, y, z), x, y, z, s);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		Block b = null;
		switch(ForgeDirection.VALID_DIRECTIONS[s]) {
			case DOWN:
				b = Blocks.obsidian;
				break;
			case EAST:
				b = Blocks.planks;
				break;
			case NORTH:
				b = Blocks.dirt;
				break;
			case SOUTH:
				b = Blocks.ice;
				break;
			case UP:
				b = Blocks.cobblestone;
				break;
			case WEST:
				b = Blocks.leaves;
				break;
			default:
				b = Blocks.stone;
				break;
		}
		return b.getIcon(0, 0);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		fenceOverlay = ico.registerIcon("geostrata:partialfencegroove");
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null || te.renderBlock == null)
			return Blocks.cobblestone.getBlockHardness(world, x, y, z);
		return te.renderBlock.blockID.getBlockHardness(world, x, y, z);
	}

	@Override
	public float getExplosionResistance(Entity e, World world, int x, int y, int z, double ex, double ey, double ez) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null || te.renderBlock == null)
			return Blocks.cobblestone.getExplosionResistance(e, world, x, y, z, ex, ey, ez);
		return te.renderBlock.blockID.getExplosionResistance(e, world, x, y, z, ex, ey, ez);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null)
			return ReikaAABBHelper.getBlockAABB(x, y, z);
		AxisAlignedBB box = te.bounds.roundToNearest(0.125).asAABB(x, y, z);
		if (te.isFence())
			box.maxY = y+1.5;
		return box;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		if (te == null)
			return;
		te.bounds.copyToBlock(this);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s) {
		Block at = iba.getBlock(x, y, z); //already shifted
		if (at != this && super.shouldSideBeRendered(iba, x, y, z, s))
			return true;
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		TilePartialBounds te = (TilePartialBounds)iba.getTileEntity(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ);
		if (te == null)
			return true;
		BlockBounds us = te.bounds;
		at.setBlockBoundsBasedOnState(iba, x, y, z);
		switch(dir) {
			case UP:
				return us.positiveY < 1 || at.getBlockBoundsMinY() > 0 || at.getBlockBoundsMinX() > us.negativeX || at.getBlockBoundsMaxX() < us.positiveX || us.positiveZ > at.getBlockBoundsMaxZ() || us.negativeZ < at.getBlockBoundsMinZ();
			case DOWN:
				return us.negativeY > 0 || at.getBlockBoundsMaxY() < 1 || at.getBlockBoundsMinX() > us.negativeX || at.getBlockBoundsMaxX() < us.positiveX || us.positiveY > at.getBlockBoundsMaxZ() || us.negativeY < at.getBlockBoundsMinZ();
			case EAST:
				return us.positiveX < 1 || at.getBlockBoundsMinX() > 0 || at.getBlockBoundsMinZ() > us.negativeZ || at.getBlockBoundsMaxZ() < us.positiveZ || us.positiveY > at.getBlockBoundsMaxY() || us.negativeY < at.getBlockBoundsMinY();
			case WEST:
				return us.negativeX > 0 || at.getBlockBoundsMaxX() < 1 || at.getBlockBoundsMinZ() > us.negativeZ || at.getBlockBoundsMaxZ() < us.positiveZ || us.positiveY > at.getBlockBoundsMaxY() || us.negativeY < at.getBlockBoundsMinY();
			case SOUTH:
				return us.positiveZ < 1 || at.getBlockBoundsMinZ() > 0 || at.getBlockBoundsMinX() > us.negativeX || at.getBlockBoundsMaxX() < us.positiveX || us.positiveY > at.getBlockBoundsMaxY() || us.negativeY < at.getBlockBoundsMinY();
			case NORTH:
				return us.negativeZ > 0 || at.getBlockBoundsMaxZ() < 1 || at.getBlockBoundsMinX() > us.negativeX || at.getBlockBoundsMaxX() < us.positiveX || us.positiveY > at.getBlockBoundsMaxY() || us.negativeY < at.getBlockBoundsMinY();
			default:
				break;
		}
		return super.shouldSideBeRendered(iba, x, y, z, s) && iba.getBlock(x, y, z) != this;
	}

	@Override
	public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z) {
		return super.getMixedBrightnessForBlock(iba, x, y, z);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TilePartialBounds();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		/*
		if (e instanceof EntityPlayer) {
			TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
			NBTTagCompound tag = ReikaPlayerAPI.getDeathPersistentNBT((EntityPlayer)e);
			if (tag.hasKey("partialbounds")) {
				te.bounds = BlockBounds.readFromNBT("partialbounds", tag);
				world.markBlockForUpdate(x, y, z);
			}
		}
		 */
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null || is.getItem() == null)
			return false;
		TilePartialBounds te = (TilePartialBounds)world.getTileEntity(x, y, z);
		String n = is.getItem().getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
		if (InterfaceCache.IWRENCH.instanceOf(is) || n.contains("wrench") || n.contains("screwdriver") || n.contains("hammer")) {
			double d = 0.03125;
			te.bounds = te.bounds.cut(ForgeDirection.VALID_DIRECTIONS[s], ep.isSneaking() ? -d : d);
			//te.bounds.writeToNBT("partialbounds", ReikaPlayerAPI.getDeathPersistentNBT(ep));
			world.markBlockForUpdate(x, y, z);
			ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.stone);
			return true;
		}
		else if (ReikaItemHelper.matchStackWithBlock(is, Blocks.fence)) {
			te.fence = !te.fence;
			world.markBlockForUpdate(x, y, z);
			ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.planks);
			return true;
		}
		else if (ReikaItemHelper.isBlock(is) && this.isAllowedBlock(Block.getBlockFromItem(is.getItem()))) {
			this.changeCover(te, world, x, y, z, is);
			//is.stackSize--;
			ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.wool);
			return true;
		}
		else if (is.getItem() == Items.book) {
			if (is.stackTagCompound != null && is.stackTagCompound.hasKey("partialbounds")) {
				te.bounds = BlockBounds.readFromNBT("partialbounds", is.stackTagCompound);
				world.markBlockForUpdate(x, y, z);
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.stone);
			}
			else {
				is.stackTagCompound = new NBTTagCompound();
				te.bounds.writeToNBT("partialbounds", is.stackTagCompound);
			}
		}
		return false;
	}

	private boolean isAllowedBlock(Block b) {
		if (b == this)
			return false;
		if (b instanceof BlockStairs || b instanceof BlockSlab)
			return false;
		return true;
	}

	private void changeCover(TilePartialBounds te, World world, int x, int y, int z, ItemStack is) {
		Block b = Block.getBlockFromItem(is.getItem());
		if (b == this)
			return;
		if (CarpenterBlockHandler.getInstance().isCarpenterBlock(b))
			return;
		if (te.renderBlock != null) {
			//ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, te.renderBlock.asItemStack());
		}
		te.renderBlock = new BlockKey(b, is.getItemDamage());
		world.markBlockForUpdate(x, y, z);
	}

	public void setBounds(World world, int x, int y, int z, double nx, double ny, double nz, double px, double py, double pz) {
		((TilePartialBounds)world.getTileEntity(x, y, z)).bounds = new BlockBounds(nx, ny, nz, px, py, pz);
		world.markBlockForUpdate(x, y, z);
	}

	public static class TilePartialBounds extends TileEntity {

		private BlockKey renderBlock;
		private BlockBounds bounds = BlockBounds.block();
		private boolean fence;

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			bounds.writeToNBT("bounds", NBT);
			if (renderBlock != null)
				renderBlock.writeToNBT("render", NBT);
			NBT.setBoolean("fence", fence);
		}

		public boolean isFence() {
			return fence;
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			bounds = BlockBounds.readFromNBT("bounds", NBT);
			if (NBT.hasKey("render"))
				renderBlock = BlockKey.readFromNBT("render", NBT);
			if (renderBlock != null && renderBlock.blockID instanceof BlockPartialBounds)
				renderBlock = null;
			fence = NBT.getBoolean("fence");
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
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	private static class IconDelegateAccess implements IBlockAccess {

		private final int range = 2;

		private final BlockKey[][][] data = new BlockKey[range*2+1][range*2+1][range*2+1];
		private final IBlockAccess original;
		private final int lowerX;
		private final int lowerY;
		private final int lowerZ;

		private IconDelegateAccess(IBlockAccess iba, int x, int y, int z) {
			original = iba;
			for (int i = -range; i <= range; i++) {
				for (int j = -range; j <= range; j++) {
					for (int k = -range; k <= range; k++) {
						int i1 = i+range;
						int i2 = j+range;
						int i3 = k+range;
						BlockKey bk = BlockKey.getAt(iba, x+i, y+j, z+k);
						if (bk.blockID instanceof BlockPartialBounds) {
							TilePartialBounds te = (TilePartialBounds)iba.getTileEntity(x+i, y+j, z+k);
							if (te.renderBlock != null)
								bk = te.renderBlock;
						}
						data[i1][i2][i3] = bk;
					}
				}
			}
			lowerX = x-range;
			lowerY = y-range;
			lowerZ = z-range;
		}

		@Override
		public Block getBlock(int x, int y, int z) {
			int dx = x-lowerX;
			int dy = y-lowerY;
			int dz = z-lowerZ;
			if (dx < 0 || dy < 0 || dz < 0 || dx >= data.length || dy >= data.length || dz >= data.length)
				return original.getBlock(x, y, z);
			return data[dx][dy][dz].blockID;
		}

		@Override
		public int getBlockMetadata(int x, int y, int z) {
			int dx = x-lowerX;
			int dy = y-lowerY;
			int dz = z-lowerZ;
			if (dx < 0 || dy < 0 || dz < 0 || dx >= data.length || dy >= data.length || dz >= data.length)
				return original.getBlockMetadata(x, y, z);
			return data[dx][dy][dz].metadata;
		}

		@Override
		public TileEntity getTileEntity(int x, int y, int z) {
			return original.getTileEntity(x, y, z);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
			return original.getLightBrightnessForSkyBlocks(x, y, z, l);
		}

		@Override
		public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
			return original.isBlockProvidingPowerTo(x, y, z, side);
		}

		@Override
		public boolean isAirBlock(int x, int y, int z) {
			return original.isAirBlock(x, y, z);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public BiomeGenBase getBiomeGenForCoords(int x, int z) {
			return original.getBiomeGenForCoords(x, z);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public int getHeight() {
			return original.getHeight();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean extendedLevelsInChunkCache() {
			return original.extendedLevelsInChunkCache();
		}

		@Override
		public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
			return original.isSideSolid(x, y, z, side, _default);
		}

	}

}
