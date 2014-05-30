/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.API;

import net.minecraft.tileentity.TileEntity;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityAccelerator;

public class AcceleratorBlacklist {

	/** Use this to blacklist your TileEntity class from being accelerated with the TileEntity acclerator.
	 * You must specify a reason (from the {@link BlacklistReason} enum) which will be put into the loading log.
	 * Arguments: TileEntity class, Reason.
	 * Sample log message:<br>
	 * <i> GEOSTRATA:
	 * "TileEntity "Miner" has been blacklisted from the TileEntity Accelerator, because the creator finds it unbalanced or overpowered."
	 * </i>*/
	public static void addBlacklist(Class<? extends TileEntity> cl, String name, BlacklistReason r) {
		GeoStrata.logger.log("TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
		TileEntityAccelerator.addEntry(cl);
	}

	public static void addBlacklist(Class<? extends TileEntity> cl, BlacklistReason r) {
		addBlacklist(cl, cl.getSimpleName(), r);
	}

	public static enum BlacklistReason {
		BUGS("it will cause bugs or other errors."),
		CRASH("it would cause a crash."),
		BALANCE("the creator finds it unbalanced or overpowered."),
		EXPLOIT("it creates an exploit."),
		OPINION("the creator wishes it to be disabled.");

		public final String message;

		private BlacklistReason(String msg) {
			message = msg;
		}
	}

}
