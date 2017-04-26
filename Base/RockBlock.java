/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Base;

import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.RotaryCraft.API.Interfaces.Laserable;

import com.carpentersblocks.api.IWrappableBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value={"com.carpentersblocks.api.IWrappableBlock", "mcp.mobius.waila.api.IWailaDataProvider"})
public abstract class RockBlock extends Block implements Laserable, IWrappableBlock, IWailaDataProvider {

	protected IIcon[] icons = new IIcon[RockTypes.rockList.length];

	public RockBlock() {
		super(Material.rock);
		this.setCreativeTab(GeoStrata.tabGeoRock);
		//blockHardness = 1F;
	}
	/*
	@Override
	public final float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		ItemStack is = ep.getCurrentEquippedItem();
		int meta = world.getBlockMetadata(x, y, z);
		float buff = ModList.ROTARYCRAFT.isLoaded() && ItemFetcher.isPlayerHoldingBedrockPick(ep) ? 1.875F : 1;
		float eff = 1;
		if (is != null) {
			int level = ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency, is);
			eff = ReikaEnchantmentHelper.getEfficiencyMultiplier(level);
		}
		if (!this.canHarvestBlock(ep, meta)) {
			return 0.01F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness;
		}
		if (is == null)
			return 0.4F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness;
		if (TinkerToolHandler.getInstance().isPick(is) || TinkerToolHandler.getInstance().isHammer(is)) {
			return 0.05F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness*6*eff;
		}
		return 0.05F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness*is.getItem().getDigSpeed(is, this, meta)*eff*buff;
	}

	@Override
	public final float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ) {
		return RockTypes.getTypeAtCoords(world, x, y, z).blastResistance/4F; // /5F is in vanilla code
	}*/

	@Override
	protected final boolean canSilkHarvest() {
		return true;
	}
	/*
	@Override
	public final boolean canHarvestBlock(EntityPlayer player, int meta) {
		if (player.capabilities.isCreativeMode)
			return false;
		return RockTypes.getTypeFromID(this).isHarvestable(player.getCurrentEquippedItem());
	}*/

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[RockTypes.getTypeFromID(this).ordinal()];
	}

	public final int getBaseRockTypeOrdinal() {
		return RockTypes.getTypeFromID(this).ordinal();
	}

	@Override
	public abstract Item getItemDropped(int id, Random r, int fortune);

	@Override
	public abstract int damageDropped(int meta);

	@Override
	public abstract int quantityDropped(Random r);

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		RockTypes rock = RockTypes.getTypeFromID(this); //not coords, to account for being used as a cover
		//ReikaJavaLibrary.pConsole(rock);
		return this.getColor(iba, x, y, z, rock);
	}

	public int getColor(IBlockAccess iba, int x, int y, int z, RockTypes rock) {
		if (rock == RockTypes.OPAL) {
			return GeoStrata.getOpalPositionColor(iba, x, y, z);
		}
		else {
			return super.colorMultiplier(iba, x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final int getRenderColor(int dmg) {
		RockTypes rock = RockTypes.getTypeFromID(this);
		if (rock == RockTypes.OPAL) {
			return GeoStrata.getOpalPositionColor(Minecraft.getMinecraft().theWorld, RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ);
		}
		else {
			return super.getRenderColor(dmg);
		}
	}

	@Override
	public void whenInBeam(World world, int x, int y, int z, long power, int range) {
		RockTypes rock = RockTypes.getTypeAtCoords(world, x, y, z);
		float chance = 50;
		if (rock.blockHardness >= 10)
			chance = 10;
		else if (rock.blockHardness >= 6)
			chance = 20;
		else if (rock.blockHardness >= 4)
			chance = 40;
		else if (rock.blockHardness >= 2)
			chance = 60;
		else
			chance = 80;

		if (ReikaRandomHelper.doWithChance(chance)) {
			world.setBlock(x, y, z, Blocks.flowing_lava);
		}
	}

	@Override
	public boolean blockBeam(World world, int x, int y, int z, long power) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorMultiplier(IBlockAccess iba, int x, int y, int z, Block b, int meta) {
		return this.getColor(iba, x, y, z, RockTypes.getTypeFromID(b));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int side, Block b, int meta) {
		return this.getIcon(side, meta);
	}

	@Override
	public int getWeakRedstone(World world, int x, int y, int z, Block b, int meta) {
		return super.isProvidingWeakPower(world, x, y, z, meta);
	}

	@Override
	public int getStrongRedstone(World world, int x, int y, int z, Block b, int meta) {
		return super.isProvidingStrongPower(world, x, y, z, meta);
	}

	@Override
	public float getHardness(World world, int x, int y, int z, Block b, int meta) {
		return RockTypes.getTypeFromID(b).blockHardness;
	}

	@Override
	public float getBlastResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ, Block b, int meta) {
		return RockTypes.getTypeFromID(b).blastResistance*3; //x3 is in setResistance
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

	@Override
	@ModDependent(ModList.WAILA)
	public final ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return null;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		if (GeoOptions.WAILA.getState()) {
			RockTypes type = RockTypes.getTypeFromID(acc.getBlock());
			String tag = String.format("%.2fR / %.2fH", type.blastResistance, type.blockHardness);
			currenttip.add(tag);
		}
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

}
