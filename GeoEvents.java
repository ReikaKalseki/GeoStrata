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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.Client.ItemEffectRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderBlockAtPosEvent;
import Reika.GeoStrata.Blocks.BlockDecoGen.Types;
import Reika.GeoStrata.Blocks.BlockPartialBounds;
import Reika.GeoStrata.Blocks.BlockPartialBounds.TilePartialBounds;
import Reika.GeoStrata.Registry.GeoBlocks;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class GeoEvents {

	public static final GeoEvents instance = new GeoEvents();

	private GeoEvents() {

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void partialBoundsFenceRender(RenderBlockAtPosEvent evt) {
		if (evt.block instanceof BlockPartialBounds && evt.renderPass == 1) {
			TilePartialBounds te = (TilePartialBounds)evt.world.getTileEntity(evt.xCoord, evt.yCoord, evt.zCoord);
			if (te.isFence()) {
				double o = 0.005;
				boolean flag = evt.render.enableAO;
				evt.render.enableAO = false;
				Tessellator.instance.setColorRGBA_I(evt.block.colorMultiplier(evt.world, evt.xCoord, evt.yCoord, evt.zCoord), 96);
				Tessellator.instance.setBrightness(evt.block.getMixedBrightnessForBlock(evt.world, evt.xCoord, evt.yCoord, evt.zCoord));
				Tessellator.instance.setNormal(0, 1, 0);
				evt.block.setBlockBoundsBasedOnState(evt.world, evt.xCoord, evt.yCoord, evt.zCoord);
				evt.render.renderMaxX = evt.block.getBlockBoundsMaxX();
				evt.render.renderMaxY = evt.block.getBlockBoundsMaxY();
				evt.render.renderMaxZ = evt.block.getBlockBoundsMaxZ();
				evt.render.renderMinX = evt.block.getBlockBoundsMinX();
				evt.render.renderMinY = evt.block.getBlockBoundsMinY();
				evt.render.renderMinZ = evt.block.getBlockBoundsMinZ();
				if (evt.block.shouldSideBeRendered(evt.world, evt.xCoord-1, evt.yCoord, evt.zCoord, ForgeDirection.WEST.ordinal()))
					evt.render.renderFaceXNeg(evt.block, evt.xCoord-o, evt.yCoord, evt.zCoord, BlockPartialBounds.fenceOverlay);
				if (evt.block.shouldSideBeRendered(evt.world, evt.xCoord+1, evt.yCoord, evt.zCoord, ForgeDirection.EAST.ordinal()))
					evt.render.renderFaceXPos(evt.block, evt.xCoord+o, evt.yCoord, evt.zCoord, BlockPartialBounds.fenceOverlay);
				if (evt.block.shouldSideBeRendered(evt.world, evt.xCoord, evt.yCoord, evt.zCoord-1, ForgeDirection.NORTH.ordinal()))
					evt.render.renderFaceZNeg(evt.block, evt.xCoord, evt.yCoord, evt.zCoord-o, BlockPartialBounds.fenceOverlay);
				if (evt.block.shouldSideBeRendered(evt.world, evt.xCoord, evt.yCoord, evt.zCoord+1, ForgeDirection.SOUTH.ordinal()))
					evt.render.renderFaceZPos(evt.block, evt.xCoord, evt.yCoord, evt.zCoord+o, BlockPartialBounds.fenceOverlay);
				evt.render.enableAO = flag;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void partialBoundsTooltips(ItemEffectRenderEvent evt) {
		if (this.isPartialBoundsBook(evt.getItem()))
			evt.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void partialBoundsTooltips(ItemTooltipEvent evt) {
		if (this.isPartialBoundsBook(evt.itemStack)) {
			evt.toolTip.add("Stores partial bounds block settings:");
			evt.toolTip.addAll(BlockBounds.readFromNBT("partialbounds", evt.itemStack.stackTagCompound).toClearString());
		}
	}

	private boolean isPartialBoundsBook(ItemStack is) {
		return is != null && is.getItem() == Items.book && is.stackTagCompound != null && is.stackTagCompound.hasKey("partialbounds");
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
