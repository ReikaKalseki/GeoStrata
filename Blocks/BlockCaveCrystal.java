/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.Base.CrystalBlock;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.RotaryCraft.API.ItemFetcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCaveCrystal extends CrystalBlock {

	private static final Random rand = new Random();

	public BlockCaveCrystal(int ID, Material mat) {
		super(ID, mat);
		this.setLightValue(0.65F);
	}

	@Override
	public final int idDropped(int id, Random r, int fortune) {
		return GeoItems.SHARD.getShiftedItemID();
	}

	@Override
	public final int damageDropped(int meta) {
		return meta;
	}

	@Override
	public final int quantityDropped(Random r) {
		return 1+r.nextInt(6)+r.nextInt(3);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		if (ModList.ROTARYCRAFT.isLoaded() && ep != null) {
			if (ItemFetcher.isPlayerHoldingBedrockPick(ep)) {
				return true;
			}
		}
		if (GeoOptions.PURPLE.getState()) {
			return meta != ReikaDyeHelper.PURPLE.ordinal();
		}
		return true;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		if (GeoOptions.PURPLE.getState() && meta == ReikaDyeHelper.PURPLE.ordinal())
			ReikaWorldHelper.splitAndSpawnXP(world, x+0.5, y+0.5, z+0.5, 400);
		ArrayList<ItemStack> li = new ArrayList();
		int num = this.getNumberDrops(meta, fortune);
		for (int i = 0; i < num; i++)
			li.add(GeoItems.SHARD.getStackOfMetadata(meta));
		return li;
	}

	private int getNumberDrops(int meta, int fortune) {
		return 1+rand.nextInt(6+fortune)+(1+fortune)*rand.nextInt(3)+rand.nextInt(1+fortune);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("GeoStrata:crystal_outline");
		}
	}

	@Override
	public boolean shouldMakeNoise() {
		return true;
	}

	@Override
	public boolean shouldGiveEffects() {
		return true;
	}

	@Override
	public int getRange() {
		return 3;
	}

	@Override
	public int getDuration() {
		return 200;
	}

	@Override
	public boolean renderBase() {
		return false;
	}

	@Override
	public Block getBaseBlock(ForgeDirection side) {
		return Block.cobblestone;
	}

	@Override
	public int getPotionLevel() {
		return 0;
	}
}
