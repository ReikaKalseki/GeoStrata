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

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Rendering.ConnectedStoneRenderer;
import Reika.GeoStrata.Rendering.OreRenderer;
import Reika.GeoStrata.Rendering.StairItemRenderer;
import Reika.GeoStrata.Rendering.VentRenderer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class GeoClient extends GeoCommon {

	private static ConnectedStoneRenderer connected;
	private static VentRenderer vent;
	//private static ShapedStoneRenderer shaped;
	private static final StairItemRenderer stair = new StairItemRenderer();
	private static OreRenderer ore;

	@Override
	public void registerSounds() {

	}

	@Override
	public void registerRenderers() {
		connectedRender = RenderingRegistry.getNextAvailableRenderId();
		connected = new ConnectedStoneRenderer(connectedRender);
		RenderingRegistry.registerBlockHandler(connectedRender, connected);

		ventRender = RenderingRegistry.getNextAvailableRenderId();
		vent = new VentRenderer();
		RenderingRegistry.registerBlockHandler(ventRender, vent);

		oreRender = RenderingRegistry.getNextAvailableRenderId();
		ore = new OreRenderer();
		RenderingRegistry.registerBlockHandler(oreRender, ore);

		//shapedRender = RenderingRegistry.getNextAvailableRenderId();
		//shaped = new ShapedStoneRenderer(shapedRender);
		//RenderingRegistry.registerBlockHandler(shapedRender, shaped);

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
