/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoStrata;

import java.util.HashMap;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

public class CrystalPotionController {

	private static HashMap<ReikaDyeHelper, Potion> map = new HashMap<ReikaDyeHelper, Potion>();

	static {
		addColorPotion(ReikaDyeHelper.BLUE, Potion.nightVision);
		addColorPotion(ReikaDyeHelper.CYAN, Potion.waterBreathing);
		addColorPotion(ReikaDyeHelper.GRAY, Potion.moveSlowdown);
		addColorPotion(ReikaDyeHelper.GREEN, Potion.poison);
		addColorPotion(ReikaDyeHelper.LIGHTBLUE, Potion.moveSpeed);
		addColorPotion(ReikaDyeHelper.LIGHTGRAY, Potion.weakness);
		addColorPotion(ReikaDyeHelper.LIME, Potion.jump);
		addColorPotion(ReikaDyeHelper.MAGENTA, Potion.regeneration);
		addColorPotion(ReikaDyeHelper.RED, Potion.resistance);
		addColorPotion(ReikaDyeHelper.ORANGE, Potion.fireResistance);
		addColorPotion(ReikaDyeHelper.PINK, Potion.damageBoost);
		addColorPotion(ReikaDyeHelper.YELLOW, Potion.digSpeed);
		addColorPotion(ReikaDyeHelper.WHITE, Potion.invisibility);
	}

	private static void addColorPotion(ReikaDyeHelper color, Potion pot) {
		map.put(color, pot);
	}

	public static PotionEffect getEffectFromColor(ReikaDyeHelper color, int dura, int level) {
		Potion pot = map.get(color);
		if (pot == null)
			return null;
		return new PotionEffect(pot.id, dura, level);
	}

	public static String getPotionName(ReikaDyeHelper color) {
		if (color == ReikaDyeHelper.BLACK)
			return "corrupting";
		if (color == ReikaDyeHelper.BROWN)
			return "lengthening";
		if (color == ReikaDyeHelper.PURPLE)
			return "enhancing";
		Potion pot = map.get(color);
		if (pot == null)
			return "";
		return StatCollector.translateToLocal(pot.getName());
	}

	public static boolean requiresCustomPotion(ReikaDyeHelper color) {
		if (color == ReikaDyeHelper.CYAN)
			return true;
		if (color == ReikaDyeHelper.YELLOW)
			return true;
		if (color == ReikaDyeHelper.LIME)
			return true;
		if (color == ReikaDyeHelper.RED)
			return true;
		return false;
	}

	public static boolean isCorruptedPotion(ReikaDyeHelper color) {
		if (color == ReikaDyeHelper.GRAY)
			return true;
		if (color == ReikaDyeHelper.LIGHTGRAY)
			return true;
		if (color == ReikaDyeHelper.WHITE)
			return true;
		return false;
	}
}
