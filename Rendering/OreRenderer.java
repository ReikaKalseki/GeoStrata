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

import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Libraries.ReikaIconHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Blocks.BlockOreTile;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class OreRenderer implements ISimpleBlockRenderingHandler {

	private static final Random rand = new Random();

	//private static final EnumMap<ModOreList, ArrayList<IIcon>> modOreIcons = new EnumMap(ModOreList.class);
	//private static final EnumMap<ReikaOreHelper, ArrayList<IIcon>> oreIcons = new EnumMap(ReikaOreHelper.class);
	private static final BlockMap<IIcon> icons = new BlockMap();

	public static void regenIcons(TextureStitchEvent.Pre evt) {
		IIcon stone = Blocks.stone.getIcon(0, 0);
		for (int i = 0; i < ReikaOreHelper.oreList.length; i++) {
			ReikaOreHelper ore = ReikaOreHelper.oreList[i];
			if (ore.canGenerateIn(Blocks.stone)) {
				Collection<ItemStack> c = ore.getAllOreBlocks();
				//ArrayList li = new ArrayList();
				//oreIcons.put(ore, li);
				int k = 0;
				for (ItemStack is : c) {
					try {
						Block b = Block.getBlockFromItem(is.getItem());
						int meta = is.getItemDamage();
						IIcon ico = b.getIcon(1, meta);
						String n = "geostrata:ore_"+ore.name().toLowerCase()+"_"+k;
						k++;
						IIcon tex = ReikaIconHelper.clipFrom(ico, stone, evt.map, n);
						//li.add(tex);
						icons.put(b, meta, tex);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		for (int i = 0; i < ModOreList.oreList.length; i++) {
			ModOreList ore = ModOreList.oreList[i];
			if (ore.canGenerateIn(Blocks.stone)) {
				Collection<ItemStack> c = ore.getAllOreBlocks();
				//ArrayList li = new ArrayList();
				//modOreIcons.put(ore, li);
				int k = 0;
				for (ItemStack is : c) {
					try {
						Block b = Block.getBlockFromItem(is.getItem());
						int meta = is.getItemDamage();
						IIcon ico = b.getIcon(1, meta);
						String n = "geostrata:ore_"+ore.name().toLowerCase()+"_"+k;
						k++;
						IIcon tex = ReikaIconHelper.clipFrom(ico, stone, evt.map, n);
						//li.add(tex);
						icons.put(b, meta, tex);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*
	private IIcon getRandomizedTexture(OreType ore) {
		ArrayList<IIcon> icons;
		if (ore instanceof ReikaOreHelper) {
			icons = oreIcons.get(ore);
		}
		else if (ore instanceof ModOreList) {
			icons = modOreIcons.get(ore);
		}
		else {
			throw new IllegalArgumentException("What kind of ore is this?!");
		}
		int index = rand.nextInt(icons.size());
		return icons.get(index);
	}*/

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {
		RockTypes r = BlockOreTile.getRockFromItem(metadata);
		if (r == null) {
			return;
		}
		//ReikaJavaLibrary.pConsole(r);
		ItemStack ore = BlockOreTile.getOreByItemMetadata(Item.getItemFromBlock(block), metadata);
		Block b = Block.getBlockFromItem(ore.getItem());
		int meta = ore.getItemDamage();
		Tessellator v5 = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		//IIcon[] ico = new IIcon[]{r.getIcon(), this.getOreTexture(b, meta)};
		IIcon[] ico = new IIcon[]{this.getOreTexture(b, meta), ((BlockOreTile)block).getRockIcon(r)};

		for (int i = 0; i < ico.length; i++) {
			double dx = 0;
			double dy = 0;
			double dz = 0;
			double d = 0.001*i;
			IIcon icon = rb.getIconSafe(ico[i]);
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-0.5, -0.5, -0.5);
			v5.startDrawingQuads();
			int color = block.getRenderColor(metadata);
			v5.setColorOpaque_I(color);
			v5.setNormal(0, -1, 0);
			rb.renderFaceYNeg(b, dx, dy-d, dz, icon);

			v5.setNormal(0, 1, 0);
			rb.renderFaceYPos(b, dx, dy+d, dz, icon);

			v5.setNormal(0, 0, -1);
			rb.renderFaceZNeg(b, dx, dy, dz-d, icon);

			v5.setNormal(0, 0, 1);
			rb.renderFaceZPos(b, dx, dy, dz+d, icon);

			v5.setNormal(-1, 0, 0);
			rb.renderFaceXNeg(b, dx-d, dy, dz, icon);

			v5.setNormal(1, 0, 0);
			rb.renderFaceXPos(b, dx+d, dy, dz, icon);
			v5.draw();

			GL11.glTranslated(0.5, 0.5, 0.5);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		//rb.renderStandardBlockWithAmbientOcclusion(te.getOreBlock(), x, y, z, 1, 1, 1);

		IIcon icon = rb.getIconSafe(te.getOreBlock().getIcon(0, te.getOreMeta()));
		int r = 127;
		v5.setColorOpaque(r, r, r);
		v5.setNormal(0, -1, 0);
		v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y-1, z));
		if (b.shouldSideBeRendered(world, x, y-1, z, ForgeDirection.DOWN.ordinal()))
			rb.renderFaceYNeg(b, x, y, z, icon);
		r = 255;
		v5.setColorOpaque(r, r, r);
		v5.setNormal(0, 1, 0);
		v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y+1, z));
		if (b.shouldSideBeRendered(world, x, y+1, z, ForgeDirection.UP.ordinal()))
			rb.renderFaceYPos(b, x, y, z, icon);
		r = 192;
		v5.setColorOpaque(r, r, r);
		v5.setNormal(0, 0, -1);
		v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z-1));
		if (b.shouldSideBeRendered(world, x, y, z-1, ForgeDirection.NORTH.ordinal()))
			rb.renderFaceZNeg(b, x, y, z, icon);
		v5.setColorOpaque(r, r, r);
		v5.setNormal(0, 0, 1);
		v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z+1));
		if (b.shouldSideBeRendered(world, x, y, z+1, ForgeDirection.SOUTH.ordinal()))
			rb.renderFaceZPos(b, x, y, z, icon);
		r = 160;
		v5.setColorOpaque(r, r, r);
		v5.setNormal(-1, 0, 0);
		v5.setBrightness(b.getMixedBrightnessForBlock(world, x-1, y, z));
		if (b.shouldSideBeRendered(world, x-1, y, z, ForgeDirection.WEST.ordinal()))
			rb.renderFaceXNeg(b, x, y, z, icon);
		v5.setColorOpaque(r, r, r);
		v5.setNormal(1, 0, 0);
		v5.setBrightness(b.getMixedBrightnessForBlock(world, x+1, y, z));
		if (b.shouldSideBeRendered(world, x+1, y, z, ForgeDirection.EAST.ordinal()))
			rb.renderFaceXPos(b, x, y, z, icon);
		v5.setColorOpaque(r, r, r);

		int color = b.colorMultiplier(world, x, y, z);
		float red = color >> 16 & 0xFF;
		float green = color >> 8 & 0xFF;
		float blue = color >> 0 & 0xFF;
		rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, red/255, green/255, blue/255);
		/*
		OreType ore = te.getOreType();
		//IIcon ico = this.getRandomizedTexture(ore);//vent.getIcon();
		IIcon ico = this.getOreTexture(te.getOreBlock(), te.getOreMeta());
		ico = rb.getIconSafe(ico);
		v5.setColorOpaque_F(255, 255, 255);
		rb.renderFaceYPos(b, x, y+0.001, z, ico);
		rb.renderFaceYNeg(b, x, y-0.001, z, ico);*/
		return true;
	}

	private IIcon getOreTexture(Block b, int meta) {
		return b.getIcon(0, meta);//icons.get(b, meta); comment out dynamic system for now
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return GeoStrata.proxy.oreRender;
	}



}
