/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.GeoStrata.Registry.DecoBlocks;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoItems;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

public class GeoRecipes {

	public static void addRecipes() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes type = RockTypes.rockList[i];
			ItemStack smooth = type.getItem(RockShapes.SMOOTH);
			ItemStack cobble = type.getItem(RockShapes.COBBLESTONE);
			ItemStack brick = type.getItem(RockShapes.BRICK);
			ItemStack fitted = type.getItem(RockShapes.FITTED);
			ItemStack tile = type.getItem(RockShapes.TILE);
			ItemStack round = type.getItem(RockShapes.ROUND);
			ItemStack engraved = type.getItem(RockShapes.ENGRAVED);
			ItemStack inscribed = type.getItem(RockShapes.INSCRIBED);

			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(brick, 4), new Object[]{
				"SS", "SS", 'S', smooth});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(round, 4), new Object[]{
				"SS", "SS", 'S', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(fitted, 2), new Object[]{
				"SS", 'S', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(tile, 4), new Object[]{
				" S ", "S S", " S ", 'S', smooth});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(inscribed, 3), new Object[]{
				"B", "S", "B", 'S', smooth, 'B', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(engraved, 2), new Object[]{
				"SB", "BS", 'S', smooth, 'B', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(engraved, 2), new Object[]{
				"BS", "SB", 'S', smooth, 'B', brick});
			FurnaceRecipes.smelting().addSmelting(cobble.itemID, cobble.getItemDamage(), smooth, 0.2F);
			//GameRegistry.addShapelessRecipe(new ItemStack(Block.cobblestone), cobble);
		}

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = GeoItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i);
			ItemStack cave = new ItemStack(GeoBlocks.CRYSTAL.getBlockID(), 1, i);
			ItemStack supercry = new ItemStack(GeoBlocks.SUPER.getBlockID(), 1, i);
			ItemStack seed = GeoItems.SEED.getStackOfMetadata(i);
			ItemStack pendant = GeoItems.PENDANT.getStackOfMetadata(i);
			ItemStack pendant3 = GeoItems.PENDANT3.getStackOfMetadata(i);

			GameRegistry.addRecipe(lamp, " s ", "sss", "SSS", 's', shard, 'S', ReikaItemHelper.stoneSlab);
			GameRegistry.addRecipe(pendant, "GSG", "QCQ", "EDE", 'E', Item.enderPearl, 'D', Item.diamond, 'G', Block.glowStone, 'Q', Item.netherQuartz, 'C', cave, 'S', Item.silk);
			GameRegistry.addRecipe(pendant3, "DSD", "GCG", "ETE", 'E', Item.eyeOfEnder, 'D', Item.diamond, 'G', Item.ingotGold, 'T', Item.ghastTear, 'C', supercry, 'S', Item.silk);
			GameRegistry.addRecipe(seed, "GSG", "SsS", "GSG", 'G', Item.glowstone, 'S', shard, 's', Item.seeds);
		}

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack shard = GeoItems.SHARD.getStackOfMetadata(i);
			ItemStack lamp = new ItemStack(GeoBlocks.LAMP.getBlockID(), 1, i);
			ItemStack potion = new ItemStack(GeoBlocks.SUPER.getBlockID(), 1, i);
			GameRegistry.addRecipe(potion, "RsG", "sss", "SDS", 's', shard, 'S', Block.obsidian, 'D', Block.blockGold, 'R', Block.blockRedstone, 'G', Block.glowStone);
			GameRegistry.addRecipe(potion, "RlG", "SDS", 'l', lamp, 'S', Block.obsidian, 'D', Block.blockGold, 'R', Block.blockRedstone, 'G', Block.glowStone);
		}

		for (int i = 0; i < DecoBlocks.list.length; i++) {
			DecoBlocks block = DecoBlocks.list[i];
			if (GeoOptions.BOXRECIPES.getState())
				block.addSizedCrafting(8, "BBB", "B B", "BBB", 'B', block.material);
			else
				block.addSizedCrafting(4, "BB", "BB", 'B', block.material);
		}

		GameRegistry.addRecipe(new ItemStack(GeoBlocks.BREWER.getBlockID(), 1, 0), "NNN", "NBN", "SSS", 'N', Item.netherQuartz, 'S', Block.stone, 'B', Item.brewingStand);


		if (ModList.THERMALEXPANSION.isLoaded()) {
			FluidStack crystal = FluidRegistry.getFluidStack("potion crystal", 8000);
			int energy = 40000;
			for (int i = 0; i < 16; i++) {
				NBTTagCompound toSend = new NBTTagCompound();
				toSend.setInteger("energy", energy);
				toSend.setCompoundTag("input", new NBTTagCompound());
				toSend.setCompoundTag("output", new NBTTagCompound());
				GeoItems.SHARD.getStackOfMetadata(i).writeToNBT(toSend.getCompoundTag("input"));
				crystal.writeToNBT(toSend.getCompoundTag("output"));
				FMLInterModComms.sendMessage(ModList.THERMALEXPANSION.modLabel, "CrucibleRecipe", toSend);
			}
		}

		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(0), " R ", "B P", " M ", 'B', getShard(ReikaDyeHelper.BLUE), 'R', getShard(ReikaDyeHelper.RED), 'P', getShard(ReikaDyeHelper.PURPLE), 'M', getShard(ReikaDyeHelper.MAGENTA));
		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(1), " Y ", "C L", " G ", 'G', getShard(ReikaDyeHelper.GREEN), 'Y', getShard(ReikaDyeHelper.YELLOW), 'C', getShard(ReikaDyeHelper.CYAN), 'L', getShard(ReikaDyeHelper.LIME));
		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(2), " B ", "P O", " L ", 'B', getShard(ReikaDyeHelper.BROWN), 'P', getShard(ReikaDyeHelper.PINK), 'O', getShard(ReikaDyeHelper.ORANGE), 'L', getShard(ReikaDyeHelper.LIGHTBLUE));
		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(3), " B ", "G L", " W ", 'B', getShard(ReikaDyeHelper.BLACK), 'G', getShard(ReikaDyeHelper.GRAY), 'L', getShard(ReikaDyeHelper.LIGHTGRAY), 'W', getShard(ReikaDyeHelper.WHITE));

		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(4), " B ", "G G", " B ", 'B', GeoItems.CLUSTER.getStackOfMetadata(2), 'G', GeoItems.CLUSTER.getStackOfMetadata(3));
		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(5), " B ", "G G", " B ", 'B', GeoItems.CLUSTER.getStackOfMetadata(0), 'G', GeoItems.CLUSTER.getStackOfMetadata(1));

		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(6), " B ", "G G", " B ", 'B', GeoItems.CLUSTER.getStackOfMetadata(4), 'G', GeoItems.CLUSTER.getStackOfMetadata(5));

		GameRegistry.addRecipe(GeoItems.CLUSTER.getStackOfMetadata(7), " B ", "BPB", " B ", 'B', GeoItems.CLUSTER.getStackOfMetadata(6), 'P', Item.eyeOfEnder);

		GameRegistry.addRecipe(new ItemStack(GeoBlocks.GUARDIAN.getBlockInstance()), "BBB", "BPB", "BBB", 'B', getShard(ReikaDyeHelper.WHITE), 'P', GeoItems.CLUSTER.getStackOfMetadata(7));
	}

	private static ItemStack getShard(ReikaDyeHelper color) {
		return GeoItems.SHARD.getStackOfMetadata(color.ordinal());
	}
}
