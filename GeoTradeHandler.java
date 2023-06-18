/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.util.Arrays;
import java.util.Collection;

import Reika.DragonAPI.Auxiliary.VillageTradeHandler.SimpleTradeHandler;
import Reika.DragonAPI.Auxiliary.VillageTradeHandler.TradeToAdd;


public class GeoTradeHandler extends SimpleTradeHandler {

	public static final GeoTradeHandler instance = new GeoTradeHandler();

	private GeoTradeHandler() {

	}

	@Override
	protected Collection<TradeToAdd> getTradesToAdd() {
		return Arrays.asList(new TradeToAdd(VoidOpalTrade.class, 0.5, "OpalTrade"), new TradeToAdd(LTDTrade.class, 0.15, "LTDTrade"));
	}

}
