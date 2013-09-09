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

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CrystalRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		v5.addTranslation(x%16, y%16, z%16);
		ReikaDyeHelper dye = ReikaDyeHelper.dyes[world.getBlockMetadata(x, y, z)];
		ReikaJavaLibrary.pConsole(v5.xOffset);
		Color color = dye.getJavaColor();
		v5.setBrightness(240);
		v5.setColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), 64);

		v5.addVertex(0, 1, 1);
		v5.addVertex(1, 1, 1);
		v5.addVertex(1, 1, 0);
		v5.addVertex(0, 1, 0);

		v5.addTranslation(-x%16, -y%16, -z%16);
		//rb.renderFaceXNeg(block, x, y, z, block.getIcon(0, 1));

		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return GeoStrata.proxy.crystalRender;
	}

}
