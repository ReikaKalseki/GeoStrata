/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Rendering;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Base.TileEntityBase;

public class ItemTileEntityBlockRenderer implements IItemRenderer {

	private final HashMap<Integer, TileEntity> tileMap = new HashMap();

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
		TileEntity tile = this.getTile(item);
		if (tile instanceof TileEntityBase)
			((TileEntityBase)tile).setBlockMetadata(item.getItemDamage());

		if (type == ItemRenderType.ENTITY) {
			double a = -0.67;
			double b = -0.4;
			double c = -0.67;
			GL11.glTranslated(a, b, c);
		}

		TileEntityRenderer.instance.renderTileEntityAt(tile, 0.1F, 0, 0.1F, 0);
	}

	private TileEntity getTile(ItemStack item) {
		TileEntity tile = tileMap.get(item.itemID);
		if (tile == null) {
			Block b = Block.blocksList[((ItemBlock)item.getItem()).getBlockID()];
			tile = b.createTileEntity(null, item.getItemDamage());
			tileMap.put(item.itemID, tile);
		}
		return tile;
	}

}
