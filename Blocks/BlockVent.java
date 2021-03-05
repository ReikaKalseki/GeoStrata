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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.Interfaces.MinerBlock;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent.UpdateFlags;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoISBRH;
import Reika.RotaryCraft.API.Interfaces.EnvironmentalHeatSource;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVent extends Block implements MinerBlock, EnvironmentalHeatSource {

	public static final String SMOKE_VENT_TAG = "GEOSMOKEVENT";

	private final IIcon[] icons = new IIcon[VentType.list.length];
	private final IIcon[] iconsNether = new IIcon[VentType.list.length];

	private static final IIcon[] internal = new IIcon[VentType.list.length];
	//private static IIcon inactive;

	public BlockVent(Material par2Material) {
		super(par2Material);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setTickRandomly(true);
		this.setHardness(Blocks.stone.blockHardness);
		this.setResistance(Blocks.stone.blockResistance/3F);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityVent();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, this, 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		TileEntityVent te = (TileEntityVent)world.getTileEntity(x, y, z);
		if (id != this && world.isBlockIndirectlyGettingPowered(x, y, z)) {
			if (te.canFire())
				te.activate();
		}
		te.checkPlug();
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return this.fetchIcon(s, meta, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		//if (s == 1)
		return this.fetchIcon(s, world.getBlockMetadata(x, y, z), Minecraft.getMinecraft().theWorld.provider.dimensionId);
		//if (world.getBlock(x, y-1, z).getMaterial() == Material.rock)
		//	return world.getBlock(x, y-1, z).getIcon(world, x, y-1, z, s);
		//return this.getIcon(s, world.getBlockMetadata(x, y, z));
	}

	private IIcon fetchIcon(int side, int meta, int dim) {
		IIcon[] arr = icons;
		Block b = Blocks.stone;
		if (dim == -1) {
			arr = iconsNether;
			b = Blocks.netherrack;
		}
		else if (dim == 1) {
			b = Blocks.end_stone;
		}
		return side == 1 ? arr[meta] : b.getIcon(0, 0);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < icons.length; i++) {
			VentType v = VentType.list[i];
			icons[i] = ico.registerIcon("geostrata:vent/"+v.name().toLowerCase(Locale.ENGLISH)+"_top");
			iconsNether[i] = ico.registerIcon("geostrata:vent/nether/"+v.name().toLowerCase(Locale.ENGLISH)+"_top");
			internal[i] = ico.registerIcon("geostrata:vent/"+v.name().toLowerCase(Locale.ENGLISH)+"_inside");
		}
		//inactive = ico.registerIcon("geostrata:vent/inactive");
	}

	@Override
	public int getRenderType() {
		return GeoISBRH.vent.getRenderID();
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {/*

		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y+1, z, x+1, y+3, z+1);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			e.attackEntityFrom(DamageSource.inFire, 2);
		}*/

		TileEntityVent te = (TileEntityVent)world.getTileEntity(x, y, z);
		if (!world.isBlockIndirectlyGettingPowered(x, y, z) && te.canFire())
			te.activate();
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world)+rand.nextInt(2400));
	}

	@Override
	public int tickRate(World world) {
		return 800;
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return Blocks.stone.getItemDropped(id, r, fortune);
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}
	/*
	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		if (EnchantmentHelper.getSilkTouchModifier(ep)) {
			ep.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
			ep.addExhaustion(0.025F);
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(this, 1, meta/2));
		}
		else {
			super.harvestBlock(world, ep, x, y, z, meta);
		}
	}*/

	@Override
	public boolean isMineable(int meta) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getHarvestItems(World world, int x, int y, int z, int meta, int fortune) {
		return ReikaJavaLibrary.makeListFrom(new ItemStack(this, 1, meta));
	}

	@Override
	public SourceType getSourceType(IBlockAccess iba, int x, int y, int z) {
		TileEntityVent te = (TileEntityVent)iba.getTileEntity(x, y, z);
		switch(te.getType()) {
			case FIRE:
				return SourceType.FIRE;
			case LAVA:
			case PYRO:
				return SourceType.LAVA;
			case WATER:
				return SourceType.WATER;
			case CRYO:
				return SourceType.ICY;
			default:
				return null;
		}
	}

	@Override
	public boolean isActive(IBlockAccess iba, int x, int y, int z) {
		TileEntityVent te = (TileEntityVent)iba.getTileEntity(x, y, z);
		return te.isActive();
	}

	@Override
	public MineralCategory getCategory() {
		return MineralCategory.MISC_UNDERGROUND;
	}

	@Override
	public Block getReplacedBlock(World world, int x, int y, int z) {
		return Blocks.stone;
	}

	@Override
	public boolean allowSilkTouch(int meta) {
		return true;
	}

	public static class TileEntityVent extends TileEntity {

		private int activeTimer = 0;
		private VentType type;
		private static final Random rand = new Random();
		private boolean plugged;

		private void activate() {
			activeTimer = 40+rand.nextInt(600);
			type = this.getType();
			//worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, type.ordinal()*2+1, 3);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "fire.ignite");
			if (this.getType() == VentType.GAS) {
				if (worldObj.getBlock(xCoord, yCoord+1, zCoord) == Blocks.fire || worldObj.getBlock(xCoord, yCoord+1, zCoord) == Blocks.lava) {
					this.explode(1.5F);
				}
			}
		}

		private void checkPlug() {
			boolean last = plugged;
			plugged = this.isBlocking(worldObj, xCoord, yCoord+1, zCoord);
			if (plugged && !last && this.isActive()) { //just got plugged, firing
				this.explode(1);
			}
		}

		private void explode(float factor) {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			boolean fire = type == VentType.FIRE || type == VentType.LAVA;
			float f = 2;
			if (type == VentType.FIRE || type == VentType.LAVA)
				f = 3;
			else if (type == VentType.STEAM)
				f = 4;
			else if (type == VentType.GAS)
				f = 6;
			worldObj.newExplosion(null, xCoord, yCoord, zCoord, f*factor, fire, true);
		}

		public boolean canFire() {
			return this.canTick(worldObj, xCoord, zCoord) && !this.isBlocking(worldObj, xCoord, yCoord+1, zCoord);
		}

		private boolean isBlocking(World world, int x, int y, int z) {
			return world.getBlock(x, y, z).isOpaqueCube();
		}

		private void onDeactivate() {
			//worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, type.ordinal()*2, 3);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void updateEntity() {
			if (this.isActive()) {

				this.onTick();

				activeTimer--;
				if (activeTimer == 0) {
					this.onDeactivate();
				}
			}
		}

		private void onTick() {
			if (activeTimer%type.getSoundInterval() == 0) {
				switch(type) {
					case FIRE:
					case LAVA:
					case PYRO:
						ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "mob.ghast.fireball", 0.25F, 1);
						break;
					case STEAM:
					case GAS:
						ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.fizz", 0.25F, 1.5F);
						break;
					case SMOKE:
						ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.fizz", 0.25F, 0.25F);
						break;
					case WATER:
						if (activeTimer%32 == 0) {
							ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "liquid.water", 2F, 1F);
							ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "liquid.water", 2F, 1F);
						}
						break;
					case ENDER:
						ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "portal.portal", 0.25F, rand.nextFloat() * 0.4F + 0.8F);
						break;
					case CRYO:
						ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "mob.silverfish.step", 0.25F, 0.25F);
						break;
				}
			}

			if (!worldObj.isRemote) {
				//ReikaJavaLibrary.pConsole(activeTimer+":"+this, yCoord == 63);
				if (type.dealsDamage()) {
					AxisAlignedBB box = this.getEffectBox();
					List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
					for (EntityLivingBase e : li) {
						e.attackEntityFrom(type.getDamageSrc(), type.damage);
						if (type == VentType.FIRE || type == VentType.LAVA || type == VentType.PYRO)
							e.setFire(type.damage);
					}
				}

				AxisAlignedBB box = type.getEffectBox(this);
				if (box != null) {
					List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
					for (EntityLivingBase e : li) {
						type.applyEntityEffect(e, rand);
					}
				}

				/*
			else if (type == VentType.SMOKE) {
				AxisAlignedBB box = this.getEffectBox();
				List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase e : li) {
					e.setAir(Math.max(0, e.getAir()-1));
				}
			}*/
				type.doAoE(worldObj, xCoord, yCoord, zCoord, rand);

				if (rand.nextInt(20) == 0) {
					int temp = type.getTemperature();
					for (int i = 0; i < 5; i++)
						ReikaWorldHelper.temperatureEnvironment(worldObj, xCoord, yCoord+i, zCoord, temp);
				}
			}

			ReikaParticleHelper p = type.getParticle();
			if (p != null) {
				int n = p == ReikaParticleHelper.FLAME ? 3 : p == ReikaParticleHelper.RAIN ? 8 : 1;
				n *= type.getParticleRate();
				for (int i = 0; i < n; i++) {
					double px = xCoord+rand.nextDouble();
					double py = yCoord+0.5+rand.nextDouble();//+rand.nextDouble()*3;
					double pz = zCoord+rand.nextDouble();
					if (p == ReikaParticleHelper.MOBSPELL || p == ReikaParticleHelper.LAVA || p == ReikaParticleHelper.RAIN)
						py += rand.nextDouble()*2;
					double vx = 0;
					double vz = 0;
					double vy = 0.25+rand.nextDouble()/2;
					//p.spawnAt(worldObj, px, py, pz);
					worldObj.spawnParticle(p.name, px, py+type.getParticleYOffset(), pz, vx, vy, vz);
				}
			}
		}

		private AxisAlignedBB getEffectBox() {
			int i = 1;
			for (i = 1; i < 4; i++) {
				if (this.isBlocking(worldObj, xCoord, yCoord+i, zCoord))
					break;
			}
			return AxisAlignedBB.getBoundingBox(xCoord, yCoord+1, zCoord, xCoord+1, yCoord+i+1, zCoord+1);
		}

		public VentType getType() {
			return VentType.list[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (type != null)
				NBT.setInteger("type", type.ordinal());
			NBT.setInteger("tick", activeTimer);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("type"))
				type = VentType.list[NBT.getInteger("type")];
			activeTimer = NBT.getInteger("tick");
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
		}

		public boolean isActive() {
			return activeTimer > 0 && canTick(worldObj, xCoord, zCoord);
		}

		private static boolean canTick(World world, int x, int z) {
			return ReikaWorldHelper.isRadiusLoaded(world, x, z, 2);
		}

	}

	public static enum VentType {
		STEAM(1),
		SMOKE(0),
		FIRE(2),
		LAVA(8),
		GAS(0),
		WATER(0),
		ENDER(0),
		PYRO(15),
		CRYO(4);

		public final int damage;

		private final Interpolation heightCurve = new Interpolation(false);
		private final Interpolation heightCurveNether = new Interpolation(false);

		public static final VentType[] list = values();

		private VentType(int dmg) {
			damage = dmg;
			heightCurve.addPoint(0, 0);
			heightCurveNether.addPoint(0, 0);
			heightCurve.addPoint(72, 0);
			heightCurveNether.addPoint(128, 0);
		}

		public boolean dealsDamage() {
			return damage > 0;
		}

		public double getSpawnWeight(int y, boolean nether) {
			return nether ? heightCurveNether.getValue(y) : heightCurve.getValue(y);
		}

		public boolean canGenerateInOverworld() {
			switch(this) {
				case ENDER:
				case PYRO:
					return false;
				default:
					return true;
			}
		}

		public boolean canGenerateInNether() {
			switch(this) {
				case STEAM:
				case SMOKE:
				case FIRE:
				case LAVA:
				case GAS:
				case PYRO:
					return true;
				default:
					return false;
			}
		}

		private AxisAlignedBB getEffectBox(TileEntityVent te) {
			switch(this) {
				case WATER:
				case SMOKE:
					return te.getEffectBox();
				case GAS:
				case PYRO:
					return ReikaAABBHelper.getBlockAABB(te).expand(3, 3, 3).offset(0, 2, 0);
				case ENDER:
					return ReikaAABBHelper.getBlockAABB(te).expand(2, 2, 2).offset(0, 1, 0);
				default:
					return null;
			}
		}

		private void applyEntityEffect(EntityLivingBase e, Random rand) {
			switch(this) {
				case WATER:
					if (e instanceof EntityEnderman) {
						e.attackEntityFrom(DamageSource.drown, 1);
						((EntityEnderman)e).teleportRandomly();
					}
					else {
						e.extinguish();
					}
					break;
				case SMOKE:
					e.getEntityData().setLong(SMOKE_VENT_TAG, e.worldObj.getTotalWorldTime());
					break;
				case GAS:
					e.addPotionEffect(new PotionEffect(Potion.poison.id, 20+rand.nextInt(200), rand.nextInt(4) == 0 ? 1 : 0));
					break;
				case ENDER:
					double ox = e.posX;
					double oy = e.posY;
					double oz = e.posZ;
					boolean flag = true;
					while (flag) {
						double rx = ReikaRandomHelper.getRandomPlusMinus(ox, 6);
						double ry = ReikaRandomHelper.getRandomPlusMinus(oy, 1);
						double rz = ReikaRandomHelper.getRandomPlusMinus(oz, 6);
						e.setPositionAndUpdate(rx, ry, rz);
						e.playSound("mob.endermen.portal", 1, 1);
						flag = !e.worldObj.getCollidingBoundingBoxes(e, e.boundingBox).isEmpty() || e.worldObj.isAnyLiquid(e.boundingBox);
					}
					break;
				case PYRO:
					e.setFire(60);
					ReikaEntityHelper.damageArmor(e, 4);
					break;
				default:
					break;
			}
		}

		private void doAoE(World world, int x, int y, int z, Random rand) {
			switch(this) {
				case WATER:
					if (rand.nextInt(20) == 0) {
						int rx = ReikaRandomHelper.getRandomPlusMinus(x, 6);
						int ry = ReikaRandomHelper.getRandomPlusMinus(y, 1);
						int rz = ReikaRandomHelper.getRandomPlusMinus(z, 6);
						if (world.checkChunksExist(rx, ry, rz, rx, ry, rz)) {
							Block b = world.getBlock(rx, ry, rz);
							if (b == Blocks.farmland) {
								int meta = world.getBlockMetadata(rx, ry, rz);
								world.setBlockMetadataWithNotify(rx, ry, rz, 7, 3);
							}
							if (rand.nextInt(3) == 0) {
								b.updateTick(world, rx, ry, rz, rand);
								BlockTickEvent.fire(world, rx, ry, rz, b, UpdateFlags.FORCED.flag+UpdateFlags.NATURAL.flag);
							}
						}
					}
					break;
				case PYRO: {
					if (rand.nextInt(10) == 0) {
						int rx = ReikaRandomHelper.getRandomPlusMinus(x, 6);
						int ry = ReikaRandomHelper.getRandomPlusMinus(y, 1);
						int rz = ReikaRandomHelper.getRandomPlusMinus(z, 6);
						if (world.checkChunksExist(rx, ry, rz, rx, ry, rz) && ReikaWorldHelper.isExposedToAir(world, rx, ry, rz)) {
							Block b = world.getBlock(rx, ry, rz);
							if (b == Blocks.stone || b == Blocks.cobblestone || b == Blocks.stonebrick) {
								world.setBlock(rx, ry, rz, Blocks.lava);
							}
						}
					}
				}
				case CRYO: {
					if (rand.nextInt(20) == 0) {
						int rx = ReikaRandomHelper.getRandomPlusMinus(x, 6);
						int ry = ReikaRandomHelper.getRandomPlusMinus(y, 1);
						int rz = ReikaRandomHelper.getRandomPlusMinus(z, 6);
						if (world.checkChunksExist(rx, ry, rz, rx, ry, rz)) {
							Block b = world.getBlock(rx, ry, rz);
							if (b == Blocks.water || b == Blocks.flowing_water) {
								world.setBlock(rx, ry, rz, Blocks.ice);
							}
							else if (b.isSideSolid(world, rx, ry, rz, ForgeDirection.UP) && world.getBlock(rx, ry+1, rz) == Blocks.air) {
								world.setBlock(rx, ry+1, rz, Blocks.snow_layer);
							}
						}
					}
				}
				break;
				default:
					break;
			}
		}

		public int getTemperature() {
			switch(this) {
				case FIRE:
					return 800;
				case LAVA:
					return 900;
				case PYRO:
					return 1500;
				case WATER:
					return 15;
				case CRYO:
					return -40;
				default:
					return 25;
			}
		}

		public DamageSource getDamageSrc() {
			switch(this) {
				case STEAM:
				case FIRE:
					return DamageSource.inFire;
				case LAVA:
				case PYRO:
					return DamageSource.lava;
				case CRYO:
					return DamageSource.generic;
				default:
					return null;
			}
		}

		public ReikaParticleHelper getParticle() {
			switch(this) {
				case STEAM:
					return ReikaParticleHelper.CLOUD;
				case SMOKE:
					return ReikaParticleHelper.LARGESMOKE;
				case FIRE:
					return ReikaParticleHelper.FLAME;
				case LAVA:
				case PYRO:
					return ReikaParticleHelper.LAVA;
				case GAS:
					return ReikaParticleHelper.MOBSPELL;
				case WATER:
					return ReikaParticleHelper.RAIN;
				case ENDER:
					return ReikaParticleHelper.PORTAL;
				default:
					return null;
			}
		}

		private double getParticleYOffset() {
			return this == ENDER ? 1.5 : 0;
		}

		public int getParticleRate() {
			return this == PYRO ? 3 : 1;
		}

		public int getSoundInterval() {
			switch(this) {
				case PYRO:
					return 2;
				case ENDER:
					return 20;
				default:
					return 4;
			}
		}

		public IIcon getIcon() {
			return internal[this.ordinal()];
		}

		//public IIcon getInactiveIcon() {
		//	return inactive;
		//}

		public boolean isSelfLit() {
			return this == FIRE || this == LAVA || this == PYRO;
		}

		static {
			STEAM.heightCurve.addPoint(4, 0);
			STEAM.heightCurve.addPoint(24, 40);

			SMOKE.heightCurve.addPoint(4, 0);
			SMOKE.heightCurve.addPoint(24, 40);

			FIRE.heightCurve.addPoint(4, 20);
			FIRE.heightCurve.addPoint(24, 40);
			FIRE.heightCurve.addPoint(32, 0);

			LAVA.heightCurve.addPoint(4, 70);
			LAVA.heightCurve.addPoint(10, 40);
			LAVA.heightCurve.addPoint(14, 30);
			LAVA.heightCurve.addPoint(20, 0);

			GAS.heightCurve.addPoint(4, 10);
			GAS.heightCurve.addPoint(16, 20);
			GAS.heightCurve.addPoint(24, 10);
			GAS.heightCurve.addPoint(32, 0);

			WATER.heightCurve.addPoint(24, 0);
			WATER.heightCurve.addPoint(30, 10);
			WATER.heightCurve.addPoint(40, 20);
			WATER.heightCurve.addPoint(60, 30);

			CRYO.heightCurve.addPoint(40, 0);
			CRYO.heightCurve.addPoint(50, 5);
			CRYO.heightCurve.addPoint(60, 20);


			STEAM.heightCurveNether.addPoint(40, 0);
			STEAM.heightCurveNether.addPoint(60, 40);
			STEAM.heightCurveNether.addPoint(120, 40);

			SMOKE.heightCurveNether.addPoint(10, 40);
			SMOKE.heightCurveNether.addPoint(120, 40);

			FIRE.heightCurveNether.addPoint(10, 60);
			FIRE.heightCurveNether.addPoint(30, 80);
			FIRE.heightCurveNether.addPoint(80, 80);
			FIRE.heightCurveNether.addPoint(110, 20);

			LAVA.heightCurveNether.addPoint(4, 80);
			LAVA.heightCurveNether.addPoint(30, 80);
			LAVA.heightCurveNether.addPoint(40, 50);
			LAVA.heightCurveNether.addPoint(70, 40);
			LAVA.heightCurveNether.addPoint(100, 10);

			GAS.heightCurveNether.addPoint(4, 20);
			GAS.heightCurveNether.addPoint(20, 50);
			GAS.heightCurveNether.addPoint(30, 40);
			GAS.heightCurveNether.addPoint(40, 20);
			GAS.heightCurveNether.addPoint(110, 20);

			PYRO.heightCurveNether.addPoint(4, 30);
			PYRO.heightCurveNether.addPoint(20, 45);
			PYRO.heightCurveNether.addPoint(30, 30);
			PYRO.heightCurveNether.addPoint(40, 0);
		}
	}


}
