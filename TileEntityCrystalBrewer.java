/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;

public class TileEntityCrystalBrewer extends TileEntityBase implements ISidedInventory {

	private ItemStack[] inv = new ItemStack[4];
	private int time = 400;

	@Override
	public int getTileEntityBlockID() {
		return GeoBlocks.BREWER.getBlockID();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.canBrew()) {
			time--;
			if (time <= 0) {
				this.brew();
				time = 400;
			}
		}
		else {
			time = 400;
		}
	}

	private boolean canBrew() {
		if (inv[0] == null)
			return false;
		if (inv[0].itemID != GeoItems.SHARD.getShiftedItemID())
			return false;
		if (!this.isSlotModifiable(1) && !this.isSlotModifiable(2) && !this.isSlotModifiable(3))
			return false;

		return true;
	}

	private boolean isSlotModifiable(int i) {
		ItemStack is = inv[i];
		ItemStack z = inv[0];
		int zd = z.getItemDamage();
		if (is == null)
			return false;
		if (is.itemID == Item.potion.itemID) {
			if (zd == ReikaDyeHelper.BLACK.ordinal() || zd == ReikaDyeHelper.BROWN.ordinal() || zd == ReikaDyeHelper.PURPLE.ordinal())
				return ReikaPotionHelper.isActualPotion(is.getItemDamage());
			else
				return true;
		}
		else if (is.itemID == GeoItems.POTION.getShiftedItemID()) {
			return zd == ReikaDyeHelper.BLACK.ordinal() || zd == ReikaDyeHelper.BROWN.ordinal() || zd == ReikaDyeHelper.PURPLE.ordinal();
		}
		return false;
	}

	private void brew() {
		ReikaDyeHelper color = ReikaDyeHelper.getColorFromDamage(inv[0].getItemDamage());
		boolean custom = CrystalPotionController.requiresCustomPotion(color);
		inv[0] = null;

		for (int i = 1; i < 4; i++) {
			if (inv[i] != null) {
				inv[i] = this.getPotionStackFromColor(color);
			}
		}
	}

	public static ItemStack getPotionStackFromColor(ReikaDyeHelper color) {
		ItemStack shard = GeoItems.SHARD.getStackOfMetadata(color.ordinal());
		String eff = shard.getItem().getPotionEffect(shard);
		boolean custom = CrystalPotionController.requiresCustomPotion(color);
		ItemStack is = new ItemStack(Item.potion, 1, ReikaPotionHelper.AWKWARD_META);
		if (custom) {
			is = new ItemStack(GeoItems.POTION.getShiftedItemID(), 1, color.ordinal());
		}
		else {
			if (CrystalPotionController.isCorruptedPotion(color)) {
				int newmeta = PotionHelper.applyIngredient(is.getItemDamage(), eff);
				is = new ItemStack(is.itemID, 1, newmeta);
				int cmeta = PotionHelper.applyIngredient(is.getItemDamage(), PotionHelper.fermentedSpiderEyeEffect);
				is = new ItemStack(is.itemID, 1, cmeta);
			}
			else {
				int newmeta = PotionHelper.applyIngredient(is.getItemDamage(), eff);
				is = new ItemStack(is.itemID, 1, newmeta);
			}
		}
		return is;
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected String getTEName() {
		return "Crystal Brewery";
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return ReikaInventoryHelper.decrStackSize(this, i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return ReikaInventoryHelper.getStackInSlotOnClosing(this, i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv[i] = itemstack;
	}

	@Override
	public String getInvName() {
		return this.getTEName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep) {
		return ReikaMathLibrary.py3d(ep.posX-xCoord-0.5, ep.posY-yCoord-0.5, ep.posZ-zCoord-0.5) <= 8;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if (is.itemID == GeoItems.POTION.getShiftedItemID())
			return i != 0;
		if (is.itemID == GeoItems.SHARD.getShiftedItemID())
			return i == 0;
		if (is.itemID == Item.potion.itemID)
			return i != 0;
		if (is.getItem().getPotionEffect(is) != null)
			return i == 0;
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return ReikaInventoryHelper.getWholeInventoryForISided(this);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return this.isItemValidForSlot(i, itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i != 0;
	}

	public int getBrewTime() {
		return time;
	}

	public void setBrewTime(int par2) {
		time = par2;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
		inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < inv.length)
			{
				inv[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		time = par1NBTTagCompound.getInteger("BrewTime");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("BrewTime", time);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; ++i)
		{
			if (inv[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				inv[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		par1NBTTagCompound.setTag("Items", nbttaglist);
	}

}
