/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Items.ItemCluster;
import Reika.GeoStrata.Items.ItemCrystalPotion;
import Reika.GeoStrata.Items.ItemCrystalSeeds;
import Reika.GeoStrata.Items.ItemCrystalShard;
import Reika.GeoStrata.Items.ItemEnderCrystal;
import Reika.GeoStrata.Items.ItemPendant;

public enum GeoItems implements RegistryEnum {

	SHARD("Crystal Shard", ItemCrystalShard.class),
	POTION("Crystal Potion", ItemCrystalPotion.class),
	CLUSTER("Crystal Cluster", ItemCluster.class),
	PENDANT("Crystal Pendant", ItemPendant.class),
	PENDANT3("Enhanced Crystal Pendant", ItemPendant.class),
	SEED("Crystal Bloom Seeds", ItemCrystalSeeds.class),
	ENDERCRYSTAL("Ender Crystal", ItemEnderCrystal.class);

	private String name;
	private Class itemClass;

	public static final GeoItems[] itemList = values();

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
			return ReikaDyeHelper.dyes[meta].colorName+" "+this.getBasicName();
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		switch(this) {
		case SHARD:
		case POTION:
		case CLUSTER:
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
		case CLUSTER:
			return ((ItemCluster)this.getItemInstance()).getNumberTypes();
		default:
			return 1;
		}
	}

	public Item getItemInstance() {
		return Item.itemsList[this.getShiftedItemID()];
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

	public int getID() {
		return this.getItemID();
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

}
