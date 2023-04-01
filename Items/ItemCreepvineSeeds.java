package Reika.GeoStrata.Items;

import net.minecraft.item.Item;

import Reika.GeoStrata.GeoStrata;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemCreepvineSeeds extends Item {

	public ItemCreepvineSeeds() {
		this.setMaxStackSize(16);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return "geostrata:creepvineseed";
	}

}
