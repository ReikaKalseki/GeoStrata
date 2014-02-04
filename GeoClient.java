/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
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
import Reika.GeoStrata.Rendering.CrystalRenderer;
import Reika.GeoStrata.Rendering.GuardianItemRenderer;
import Reika.GeoStrata.Rendering.GuardianStoneRenderer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class GeoClient extends GeoCommon {

	public static final CrystalRenderer crystal = new CrystalRenderer();

	@Override
	public void registerSounds() {
		//RotarySounds.addSounds();
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.instance, SoundRegistry.soundList));
	}

	@Override
	public void registerRenderers() {
		crystalRender = RenderingRegistry.getNextAvailableRenderId();
		oreRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(crystalRender, crystal);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuardianStone.class, new GuardianStoneRenderer());

		MinecraftForgeClient.registerItemRenderer(GeoBlocks.GUARDIAN.getBlockID(), new GuardianItemRenderer());
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
