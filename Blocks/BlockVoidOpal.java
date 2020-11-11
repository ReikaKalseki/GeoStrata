/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Blocks;

import java.util.Random;

import com.carpentersblocks.api.IWrappableBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.API.RockProofStone;
import Reika.GeoStrata.Rendering.VoidOpalRenderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@Strippable(value={"com.carpentersblocks.api.IWrappableBlock"})
public class BlockVoidOpal extends Block implements RockProofStone, IWrappableBlock {

	private final IIcon[][] subTextures = new IIcon[4][4];

	private static IIcon baseTexture;
	private static IIcon baseTexture_Trans;

	private static IIcon[] fleckTextures = new IIcon[32];

	public BlockVoidOpal(Material m) {
		super(m);

		this.setHardness(12);
		this.setResistance(90000);

		this.setCreativeTab(GeoStrata.tabGeo);

		this.setTickRandomly(true);
	}

	public static IIcon getBaseTexture(boolean trans) {
		return trans ? baseTexture_Trans : baseTexture;
	}

	public static IIcon getRandomFleckTexture(Random rand) {
		return fleckTextures[rand.nextInt(fleckTextures.length)];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.PARTICLE.ordinal(), world, x, y, z, 256, ReikaParticleHelper.BONEMEAL.ordinal(), 3);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return GeoStrata.proxy.voidopalRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		VoidOpalRenderer.renderPass = pass;
		return pass <= 1;
	}

	public static int getXIndex(int x, int y, int z, int s) {
		int a = 0;
		switch(ForgeDirection.VALID_DIRECTIONS[s]) {
			case UP:
				a = x%4;
				break;
			case DOWN:
				a = x%4;
				break;
			case EAST:
				a = z%4;
				a = 3-a;
				break;
			case WEST:
				a = z%4;
				break;
			case NORTH:
				a = x%4;
				a = 3-a;
				break;
			case SOUTH:
				a = x%4;
				break;
			default:
				break;
		}
		a = (a+4)%4;
		return a;
	}

	public static int getYIndex(int x, int y, int z, int s) {
		int b = 0;
		switch(ForgeDirection.VALID_DIRECTIONS[s]) {
			case UP:
				b = z%4;
				break;
			case DOWN:
				b = z%4;
				break;
			case EAST:
				b = y%4;
				b = 3-b;
				break;
			case WEST:
				b = y%4;
				b = 3-b;
				break;
			case NORTH:
				b = y%4;
				b = 3-b;
				break;
			case SOUTH:
				b = y%4;
				b = 3-b;
				break;
			default:
				break;
		}
		b = (b+4)%4;
		return b;
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return subTextures[getXIndex(x, y, z, s)][getYIndex(x, y, z, s)];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("geostrata:voidopal/full");
		baseTexture = ico.registerIcon("geostrata:voidopal/glowmix");
		baseTexture_Trans = ico.registerIcon("geostrata:voidopal/glowcover");
		/*
		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 4; k++) {
				subTextures[i][k] = ico.registerIcon("geostrata:voidopal/"+i+"-"+k);
			}
		}
		for (int k = 0; k < fleckTextures.length; k++) {
			fleckTextures[k] = ico.registerIcon("geostrata:voidopal/fleck/"+(k+1));
		}*/
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return entity instanceof EntityDragon ? false : super.canEntityDestroy(world, x, y, z, entity);
	}

	@Override
	@SuppressWarnings("incomplete-switch")
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int s)  {
		ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[s];
		if (this.isBlockedOnSide(world, x, y, z, side)) {
			int a = 0;
			int b = 0;
			int c = 0;
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					switch(side) {
						case UP:
						case DOWN:
							a = i;
							c = k;
							break;
						case EAST:
						case WEST:
							b = i;
							c = k;
							break;
						case NORTH:
						case SOUTH:
							b = i;
							a = k;
							break;
					}
					int dx = x+a;
					int dy = y+b;
					int dz = z+c;
					if (world.getBlock(dx, dy, dz) == this && !this.isBlockedOnSide(world, dx, dy, dz, side)) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}

	private boolean isBlockedOnSide(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		Block at = world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ);
		if (at == this || at.isOpaqueCube()) {
			return true;
		}
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		return 4;
	}

	@Override
	public int getLightOpacity(IBlockAccess iba, int x, int y, int z) {
		return 0;
	}

	@Override
	public boolean blockRockGeneration(World world, int x, int y, int z, Block b, int meta) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorMultiplier(IBlockAccess iba, int x, int y, int z, Block b, int meta) {
		return this.colorMultiplier(iba, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int side, Block b, int meta) {
		return this.getIcon(iba, x, y, z, side);
	}

	@Override
	public int getWeakRedstone(World world, int x, int y, int z, Block b, int meta) {
		return 0;
	}

	@Override
	public int getStrongRedstone(World world, int x, int y, int z, Block b, int meta) {
		return 0;
	}

	@Override
	public float getHardness(World world, int x, int y, int z, Block b, int meta) {
		return this.getBlockHardness(world, x, y, z);
	}

	@Override
	public float getBlastResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ, Block b, int meta) {
		return this.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
	}

	@Override
	public int getFlammability(IBlockAccess iba, int x, int y, int z, ForgeDirection side, Block b, int meta) {
		return 0;
	}

	@Override
	public int getFireSpread(IBlockAccess iba, int x, int y, int z, ForgeDirection side, Block b, int meta) {
		return 0;
	}

	@Override
	public boolean sustainsFire(IBlockAccess iba, int x, int y, int z, ForgeDirection side, Block b, int meta) {
		return false;
	}

	@Override
	public boolean isLog(IBlockAccess iba, int x, int y, int z, Block b, int meta) {
		return false;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess iba, int x, int y, int z, Entity e, Block b, int meta) {
		return this.canEntityDestroy(iba, x, y, z, e);
	}

}
