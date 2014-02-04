/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Rendering;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import Reika.GeoStrata.Guardian.TileEntityGuardianStone;

public class GuardianItemRenderer implements IItemRenderer {

	private static final TileEntityGuardianStone tile = new TileEntityGuardianStone();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		RenderBlocks rb = (RenderBlocks)data[0];
		//rb.renderBlockAsItem(GeoBlocks.GUARDIAN.getBlockInstance(), 0, 1);
		TileEntityRenderer.instance.renderTileEntityAt(tile, 0.1F, 0, 0.1F, 0);
	}

}
