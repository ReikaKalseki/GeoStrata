package Reika.GeoStrata.Blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.Block.Submergeable;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoISBRH;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCreepvine extends Block implements Submergeable, IPlantable, IShearable {

	private final IIcon[] rootIcons = new IIcon[2];
	private final IIcon[] stemIcons = new IIcon[4];
	private final IIcon[] stemEmptyIcons = new IIcon[2];
	private final IIcon[] seedIcons = new IIcon[5];
	private final IIcon[] topIcons = new IIcon[4];
	private final IIcon[] topYoungIcons = new IIcon[1];
	private IIcon topIcon;

	public BlockCreepvine() {
		super(Material.water);
		this.setTickRandomly(true);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setStepSound(soundTypeGrass);
	}

	public static enum Pieces {
		ROOT,
		STEM,
		TOP,
		CORE_EMPTY,
		CORE_1,
		CORE_2,
		CORE_3,
		CORE_4,
		CORE_5,
		TOP_YOUNG,
		STEM_EMPTY;

		public static final Pieces[] list = values();

		public boolean canGrowSeeds() {
			return this.ordinal() >= CORE_EMPTY.ordinal() && this.ordinal() <= CORE_4.ordinal();
		}

		public boolean canBeHarvested() {
			return this.ordinal() > CORE_EMPTY.ordinal() && this.ordinal() <= CORE_5.ordinal();
		}

		public boolean isCore() {
			return this.ordinal() >= CORE_EMPTY.ordinal() && this.ordinal() <= CORE_5.ordinal();
		}

		public int getSeedCount() {
			return this.canBeHarvested() ? this.ordinal()-CORE_EMPTY.ordinal() : 0;
		}

		public int getLightLevel() {
			return this.canBeHarvested() ? this.getSeedCount()*3 : 0;
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}
	/*
	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return s <= 1 ? topIcon : stemIcon;
	}
	 */
	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("geostrata:creepvine/core");
		topIcon = ico.registerIcon("geostrata:creepvine/block_top");
		for (int i = 0; i < stemIcons.length; i++) {
			stemIcons[i] = ico.registerIcon("geostrata:creepvine/stem_"+i);
		}
		for (int i = 0; i < stemEmptyIcons.length; i++) {
			stemEmptyIcons[i] = ico.registerIcon("geostrata:creepvine/stem_empty_"+i);
		}
		for (int i = 0; i < rootIcons.length; i++) {
			rootIcons[i] = ico.registerIcon("geostrata:creepvine/root_"+i);
		}
		for (int i = 0; i < seedIcons.length; i++) {
			seedIcons[i] = ico.registerIcon("geostrata:creepvine/seed_"+i);
		}
		for (int i = 0; i < topIcons.length; i++) {
			topIcons[i] = ico.registerIcon("geostrata:creepvine/top_"+i);
		}
		for (int i = 0; i < topYoungIcons.length; i++) {
			topYoungIcons[i] = ico.registerIcon("geostrata:creepvine/top_young_"+i);
		}
	}

	public IIcon getRandomRootIcon(Random rand) {
		return rootIcons[rand.nextInt(rootIcons.length)];
	}

	public IIcon getRandomStemIcon(Random rand, boolean empty) {
		IIcon[] arr = empty ? stemEmptyIcons : stemIcons;
		return arr[rand.nextInt(arr.length)];
	}

	public IIcon getRandomTopIcon(Random rand, boolean young) {
		IIcon[] arr = young ? topYoungIcons : topIcons;
		return arr[rand.nextInt(arr.length)];
	}

	public IIcon getSeedIcon(int idx) {
		return seedIcons[idx];
	}

	public IIcon getBlockTop() {
		return topIcon;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int lvl = Pieces.list[world.getBlockMetadata(x, y, z)].getLightLevel();
		return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(0xffd620, lvl) : lvl;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (this.checkStability(world, x, y, z)) {
			int m = world.getBlockMetadata(x, y, z);
			if (Pieces.list[m].canGrowSeeds()) {
				world.setBlockMetadataWithNotify(x, y, z, m+1, 3);
				world.markBlockForUpdate(x, y, z);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		int m = world.getBlockMetadata(x, y, z);
		if (Pieces.list[m].canBeHarvested()) {
			world.setBlockMetadataWithNotify(x, y, z, m-1, 3);
			world.markBlockForUpdate(x, y, z);
			ReikaPlayerAPI.addOrDropItem(new ItemStack(GeoStrata.creepvineSeeds), ep);
			ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.click", 0.5F, 2F);
			return true;
		}
		else if (Pieces.list[m] == Pieces.CORE_EMPTY)
			return true;
		return false;
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y-1, z);
		int m = world.getBlockMetadata(x, y-1, z);
		Pieces p = world.getBlock(x, y, z) == this ? Pieces.list[world.getBlockMetadata(x, y, z)] : Pieces.ROOT;
		switch(p) {
			case CORE_EMPTY:
			case CORE_1:
			case CORE_2:
			case CORE_3:
			case CORE_4:
			case CORE_5:
				return b == this && m == Pieces.STEM_EMPTY.ordinal();
			case ROOT:
				return b == Blocks.dirt || b == Blocks.gravel || b == Blocks.clay || b == Blocks.sand || b.canSustainPlant(world, x, y-1, z, ForgeDirection.UP, this);
			case STEM:
			case STEM_EMPTY:
				return b == this && (m == Pieces.ROOT.ordinal() || m == p.ordinal());
			case TOP:
				return b == this && (Pieces.list[m].isCore() || m == Pieces.TOP.ordinal());
			case TOP_YOUNG:
				return b == this && m == Pieces.STEM.ordinal();
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		this.checkStability(world, x, y, z);
	}

	protected final boolean checkStability(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canPlaceBlockAt(world, x, y, z);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return GeoISBRH.creepvine.getRenderID();
	}

	@Override
	public boolean isSubmergeable(IBlockAccess iba, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean renderLiquid(int meta) {
		return true;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		GeoISBRH.creepvine.setRenderPass(pass);
		return pass <= 1;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float s = 0.375F;//0.125F;
		this.setBlockBounds(0.5F-s, 0, 0.5F-s, 0.5F+s, 1, 0.5F+s);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Item getItemDropped(int dmg, Random rand, int fortune) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return 0xffffff;//world.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Water;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}
	/*
	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		return ReikaJavaLibrary.makeListFrom(new ItemStack(this));
	}*/

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		return ReikaJavaLibrary.makeListFrom(new ItemStack(this));
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && is.getItem() instanceof ItemShears) {
			return;
		}
		super.harvestBlock(world, ep, x, y, z, meta);
	}
}