/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Bees;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;

public class BlockCrystalHive extends Block {

	private static final Random rand = new Random();

	private final Icon[][] icons = new Icon[16][6];

	public BlockCrystalHive(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setHardness(3);
		this.setResistance(5);
		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public boolean canDragonDestroy(World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		BeeSpecies bee = this.getBeeForMeta(metadata);
		if (bee != null) {
			float chance = Math.min(0.95F, (1+fortune)*0.25F);
			int drones = ReikaRandomHelper.doWithChance(chance) ? 2 : 1;
			for (int i = 0; i < drones; i++) {
				li.add(bee.getBeeItem(world, EnumBeeType.DRONE));
			}
			li.add(bee.getBeeItem(world, EnumBeeType.PRINCESS));
		}
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(GeoBlocks.HIVE.getBlockID(), 1, meta);
	}

	private BeeSpecies getBeeForMeta(int meta) {
		switch(meta) {
		case 0:
			return CrystalBees.crystal;
		case 1:
			return CrystalBees.purity;
		default:
			return null;
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		ReikaParticleHelper p = meta == 0 ? ReikaParticleHelper.AMBIENTMOBSPELL : ReikaParticleHelper.ENCHANTMENT;
		int dy = meta == 0 ? y : y+1;
		p.spawnAroundBlock(world, x, dy, z, 8);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ico)
	{
		for (int i = 0; i < 2; i++) {
			icons[0][i] = ico.registerIcon("geostrata:hives/crystal_top"); //make crystal hive translucent?
		}
		for (int i = 2; i < 6; i++) {
			icons[0][i] = ico.registerIcon("geostrata:hives/crystal_side");
		}

		for (int i = 0; i < 2; i++) {
			icons[1][i] = ico.registerIcon("geostrata:hives/pure_top");
		}
		for (int i = 2; i < 6; i++) {
			icons[1][i] = ico.registerIcon("geostrata:hives/pure_side");
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta][s];
	}

}
