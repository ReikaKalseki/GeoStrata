/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.GeoStrata.Registry.DecoBlocks;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.Registry.GeoOptions;
import Reika.GeoStrata.Registry.RockShapes;
import Reika.GeoStrata.Registry.RockTypes;
import buildcraft.BuildCraftCore;
import cpw.mods.fml.common.registry.GameRegistry;

public class GeoRecipes {

	public static void addRecipes() {
		for (int i = 0; i < RockTypes.rockList.length; i++) {
			RockTypes type = RockTypes.rockList[i];
			ItemStack smooth = type.getItem(RockShapes.SMOOTH);
			ItemStack cobble = type.getItem(RockShapes.COBBLE);
			ItemStack brick = type.getItem(RockShapes.BRICK);
			ItemStack fitted = type.getItem(RockShapes.FITTED);
			ItemStack tile = type.getItem(RockShapes.TILE);
			ItemStack round = type.getItem(RockShapes.ROUND);
			ItemStack engraved = type.getItem(RockShapes.ENGRAVED);
			ItemStack inscribed = type.getItem(RockShapes.INSCRIBED);
			ItemStack connected = type.getItem(RockShapes.CONNECTED);
			ItemStack connected2 = type.getItem(RockShapes.CONNECTED2);
			ItemStack etched = type.getItem(RockShapes.ETCHED);
			ItemStack centered = type.getItem(RockShapes.CENTERED);
			ItemStack cubed = type.getItem(RockShapes.CUBED);
			ItemStack lined = type.getItem(RockShapes.LINED);
			ItemStack embossed = type.getItem(RockShapes.EMBOSSED);
			ItemStack raised = type.getItem(RockShapes.RAISED);
			ItemStack fan = type.getItem(RockShapes.FAN);
			ItemStack spiral = type.getItem(RockShapes.SPIRAL);
			ItemStack moss = type.getItem(RockShapes.MOSSY);

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
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(engraved, 4), new Object[]{
				"SB", "BS", 'S', smooth, 'B', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(engraved, 4), new Object[]{
				"BS", "SB", 'S', smooth, 'B', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(connected, 8), new Object[]{
				"SSS", "S S", "SSS", 'S', smooth});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(connected2, 8), new Object[]{
				"SSS", "S S", "SSS", 'S', connected});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(etched, 3), new Object[]{
				"SSS", 'S', inscribed});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(cubed, 9), new Object[]{
				"SSS", "SSS", "SSS", 'S', smooth});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(centered, 5), new Object[]{
				" S ", "SRS", " S ", 'S', smooth, 'R', round});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(lined, 5), new Object[]{
				" S ", "SES", " S ", 'S', smooth, 'E', engraved});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(embossed, 3), new Object[]{
				"S", "T", "S", 'S', smooth, 'T', tile});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(raised, 4), new Object[]{
				"SS", "SS", 'S', tile});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(fan, 8), new Object[]{
				"AAB", "B B", "BAA", 'A', smooth, 'B', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(spiral, 8), new Object[]{
				"ABA", "B B", "ABA", 'A', smooth, 'B', brick});
			GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(moss, 2), new Object[]{
				"AB", "BA", 'A', smooth, 'B', Blocks.vine});

			for (int k = 0; k < RockShapes.shapeList.length; k++) {
				RockShapes shape = RockShapes.shapeList[k];
				ItemStack item = type.getItem(shape);
				if (shape != RockShapes.SMOOTH) {
					ReikaRecipeHelper.addSmelting(item, smooth, 0F);
				}
				ItemStack stair = ReikaItemHelper.getSizedItemStack(type.getStair(shape), 4);
				ItemStack slab = ReikaItemHelper.getSizedItemStack(type.getSlab(shape), 6);
				GameRegistry.addRecipe(slab, "BBB", 'B', item);
				GameRegistry.addRecipe(stair, "  B", " BB", "BBB", 'B', item);
				GameRegistry.addRecipe(stair, "B  ", "BB ", "BBB", 'B', item);
				GameRegistry.addRecipe(item, "B", "B", 'B', slab);
			}
			//GameRegistry.addShapelessRecipe(new ItemStack(Blocks.cobblestone), cobble);
		}

		for (int i = 0; i < DecoBlocks.list.length; i++) {
			DecoBlocks block = DecoBlocks.list[i];
			if (GeoOptions.BOXRECIPES.getState())
				block.addSizedCrafting(8*block.recipeMultiplier, "BBB", "B B", "BBB", 'B', block.material);
			else
				block.addSizedCrafting(4*block.recipeMultiplier, "BB", "BB", 'B', block.material);
		}

		ItemStack g = new ItemStack(Items.stick);
		if (ModList.BUILDCRAFT.isLoaded())
			g = getWoodGear();
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(GeoBlocks.PARTIAL.getBlockInstance(), 24, 0), "BSB", "SPS", "gSg", 'P', Blocks.planks, 'S', "stone", 'B', Blocks.iron_bars, 'g', g));

	}

	@ModDependent(ModList.BUILDCRAFT)
	private static ItemStack getWoodGear() {
		return new ItemStack(BuildCraftCore.woodenGearItem);
	}
}
