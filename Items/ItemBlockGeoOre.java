package Reika.GeoStrata.Items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.GeoStrata.TileEntityGeoOre;
import Reika.GeoStrata.Blocks.BlockOreTile;
import Reika.GeoStrata.Registry.RockTypes;

public class ItemBlockGeoOre extends ItemBlock {

	public ItemBlockGeoOre(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag) {
			TileEntityGeoOre te = (TileEntityGeoOre)world.getTileEntity(x, y, z);
			ItemStack is = BlockOreTile.getOreByItemBlock(stack);
			Block b = Block.getBlockFromItem(is.getItem());
			int meta = is.getItemDamage();
			RockTypes rock = this.getType(meta);
			te.initialize(rock, b, meta);
		}
		return flag;
	}

	public static RockTypes getType(int dmg) {
		return RockTypes.rockList[dmg%RockTypes.rockList.length];
	}

	@Override
	public int getMetadata(int meta) {
		return 0;
	}

}
