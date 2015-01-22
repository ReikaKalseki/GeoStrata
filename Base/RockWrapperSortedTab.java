package Reika.GeoStrata.Base;

import java.util.Comparator;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.GUI.SortedCreativeTab;

public abstract class RockWrapperSortedTab extends SortedCreativeTab {

	public RockWrapperSortedTab(String name) {
		super(name);
	}

	@Override
	protected final Comparator<ItemStack> getComparator() {
		return sorter;
	}

	private static final RockWrapperSorter sorter = new RockWrapperSorter();

	private static class RockWrapperSorter implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return o1.getItemDamage()-o2.getItemDamage();
		}

	}
}
