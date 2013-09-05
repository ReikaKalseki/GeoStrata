/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Base;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Blocks.BlockCaveCrystal;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class CrystalBlock extends Block {

	protected final Icon[] icons = new Icon[ReikaDyeHelper.dyes.length];

	public CrystalBlock(int ID, Material mat) {
		super(ID, mat);
		this.setCreativeTab(GeoStrata.tabGeo);
		this.setHardness(1F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final Icon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public final int getRenderType() {
		return 6;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		int color = world.getBlockMetadata(x, y, z);
		double[] v = ReikaDyeHelper.getColorFromDamage(color).getRedstoneParticleVelocityForColor();
		world.spawnParticle("reddust", x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), v[0], v[1], v[2]);/*
		//ReikaJavaLibrary.pConsole(FMLCommonHandler.instance().getEffectiveSide());
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.orb", 1F, 1);*/
		if (rand.nextInt(3) == 0)
			ReikaPacketHelper.sendUpdatePacket(GeoStrata.packetChannel, 0, world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addBlockHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		Random rand = new Random();
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int color = world.getBlockMetadata(x, y, z);
		double[] v = ReikaDyeHelper.getColorFromDamage(color).getRedstoneParticleVelocityForColor();
		for (int i = 0; i < 4; i++)
			world.spawnParticle("reddust", x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), v[0], v[1], v[2]);
		ReikaPacketHelper.sendUpdatePacket(GeoStrata.packetChannel, 0, world, x, y, z);
		return false;
	}

	public void updateEffects(World world, int x, int y, int z) {
		Random rand = new Random();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.orb", 0.1F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
		if (this instanceof BlockCaveCrystal) {
			AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, y+1, z+1).expand(3, 3, 3);
			List inbox = world.getEntitiesWithinAABB(EntityLiving.class, box);
			for (int i = 0; i < inbox.size(); i++) {
				EntityLiving e = (EntityLiving)inbox.get(i);
				if (ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY+e.getEyeHeight()/2F-y-0.5, e.posZ-z-0.5) <= 4)
					this.getEffectFromColor(e, ReikaDyeHelper.getColorFromDamage(world.getBlockMetadata(x, y, z)));
			}
		}
	}

	private void getEffectFromColor(EntityLiving e, ReikaDyeHelper color) {
		int dura = 200;
		switch(color) {
		case BLACK:
			if (e instanceof EntityMob) {
				EntityMob m = (EntityMob)e;
				m.setAttackTarget(null);
				m.getNavigator().clearPathEntity();
			}
			break;
		case BLUE:
			e.addPotionEffect(new PotionEffect(Potion.nightVision.id, dura, 0));
			break;
		case BROWN:
			if (e instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)e;
				float sat = ep.getFoodStats().getSaturationLevel();
				sat += 0.5F;
				ep.getFoodStats().setFoodSaturationLevel(sat);
				break;
			}
		case CYAN:
			e.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, dura, 0));
			break;
		case GRAY:
			e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, dura, 0));
			break;
		case GREEN:
			e.addPotionEffect(new PotionEffect(Potion.poison.id, dura, 0));
			break;
		case LIGHTBLUE:
			e.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, dura, 0));
			break;
		case LIGHTGRAY:
			e.addPotionEffect(new PotionEffect(Potion.weakness.id, dura, 0));
			break;
		case LIME:
			e.addPotionEffect(new PotionEffect(Potion.jump.id, dura, 0));
			break;
		case MAGENTA:
			e.addPotionEffect(new PotionEffect(Potion.regeneration.id, dura, 0));
			break;
		case ORANGE:
			e.addPotionEffect(new PotionEffect(Potion.fireResistance.id, dura, 0));
			break;
		case PINK:
			e.addPotionEffect(new PotionEffect(Potion.damageBoost.id, dura, 0));
			break;
		case PURPLE:
			if (!e.worldObj.isRemote && new Random().nextInt(12) == 0)
				e.worldObj.spawnEntityInWorld(new EntityXPOrb(e.worldObj, e.posX, e.posY, e.posZ, 1));
			break;
		case RED:
			e.addPotionEffect(new PotionEffect(Potion.resistance.id, dura, 0));
			break;
		case WHITE:
			e.clearActivePotions();
			break;
		case YELLOW:
			e.addPotionEffect(new PotionEffect(Potion.digSpeed.id, dura, 0));
			break;
		default:
			break;
		}
	}
}
