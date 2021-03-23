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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoISBRH;
import Reika.GeoStrata.World.LavaRockGenerator;
import Reika.RotaryCraft.API.Interfaces.EnvironmentalHeatSource;


public class BlockLavaRock extends Block implements EnvironmentalHeatSource {

	private final IIcon[] overlay = new IIcon[4];

	public BlockLavaRock(Material mat) {
		super(mat);

		this.setHardness(Blocks.stone.blockHardness);
		this.setResistance(Blocks.stone.blockResistance/3F);

		this.setCreativeTab(GeoStrata.tabGeo);
	}

	public static enum Flags {
		NONREPLACEABLE();

		public int flag() {
			return 1 << (2+this.ordinal());
		}

		public boolean applies(int meta) {
			return (meta & this.flag()) != 0;
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float maxY = 1;
		int meta = world.getBlockMetadata(x, y, z)%4;
		if (world.getBlock(x, y+1, z).isAir(world, x, y+1, z)) {
			switch(meta) {
				case 0:
					maxY = 1-0.125F+0.02F;
					break;
				case 1:
					maxY = 1-0.09375F;
					break;
				case 2:
					maxY = 1-0.0625F;
					break;
			}
		}
		this.setBlockBounds(0, 0, 0, 1, maxY, 1);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		float maxY = 1;
		int meta = world.getBlockMetadata(x, y, z)%4;
		switch(3-meta) {
			case 0:
				maxY = 1;
				break;
			case 1:
				maxY = 0.9375F;
				break;
			case 2:
				maxY = 0.875F;
				break;
			case 3:
				maxY = 0.75F;
				break;
		}
		return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + maxY, z + 1);
	}

	@Override
	public void getSubBlocks(Item id, CreativeTabs tab, List li) {
		for (int i = 0; i < 4; i++) {
			li.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int getRenderType() {
		return GeoISBRH.lavarock.getRenderID();
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
	public IIcon getIcon(int s, int meta) {
		return overlay[meta%4];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 4; i++) {
			overlay[i] = ico.registerIcon("geostrata:semilava/"+i);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityItem || e instanceof EntityXPOrb)
			return;
		int meta = world.getBlockMetadata(x, y, z)%4;
		if (meta == 3)
			return;
		boolean doEffect = true;
		if (e instanceof EntityLivingBase) {
			doEffect = !((EntityLivingBase)e).isPotionActive(Potion.fireResistance);
			if (e instanceof EntityPlayer) {
				doEffect &= !((EntityPlayer)e).capabilities.isCreativeMode;
			}
		}
		if (doEffect) {
			e.attackEntityFrom(meta == 0 ? DamageSource.lava : DamageSource.inFire, 3-meta);
			if (meta == 0) { //lava is 15
				e.setFire(8);
			}
			else if (meta == 1) {
				e.setFire(4);
			}
		}
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target) {
		if (Flags.NONREPLACEABLE.applies(world.getBlockMetadata(x, y, z)))
			return false;
		return target == this || target == Blocks.stone || target.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	@Override
	public SourceType getSourceType(IBlockAccess iba, int x, int y, int z) {
		return iba.getBlockMetadata(x, y, z)%4 == 0 ? SourceType.LAVA : SourceType.FIRE;
	}

	@Override
	public boolean isActive(IBlockAccess iba, int x, int y, int z) {
		return true;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		this.onBlockAdded(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (LavaRockGenerator.instance.doingLavaRockGen || !ReikaWorldHelper.isChunkPastCompletelyFinishedGenerating(world, x >> 4, z >> 4))
			return;
		int meta = world.getBlockMetadata(x, y, z)%4;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Material mat2 = ReikaWorldHelper.getMaterial(world, dx, dy, dz);
				if (ReikaBlockHelper.matchMaterialsLoosely(Material.water, mat2)) {
					int chance = 3+3*meta*meta; // 1 in: 3, 6, 15, 30
					boolean obsidian = world.rand.nextInt(chance) == 0;
					world.setBlock(x, y, z, obsidian ? Blocks.obsidian : (meta <= 1 ? Blocks.cobblestone : Blocks.stone));
				}
				else {

				}
			}
		}
		ReikaWorldHelper.temperatureEnvironment(world, x, y, z, this.getEffectiveTemperature(meta));
	}

	private int getEffectiveTemperature(int meta) { //0 is lava
		return 750-(meta%4)*150;
	}

}
