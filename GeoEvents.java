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

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.EntityDecreaseAirEvent;
import Reika.DragonAPI.Instantiable.Event.PlayerPlaceBlockEvent;
import Reika.DragonAPI.Instantiable.Event.Client.BlockIconEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ItemEffectRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderBlockAtPosEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderBlockAtPosEvent.BlockRenderWatcher;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import Reika.DragonAPI.Instantiable.Math.Noise.Simplex3DGenerator;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.GeoStrata.Blocks.BlockDecoGen.Types;
import Reika.GeoStrata.Blocks.BlockPartialBounds;
import Reika.GeoStrata.Blocks.BlockPartialBounds.TilePartialBounds;
import Reika.GeoStrata.Blocks.BlockVent;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.World.ArcticSpiresGenerator;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class GeoEvents implements BlockRenderWatcher {

	public static final GeoEvents instance = new GeoEvents();

	private IIcon whitePackedIce;
	private IIcon glossyPackedIce;

	private final Simplex3DGenerator iceGlowNoise = (Simplex3DGenerator)new Simplex3DGenerator(45872187).setFrequency(0.25);

	private GeoEvents() {
		RenderBlockAtPosEvent.addListener(this);
	}

	/*
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderOpalFlecks(RenderWorldEvent.Post evt) {
		GL11.glDepthMask(false);
		GeoClient.getOpalRender().renderFlecks(evt);
		GL11.glDepthMask(true);
	}
	 */


	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyArctic(PlayerTickEvent evt) {
		if (evt.phase == Phase.START && ReikaEntityHelper.isInBiome(evt.player, GeoStrata.arcticSpires) && ReikaEntityHelper.getSkyLightAt(evt.player) >= 10) {
			if (evt.player.worldObj.isRemote) {
				if (DragonAPICore.rand.nextInt(800) == 0) {
					//sound
				}
			}
			else {
				if (!evt.player.isInsideOfMaterial(Material.water)) {
					boolean snow = evt.player.worldObj.isRaining();
					if (evt.player.getAir() < (snow ? 150 : 50)) //1/6 or 1/2 the meter if sun or snow
						evt.player.attackEntityFrom(GeoStrata.coldDamage, snow ? 1 : 2);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void setBiomeWaterColor(FogColors evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (ep == null || ep.worldObj == null)
			return;
		if (!ep.isInsideOfMaterial(Material.water))
			return;
		if (ReikaEntityHelper.isInBiome(ep, GeoStrata.arcticSpires)) {
			evt.red = 50/255F;
			evt.green = 53/255F;
			evt.blue = 0.9F;
		}
		else if (ReikaEntityHelper.isInBiome(ep, GeoStrata.kelpForest)) {
			evt.red = 76/255F;
			evt.green = 130/255F;
			evt.blue = 80/255F;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clearCachedNoise(SinglePlayerLogoutEvent evt) {
		ArcticSpiresGenerator.instance.clear();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clearCachedNoise(ClientDisconnectionFromServerEvent evt) {
		ArcticSpiresGenerator.instance.clear();
	}

	@SubscribeEvent
	public void correctPackedIceDrops(HarvestDropsEvent evt) {
		if (evt.block == Blocks.packed_ice && evt.blockMetadata > 0) {
			for (int i = 0; i < evt.drops.size(); i++) {
				ItemStack is = evt.drops.get(i);
				if (ReikaItemHelper.matchStackWithBlock(is, Blocks.packed_ice))
					evt.drops.set(i, new ItemStack(Blocks.packed_ice, is.stackSize, evt.blockMetadata));
			}
		}
	}

	@SubscribeEvent
	public void correctPackedIcePlacement(PlayerPlaceBlockEvent evt) {
		if (evt.block == Blocks.packed_ice && evt.getItem().getItemDamage() > 0) {
			evt.world.setBlock(evt.xCoord, evt.yCoord, evt.zCoord, evt.block, evt.getItem().getItemDamage(), 3);
			ReikaSoundHelper.playPlaceSound(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, evt.block);
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 0) {
			whitePackedIce = event.map.registerIcon("geostrata:whiteice");
			glossyPackedIce = event.map.registerIcon("geostrata:glossyice");
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void retexturePackedIce(BlockIconEvent evt) {
		if (evt.blockType == Blocks.packed_ice) {
			switch(evt.meta) {
				case 1:
					evt.icon = whitePackedIce;
					break;
				case 2:
					evt.icon = glossyPackedIce;
					break;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fixAirRender(RenderGameOverlayEvent.Pre evt) {
		if (evt.type == ElementType.AIR) {
			evt.setCanceled(true);

			ReikaTextureHelper.bindHUDTexture();
			Minecraft mc = Minecraft.getMinecraft();
			mc.mcProfiler.startSection("air");
			GL11.glEnable(GL11.GL_BLEND);
			int left = evt.resolution.getScaledWidth() / 2 + 91;
			int top = evt.resolution.getScaledHeight() - GuiIngameForge.right_height;

			if (mc.thePlayer.getAir() < 300) { //instead of in water
				int air = mc.thePlayer.getAir();
				int full = MathHelper.ceiling_double_int((air - 2) * 10.0D / 300.0D);
				int partial = MathHelper.ceiling_double_int(air * 10.0D / 300.0D) - full;

				for (int i = 0; i < full + partial; ++i)
				{
					ReikaGuiAPI.instance.drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
				}
				GuiIngameForge.right_height += 10;
			}

			GL11.glDisable(GL11.GL_BLEND);
			mc.mcProfiler.endSection();
		}
	}

	@SubscribeEvent
	public void smokeVentAir(EntityDecreaseAirEvent evt) {
		/*
		int x = MathHelper.floor_double(evt.entityLiving.posX);
		int y = MathHelper.floor_double(evt.entityLiving.posY-(evt.entityLiving.worldObj.isRemote ? 1.75 : 0.5));
		int z = MathHelper.floor_double(evt.entityLiving.posZ);
		//ReikaJavaLibrary.pConsole(evt.entityLiving.worldObj.getBlock(x, y, z)+" @ "+x+", "+z, Side.CLIENT, evt.entityLiving instanceof EntityPlayer);
		if (evt.entityLiving.worldObj.getBlock(x, y, z) == GeoBlocks.VENT.getBlockInstance()) {
			TileEntityVent te = (TileEntityVent)evt.entityLiving.worldObj.getTileEntity(x, y, z);
			if (te.getType() == VentType.SMOKE && te.isActive()) {
				evt.setResult(Result.ALLOW);
				return;
			}
		}*/
		if (evt.entityLiving instanceof EntityPlayer && ReikaEntityHelper.isInBiome(evt.entityLiving, GeoStrata.arcticSpires) && ReikaEntityHelper.getSkyLightAt(evt.entityLiving) >= 10) {
			if (evt.entityLiving.getAir() >= 40)
				evt.setResult(Result.ALLOW);
			return;
		}
		long last = evt.entityLiving.getEntityData().getLong(BlockVent.SMOKE_VENT_TAG);
		if (evt.entityLiving.worldObj.getTotalWorldTime()-last <= 8) {
			evt.setResult(Result.ALLOW);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onBlockTriedRender(Block block, int x, int y, int z, WorldRenderer wr, RenderBlocks render, int renderPass) {
		if (block instanceof BlockPartialBounds && renderPass == 1) {
			TilePartialBounds te = (TilePartialBounds)render.blockAccess.getTileEntity(x, y, z);
			if (te.isFence()) {
				IBlockAccess access = render.blockAccess;
				double o = 0.005;
				boolean flag = render.enableAO;
				render.enableAO = false;
				Tessellator.instance.setColorRGBA_I(block.colorMultiplier(access, x, y, z), 96);
				Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(access, x, y, z));
				Tessellator.instance.setNormal(0, 1, 0);
				block.setBlockBoundsBasedOnState(access, x, y, z);
				render.renderMaxX = block.getBlockBoundsMaxX();
				render.renderMaxY = block.getBlockBoundsMaxY();
				render.renderMaxZ = block.getBlockBoundsMaxZ();
				render.renderMinX = block.getBlockBoundsMinX();
				render.renderMinY = block.getBlockBoundsMinY();
				render.renderMinZ = block.getBlockBoundsMinZ();
				if (block.shouldSideBeRendered(access, x-1, y, z, ForgeDirection.WEST.ordinal()))
					render.renderFaceXNeg(block, x-o, y, z, BlockPartialBounds.fenceOverlay);
				if (block.shouldSideBeRendered(access, x+1, y, z, ForgeDirection.EAST.ordinal()))
					render.renderFaceXPos(block, x+o, y, z, BlockPartialBounds.fenceOverlay);
				if (block.shouldSideBeRendered(access, x, y, z-1, ForgeDirection.NORTH.ordinal()))
					render.renderFaceZNeg(block, x, y, z-o, BlockPartialBounds.fenceOverlay);
				if (block.shouldSideBeRendered(access, x, y, z+1, ForgeDirection.SOUTH.ordinal()))
					render.renderFaceZPos(block, x, y, z+o, BlockPartialBounds.fenceOverlay);
				render.enableAO = flag;
			}
		}
		else if (block == Blocks.packed_ice && render.blockAccess.getBlockMetadata(x, y, z) == 2) {
			IBlockAccess access = render.blockAccess;
			boolean flag = render.enableAO;
			render.enableAO = false;
			int c = block.colorMultiplier(access, x, y, z);
			Tessellator.instance.setNormal(0, 1, 0);
			block.setBlockBoundsBasedOnState(access, x, y, z);
			render.renderMaxX = block.getBlockBoundsMaxX();
			render.renderMaxY = block.getBlockBoundsMaxY();
			render.renderMaxZ = block.getBlockBoundsMaxZ();
			render.renderMinX = block.getBlockBoundsMinX();
			render.renderMinY = block.getBlockBoundsMinY();
			render.renderMinZ = block.getBlockBoundsMinZ();
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (block.shouldSideBeRendered(access, dx, dy, dz, dir.ordinal())) {
					IIcon ico = block.getIcon(access, x, y, z, dir.ordinal());

					int i1 = Minecraft.getMinecraft().theWorld.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, dx, dy, dz);
					//int j1 = Minecraft.getMinecraft().theWorld.getSkyBlockTypeBrightness(EnumSkyBlock.Block, dx, dy, dz);
					int lv = (int)ReikaMathLibrary.normalizeToBounds(iceGlowNoise.getValue(dx, dy, dz), 1, 14);

					int br = i1 << 20 | lv << 4;

					Tessellator.instance.setBrightness(br);
					switch(dir) {
						case DOWN:
							Tessellator.instance.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.5F));
							render.renderFaceYNeg(block, x, y, z, ico);
							break;
						case UP:
							Tessellator.instance.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 1));
							render.renderFaceYPos(block, x, y, z, ico);
							break;
						case WEST:
							Tessellator.instance.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.6F));
							render.renderFaceXNeg(block, x, y, z, ico);
							break;
						case EAST:
							Tessellator.instance.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.6F));
							render.renderFaceXPos(block, x, y, z, ico);
							break;
						case NORTH:
							Tessellator.instance.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.8F));
							render.renderFaceZNeg(block, x, y, z, ico);
							break;
						case SOUTH:
							Tessellator.instance.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.8F));
							render.renderFaceZPos(block, x, y, z, ico);
							break;
						default:
							break;
					}
				}
			}
			render.enableAO = flag;
			return true;
		}
		return false;
	}

	@Override
	public int watcherSortIndex() {
		return 0;
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
