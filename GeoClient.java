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

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoISBRH;
import Reika.GeoStrata.Rendering.StairItemRenderer;

import cpw.mods.fml.client.FMLClientHandler;

public class GeoClient extends GeoCommon {

	private static final StairItemRenderer stair = new StairItemRenderer();

	@Override
	public void registerSounds() {

	}

	@Override
	public void registerRenderers() {
		ReikaRegistryHelper.instantiateAndRegisterISBRHs(GeoStrata.instance, GeoISBRH.values());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GeoBlocks.SLAB.getBlockInstance()), stair);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GeoBlocks.STAIR.getBlockInstance()), stair);
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
