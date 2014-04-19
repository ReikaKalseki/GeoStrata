/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata.Base;

import java.awt.Color;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.RotaryCraft.API.ItemFetcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class RockBlock extends Block {

	protected Icon[] icons = new Icon[16];

	public RockBlock(int ID, Material mat) {
		super(ID, mat);
		this.setCreativeTab(GeoStrata.tabGeo);
		blockHardness = 1F;
	}

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
		return 0.05F/RockTypes.getTypeAtCoords(world, x, y, z).blockHardness*is.getItem().getStrVsBlock(is, this)*eff*buff;
	}

	@Override
	public final float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ) {
		return RockTypes.getTypeAtCoords(world, x, y, z).blastResistance/4F; // /5F is in vanilla code
	}

	@Override
	protected final boolean canSilkHarvest() {
		return true;
	}

	@Override
	public final boolean canHarvestBlock(EntityPlayer player, int meta) {
		if (player.capabilities.isCreativeMode)
			return false;
		return RockTypes.getTypeFromIDandMeta(blockID, meta).isHarvestable(player.getCurrentEquippedItem());
	}

	@Override
	public final Icon getIcon(int s, int meta) {
		return icons[meta];
	}

	public final int getBaseRockTypeOrdinal() {
		return RockTypes.getTypeFromIDandMeta(blockID, 0).ordinal();
	}

	@Override
	public abstract int idDropped(int id, Random r, int fortune);

	@Override
	public abstract int damageDropped(int meta);

	@Override
	public abstract int quantityDropped(Random r);

	@Override
	public final int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		RockTypes rock = RockTypes.getTypeAtCoords(iba, x, y, z);
		//ReikaJavaLibrary.pConsole(rock);
		if (rock == RockTypes.OPAL) {
			int sc = 48;
			float hue1 = (float)(ReikaMathLibrary.py3d(x, y*4, z+x)%sc)/sc;
			//float hue2 = (float)(Math.cos(x/24D)+Math.sin(z/24D))+(y%360)*0.05F;
			return Color.HSBtoRGB(hue1, 0.4F, 1F);
		}
		else {
			return super.colorMultiplier(iba, x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final int getRenderColor(int dmg) {
		RockTypes rock = RockTypes.getTypeFromIDandMeta(blockID, dmg);
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		if (rock == RockTypes.OPAL) {
			int sc = 48;
			float hue1 = (float)(ReikaMathLibrary.py3d(x, y*4, z+x)%sc)/sc;
			//float hue2 = (float)(Math.cos(x/24D)+Math.sin(z/24D))+(y%360)*0.05F;
			return Color.HSBtoRGB(hue1, 0.4F, 1F);
		}
		else {
			return super.getRenderColor(dmg);
		}
	}

}
