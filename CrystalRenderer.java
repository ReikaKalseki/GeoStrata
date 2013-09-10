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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CrystalRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelID, RenderBlocks rb) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.dyes[meta];
		int red = dye.getRed();
		int green = dye.getGreen();
		int blue = dye.getBlue();
		int alpha = 192;

		Icon ico = b.getIcon(0, meta);
		ico = Block.sand.getIcon(0, 0);
		double u = ico.getMinU();
		double v = ico.getMinV();
		double xu = ico.getMaxU();
		double xv = ico.getMaxV();

		//xu = u = xv = v = 0;

		Tessellator v5 = Tessellator.instance;

		double maxx = b.getBlockBoundsMaxX();
		double minx = b.getBlockBoundsMinX();
		double miny = b.getBlockBoundsMinY();
		double maxy = b.getBlockBoundsMaxY();
		double maxz = b.getBlockBoundsMaxZ();
		double minz = b.getBlockBoundsMinZ();

		int l = b.getMixedBrightnessForBlock(world, x, y, z);

		//v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
		v5.setBrightness(240);
		v5.addTranslation(x, y, z);
		v5.setColorRGBA_F(red/255F, green/255F, blue/255F, 255/255F);

		this.renderSpike(v5, u, v, xu, xv);
		if (x%4 == 0)
			this.renderXAngledSpike(v5, u, v, xu, xv, 0.1875);
		if (x%4 == 2)
			this.renderXAngledSpike(v5, u, v, xu, xv, -0.1875);
		if (x%2 == 0)
			this.renderZAngledSpike(v5, u, v, xu, xv, 0.1875);
		if (x%2 == 1)
			this.renderZAngledSpike(v5, u, v, xu, xv, -0.1875);

		v5.addTranslation(-x, -y, -z);

		return true;
	}

	private void renderSpike(Tessellator v5, double u, double v, double xu, double xv) {
		double core = 0.15;
		double vl = 0.8;

		v5.addVertexWithUV(0.5-core, 0, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core, 0, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core, xu, xv);
		v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5-core, 0, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core, 0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5-core, vl, 0.5+core, u, xv);

		v5.addVertexWithUV(0.5+core, 0, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core, 0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5-core, 0, 0.5-core, u, v);
		v5.addVertexWithUV(0.5-core, 0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5-core, vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5-core, vl, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
		v5.addVertexWithUV(0.5, 1, 0.5, u, xv);

		v5.addVertexWithUV(0.5+core, vl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
		v5.addVertexWithUV(0.5, 1, 0.5, u, xv);

		v5.addVertexWithUV(0.5-core, vl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core, vl, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
		v5.addVertexWithUV(0.5, 1, 0.5, u, xv);

		v5.addVertexWithUV(0.5-core, vl, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core, vl, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5, 1, 0.5, xu, xv);
		v5.addVertexWithUV(0.5, 1, 0.5, u, xv);
	}

	private void renderXAngledSpike(Tessellator v5, double u, double v, double xu, double xv, double out) {
		double core = 0.12;
		double vl = 0.55;
		double dvl = vl/6D;
		double dy = -0.1;
		double tout = out;//0.1875;
		double htip = 0.1;
		int dir = out > 0 ? 1 : -1;

		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3, dy+0, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3, dy+0, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, u, xv);

		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir, dy+dvl, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5+core*dir*3, dy, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3, dy, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, xu, xv);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, u, xv);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, u, xv);

		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, u, xv);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5+core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5+core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, u, xv);

		v5.addVertexWithUV(0.5+core*dir+out, dy+vl+dvl, 0.5-core, u, v);
		v5.addVertexWithUV(0.5+core*dir*3+out, dy+vl, 0.5-core, xu, v);
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, xu, xv); //tip
		v5.addVertexWithUV(0.5+core*dir+out+tout, dy+vl+dvl+htip, 0.5, u, xv);
	}

	private void renderZAngledSpike(Tessellator v5, double u, double v, double xu, double xv, double out) {
		double core = 0.12;
		double vl = 0.55;
		double dvl = vl/6D;
		double dy = -0.1;
		double tout = out;//0.1875;
		double htip = 0.1;
		int dir = out > 0 ? 1 : -1;

		v5.addVertexWithUV(0.5-core, dy+dvl, 0.5+core*dir, u, v);
		v5.addVertexWithUV(0.5-core, dy+0, 0.5+core*dir*3, xu, v);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, xu, xv);
		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, u, xv);

		v5.addVertexWithUV(0.5+core, dy+dvl, 0.5+core*dir, u, v);
		v5.addVertexWithUV(0.5+core, dy+0, 0.5+core*dir*3, xu, v);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, xu, xv);
		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, u, xv);

		v5.addVertexWithUV(0.5-core, dy+dvl, 0.5+core*dir, u, v);
		v5.addVertexWithUV(0.5+core, dy+dvl, 0.5+core*dir, xu, v);
		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, xu, xv);
		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, u, xv);

		v5.addVertexWithUV(0.5-core, dy, 0.5+core*dir*3, u, v);
		v5.addVertexWithUV(0.5+core, dy, 0.5+core*dir*3, xu, v);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, xu, xv);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, u, xv);

		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, u, v);
		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, xu, v);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, u, xv);

		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, u, v);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, xu, v);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, u, xv);

		v5.addVertexWithUV(0.5+core, dy+vl+dvl, 0.5+core*dir+out, u, v);
		v5.addVertexWithUV(0.5+core, dy+vl, 0.5+core*dir*3+out, xu, v);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, u, xv);

		v5.addVertexWithUV(0.5-core, dy+vl+dvl, 0.5+core*dir+out, u, v);
		v5.addVertexWithUV(0.5-core, dy+vl, 0.5+core*dir*3+out, xu, v);
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, xu, xv); //tip
		v5.addVertexWithUV(0.5, dy+vl+dvl+htip, 0.5+core*dir+out+tout, u, xv);
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
