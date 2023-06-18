package Reika.GeoStrata;

import net.minecraft.item.ItemStack;

import Reika.GeoStrata.Registry.GeoBlocks;

public class EDVoidOpalTrade extends GeoEDTrade {

	private static final int BASELINE_PRICE = 520000; //was 540k
	private static final int BASELINE_PEAK = 1200000;

	public EDVoidOpalTrade() {
		super(new ItemStack(GeoBlocks.VOIDOPAL.getBlockInstance(), VoidOpalTrade.STACK_SIZE), BASELINE_PRICE, BASELINE_PEAK, 3);
	}

	@Override
	public String getCommodityID() {
		return "Void Opals";
	}

	@Override
	public double getChancePerVillager() {
		return 0.5;
	}

}
