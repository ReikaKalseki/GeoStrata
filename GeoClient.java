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
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class GeoClient extends GeoCommon {

	public static final CrystalRenderer crystal = new CrystalRenderer();
	public static final OreRenderer ore = new OreRenderer();

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
		RenderingRegistry.registerBlockHandler(oreRender, ore);
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
