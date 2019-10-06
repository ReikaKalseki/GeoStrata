package Reika.GeoStrata.Items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.GeoStrata.Blocks.BlockRFCrystalSeed.TileRFCrystal;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;


public class ItemBlockFluxCrystal extends ItemBlock {

	public ItemBlockFluxCrystal(Block b) {
		super(b);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		boolean ret = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (ret) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileRFCrystal) {
				if (!GeoOptions.RFACTIVATE.getState() || (stack.stackTagCompound != null && stack.stackTagCompound.getBoolean("activated")))
					((TileRFCrystal)te).activate();
			}
		}
		return ret;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return GeoBlocks.RFCRYSTALSEED.hasMultiValuedName() ? GeoBlocks.RFCRYSTALSEED.getMultiValuedName(is.getItemDamage()) : GeoBlocks.RFCRYSTALSEED.getBasicName();
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (GeoOptions.RFACTIVATE.getState()) {
			if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("activated")) {
				li.add("Activated");
			}
			else {
				li.add("Needs activation");
			}
		}
	}

}
