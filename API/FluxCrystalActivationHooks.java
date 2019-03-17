package Reika.GeoStrata.API;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class FluxCrystalActivationHooks {

	public static FluxCrystalActivationRegistry instance;

	public static interface FluxCrystalActivationRegistry {

		public void addHook(FluxCrystalActivationHook h);

		//public boolean isFluxCrystalActive(TileEntity te);

	}

	public static interface FluxCrystalActivationHook {

		public boolean isFluxCrystalActivatable(ItemStack is, TileEntity te, EntityPlayer ep);

	}

}
