/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.GeoGen;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.minecraftforge.common.Configuration;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.GeoGen.Registry.GeoBlocks;
import Reika.GeoGen.Registry.GeoItems;
import Reika.GeoGen.Registry.GeoOptions;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class GeoConfig {

	public static Configuration config;

	/** Change this to cause auto-deletion of users' config files to load new copies */
	private static final int CURRENT_CONFIG_ID = 0;
	private static int readID;
	private static File configFile;

	public static Object[] controls = new Object[GeoOptions.optionList.length];
	public static int[] blockIDs = new int[GeoBlocks.blockList.length];
	public static int[] itemIDs = new int[GeoItems.itemList.length];

	public static void initProps(FMLPreInitializationEvent event) {

		//allocate the file to the config
		configFile = event.getSuggestedConfigurationFile();
		config = new Configuration(configFile);

		//load data
		config.load();

		if (checkReset(config)) {
			ReikaJavaLibrary.pConsole("GEOGEN: Config File Format Changed. Resetting...");
			resetConfigFile();
			initProps(event);
			return;
		}

		for (int i = 0; i < GeoOptions.optionList.length; i++) {
			String label = GeoOptions.optionList[i].getLabel();
			if (GeoOptions.optionList[i].isBoolean())
				controls[i] = GeoOptions.optionList[i].setState(config);
			if (GeoOptions.optionList[i].isNumeric())
				controls[i] = GeoOptions.optionList[i].setValue(config);
		}

		for (int i = 0; i < GeoBlocks.blockList.length; i++) {
			String name = GeoBlocks.blockList[i].getBasicName();
			blockIDs[i] = config.get("Rock Block IDs", name, 800+i).getInt();
		}

		for (int i = 0; i < GeoItems.itemList.length; i++) {
			String name = GeoItems.itemList[i].getBasicName();
			itemIDs[i] = config.get("Item IDs", name, 10000+i).getInt();
		}

		/*******************************/
		//save the data
		config.save();
	}

	private static boolean checkReset(Configuration config) {
		readID = config.get("Control", "Config ID - Edit to have your config auto-deleted", CURRENT_CONFIG_ID).getInt();
		return readID != CURRENT_CONFIG_ID;
	}

	private static void resetConfigFile() {
		String path = configFile.getAbsolutePath().substring(0, configFile.getAbsolutePath().length()-4)+"_Old_Config_Backup.txt";
		File backup = new File(path);
		if (backup.exists())
			backup.delete();
		try {
			ReikaJavaLibrary.pConsole("GEOGEN: Writing Backup File to "+path);
			ReikaJavaLibrary.pConsole("GEOGEN: Use this to restore custom IDs if necessary.");
			backup.createNewFile();
			if (!backup.exists())
				ReikaJavaLibrary.pConsole("GEOGEN: Could not create backup file at "+path+"!");
			else {
				PrintWriter p = new PrintWriter(backup);
				p.println("#####----------THESE ARE ALL THE OLD CONFIG SETTINGS YOU WERE USING----------#####");
				p.println("#####---IF THEY DIFFER FROM THE DEFAULTS, YOU MUST RE-EDIT THE CONFIG FILE---#####");



				p.close();
			}
		}
		catch (IOException e) {
			ReikaJavaLibrary.pConsole("GEOGEN: Could not create backup file due to IOException!");
			e.printStackTrace();
		}
		configFile.delete();
	}

}
