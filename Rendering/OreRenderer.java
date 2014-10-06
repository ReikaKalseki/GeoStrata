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
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Interfaces.OreType;
import Reika.DragonAPI.Libraries.ReikaIconHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Blocks.BlockOreTile;
import Reika.GeoStrata.Items.ItemBlockGeoOre;
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
					Block b = Block.getBlockFromItem(is.getItem());
					int meta = is.getItemDamage();
					IIcon ico = b.getIcon(1, meta);
					String n = "geostrata:ore_"+ore.name().toLowerCase()+"_"+k;
					k++;
					IIcon tex = ReikaIconHelper.clipFrom(ico, stone, TextureMap.locationBlocksTexture, evt.map, n);
					//li.add(tex);
					icons.put(b, meta, tex);
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
					Block b = Block.getBlockFromItem(is.getItem());
					int meta = is.getItemDamage();
					IIcon ico = b.getIcon(1, meta);
					String n = "geostrata:ore_"+ore.name().toLowerCase()+"_"+k;
					k++;
					IIcon tex = ReikaIconHelper.clipFrom(ico, stone, TextureMap.locationBlocksTexture, evt.map, n);
					//li.add(tex);
					icons.put(b, meta, tex);
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
		RockTypes r = ItemBlockGeoOre.getType(metadata);
		ItemStack ore = BlockOreTile.getOreByItemMetadata(metadata);
		Block b = Block.getBlockFromItem(ore.getItem());
		int meta = ore.getItemDamage();
		Tessellator v5 = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		IIcon[] ico = new IIcon[]{r.getIcon(), this.getOreTexture(b, meta)};

		for (int i = 0; i < ico.length; i++) {
			double dx = 0;
			double dy = 0;
			double dz = 0;
			double d = 0.001*i;

			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-0.5, -0.5, -0.5);
			v5.startDrawingQuads();
			v5.setNormal(0, -1, 0);
			rb.renderFaceYNeg(b, dx, dy-d, dz, ico[i]);
			v5.draw();

			v5.startDrawingQuads();
			v5.setNormal(0, 1, 0);
			rb.renderFaceYPos(b, dx, dy+d, dz, ico[i]);
			v5.draw();

			v5.startDrawingQuads();
			v5.setNormal(0, 0, -1);
			rb.renderFaceZNeg(b, dx, dy, dz-d, ico[i]);
			v5.draw();
			v5.startDrawingQuads();
			v5.setNormal(0, 0, 1);
			rb.renderFaceZPos(b, dx, dy, dz+d, ico[i]);
			v5.draw();
			v5.startDrawingQuads();
			v5.setNormal(-1, 0, 0);
			rb.renderFaceXNeg(b, dx-d, dy, dz, ico[i]);
			v5.draw();
			v5.startDrawingQuads();
			v5.setNormal(1, 0, 0);
			rb.renderFaceXPos(b, dx+d, dy, dz, ico[i]);
			v5.draw();
		}

		GL11.glTranslated(0.5, 0.5, 0.5);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
		TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
		OreType ore = te.getOreType();
		//IIcon ico = this.getRandomizedTexture(ore);//vent.getIcon();
		IIcon ico = this.getOreTexture(te.getOreBlock(), te.getOreMeta());
		v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y+1, z));
		v5.setColorOpaque_F(255, 255, 255);
		rb.renderFaceYPos(b, x, y-0.002, z, ico);
		return true;
	}

	private IIcon getOreTexture(Block b, int meta) {
		return icons.get(b, meta);
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
