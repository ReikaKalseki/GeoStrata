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

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Items.ItemCrystalPotion;
import Reika.GeoStrata.Items.ItemCrystalShard;

public enum GeoItems implements RegistrationList, IDRegistry {

	SHARD("Crystal Shard", ItemCrystalShard.class),
	POTION("Crystal Potion", ItemCrystalPotion.class);

	private String name;
	private Class itemClass;

	public static final GeoItems[] itemList = GeoItems.values();

	private GeoItems(String n, Class<? extends Item> cl) {
		name = n;
		itemClass = cl;
	}

	public ItemStack getStackOf() {
		return new ItemStack(this.getShiftedItemID(), 1, 0);
	}

	public ItemStack getStackOfMetadata(int meta) {
		return new ItemStack(this.getShiftedItemID(), 1, meta);
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getItemID()};
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name);
	}

	@Override
	public Class getObjectClass() {
		return itemClass;
	}

	@Override
	public String getBasicName() {
		return name;
	}

	@Override
	public String getMultiValuedName(int meta) {
		switch(this) {
		case SHARD:
		case POTION:
			return ReikaDyeHelper.dyes[meta].getName()+" "+this.getBasicName();
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		switch(this) {
		case SHARD:
		case POTION:
			return true;
		default:
			return false;
		}
	}

	@Override
	public int getNumberMetadatas() {
		switch(this) {
		case SHARD:
			return 16;
		case POTION:
			return 16;
		default:
			return 1;
		}
	}

	public int getItemID() {
		return GeoStrata.config.getItemID(this.ordinal());
	}

	public int getShiftedItemID() {
		return GeoStrata.config.getItemID(this.ordinal())+256;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return false;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 10000+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String getCategory() {
		return "Item IDs";
	}

	public boolean isDummiedOut() {
		return itemClass == null;
	}

}
