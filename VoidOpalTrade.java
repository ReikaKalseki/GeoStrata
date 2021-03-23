package Reika.GeoStrata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;

import Reika.ChromatiCraft.Magic.Artefact.UATrades.EDCommodityHook;
import Reika.DragonAPI.Interfaces.PlayerSpecificTrade;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.GeoStrata.Registry.GeoBlocks;

public class VoidOpalTrade implements EDCommodityHook {

	private static final int STACK_SIZE = 24; //was 16
	private static final int BASELINE_PRICE = 520000; //was 540k
	private static final int BASELINE_PEAK = 1200000;

	private static final int REDUCTION_THRESHOLD = 192;
	private static final double REDUCTION_EXPONENT = 0.998; //value raised to this power for every surplus item over thresh until thresh 2
	private static final int REDUCTION_THRESHOLD_2 = 1024;
	private static final double REDUCTION_EXPONENT_2 = 0.985; //value raised to this power for every surplus item over thresh 2

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
		double fac = Math.pow(price/BASELINE_PRICE, 2)*Math.pow((double)max/BASELINE_PEAK, 6); //was ^2*
		emeraldValue = Math.min(64, MathHelper.ceiling_double_int(128*fac)); //used to be 32 and have *inputStack.stackSize/inputStack.getMaxStackSize()
		GeoStrata.logger.log("Received void opal price data: "+average+"/"+upper+"/"+max+" -> "+price+"/"+fac+" -> "+emeraldValue+"x emeralds per "+STACK_SIZE);
		emeraldValue = Math.max(1, emeraldValue);
	}

	@Override
	public MerchantRecipe createTrade() {
		return new Trade(inputStack, emeraldValue);
	}

	private static class Trade extends MerchantRecipe implements PlayerSpecificTrade {

		private final int baseValue;

		private int currentValue;
		private int currentPrice;

		private Trade(ItemStack in, int out) {
			super(in, new ItemStack(Items.emerald, out, 0));
			baseValue = out;
		}

		@Override
		public boolean isValid(EntityPlayer ep) {
			float val = baseValue;
			currentPrice = STACK_SIZE;
			int amt = ReikaInventoryHelper.countItem(GeoBlocks.VOIDOPAL.getItem(), ep.inventory.mainInventory)+Math.max(0, 2*(baseValue-1));
			int over = amt-REDUCTION_THRESHOLD;
			if (over > 0) {
				int over2 = amt-REDUCTION_THRESHOLD_2;
				if (over2 > 0) {
					over -= over2;
					val *= Math.pow(REDUCTION_EXPONENT_2, over2);
				}
				val *= Math.pow(REDUCTION_EXPONENT, over);
			}
			currentValue = (int)Math.max(1, val);
			if (val < 1) {
				currentPrice /= val;
			}
			this.getItemToSell().stackSize = currentValue;
			this.getItemToBuy().stackSize = Math.min(this.getItemToBuy().getMaxStackSize(), currentPrice);
			return true;
		}
	}

}
