/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;

public class BlockShapedRock extends RockBlock {

	//public final String shapeType;

	private static final IIcon[][] textures = new IIcon[RockTypes.rockList.length][RockShapes.shapeList.length];
	private static boolean texturesLoaded = false;

	public BlockShapedRock() {
		super();
		//shapeType = type.toLowerCase();
	}

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1;
	}

	@Override
	public int getRenderType() {
		return 0;//GeoStrata.proxy.shapedRender;
	}

	@Override
	public final IIcon getIcon(int side, int meta) {
		RockTypes r = RockTypes.getTypeFromID(this);
		RockShapes s = RockShapes.getShape(this, meta);
		return textures[r.ordinal()][s.ordinal()];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		if (texturesLoaded)
			return;
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			for (int k = 0; k < RockShapes.shapeList.length; k++) {
				//int a = RockShapes.getShape(this).ordinal();
				//int b = type.ordinal();
				textures[i][k] = ico.registerIcon("GeoStrata:shaped/unstitched/tile"+k+"_"+i);
				//GeoStrata.logger.debug("Adding "+type.getName()+" "+shapeType+" icon "+icons[i].getIconName());
				texturesLoaded = true;
			}
		}
		/*
		for (int i = 0; i < blends.length; i++) {
			blendicons[i] = ico.registerIcon("GeoStrata:shaped/"+blends[i]+"/"+shapeType+"base");
		}*/
		/*
		int cols = RockShapes.shapeList.length;
		int rows = RockTypes.rockList.length;
		ReikaImageLoader.unstitchIconsFromSheet(textures, ico, "geostrata:shaped/sheet", cols, rows);
		 */
	}
	/*
	@Override
	public Icon getIcon(int s, int meta) {
		RockTypes rock = RockTypes.getTypeFromIDandMeta(blockID, meta);
		RockShapes shape = RockShapes.getShape(this);
		return textures[rock.ordinal()][shape.ordinal()];
	}*/

	//public String getDisplayName() {
	//	return ReikaStringParser.capFirstChar(shapeType);
	//}

	@Override
	public void whenInBeam(World world, int x, int y, int z, long power, int range) {
		RockShapes shape = RockShapes.getShape(world, x, y, z);
		if (shape == RockShapes.SMOOTH || shape == RockShapes.COBBLE)
			super.whenInBeam(world, x, y, z, power, range);
	}
}
