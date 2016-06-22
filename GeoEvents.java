/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import net.minecraft.block.Block;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.GeoStrata.Blocks.BlockDecoGen.Types;
import Reika.GeoStrata.Registry.GeoBlocks;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class GeoEvents {

	public static final GeoEvents instance = new GeoEvents();

	private GeoEvents() {

	}

	@SubscribeEvent
	public void spikyFall(LivingFallEvent evt) {
		Coordinate c = new Coordinate(evt.entityLiving).offset(0, -1, 0);
		Block b = c.getBlock(evt.entityLiving.worldObj);
		int meta = c.getBlockMetadata(evt.entityLiving.worldObj);
		if (b == GeoBlocks.DECOGEN.getBlockInstance() && meta == Types.CRYSTALSPIKE.ordinal())
			evt.distance *= 1.5F;
	}
}
