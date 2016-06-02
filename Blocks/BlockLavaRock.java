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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.GeoStrata.GeoStrata;


public class BlockLavaRock extends Block {

	public static final IIcon[] overlay = new IIcon[4];

	public BlockLavaRock(Material mat) {
		super(mat);

		this.setHardness(Blocks.stone.blockHardness);
		this.setResistance(Blocks.stone.blockResistance/3F);

		this.setCreativeTab(GeoStrata.tabGeo);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float maxY = 1;
		int meta = world.getBlockMetadata(x, y, z);
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
	public void getSubBlocks(Item id, CreativeTabs tab, List li) {
		for (int i = 0; i < 4; i++) {
			li.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int getRenderType() {
		return GeoStrata.proxy.lavarockRender;
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
		return Blocks.stone.getIcon(s, meta);//icons[meta];
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
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0)
			return;
		boolean doEffect = true;
		if (e instanceof EntityLivingBase) {
			doEffect = !((EntityLivingBase)e).isPotionActive(Potion.fireResistance);
		}
		if (doEffect) {
			e.attackEntityFrom(meta == 0 ? DamageSource.lava : DamageSource.inFire, 3-meta);
		}
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target)
	{
		return target == this || target == Blocks.stone;
	}

}
