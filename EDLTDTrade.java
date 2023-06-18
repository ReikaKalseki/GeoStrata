package Reika.GeoStrata;

import net.minecraft.item.ItemStack;

public class EDLTDTrade extends GeoEDTrade {

	private static final int BASELINE_PRICE = 168000;
	private static final int BASELINE_PEAK = 800000;

	public EDLTDTrade() {
		super(new ItemStack(GeoStrata.lowTempDiamonds), BASELINE_PRICE, BASELINE_PEAK, 9);
	}

	@Override
	public String getCommodityID() {
		return "Low-Temperature Diamonds";
	}

	@Override
	public double getChancePerVillager() {
		return 0.25;
	}

}
