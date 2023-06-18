package Reika.GeoStrata.Items;

import net.minecraft.item.Item;

import Reika.GeoStrata.GeoStrata;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemLowTempDiamonds extends Item {

	public ItemLowTempDiamonds() {
		this.setMaxStackSize(64);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return "geostrata:lowtempdiamonds";
	}

}
