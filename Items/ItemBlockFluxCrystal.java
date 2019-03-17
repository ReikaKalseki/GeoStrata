package Reika.GeoStrata.Items;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.GeoStrata.API.FluxCrystalActivationHooks;
import Reika.GeoStrata.API.FluxCrystalActivationHooks.FluxCrystalActivationHook;
import Reika.GeoStrata.API.FluxCrystalActivationHooks.FluxCrystalActivationRegistry;
import Reika.GeoStrata.Blocks.BlockRFCrystalSeed.TileRFCrystal;


public class ItemBlockFluxCrystal extends ItemBlock implements FluxCrystalActivationRegistry {

	private final HashSet<FluxCrystalActivationHook> hooks = new HashSet();

	public ItemBlockFluxCrystal(Block b) {
		super(b);

		FluxCrystalActivationHooks.instance = this;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		boolean ret = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (ret) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileRFCrystal) {
				boolean flag = true;
				for (FluxCrystalActivationHook h : hooks) {
					if (!h.isFluxCrystalActivatable(stack, te, player)) {
						flag = false;
						break;
					}
				}
				if (flag)
					((TileRFCrystal)te).activate();
			}
		}
		return ret;
	}

	@Override
	public void addHook(FluxCrystalActivationHook h) {
		hooks.add(h);
	}

}
