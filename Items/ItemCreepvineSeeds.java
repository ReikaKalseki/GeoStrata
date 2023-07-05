package Reika.GeoStrata.Items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Blocks.BlockCreepvine;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.World.CreepvineGenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemCreepvineSeeds extends Item {

	public ItemCreepvineSeeds() {
		this.setMaxStackSize(16);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return "geostrata:creepvineseed";
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add("Can be planted in sufficiently deep and open water.");
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		x += dir.offsetX;
		y += dir.offsetY;
		z += dir.offsetZ;
		if (!world.isRemote && BlockCreepvine.canGrowOn(world, x, y-1, z) && BlockCreepvine.hasSurroundingWater(world, x, y, z, true)) {
			boolean flag = false;
			int tries = 0;
			while (!flag && tries < 25) {
				flag = CreepvineGenerator.instance.generate(world, x, y, z, itemRand, 8, 9, 0.8F, false);
				tries++;
			}
			if (flag) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, GeoBlocks.CREEPVINE.getBlockInstance());
				is.stackSize--;
				return true;
			}
		}
		return false;
	}

}
