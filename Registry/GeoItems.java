/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen.Registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.GeoGen.GeoConfig;
import Reika.GeoGen.Items.GeoItem;

public enum GeoItems implements RegistrationList {

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
		return new Object[]{GeoConfig.itemIDs[this.ordinal()], spriteIndex};
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
		return GeoConfig.itemIDs[this.ordinal()];
	}

	public int getShiftedItemID() {
		return GeoConfig.itemIDs[this.ordinal()]+256;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return false;
	}

}
