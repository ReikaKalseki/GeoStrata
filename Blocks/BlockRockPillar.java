package Reika.GeoStrata.Blocks;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.RockTypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockRockPillar extends RockBlock {

	private IIcon sideIcon;
	private IIcon endIcon;

	public BlockRockPillar() {
		super();
	}

	protected IIcon getSideIcon(int side) {
		return sideIcon;
	}

	protected IIcon getTopIcon(int side) {
		return endIcon;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		RockTypes r = RockTypes.getTypeFromID(this);
		int offset = r.ordinal();
		sideIcon = ico.registerIcon("geostrata:pillar/sheetpng/tile0_"+offset);
		endIcon = ico.registerIcon("geostrata:pillar/sheetpng/tile1_"+offset);
	}

	@Override
	public int getRenderType() {
		return 31;
	}

	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float a, float b, float c, int meta) {
		int j1 = this.damageDropped(meta);
		byte b0 = 0;

		switch (side) {
			case 0:
			case 1:
				b0 = 0;
				break;
			case 2:
			case 3:
				b0 = 8;
				break;
			case 4:
			case 5:
				b0 = 4;
		}

		return j1 | b0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta) {
		int k = meta & 12;
		int l = meta & 3;
		return k == 0 && (s == 1 || s == 0) ? this.getTopIcon(l) : (k == 4 && (s == 5 || s == 4) ? this.getTopIcon(l) : (k == 8 && (s == 2 || s == 3) ? this.getTopIcon(l) : this.getSideIcon(l)));
	}

	@Override
	public int damageDropped(int meta) {
		return meta & 3;
	}

	@Override
	protected ItemStack createStackedBlock(int meta) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(meta));
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public int quantityDropped(Random r) {
		return 1;
	}

}
