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
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Items.GeoItem;

public enum GeoItems implements RegistrationList, IDRegistry {

	SHARD(0, "Crystal Shard", GeoItem.class);

	private String name;
	private Class itemClass;
	private int spriteIndex;

	public static final GeoItems[] itemList = GeoItems.values();

	private GeoItems(int ind, String n, Class<? extends Item> cl) {
		name = n;
		itemClass = cl;
		spriteIndex = ind;
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class, int.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getItemID(), spriteIndex};
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaJavaLibrary.stripSpaces(name);
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
		return null;
	}

	@Override
	public boolean hasMultiValuedName() {
		return false;
	}

	@Override
	public int getNumberMetadatas() {
		return 16;
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
		return 10000;
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

}
