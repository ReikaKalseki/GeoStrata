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

import java.awt.Color;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.TileEntityCrystalPlant;
import Reika.GeoStrata.Blocks.BlockCrystalPlant;
import Reika.GeoStrata.Blocks.BlockGuardianStone;
import Reika.GeoStrata.Registry.GeoBlocks;

public class CrystalPlantRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity te, double par2, double par4, double par6, float f) {
		TileEntityCrystalPlant tile = (TileEntityCrystalPlant)te;
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		ReikaTextureHelper.bindTerrainTexture();
		if (tile.renderPod()) {
			//this.drawInner(tile);
			GL11.glTranslated(0, 0.0625, 0);
			this.drawBulb(tile);
			GL11.glTranslated(0, -0.0625, 0);

			if (tile.emitsLight()) {
				GL11.glColor4f(1, 1, 1, 1);
				ReikaDyeHelper dye = tile.getColor();
				Color c = dye.getJavaColor().brighter();
				GL11.glColor4f(c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F, 1);
				double s = 0.25;
				GL11.glTranslated(0.5, -0.25, 0.5);
				GL11.glScaled(s, s, s);
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR);
				this.drawGlow(tile);
				this.drawSparkle(tile);
				GL11.glScaled(1/s, 1/s, 1/s);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawSparkle(TileEntityCrystalPlant tile) {
		Tessellator v5 = Tessellator.instance;
		Icon ico = ((BlockCrystalPlant)GeoBlocks.PLANT.getBlockInstance()).getSparkle();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
	}

	private void drawGlow(TileEntityCrystalPlant tile) {
		Tessellator v5 = Tessellator.instance;
		Icon ico = ((BlockCrystalPlant)GeoBlocks.PLANT.getBlockInstance()).getGlowSprite();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
	}

	private void drawBulb(TileEntityCrystalPlant tile) {
		Tessellator v5 = Tessellator.instance;
		Icon ico = ((BlockCrystalPlant)GeoBlocks.PLANT.getBlockInstance()).getBulbIcon(tile.getColor());
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		float mu = u+(du-u)/2;
		float mv = v+(dv-v)/2;

		double s = 0.1;
		double h = 0.3;
		double ph = 0.15;

		GL11.glColor4f(1, 1, 1, 0.25F);

		ReikaDyeHelper dye = tile.getColor();
		Color c = dye.getJavaColor().brighter();
		GL11.glColor4f(c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F, 1);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		v5.startDrawingQuads();
		//Bottom
		v5.addVertexWithUV(0.5-s, 0, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5+s, 0, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5+s, 0, 0.5-s, du, v);
		v5.addVertexWithUV(0.5-s, 0, 0.5-s, u, v);

		//Top point
		v5.addVertexWithUV(0.5-s, -h, 0.5-s, u, dv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, mu, dv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, u, mv);

		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, u, mv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, mu, v);
		v5.addVertexWithUV(0.5-s, -h, 0.5+s, u, v);

		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, u, mv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, mu, v);
		v5.addVertexWithUV(0.5+s, -h, 0.5-s, u, v);

		v5.addVertexWithUV(0.5+s, -h, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, mu, dv);
		v5.addVertexWithUV(0.5, -h-ph, 0.5, mu, mv);
		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, u, mv);


		v5.addVertexWithUV(0.5+s, 0, 0.5, u, mv);
		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5+s, -h, 0.5-s, du, v);
		v5.addVertexWithUV(0.5+s, 0, 0.5-s, u, v);

		v5.addVertexWithUV(0.5+s, 0, 0.5+s, u, dv);
		v5.addVertexWithUV(0.5+s, -h, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5+s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5+s, 0, 0.5, u, mv);

		v5.addVertexWithUV(0.5-s, 0, 0.5, u, mv);
		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5-s, -h, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5-s, 0, 0.5+s, u, dv);

		v5.addVertexWithUV(0.5-s, 0, 0.5-s, u, v);
		v5.addVertexWithUV(0.5-s, -h, 0.5-s, du, v);
		v5.addVertexWithUV(0.5-s, -h-ph/2, 0.5, du, mv);
		v5.addVertexWithUV(0.5-s, 0, 0.5, u, mv);

		v5.addVertexWithUV(0.5, 0, 0.5+s, u, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, du, mv);
		v5.addVertexWithUV(0.5+s, -h, 0.5+s, du, dv);
		v5.addVertexWithUV(0.5+s, 0, 0.5+s, u, dv);

		v5.addVertexWithUV(0.5-s, 0, 0.5+s, u, v);
		v5.addVertexWithUV(0.5-s, -h, 0.5+s, du, v);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5+s, du, mv);
		v5.addVertexWithUV(0.5, 0, 0.5+s, u, mv);

		v5.addVertexWithUV(0.5+s, 0, 0.5-s, u, dv);
		v5.addVertexWithUV(0.5+s, -h, 0.5-s, du, dv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, du, mv);
		v5.addVertexWithUV(0.5, 0, 0.5-s, u, mv);

		v5.addVertexWithUV(0.5, 0, 0.5-s, u, mv);
		v5.addVertexWithUV(0.5, -h-ph/2, 0.5-s, du, mv);
		v5.addVertexWithUV(0.5-s, -h, 0.5-s, du, v);
		v5.addVertexWithUV(0.5-s, 0, 0.5-s, u, v);
		v5.draw();

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}


	private void drawInner(TileEntityCrystalPlant te) {
		ReikaTextureHelper.bindTerrainTexture();
		BlockGuardianStone b = (BlockGuardianStone)GeoBlocks.GUARDIAN.getBlockInstance();
		Icon ico = b.getInnerIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		float uu = du-u;
		float vv = dv-v;

		float r = 16;
		u += uu/r;
		du -= uu/r;
		v += vv/r;
		dv -= vv/r;

		Tessellator v5 = Tessellator.instance;
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR);

		double s = 0.5;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, -0.15, 0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			GL11.glRotated(-90, 0, 1, 0);
		}
		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (te.hasWorldObj()) {
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(-rm.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glScaled(1/s, 1/s, 1/s);
			GL11.glTranslated(-0.5, 0.15, -0.5);
		}
		else {
			GL11.glRotated(90, 0, 1, 0);
			GL11.glScaled(1/s, 1/s, 1/s);
			GL11.glTranslated(-0.5, -0.5, -0.5);
		}
	}

}
