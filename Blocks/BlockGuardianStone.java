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

import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Guardian.GuardianStoneManager;
import Reika.GeoStrata.Guardian.TileEntityGuardianStone;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockGuardianStone extends Block {

	private Icon outerIcon;
	private Icon innerIcon;
	private Icon middleIcon;

	public BlockGuardianStone(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setLightValue(1F);
		this.setResistance(6000);
		this.setHardness(6);
		stepSound = new StepSound("stone", 1.0F, 0.5F);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return this.hasTileEntity(meta) ? new TileEntityGuardianStone() : null;
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
	public Icon getIcon(int s, int meta) {
		return outerIcon;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		outerIcon = ico.registerIcon("geostrata:guardian_outer");
		innerIcon = ico.registerIcon("geostrata:guardian_inner");
		middleIcon = ico.registerIcon("geostrata:guardian_middle");
	}

	public Icon getOuterIcon() {
		return outerIcon;
	}

	public Icon getInnerIcon() {
		return innerIcon;
	}

	public Icon getMiddleIcon() {
		return middleIcon;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int s) {
		if (Minecraft.getMinecraft().gameSettings.fancyGraphics)
			return true;
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int id = world.getBlockId(dx, dy, dz);
		if (id == 0)
			return true;
		if (id == blockID)
			return false;
		return !Block.blocksList[id].isOpaqueCube();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int oldid, int oldmeta) {
		TileEntityGuardianStone te = (TileEntityGuardianStone)world.getBlockTileEntity(x, y, z);
		if (te != null) {
			GuardianStoneManager.instance.removeAreasForStone(te);
		}
		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		if (this.canHarvest(world, player, x, y, z) && !world.isRemote)
			this.harvestBlock(world, player, x, y, z, 0);
		return world.setBlock(x, y, z, 0);
	}

	public boolean canHarvest(World world, EntityPlayer player, int x, int y, int z)
	{
		if (player.capabilities.isCreativeMode)
			return false;
		if (world.getBlockId(x, y, z) != blockID)
			return false;
		ItemStack is = player.getCurrentEquippedItem();
		return RockTypes.GRANITE.isHarvestable(is);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		boolean silk = EnchantmentHelper.getSilkTouchModifier(ep);
		if (world.getBlockTileEntity(x, y, z) instanceof TileEntityGuardianStone) {
			if (silk) {
				ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(GeoBlocks.GUARDIAN.getBlockInstance()));
			}
			else {
				ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getPieces(world, x, y, z));
			}
		}
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		return this.getPieces(world, x, y, z);
	}

	public ArrayList<ItemStack> getPieces(World world, int x, int y, int z) {
		ArrayList<ItemStack> li = new ArrayList();
		ItemStack is = GeoItems.CLUSTER.getStackOfMetadata(7);
		li.add(is);
		return li;
	}

}
