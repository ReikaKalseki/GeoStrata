package Reika.GeoStrata.Rendering;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Blocks.BlockGlowingVines.TileGlowingVines;


public class GlowVineRenderer implements ISBRH {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		if (renderPass != 1)
			return false;
		int dx = (x%4+4)%4;
		int dy = (y%4+4)%4;
		int dz = (z%4+4)%4;
		Tessellator.instance.setBrightness(240);
		Tessellator.instance.setColorOpaque_I(0xffffff);
		TileGlowingVines te = (TileGlowingVines)world.getTileEntity(x, y, z);
		boolean flag = false;
		for (int i = 0; i < 6; i++) {
			if (te.hasSide(i)) {
				this.renderSide(world, x, y, z, block, rb, ForgeDirection.VALID_DIRECTIONS[i], dx, dy, dz);
				flag = true;
			}
		}
		return flag;
	}

	private void renderSide(IBlockAccess world, int x, int y, int z, Block b, RenderBlocks rb, ForgeDirection side, int dx, int dy, int dz) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = b.blockIcon;
		double o = 0.005;
		switch(side) {
			case DOWN: {
				float u = ico.getInterpolatedU(dx*4);
				float v = ico.getInterpolatedV(dz*4);
				float du = ico.getInterpolatedU((dx+1)*4);
				float dv = ico.getInterpolatedV((dz+1)*4);
				//ReikaJavaLibrary.pConsole(dx+","+dz+" ["+(dx+1)+","+(dz+1)+"]  >>  "+u+" & "+v+", "+du+ "& "+dv);
				v5.addVertexWithUV(x, y+o, z+1, u, dv);
				v5.addVertexWithUV(x+1, y+o, z+1, du, dv);
				v5.addVertexWithUV(x+1, y+o, z, du, v);
				v5.addVertexWithUV(x, y+o, z, u, v);
				break;
			}
			case UP: {
				float u = ico.getInterpolatedU(dx*4);
				float v = ico.getInterpolatedV(dz*4);
				float du = ico.getInterpolatedU((dx+1)*4);
				float dv = ico.getInterpolatedV((dz+1)*4);
				v5.addVertexWithUV(x, y+1-o, z, u, v);
				v5.addVertexWithUV(x+1, y+1-o, z, du, v);
				v5.addVertexWithUV(x+1, y+1-o, z+1, du, dv);
				v5.addVertexWithUV(x, y+1-o, z+1, u, dv);
				break;
			}
			case EAST: {
				float u = ico.getInterpolatedU(dy*4);
				float v = ico.getInterpolatedV(dz*4);
				float du = ico.getInterpolatedU((dy+1)*4);
				float dv = ico.getInterpolatedV((dz+1)*4);
				v5.addVertexWithUV(x+1-o, y, z+1, u, dv);
				v5.addVertexWithUV(x+1-o, y+1, z+1, du, dv);
				v5.addVertexWithUV(x+1-o, y+1, z, du, v);
				v5.addVertexWithUV(x+1-o, y, z, u, v);
				break;
			}
			case WEST: {
				float u = ico.getInterpolatedU(dy*4);
				float v = ico.getInterpolatedV(dz*4);
				float du = ico.getInterpolatedU((dy+1)*4);
				float dv = ico.getInterpolatedV((dz+1)*4);
				v5.addVertexWithUV(x+o, y, z, u, v);
				v5.addVertexWithUV(x+o, y+1, z, du, v);
				v5.addVertexWithUV(x+o, y+1, z+1, du, dv);
				v5.addVertexWithUV(x+o, y, z+1, u, dv);
				break;
			}
			case NORTH: {
				float u = ico.getInterpolatedU(dy*4);
				float v = ico.getInterpolatedV(dx*4);
				float du = ico.getInterpolatedU((dy+1)*4);
				float dv = ico.getInterpolatedV((dx+1)*4);
				v5.addVertexWithUV(x+1, y, z+o, u, dv);
				v5.addVertexWithUV(x+1, y+1, z+o, du, dv);
				v5.addVertexWithUV(x, y+1, z+o, du, v);
				v5.addVertexWithUV(x, y, z+o, u, v);
				break;
			}
			case SOUTH: {
				float u = ico.getInterpolatedU(dy*4);
				float v = ico.getInterpolatedV(dx*4);
				float du = ico.getInterpolatedU((dy+1)*4);
				float dv = ico.getInterpolatedV((dx+1)*4);
				v5.addVertexWithUV(x, y, z+1-o, u, v);
				v5.addVertexWithUV(x, y+1, z+1-o, du, v);
				v5.addVertexWithUV(x+1, y+1, z+1-o, du, dv);
				v5.addVertexWithUV(x+1, y, z+1-o, u, dv);
				break;
			}
			default:
				break;
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return GeoStrata.proxy.vineRender;
	}

}
