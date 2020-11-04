package Reika.GeoStrata;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import Reika.ChromatiCraft.Magic.Artefact.UATrades.EDCommodityHook;
import Reika.GeoStrata.Registry.GeoBlocks;

public class VoidOpalTrade implements EDCommodityHook {

	private static final int STACK_SIZE = 16;
	private static final int BASELINE_PRICE = 540000;
	private static final int BASELINE_PEAK = 1200000;

	private final ItemStack inputStack;
	private int emeraldValue;

	public VoidOpalTrade() {
		inputStack = new ItemStack(GeoBlocks.VOIDOPAL.getBlockInstance(), STACK_SIZE);
	}

	@Override
	public String getCommodityID() {
		return "Void Opals";
	}

	@Override
	public void onPriceReceived(int average, int upper, int max) {
		double price = upper/2D+average/2D;
		double fac = Math.pow(price/BASELINE_PRICE, 2)*Math.pow((double)max/BASELINE_PEAK, 2);
		emeraldValue = (int)Math.min(64, 32*fac*inputStack.stackSize/inputStack.getMaxStackSize());
		GeoStrata.logger.log("Received void opal price data: "+average+"/"+upper+"/"+max+" -> "+emeraldValue+"x emeralds per "+STACK_SIZE);
	}

	@Override
	public MerchantRecipe createTrade() {
		return new Trade(inputStack, emeraldValue);
	}

	private static class Trade extends MerchantRecipe {

		private Trade(ItemStack in, int out) {
			super(in, new ItemStack(Items.emerald, out, 0));
		}
	}

}
