/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.GeoStrata.Guardian.TileEntityGuardianStone;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Rendering.AcceleratorRenderer;
import Reika.GeoStrata.Rendering.ConnectedStoneRenderer;
import Reika.GeoStrata.Rendering.CrystalPlantRenderer;
import Reika.GeoStrata.Rendering.CrystalRenderer;
import Reika.GeoStrata.Rendering.EnderCrystalRenderer;
import Reika.GeoStrata.Rendering.GuardianStoneRenderer;
import Reika.GeoStrata.Rendering.ItemTileEntityBlockRenderer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class GeoClient extends GeoCommon {

	private static final CrystalRenderer crystal = new CrystalRenderer();
	private static ConnectedStoneRenderer connected;
	//private static ShapedStoneRenderer shaped;

	private static final ItemTileEntityBlockRenderer teibr = new ItemTileEntityBlockRenderer();
	private static final EnderCrystalRenderer csr = new EnderCrystalRenderer();

	@Override
	public void registerSounds() {

	}

	@Override
	public void registerRenderers() {
		crystalRender = RenderingRegistry.getNextAvailableRenderId();

		connectedRender = RenderingRegistry.getNextAvailableRenderId();
		connected = new ConnectedStoneRenderer(connectedRender);

		//shapedRender = RenderingRegistry.getNextAvailableRenderId();
		//shaped = new ShapedStoneRenderer(shapedRender);

		RenderingRegistry.registerBlockHandler(crystalRender, crystal);
		RenderingRegistry.registerBlockHandler(connectedRender, connected);
		//RenderingRegistry.registerBlockHandler(shapedRender, shaped);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuardianStone.class, new GuardianStoneRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalPlant.class, new CrystalPlantRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAccelerator.class, new AcceleratorRenderer());

		MinecraftForgeClient.registerItemRenderer(GeoBlocks.GUARDIAN.getBlockID(), teibr);
		MinecraftForgeClient.registerItemRenderer(GeoBlocks.ACCELERATOR.getBlockID(), teibr);
		MinecraftForgeClient.registerItemRenderer(GeoItems.ENDERCRYSTAL.getShiftedItemID(), csr);
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
